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

                    sh '''
                        # Instala o kubectl (assumindo um sistema de pacotes baseado em apt/get, comum em muitas bases)
                        # Este bloco pode falhar se o Agente tiver permissões restritas ou um OS diferente.
                        # Uma solução mais robusta seria usar uma imagem base como 'bitnami/kubectl'
                        # ou 'ubuntu:latest' no 'agent { container '...' }'

                        echo "Tentando instalar kubectl..."
                        if command -v apt >/dev/null 2>&1; then
                          sudo apt-get update -qq && sudo apt-get install -y kubectl
                        elif command -v apk >/dev/null 2>&1; then
                          sudo apk add kubectl
                        else
                          echo "Erro: Não foi possível encontrar apt ou apk para instalar kubectl."
                        fi
                    '''

                    withKubeConfig(credentialsId: 'kubeconfig') {
                        sh 'kubectl apply -f ./k8s/'
                    }
                }
            }
        }
    }
}