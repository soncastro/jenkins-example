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
                        echo "Instalando kubectl no Agente..."
                        # Atualiza os índices e instala o kubectl. A flag --no-cache otimiza o uso de espaço.
                        # Esta operação é feita com as permissões do usuário do contêiner (geralmente root ou jenkins)
                        apk update --no-cache
                        apk add kubectl
                        echo "kubectl instalado com sucesso!"
                    '''

                    withKubeConfig(credentialsId: 'kubeconfig') {
                        sh 'kubectl apply -f ./k8s/'
                    }
                }
            }
        }
    }
}