// Pipeline BACKEND (Microservicio1 - API Gestor): CI/CD completo con JDK 21 + Maven
// Build -> Test -> JaCoCo -> SAST (Semgrep) -> SonarQube -> Quality Gate -> Package
//       -> Publicar imagen (GHCR) -> Desplegar en Azure (Ansible) -> Prueba de humo
pipeline {
    agent any

    tools {
        // Configurados en Administrar Jenkins > Tools
        jdk 'JDK21'
        maven 'Maven21'
    }

    options {
        timestamps()
        // Un solo checkout (etapa Checkout) en lugar de dos, con reintentos ante fallos de red
        skipDefaultCheckout()
        disableConcurrentBuilds()
        timeout(time: 45, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '15'))
    }

    environment {
        SERVICIO = 'api'
        VM_HOST  = 'gestor-717e46.northcentralus.cloudapp.azure.com'
        IAC_REPO = 'https://github.com/Karen-Anahi-Padron-Martinez/Infraestructura-IaC.git'
        BD_REPO  = 'https://github.com/Karen-Anahi-Padron-Martinez/Microservicio-BD.git'
    }

    stages {
        stage('Checkout') {
            steps {
                retry(3) {
                    checkout scm
                }
                // Scripts de CI/CD (Infraestructura-IaC) y SQL (Microservicio-BD) fuera del workspace analizado
                dir("${env.WORKSPACE}@iac") { git url: env.IAC_REPO, branch: 'main' }
                dir("${env.WORKSPACE}@bd") { git url: env.BD_REPO, branch: 'main' }
            }
        }

        stage('Build') {
            steps {
                sh 'java -version && mvn -v'
                sh 'mvn -B -ntp clean compile'
            }
        }

        stage('Test') {
            steps {
                // Pruebas JUnit 5 + Mockito; H2 en memoria, no requiere MariaDB
                sh 'mvn -B -ntp test jacoco:report'
            }
            post {
                always {
                    junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: false
                }
            }
        }

        stage('JaCoCo report') {
            steps {
                jacoco execPattern: 'target/jacoco.exec',
                       classPattern: 'target/classes',
                       sourcePattern: 'src/main/java',
                       exclusionPattern: '**/Ejercicio5MyApiGestorApplication.class',
                       changeBuildStatus: true,
                       minimumMethodCoverage: '80', minimumLineCoverage: '80'
            }
        }

        stage('SAST (Semgrep)') {
            steps {
                // Falla solo con hallazgos ERROR; WARNING/INFO quedan en el reporte
                sh 'bash "$WORKSPACE@iac/ci/semgrep.sh" . p/java p/owasp-top-ten p/secrets'
            }
            post {
                always {
                    archiveArtifacts artifacts: 'semgrep.json, semgrep.sarif', allowEmptyArchive: true
                }
            }
        }

        stage('Análisis SonarQube') {
            steps {
                // URL y token salen de la configuración "sonar" de Jenkins (credencial sonar-token)
                withSonarQubeEnv('sonar') {
                    sh "mvn -B -ntp sonar:sonar -Dsonar.projectVersion=${env.BUILD_NUMBER}"
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package / Archive') {
            steps {
                sh 'mvn -B -ntp -DskipTests package'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                archiveArtifacts artifacts: 'target/site/jacoco/**', allowEmptyArchive: false
            }
        }

        stage('Publicar imagen (GHCR)') {
            steps {
                // Jib construye y sube la imagen sin Docker; etiquetas: número de build y latest
                withCredentials([usernamePassword(credentialsId: 'ghcr-token',
                                                  usernameVariable: 'GHCR_USER', passwordVariable: 'GHCR_TOKEN')]) {
                    sh '''mvn -B -ntp -DskipTests jib:build \
                            -Dimagen.etiqueta="$BUILD_NUMBER" \
                            -Djib.to.auth.username="$GHCR_USER" -Djib.to.auth.password="$GHCR_TOKEN"'''
                }
            }
        }

        stage('Desplegar en Azure (Ansible)') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'azure-vm-ssh', keyFileVariable: 'ANSIBLE_PRIVATE_KEY_FILE'),
                                 file(credentialsId: 'ansible-vault-gestor', variable: 'ANSIBLE_VAULT_PASSWORD_FILE')]) {
                    sh 'BD_SQL_DIR="$WORKSPACE@bd/sql" bash "$WORKSPACE@iac/ci/desplegar.sh" "$SERVICIO" "$BUILD_NUMBER"'
                }
            }
        }

        stage('Prueba de humo') {
            steps {
                sh 'bash "$WORKSPACE@iac/ci/humo.sh" "http://$VM_HOST"'
            }
        }
    }

    post {
        success {
            echo "Desplegado ${env.SERVICIO}:${env.BUILD_NUMBER} en http://${env.VM_HOST}"
        }
    }
}
