pipeline {
    agent any

    environment {
        IMAGE_NAME = "andersongomesc/jenkins01"
    }

    stages {

        stage('Verificar Usuário') {
            steps {
                sh 'whoami'
            }
        }

        stage('Checkout Source') {
            steps {
                git url: 'https://github.com/soncastro/jenkins-example.git', branch: 'main'
            }
        }

        stage('Build & Push Image com Kaniko') {
            agent {
                docker {
                    // Usa o executor oficial do Kaniko
                    image 'gcr.io/kaniko-project/executor:latest'
                    args '-u root:root -v /kaniko/.docker:/kaniko/.docker'
                }
            }
            environment {
                IMAGE_TAG = "${env.BUILD_ID}"
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                        echo "===> Configurando autenticação Docker Hub para o Kaniko..."
                        mkdir -p /kaniko/.docker
                        echo "{\"auths\":{\"https://index.docker.io/v1/\":{\"auth\":\"$(echo -n '${DOCKER_USER}:${DOCKER_PASS}' | base64)\"}}}" > /kaniko/.docker/config.json

                        echo "===> Iniciando build e push da imagem com Kaniko..."
                        /kaniko/executor \
                          --context `pwd` \
                          --dockerfile `pwd`/Dockerfile \
                          --destination=${IMAGE_NAME}:${IMAGE_TAG} \
                          --destination=${IMAGE_NAME}:latest \
                          --cache=true
                    '''
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
                    sh '''
                        echo "===> Atualizando versão da imagem no manifesto Kubernetes..."
                        sed -i "s/{{tag}}/$tag_version/g" ./k8s/deployment.yaml
                        cat ./k8s/deployment.yaml
                    '''

                    sh '''
                        echo "===> Baixando kubectl..."
                        KUBE_VERSION="v1.28.0"
                        KUBECTL_URL="https://storage.googleapis.com/kubernetes-release/release/$KUBE_VERSION/bin/linux/amd64/kubectl"
                        curl -sLO $KUBECTL_URL
                        chmod +x kubectl
                        ./kubectl version --client
                    '''

                    withKubeConfig(credentialsId: 'kubeconfig') {
                        sh '''
                            echo "===> Aplicando manifestos no cluster Kubernetes..."
                            ./kubectl apply -f ./k8s/
                        '''
                    }
                }
            }
        }
    }
}
