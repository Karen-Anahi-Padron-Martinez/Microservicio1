// Pipeline BACKEND (Microservicio1 - API Gestor): calidad y análisis de código con JDK 21 + Maven
pipeline {
    agent any

    tools {
        // Configurados en Administrar Jenkins > Tools
        jdk 'JDK21'
        maven 'Maven21'
    }

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '15'))
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
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
    }
}
