pipeline {
    agent none
    stages {
        stage("Integration tests...")
        {
            parallel {
                stage('Gradle') {
                    agent {
                        label "alpine:jdk8-gradle-4.5"
                    }
                    steps {
                        checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '7ffc18db-78bd-40d4-b6ac-6c159f6e41cb', url: 'https://github.com/strongbox/strongbox-web-integration-tests']]]
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
                    steps {
                        checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '7ffc18db-78bd-40d4-b6ac-6c159f6e41cb', url: 'https://github.com/strongbox/strongbox-web-integration-tests']]]
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
                    steps {
                        checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '7ffc18db-78bd-40d4-b6ac-6c159f6e41cb', url: 'https://github.com/strongbox/strongbox-web-integration-tests']]]
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
                    steps {
                        checkout changelog: false, poll: false, scm: [$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '7ffc18db-78bd-40d4-b6ac-6c159f6e41cb', url: 'https://github.com/strongbox/strongbox-web-integration-tests']]]
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
}

