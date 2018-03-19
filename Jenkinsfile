@Library('jenkins-shared-libraries')

def workspaceUtils = new org.carlspring.jenkins.workspace.WorkspaceUtils();
def mvnBaseLocalRepo = "/cache/${env.JOB_BASE_NAME}/.m2/repository";

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
                        sh "gradle --version"
                        sh "mvn -version"

                        script {
                            def mvnLocalRepo = workspaceUtils.generateStageSafeM2LocalRepoPath()
                            sh "mkdir -p ${mvnLocalRepo}"

                            dir("gradle") {
                                withMaven(mavenLocalRepo: mvnLocalRepo, mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833') {
                                    // Download dependencies before build to avoid noise in logs.
                                    sh 'mvn -T 1C dependency:go-offline -U'
                                    // Build
                                    sh 'mvn clean install'
                                }
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
                        sh "mvn -version"

                        script {
                            def mvnLocalRepo = workspaceUtils.generateStageSafeM2LocalRepoPath()
                            sh "mkdir -p ${mvnLocalRepo}"

                            dir("maven") {
                                withMaven(mavenLocalRepo: mvnLocalRepo, mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833') {
                                    // Download dependencies before build to avoid noise in logs.
                                    sh 'mvn -T 1C dependency:go-offline -U'
                                    // Build
                                    sh 'mvn clean install'
                                }
                            }
                        }
                    }
                    post {
                        always {
                            // This is necessary, because Jenkins sometimes gets confused what's the CWD!
                            dir("") {
                                archiveArtifacts 'maven/target/**'
                                // Cleanup
                                cleanWs deleteDirs: true, externalDelete: 'rm -rf %s', notFailBuild: true
                            }
                        }
                    }
                }

                stage('NPM') {
                    agent {
                        node {
                            label "alpine:node-9.4"
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
                        sh "echo 'NPM version' && npm --version"
                        sh "echo 'Node version' && node --version"
                        sh "echo 'Yarn version' && yarn --version"
                        sh "mvn -version"

                        script {
                            def mvnLocalRepo = workspaceUtils.generateStageSafeM2LocalRepoPath()
                            sh "mkdir -p ${mvnLocalRepo}"

                            dir("npm") {
                                withMaven(mavenLocalRepo: mvnLocalRepo, mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833') {
                                    // Download dependencies before build to avoid noise in logs.
                                    sh 'mvn -T 1C dependency:go-offline -U'
                                    // Build
                                    sh 'mvn clean install'
                                }
                            }
                        }
                    }
                    post {
                        always {
                            // This is necessary, because Jenkins sometimes gets confused what's the CWD!
                            dir("") {
                                archiveArtifacts 'npm/target/**'
                                // Cleanup
                                cleanWs deleteDirs: true, externalDelete: 'rm -rf %s', notFailBuild: true
                            }
                        }
                    }
                }

                stage('Nuget-mono') {
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
                        sh "\$NUGET_V3_EXEC | head -n1"
                        sh "mvn -version"

                        script {
                            def mvnLocalRepo = workspaceUtils.generateStageSafeM2LocalRepoPath()
                            sh "mkdir -p ${mvnLocalRepo}"

                            dir("nuget") {
                                withMaven(mavenLocalRepo: mvnLocalRepo, mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833') {
                                    // Download dependencies before build to avoid noise in logs.
                                    sh 'mvn -T 1C dependency:go-offline -U'
                                    // Build
                                    sh 'mvn clean install'
                                }
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
                        sh "mvn -version"

                        script {
                            def mvnLocalRepo = workspaceUtils.generateStageSafeM2LocalRepoPath()
                            sh "mkdir -p ${mvnLocalRepo}"

                            dir("sbt") {
                                withMaven(mavenLocalRepo: mvnLocalRepo, mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833') {
                                    // Download dependencies before build to avoid noise in logs.
                                    sh 'mvn -T 1C dependency:go-offline -U'
                                    // Build
                                    sh 'mvn clean install'
                                }
                            }
                        }
                    }
                    post {
                        always {
                            // This is necessary, because Jenkins sometimes gets confused what's the CWD!
                            dir("") {
                                archiveArtifacts 'sbt/target/**'
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


