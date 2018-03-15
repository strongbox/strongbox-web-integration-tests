@Library('jenkins-shared-libraries@master')

def workspaceUtils = new org.carlspring.jenkins.workspace.WorkspaceUtils();

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
                        node {
                            label "alpine:jdk8-gradle-4.5"
                            customWorkspace workspaceUtils.generateUniqueWorkspacePath()
                        }
                    }
                    options {
                        timeout(time: 1, unit: 'HOURS')
                    }
                    steps {
                        echo "Node information:"

                        sh "cat /etc/node"
                        sh "cat /etc/os-release"

                        dir("gradle") {
                            withMaven(mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833') {
                                sh 'mvn clean install -U'
                            }
                        }
                    }
                    post {
                        always {
                            // This is necessary, because Jenkins sometimes gets confused what's the CWD!
                            dir("") {
                                archiveArtifacts 'gradle/target/**'
                                // Cleanup
                                cleanWs deleteDirs: true, externalDelete: 'rm -rf %s', notFailBuild: true
                            }
                        }
                    }
                }

                stage('Maven') {
                    agent {
                        node {
                            label "alpine:jdk8-mvn-3.3"
                            customWorkspace workspaceUtils.generateUniqueWorkspacePath()
                        }
                    }
                    options {
                        timeout(time: 1, unit: 'HOURS')
                    }
                    steps {
                        echo "Node information:"

                        sh "cat /etc/node"
                        sh "cat /etc/os-release"

                        dir("maven") {
                            withMaven(mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833') {
                                sh 'mvn clean install -U'
                            }
                        }
                    }
                    post {
                        always {
                            // This is necessary, because Jenkins sometimes gets confused what's the CWD!
                            dir("") {
                                archiveArtifacts 'gradle/target/**'
                                // Cleanup
                                cleanWs deleteDirs: true, externalDelete: 'rm -rf %s', notFailBuild: true
                            }
                        }
                    }
                }

                stage('Nuget') {
                    agent {
                        node {
                            label "alpine:nuget-3.4-mono"
                            customWorkspace workspaceUtils.generateUniqueWorkspacePath()
                        }
                    }
                    options {
                        timeout(time: 1, unit: 'HOURS')
                    }
                    steps {
                        echo "Node information:"

                        sh "cat /etc/node"
                        sh "cat /etc/os-release"

                        dir("nuget") {
                            withMaven(mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833') {
                                sh 'mvn clean install -U'
                            }
                        }
                    }
                    post {
                        always {
                            // This is necessary, because Jenkins sometimes gets confused what's the CWD!
                            dir("") {
                                archiveArtifacts 'nuget/target/**'
                                // Cleanup
                                cleanWs deleteDirs: true, externalDelete: 'rm -rf %s', notFailBuild: true
                            }
                        }
                    }
                }

                stage('SBT') {
                    agent {
                        node {
                            label "alpine:jdk8-sbt-1.1"
                            customWorkspace workspaceUtils.generateUniqueWorkspacePath()
                        }
                    }
                    options {
                        timeout(time: 1, unit: 'HOURS')
                    }
                    steps {
                        echo "Node information:"

                        sh "cat /etc/node"
                        sh "cat /etc/os-release"

                        dir("sbt") {
                            withMaven(mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833') {
                                sh 'mvn clean install -U'
                            }
                        }
                    }
                    post {
                        always {
                            // This is necessary, because Jenkins sometimes gets confused what's the CWD!
                            dir("") {
                                archiveArtifacts 'gradle/target/**'
                                // Cleanup
                                cleanWs deleteDirs: true, externalDelete: 'rm -rf %s', notFailBuild: true
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

