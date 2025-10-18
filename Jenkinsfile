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

                    // CORREÇÃO ROBUSTA: Baixar e usar o binário do kubectl.
                    sh '''
                        echo "Baixando o binário do kubectl para o Agente..."

                        # Definindo a versão
                        KUBE_VERSION="v1.28.0"
                        KUBECTL_URL="https://storage.googleapis.com/kubernetes-release/release/$KUBE_VERSION/bin/linux/amd64/kubectl"

                        # Tentativa de download com curl, fallback para wget
                        if command -v curl >/dev/null 2>&1; then
                          curl -sLO $KUBECTL_URL
                        elif command -v wget >/dev/null 2>&1; then
                          wget -q $KUBECTL_URL
                        else
                          echo "ERRO CRÍTICO: Nem 'curl' nem 'wget' encontrados para baixar kubectl. O agente está muito minimalista."
                          exit 1
                        fi

                        # Tornar executável
                        chmod +x kubectl

                        echo "kubectl versão (apenas --client):"
                        ./kubectl version --client

                        echo "kubectl pronto para uso."

                        # NOVO: Imprime o caminho absoluto para ser capturado pela variável Groovy.
                        pwd > .kubectl_pwd
                    '''

                    // Captura o caminho absoluto do workspace em Groovy e define a variável de ambiente
                    def kubectl_path = sh(returnStdout: true, script: 'cat .kubectl_pwd').trim()
                    env.KUBECTL_PATH = "${kubectl_path}/kubectl"
                    echo "Caminho Absoluto do Kubectl Definido: ${env.KUBECTL_PATH}"

                    // NOVO: Usa o step 'withKubeConfig' para carregar a credencial
                    // e executa o comando 'kubectl' usando o caminho absoluto (${env.KUBECTL_PATH})
                    withKubeConfig(credentialsId: 'kubeconfig') {
                        sh """
                            # Aplica todos os manifestos YAML na pasta k8s usando o caminho absoluto.
                            ${env.KUBECTL_PATH} apply -f ./k8s/
                        """
                    }
                }
            }
        }
    }
}