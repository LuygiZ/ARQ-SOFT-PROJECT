pipeline {
    agent any  // Usa qualquer agente disponível

    stages {
        stage('Build') {
            steps {
                echo 'A iniciar o processo de build...'
            }
        }

        stage('Test') {
            steps {
                echo 'A correr testes...'
            }
        }

        stage('Deploy') {
            steps {
                echo 'A fazer deploy...'
            }
        }
    }

    post {
        success {
            echo 'Pipeline concluído com sucesso! 🚀'
        }
        failure {
            echo 'O pipeline falhou ❌'
        }
    }
}
