pipeline {
    agent none
    options {
        timeout(time: 2, unit: 'HOURS')
        disableConcurrentBuilds()
    }
    stages {
        stage("Integration tests...")
        {
            parallel {
                stage('Gradle') {
                    agent {
                        label "alpine:jdk8-gradle-4.5"
                    }
                    options {
                        timeout(time: 1, unit: 'HOURS')
                    }
                    steps {
                        dir("gradle") {
                            withMaven(mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833') {
                                sh 'mvn clean install'
                            }
                        }
                    }
                }

                stage('Maven') {
                    agent {
                        label "alpine:jdk8-mvn-3.3"
                    }
                    options {
                        timeout(time: 1, unit: 'HOURS')
                    }
                    steps {
                        dir("maven") {
                            withMaven(mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833') {
                                sh 'mvn clean install'
                            }
                        }
                    }
                }

                stage('Nuget') {
                    agent {
                        label "alpine:nuget-3.4-mono"
                    }
                    options {
                        timeout(time: 1, unit: 'HOURS')
                    }
                    steps {
                        dir("nuget") {
                            withMaven(mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833') {
                                sh 'mvn clean install'
                            }
                        }
                    }
                }
                
                stage('SBT') {
                    agent {
                        label "alpine:jdk8-sbt-1.1"
                    }
                    options {
                        timeout(time: 1, unit: 'HOURS')
                    }
                    steps {
                        dir("sbt") {
                            withMaven(mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833') {
                                sh 'mvn clean install'
                            }
                        }
                    }
                }

            }
        }
    }
    post {
        always {
            // Email notification
            script {
                def email = new org.carlspring.jenkins.notification.email.Email()
                if(BRANCH_NAME == 'master') {
                    email.sendNotification()
                } else {
                    email.sendNotification(null, false, null, [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']])
                }
            }
        }
    }
}


