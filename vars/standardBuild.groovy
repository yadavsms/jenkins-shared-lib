def call(Map config) {
    node {
        def app = config.app
        stage('Checkout') {
            checkout scm
        }
        stage("Build image") {
            app = docker.build("config.environment")
        }
        stage('Push Image') {
            withDockerRegistry(registry: [url: 'https://index.docker.io/v1/', credentialsId:'docker-hub-credentials']) {
                app.push("latest")
            }
        }
        stage('Main') {
            docker.image(config.environment).inside {
                sh config.mainScript
            }
        }
        stage('Post') {
            docker.image(config.environment).inside {
                sh config.postScript
            } 
        }
    }
}
