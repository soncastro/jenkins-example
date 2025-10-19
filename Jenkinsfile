pipeline {
    agent any

    stages {
        stage('Checkout Source') {
            steps {
                git url:'https://github.com/soncastro/jenkins-example.git', branch:'main'
            }
        }

        stage('Buid Image') {
            steps {
                script {
                    dockerapp = docker.build("andersongomesc/jenkins01:${env.BUILD_ID}", '.')
                }
            }
        }

        stage('Push Image') {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'dockerhub') {
                        dockerapp.push('latest')
                        dockerapp.push("${env.BUILD_ID}")
                    }
                }
            }
        }

        stage('Deploy Kubernetes') {
            agent {
                kubernetes {
                    cloud 'kubernetes'
                }
            }
            environment {
                tag_version = "${env.BUILD_ID}"
            }
            steps {
                script {
                    sh 'sed -i "s/{{tag}}/$tag_version/g" ./k8s/deployment.yaml'
                    sh 'cat ./k8s/deployment.yaml'
                    sh 'kubectl config use-context cluster-jenkins-01'
                    sh 'kubectl apply -f ./k8s/deployment.yaml'
                    sh 'kubectl apply -f ./k8s/service.yaml'
                }
            }
        }
    }
}