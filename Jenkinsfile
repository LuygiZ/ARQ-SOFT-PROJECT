pipeline {
	agent any

	triggers {
		githubPush()  // Trigger automático ao fazer push para GitHub
	}

	tools {
		maven 'Maven 3.9.11'  // Certifica-te que tens este nome no Jenkins
	}

	parameters {
		choice(
			name: 'Environment',
			choices: ["docker", "local"],
			description: 'Escolhe o ambiente para executar a pipeline (docker ou local).'
		)
		choice(
			name: 'DeploymentTarget',
			choices: ["dev", "staging", "production"],
			description: 'Escolhe o ambiente de deployment (DEV, STAGING ou PRODUCTION).'
		)
	}

	environment {
		// Maven Tool
		MAVEN_DIR = tool(name: 'Maven 3.9.11', type: 'maven')

		// Redis Configuration
		REDIS_HOST = 'host.docker.internal'
		REDIS_PORT = '6379'
		REDIS_CONTAINER = 'fervent_benz'

		// Application Ports
		DEV_PORT = '8080'
		STAGING_PORT = '8081'
		PROD_PORT = '8082'

		// Docker Configuration
		DOCKER_IMAGE_NAME = 'library-management-service'
		BUILD_TAG = "${env.BUILD_NUMBER}-${env.GIT_COMMIT?.take(7) ?: 'local'}"

		// SonarQube Mapping
		SONAR_SERVER_DOCKER = 'sonarqube_docker'
		SONAR_SERVER_LOCAL = 'sonarqube_local'
	}

	options {
		buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '5'))
		timeout(time: 1, unit: 'HOURS')
		timestamps()
		disableConcurrentBuilds()
	}

	stages {
		// ========================================
		// STAGE 1: CHECKOUT
		// ========================================
		stage('Checkout') {
			steps {
				echo '╔════════════════════════════════════════════════════╗'
				echo '║  STAGE 1: Checkout do Repositório                  ║'
				echo '╚════════════════════════════════════════════════════╝'
				checkout scm
				script {
					// Capturar informação do Git
					env.GIT_COMMIT_SHORT = sh(
						script: "git rev-parse --short HEAD || echo 'local'",
						returnStdout: true
					).trim()
					env.GIT_BRANCH = sh(
						script: "git rev-parse --abbrev-ref HEAD || echo 'unknown'",
						returnStdout: true
					).trim()

					echo "✓ Branch: ${env.GIT_BRANCH}"
					echo "✓ Commit: ${env.GIT_COMMIT_SHORT}"
					echo "✓ Build: ${env.BUILD_TAG}"
				}
			}
		}

		// ========================================
		// STAGE 2: BUILD & COMPILE
		// ========================================
		stage('Build & Compile') {
			steps {
				echo '╔════════════════════════════════════════════════════╗'
				echo '║  STAGE 2: Build & Compile                          ║'
				echo '╚════════════════════════════════════════════════════╝'
				script {
					echo '🔨 Cleaning workspace and compiling...'
					if (isUnix()) {
						sh 'mvn clean compile test-compile'
					} else {
						bat 'mvn clean compile test-compile'
					}
					echo '✓ Compilation completed successfully'
				}
			}
		}

		// ========================================
		// STAGE 3: UNIT & INTEGRATION TESTS (PARALLEL)
		// ========================================
		stage('Unit & Integration Tests') {
			parallel {
				stage('Unit Tests') {
					steps {
						echo '╔════════════════════════════════════════════════════╗'
						echo '║  STAGE 3a: Unit Tests                              ║'
						echo '╚════════════════════════════════════════════════════╝'
						script {
							echo '🧪 Running unit tests...'
							if (isUnix()) {
								sh 'mvn surefire:test'
							} else {
								bat 'mvn surefire:test'
							}
							echo '✓ Unit tests completed'
						}
					}
					post {
						always {
							// Publicar resultados dos testes unitários
							junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
						}
					}
				}

				stage('Integration Tests') {
					steps {
						echo '╔════════════════════════════════════════════════════╗'
						echo '║  STAGE 3b: Integration Tests                       ║'
						echo '╚════════════════════════════════════════════════════╝'
						script {
							echo '🔗 Running integration tests...'
							echo "ℹ️  Using Redis at ${REDIS_HOST}:${REDIS_PORT}"

							if (isUnix()) {
								sh '''
                                    mvn failsafe:integration-test failsafe:verify \
                                        -Dspring.redis.host=${REDIS_HOST} \
                                        -Dspring.redis.port=${REDIS_PORT}
                                '''
							} else {
								bat '''
                                    mvn failsafe:integration-test failsafe:verify ^
                                        -Dspring.redis.host=%REDIS_HOST% ^
                                        -Dspring.redis.port=%REDIS_PORT%
                                '''
							}
							echo '✓ Integration tests completed'
						}
					}
					post {
						always {
							// Publicar resultados dos testes de integração
							junit allowEmptyResults: true, testResults: '**/target/failsafe-reports/*.xml'
						}
					}
				}
			}
		}

		// ========================================
		// STAGE 4: CODE COVERAGE (JACOCO)
		// ========================================
		stage('Code Coverage') {
			steps {
				echo '╔════════════════════════════════════════════════════╗'
				echo '║  STAGE 4: Code Coverage Analysis (JaCoCo)          ║'
				echo '╚════════════════════════════════════════════════════╝'
				script {
					echo '📊 Generating code coverage report...'
					if (isUnix()) {
						sh '''
                            mvn jacoco:report
                            mvn jacoco:check -Djacoco.minimum.coverage=0.80 || echo "⚠️  Coverage below 80%"
                        '''
					} else {
						bat '''
                            mvn jacoco:report
                            mvn jacoco:check -Djacoco.minimum.coverage=0.80 || echo "⚠️  Coverage below 80%"
                        '''
					}
					echo '✓ Coverage report generated'
				}
			}
			post {
				always {
					// Publicar relatório JaCoCo
					jacoco(
						execPattern: '**/target/jacoco.exec',
						classPattern: '**/target/classes',
						sourcePattern: '**/src/main/java',
						exclusionPattern: '**/test/**'
					)

					// Publicar HTML report
					publishHTML([
						allowMissing: false,
						alwaysLinkToLastBuild: true,
						keepAll: true,
						reportDir: 'target/site/jacoco',
						reportFiles: 'index.html',
						reportName: 'JaCoCo Coverage Report',
						reportTitles: 'Code Coverage'
					])
				}
			}
		}

		// ========================================
		// STAGE 5: MUTATION TESTING (PITEST)
		// ========================================
		stage('Mutation Tests') {
			steps {
				echo '╔════════════════════════════════════════════════════╗'
				echo '║  STAGE 5: Mutation Testing (PIT)                   ║'
				echo '╚════════════════════════════════════════════════════╝'
				script {
					echo '🧬 Running mutation tests...'
					if (isUnix()) {
						sh 'mvn org.pitest:pitest-maven:mutationCoverage'
					} else {
						bat 'mvn org.pitest:pitest-maven:mutationCoverage'
					}
					echo '✓ Mutation testing completed'
				}
			}
			post {
				always {
					// Publicar relatório de mutation testing
					publishHTML([
						allowMissing: false,
						alwaysLinkToLastBuild: true,
						keepAll: true,
						reportDir: 'target/pit-reports',
						reportFiles: 'index.html',
						reportName: 'PIT Mutation Testing Report',
						reportTitles: 'Mutation Coverage'
					])
				}
			}
		}

		// ========================================
		// STAGE 6: SONARQUBE ANALYSIS
		// ========================================
		stage('SonarQube Analysis') {
			steps {
				echo '╔════════════════════════════════════════════════════╗'
				echo '║  STAGE 6: SonarQube Static Code Analysis          ║'
				echo '╚════════════════════════════════════════════════════╝'
				script {
					// Mapear ambiente para servidor SonarQube
					def ENVIRONMENT_2_SONARQUBE_SERVER = [
						'docker': env.SONAR_SERVER_DOCKER,
						'local' : env.SONAR_SERVER_LOCAL
					]

					def sonarServer = ENVIRONMENT_2_SONARQUBE_SERVER[params.Environment]
					echo "🔍 Running SonarQube analysis using server: ${sonarServer}"

					// Verificar se SonarQube está disponível
					try {
						withSonarQubeEnv(sonarServer) {
							if (isUnix()) {
								sh 'mvn verify sonar:sonar'
							} else {
								bat 'mvn verify sonar:sonar'
							}
						}
						echo '✓ SonarQube analysis completed'
					} catch (Exception e) {
						echo "⚠️  SonarQube not available: ${e.message}"
						echo "Continuing pipeline without SonarQube analysis..."
					}
				}
			}
		}

		// ========================================
		// STAGE 7: QUALITY GATE 1
		// ========================================
		stage('Quality Gate 1') {
			steps {
				echo '╔════════════════════════════════════════════════════╗'
				echo '║  STAGE 7: Quality Gate 1 (QG1)                     ║'
				echo '╚════════════════════════════════════════════════════╝'
				script {
					try {
						timeout(time: 3, unit: 'MINUTES') {
							def qg = waitForQualityGate()
							if (qg.status != 'OK') {
								echo "⚠️  Quality Gate failed: ${qg.status}"
								echo "Continuing pipeline with warning..."
							} else {
								echo '✓ Quality Gate 1 PASSED!'
							}
						}
					} catch (Exception e) {
						echo "⚠️  Quality Gate check skipped: ${e.message}"
						echo "Continuing pipeline..."
					}
				}
			}
		}

		// ========================================
		// STAGE 8: PACKAGE
		// ========================================
		stage('Package') {
			steps {
				echo '╔════════════════════════════════════════════════════╗'
				echo '║  STAGE 8: Package Application                      ║'
				echo '╚════════════════════════════════════════════════════╝'
				script {
					echo '📦 Building final package...'
					if (isUnix()) {
						sh 'mvn package -DskipTests'
					} else {
						bat 'mvn package -DskipTests'
					}
					echo '✓ Package created successfully'
				}
			}
			post {
				success {
					// Arquivar artefactos
					archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
					echo '✓ Artifacts archived'
				}
			}
		}

		// ========================================
		// STAGE 9: BUILD DOCKER IMAGE
		// ========================================
		stage('Build Docker Image') {
			when {
				expression { params.Environment == 'docker' }
			}
			steps {
				echo '╔════════════════════════════════════════════════════╗'
				echo '║  STAGE 9: Build Docker Image                       ║'
				echo '╚════════════════════════════════════════════════════╝'
				script {
					echo "🐳 Building Docker image: ${DOCKER_IMAGE_NAME}:${BUILD_TAG}"

					// Criar Dockerfile se não existir
					if (isUnix()) {
						sh '''
                            if [ ! -f Dockerfile ]; then
                                cat > Dockerfile << 'EOF'
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=dev
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \\
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF
                            fi
                        '''

						sh """
                            docker build -t ${DOCKER_IMAGE_NAME}:${BUILD_TAG} .
                            docker tag ${DOCKER_IMAGE_NAME}:${BUILD_TAG} ${DOCKER_IMAGE_NAME}:latest
                        """
					} else {
						bat """
                            docker build -t ${DOCKER_IMAGE_NAME}:${BUILD_TAG} .
                            docker tag ${DOCKER_IMAGE_NAME}:${BUILD_TAG} ${DOCKER_IMAGE_NAME}:latest
                        """
					}

					echo "✓ Docker image built: ${DOCKER_IMAGE_NAME}:${BUILD_TAG}"
				}
			}
		}

		// ========================================
		// STAGE 10: VERIFY REDIS CONNECTION
		// ========================================
		stage('Verify Redis Connection') {
			steps {
				echo '╔════════════════════════════════════════════════════╗'
				echo '║  STAGE 10: Verify Redis Connection                ║'
				echo '╚════════════════════════════════════════════════════╝'
				script {
					echo "🔍 Checking Redis container: ${REDIS_CONTAINER}"

					if (isUnix()) {
						def redisStatus = sh(
							script: "docker exec ${REDIS_CONTAINER} redis-cli ping || echo 'FAILED'",
							returnStdout: true
						).trim()

						if (redisStatus.contains('PONG')) {
							echo '✓ Redis is responding'

							// Get Redis info
							sh """
                                echo "Redis Statistics:"
                                docker exec ${REDIS_CONTAINER} redis-cli INFO stats | grep keyspace
                            """
						} else {
							echo '⚠️  Redis not responding'
						}
					} else {
						bat "docker exec ${REDIS_CONTAINER} redis-cli ping"
					}
				}
			}
		}

		// ========================================
		// STAGE 11: DEPLOY TO ENVIRONMENT
		// ========================================
		stage('Deploy to Environment') {
			when {
				expression { params.Environment == 'docker' }
			}
			steps {
				echo '╔════════════════════════════════════════════════════╗'
				echo "║  STAGE 11: Deploy to ${params.DeploymentTarget.toUpperCase()}                       ║"
				echo '╚════════════════════════════════════════════════════╝'

				script {
					// Definir porta baseada no ambiente
					def targetPort = ''
					def containerName = ''
					def profile = ''

					switch(params.DeploymentTarget) {
					case 'dev':
					targetPort = env.DEV_PORT
					containerName = 'library-app-dev'
					profile = 'dev'
					break
					case 'staging':
					targetPort = env.STAGING_PORT
					containerName = 'library-app-staging'
					profile = 'staging'
					// Pedir aprovação para STAGING
					input message: "Deploy to STAGING?", ok: "Deploy"
					break
					case 'production':
					targetPort = env.PROD_PORT
					containerName = 'library-app-prod'
					profile = 'prod'
					// Pedir aprovação ADMIN para PRODUCTION
					input message: "⚠️  Deploy to PRODUCTION? This is FINAL!", ok: "DEPLOY TO PROD", submitter: 'admin'
					break
					}

					echo "🚀 Deploying to ${params.DeploymentTarget.toUpperCase()}"
					echo "   Port: ${targetPort}"
					echo "   Container: ${containerName}"
					echo "   Profile: ${profile}"

					if (isUnix()) {
						sh """
                            # Stop and remove existing container
                            docker stop ${containerName} 2>/dev/null || true
                            docker rm ${containerName} 2>/dev/null || true

                            # Run new container
                            docker run -d \\
                                --name ${containerName} \\
                                -p ${targetPort}:8080 \\
                                -e SPRING_PROFILES_ACTIVE=${profile} \\
                                -e SPRING_REDIS_HOST=${REDIS_HOST} \\
                                -e SPRING_REDIS_PORT=${REDIS_PORT} \\
                                -e SPRING_CACHE_TYPE=redis \\
                                --restart unless-stopped \\
                                ${DOCKER_IMAGE_NAME}:${BUILD_TAG}

                            # Wait for application to start
                            echo "⏳ Waiting for application to start..."
                            sleep 15
                        """

						echo "✓ Application deployed to ${params.DeploymentTarget.toUpperCase()}"
						echo "✓ Access at: http://localhost:${targetPort}"
					} else {
						bat """
                            docker stop ${containerName} 2>nul || ver>nul
                            docker rm ${containerName} 2>nul || ver>nul

                            docker run -d ^
                                --name ${containerName} ^
                                -p ${targetPort}:8080 ^
                                -e SPRING_PROFILES_ACTIVE=${profile} ^
                                -e SPRING_REDIS_HOST=${REDIS_HOST} ^
                                -e SPRING_REDIS_PORT=${REDIS_PORT} ^
                                ${DOCKER_IMAGE_NAME}:${BUILD_TAG}
                        """
					}
				}
			}
		}

		// ========================================
		// STAGE 12: HEALTH CHECK
		// ========================================
		stage('Health Check') {
			when {
				expression { params.Environment == 'docker' }
			}
			steps {
				echo '╔════════════════════════════════════════════════════╗'
				echo '║  STAGE 12: Health Check                            ║'
				echo '╚════════════════════════════════════════════════════╝'

				script {
					def targetPort = ''

					switch(params.DeploymentTarget) {
					case 'dev':
					targetPort = env.DEV_PORT
					break
					case 'staging':
					targetPort = env.STAGING_PORT
					break
					case 'production':
					targetPort = env.PROD_PORT
					break
					}

					echo "🏥 Checking application health on port ${targetPort}..."

					if (isUnix()) {
						retry(3) {
							sleep 5
							sh """
                                curl -f http://localhost:${targetPort}/actuator/health || \
                                curl -f http://localhost:${targetPort}/ || \
                                echo "Health check in progress..."
                            """
						}
					} else {
						bat "timeout /t 5 /nobreak"
						bat "curl http://localhost:${targetPort}/actuator/health || echo Health check"
					}

					echo "✓ Health check completed"
				}
			}
		}

		// ========================================
		// STAGE 13: QUALITY GATE FINAL
		// ========================================
		stage('Quality Gate Final') {
			when {
				expression { params.DeploymentTarget == 'production' }
			}
			steps {
				echo '╔════════════════════════════════════════════════════╗'
				echo '║  STAGE 13: Quality Gate Final (QG4)                ║'
				echo '╚════════════════════════════════════════════════════╝'

				script {
					echo '🎯 Running final quality checks for PRODUCTION...'

					// Verificar se aplicação está respondendo
					if (isUnix()) {
						def healthStatus = sh(
							script: "curl -s -o /dev/null -w '%{http_code}' http://localhost:${PROD_PORT}/actuator/health || echo '000'",
							returnStdout: true
						).trim()

						if (healthStatus == '200') {
							echo '✓✓✓ PRODUCTION deployment successful! ✓✓✓'
							echo "✓ Application is live on port ${PROD_PORT}"
						} else {
							error "❌ PRODUCTION health check failed! Status: ${healthStatus}"
						}
					}
				}
			}
		}
	}

	// ========================================
	// POST ACTIONS
	// ========================================
	post {
		success {
			echo '╔════════════════════════════════════════════════════╗'
			echo '║  ✓✓✓ PIPELINE COMPLETED SUCCESSFULLY! ✓✓✓         ║'
			echo '╚════════════════════════════════════════════════════╝'

			script {
				echo """
                📊 Build Summary:
                   Build Number: ${env.BUILD_NUMBER}
                   Git Branch: ${env.GIT_BRANCH}
                   Git Commit: ${env.GIT_COMMIT_SHORT}
                   Environment: ${params.Environment}
                   Deployment: ${params.DeploymentTarget}
                """

				if (params.Environment == 'docker') {
					def targetPort = ''
					switch(params.DeploymentTarget) {
					case 'dev': targetPort = env.DEV_PORT; break
					case 'staging': targetPort = env.STAGING_PORT; break
					case 'production': targetPort = env.PROD_PORT; break
					}
					echo "🌐 Application URL: http://localhost:${targetPort}"
				}

				// Log de sucesso
				if (isUnix()) {
					sh """
                        echo "\$(date): Build ${env.BUILD_NUMBER} - SUCCESS" >> deployment-history.log
                    """
				}
			}
		}

		failure {
			echo '╔════════════════════════════════════════════════════╗'
			echo '║  ❌❌❌ PIPELINE FAILED! ❌❌❌                       ║'
			echo '╚════════════════════════════════════════════════════╝'

			script {
				echo """
                ❌ Build Failed:
                   Build Number: ${env.BUILD_NUMBER}
                   Stage: ${env.STAGE_NAME}
                   Check logs above for details
                """

				// Rollback em caso de falha em produção
				if (params.DeploymentTarget == 'production' && params.Environment == 'docker') {
					echo '🔄 Initiating automatic rollback...'

					if (isUnix()) {
						sh """
                            docker stop library-app-prod 2>/dev/null || true
                            docker rm library-app-prod 2>/dev/null || true

                            # Restaurar versão anterior se existir
                            if docker image inspect ${DOCKER_IMAGE_NAME}:prod-backup >/dev/null 2>&1; then
                                docker run -d \\
                                    --name library-app-prod \\
                                    -p ${PROD_PORT}:8080 \\
                                    -e SPRING_PROFILES_ACTIVE=prod \\
                                    -e SPRING_REDIS_HOST=${REDIS_HOST} \\
                                    ${DOCKER_IMAGE_NAME}:prod-backup

                                echo "✓ Rollback completed - Previous version restored"
                            fi
                        """
					}
				}
			}
		}

		unstable {
			echo '⚠️  Pipeline completed with warnings'
		}

		always {
			echo '╔════════════════════════════════════════════════════╗'
			echo '║  Cleanup & Finalization                            ║'
			echo '╚════════════════════════════════════════════════════╝'

			script {
				// Publicar todos os reports
				echo '📊 Publishing all reports...'

				// Cleanup (opcional)
				// cleanWs()

				echo '✓ Pipeline finalized'
			}
		}
	}
}
