pipeline {
	agent any


	triggers{
		githubPush()
	}

	tools {
		maven 'Maven 3.9.11'
	}

	environment {
		MVN_CMD = "mvn"
	}

	stages{
		stage('Checkout') {
			steps {
				echo '📥 A fazer checkout do repositório...'
				git url: 'https://github.com/LuygiZ/ARQ-SOFT-PROJECT.git', branch: 'master'
			}
		}

		stage('Build & Compile') {
			steps {
				echo '🚀 A iniciar o build...'
				bat '${MVN_CMD} clean compile -B'
			}
		}

		stage('Code Quality / SonarQube Analysis') {
			steps {
				script {

					withSonarQubeEnv(installationName: 'Sonarqube') {
						bat 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:4.0.0.4121:sonar'

					}
				}
			}
		}

		stage('Package') {
			steps {
				echo 'Gerando artefato...'
				bat "${MVN_CMD} package -B -DskipTests"
				//         archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
			}
		}
	}

	post {
		always {
			echo '🏁 Pipeline terminada!'

		}
		failure {
			echo "Error na pipeline!"

		}
	}
}
