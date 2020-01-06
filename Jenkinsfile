@Library('jenkins-shared-libraries') _

// Notification settings for "master" and "branch/pr"
def notifyMaster = [notifyAdmins: true, recipients: [culprits(), requestor()]]
def notifyBranch = [recipients: [brokenTestsSuspects(), requestor()]]

def BUILD_COMMAND = "mvn -B -U clean install -DskipTests"
def BUILD_STAGE_M2_REPO = workspace().getM2LocalRepoPath("strongbox-build")
def MAVEN_OPTS = "-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
def CHECKOUT_DIR = "strongbox-build"

def STRONGBOX_BRANCH = params.getOrDefault('STRONGBOX_BRANCH', 'master')
def STRONGBOX_DEBUG = params.getOrDefault('STRONGBOX_DEBUG', false)

pipeline {
    agent none
    parameters {
        string(defaultValue: 'master', description: 'Use a specific branch/pr of strongbox/strongbox? (use only when the current PR requires another one from strongbox/strongbox)', name: 'STRONGBOX_BRANCH', trim: true)
        booleanParam(defaultValue: true, description: 'Enable debug logging?', name: 'STRONGBOX_DEBUG')
        booleanParam(defaultValue: true, description: 'Send email notification?', name: 'NOTIFY_EMAIL')
    }
    options {
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '5', daysToKeepStr: env.BRANCH_NAME == 'master' ? '' : '7', numToKeepStr: env.BRANCH_NAME == 'master' ? '1000' : '10')
        timeout(time: 60, unit: 'MINUTES')
        disableResume()
        durabilityHint 'PERFORMANCE_OPTIMIZED'
        disableConcurrentBuilds()
        skipStagesAfterUnstable()
    }
    triggers {
        cron(env.BRANCH_NAME == 'master' ? 'H H * * 1-5' : '')
    }
    stages {
        stage("Building strongbox...") {
            when {
                beforeAgent true
                expression {
                    // This stage is only necessary when a PR in `s-w-i-t` depends on a PR in `strongbox/strongbox`.
                    // In all other cases, we can use the deployed artifacts from our repository.
                    !STRONGBOX_BRANCH.equals("master") && !STRONGBOX_BRANCH.equals("")
                }
            }
            agent {
                label 'alpine-jdk8-mvn3.6'
            }
            options {
                skipDefaultCheckout true
            }
            steps {
                container('maven') {
                    script {
                        nodeInfo('mvn')

                        gitClone(
                                url: "https://github.com/strongbox/strongbox",
                                branch: STRONGBOX_BRANCH,
                                targetDir: CHECKOUT_DIR,
                                extensions: [[$class: 'CloneOption', depth: 0, honorRefspec: true, noTags: true, reference: '', shallow: true]]
                        )

                        dir(CHECKOUT_DIR) {
                            withMavenPlus(mavenLocalRepo: BUILD_STAGE_M2_REPO,
                                          mavenSettingsConfig: '67aaee2b-ca74-4ae1-8eb9-c8f16eb5e534',
                                          mavenOpts: MAVEN_OPTS,
                                          options: [artifactsPublisher(disabled: true)]) {
                                sh label: "Building Strongbox",
                                   script: "${BUILD_COMMAND}"
                            }
                        }
                    }
                }
            }
            post {
                success {
                    script {
                        container("maven") {
                            dir(BUILD_STAGE_M2_REPO + "/org/carlspring/strongbox") {
                                stash name: 'strongboxArtifacts', includes: '**/*'
                            }
                        }
                    }
                }
                failure {
                    script {
                        container("maven") {
                            archiveArtifacts '**/target/strongbox-vault/logs/**'
                        }
                    }
                }
                cleanup {
                    container('maven') {
                        script {
                            workspace().clean()
                        }
                    }
                }
            }
        }
        stage("Integration tests...") {
            steps {
                script {
                    parallel parallelSWITStages {
                        clone = false
                        branch = null
                        unstash = (!STRONGBOX_BRANCH.equals("master") && !STRONGBOX_BRANCH.equals(""))
                        env = [
                                "STRONGBOX_DEBUG"              : STRONGBOX_DEBUG,
                                "STRONGBOX_LOG_CONSOLE_ENABLED": STRONGBOX_DEBUG,
                                'JENKINS_NODE_COOKIE'          : 'dontKillMe'
                        ]
                        modules = [
                                gradle: {
                                    agent = 'alpine-jdk8-mvn3.6-gradle5.6'
                                    tools = 'mvn gradle'
                                },
                                maven: {},
                                npm: {
                                    agent = 'alpine-jdk8-mvn3.6-node12'
                                    tools = 'mvn npm node yarn'
                                },
                                nuget: {
                                    agent = 'alpine-jdk8-mvn3.6-mono5-nuget3.4'
                                    tools = 'mvn mono'
                                },
                                sbt: {
                                    agent = 'alpine-jdk8-mvn3.6-sbt1.3'
                                    tools = 'mvn'
                                },
                                // Enable when ready.
                                //pypi: {
                                //    agent = 'alpine-jdk8-mvn3.6-pip19.3'
                                //    tools = 'mvn python pip'
                                //},
                                raw: {
                                    container = 'maven'
                                    tools = 'mvn'
                                }
                        ]
                    }
                }
            }
        }
    }
    post {
        failure {
            script {
                if (params.NOTIFY_EMAIL)
                {
                    notifyFailed((BRANCH_NAME == "master") ? notifyMaster : notifyBranch)
                }
            }
        }
        unstable {
            script {
                if (params.NOTIFY_EMAIL)
                {
                    notifyUnstable((BRANCH_NAME == "master") ? notifyMaster : notifyBranch)
                }
            }
        }
        fixed {
            script {
                if (params.NOTIFY_EMAIL)
                {
                    notifyFixed((BRANCH_NAME == "master") ? notifyMaster : notifyBranch)
                }
            }
        }
    }
}
