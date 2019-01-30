@Library('jenkins-shared-libraries') _

// Notification settings for "master" and "branch/pr"
def notifyMaster = [notifyAdmins: true, recipients: [culprits(), requestor()]]
def notifyBranch = [recipients: [brokenTestsSuspects(), requestor()]]

pipeline {
    agent none
    parameters {
        booleanParam(defaultValue: true, description: 'Send email notification?', name: 'NOTIFY_EMAIL')
    }
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
                            customWorkspace workspace().getUniqueWorkspacePath()
                        }
                    }
                    options {
                        timeout(time: 1, unit: 'HOURS')
                    }
                    steps {
                        nodeInfo("mvn gradle")

                        script {
                            def mvnLocalRepo = workspace().getStageSafeM2LocalRepoPath()
                            sh "mkdir -p ${mvnLocalRepo}"

                            dir("gradle") {
                                withMavenPlus(mavenLocalRepo: mvnLocalRepo, mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833') {
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
                            label "alpine:jdk8-mvn-3.5"
                            customWorkspace workspace().getUniqueWorkspacePath()
                        }
                    }
                    options {
                        timeout(time: 1, unit: 'HOURS')
                    }
                    steps {

                         nodeInfo("mvn")

                        script {
                            def mvnLocalRepo = workspace().getStageSafeM2LocalRepoPath()
                            sh "mkdir -p ${mvnLocalRepo}"

                            dir("maven") {
                                withMavenPlus(mavenLocalRepo: mvnLocalRepo, mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833') {
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
                            label "alpine:node-10"
                            customWorkspace workspace().getUniqueWorkspacePath()
                        }
                    }
                    options {
                        timeout(time: 1, unit: 'HOURS')
                    }
                    steps {

                        nodeInfo("mvn npm node yarn")

                        script {
                            def mvnLocalRepo = workspace().getStageSafeM2LocalRepoPath()
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
                            customWorkspace workspace().getUniqueWorkspacePath()
                        }
                    }
                    options {
                        timeout(time: 1, unit: 'HOURS')
                    }
                    steps {

                        nodeInfo("mvn mono")
                        sh "\$NUGET_V3_EXEC | head -n1"

                        script {
                            def mvnLocalRepo = workspace().getStageSafeM2LocalRepoPath()
                            sh "mkdir -p ${mvnLocalRepo}"

                            dir("nuget") {
                                withMavenPlus(mavenLocalRepo: mvnLocalRepo, mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833') {
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
                            customWorkspace workspace().getUniqueWorkspacePath()
                        }
                    }
                    options {
                        timeout(time: 1, unit: 'HOURS')
                    }
                    steps {

                        nodeInfo("mvn")

                        script {
                            def mvnLocalRepo = workspace().getStageSafeM2LocalRepoPath()
                            sh "mkdir -p ${mvnLocalRepo}"

                            dir("sbt") {
                                withMavenPlus(mavenLocalRepo: mvnLocalRepo, mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833') {
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
        failure {
            script {
                if(params.NOTIFY_EMAIL) {
                    notifyFailed((BRANCH_NAME == "master") ? notifyMaster : notifyBranch)
                }
            }
        }
        unstable {
            script {
                if(params.NOTIFY_EMAIL) {
                    notifyUnstable((BRANCH_NAME == "master") ? notifyMaster : notifyBranch)
                }
            }
        }
        fixed {
            script {
                if(params.NOTIFY_EMAIL) {
                    notifyFixed((BRANCH_NAME == "master") ? notifyMaster : notifyBranch)
                }
            }
        }
    }
}

