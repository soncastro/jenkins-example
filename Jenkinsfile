pipeline {
    agent {
        kubernetes {
            yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: kaniko
    image: gcr.io/kaniko-project/executor:latest
    command:
    - cat
    tty: true
    volumeMounts:
    - name: kaniko-secret
      mountPath: /kaniko/.docker
  volumes:
  - name: kaniko-secret
    secret:
      secretName: dockerhub-secret
"""
        }
    }

    environment {
        REGISTRY = "docker.io"
        IMAGE_NAME = "andersongomesc/jenkins01"
        K8S_NAMESPACE = "default"
    }

    stages {

        stage('Verificar Usuário') {
            steps {
                sh 'whoami'
            }
        }

        stage('Checkout') {
            steps {
                git url:'https://github.com/soncastro/jenkins-example.git', branch:'main'
            }
        }

        stage('Build com Kaniko') {
            steps {
                script {
                    // Recupera as credenciais salvas no Jenkins (tipo "Username with password")
                    withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {

                        sh """
                        # Cria diretório temporário para o auth.json do Docker
                        mkdir -p /kaniko/.docker

                        # Gera arquivo de credencial para o Kaniko
                        echo '{"auths":{"$REGISTRY":{"username":"$DOCKER_USER","password":"$DOCKER_PASS"}}}' > /kaniko/.docker/config.json

                        # Executa o Kaniko para buildar e enviar a imagem
                        /kaniko/executor \
                          --context `pwd` \
                          --dockerfile `pwd`/Dockerfile \
                          --destination $REGISTRY/$IMAGE_NAME:latest \
                          --cleanup
                        """
                    }
                }
            }
        }

        stage('Deploy no Kubernetes') {
            steps {
                script {
                    sh """
                    kubectl set image deployment/jenkins01 \
                      jenkins01=$REGISTRY/$IMAGE_NAME:latest \
                      -n $K8S_NAMESPACE
                    """
                }
            }
        }
    }

    post {
        success {
            echo 'Deploy realizado com sucesso!'
        }
        failure {
            echo 'Falha na pipeline.'
        }
    }
}
