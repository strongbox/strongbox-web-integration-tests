@Library('jenkins-shared-libraries') _

// Notification settings for "master" and "branch/pr"
def notifyMaster = [notifyAdmins: true, recipients: [culprits(), requestor()]]
def notifyBranch = [recipients: [brokenTestsSuspects(), requestor()]]

def BUILD_COMMAND = "mvn -B -U clean install -DskipTests"
def BUILD_STAGE_M2_REPO = workspace().getM2LocalRepoPath("strongbox-build")
def MAVEN_OPTS = "-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
def CHECKOUT_DIR = "strongbox-build"

pipeline {
    agent none
    parameters {
        string(defaultValue: 'master', description: 'Use a specific branch/pr of strongbox/strongbox? (use only when the current PR requires another one from strongbox/strongbox)', name: 'STRONGBOX_BRANCH', trim: true)
        booleanParam(defaultValue: true, description: 'Send email notification?', name: 'NOTIFY_EMAIL')
    }
    options {
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '50', daysToKeepStr: '', numToKeepStr: '1000')
        timeout(time: 60, unit: 'MINUTES')
        disableResume()
        durabilityHint 'PERFORMANCE_OPTIMIZED'
        disableConcurrentBuilds()
        skipStagesAfterUnstable()
    }
    stages {
        stage("Building strongbox...") {
            when {
                beforeAgent true
                expression {
                    // This stage is only necessary when a PR in `s-w-i-t` depends on a PR in `strongbox/strongbox`.
                    // In all other cases, we can use the already deployed artifacts from our repository.
                    !params.STRONGBOX_BRANCH.equals("master") && !params.STRONGBOX_BRANCH.equals("")
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
                            branch: params.STRONGBOX_BRANCH,
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
        stage("Integration tests...")
        {
            steps {
                script {
                    parallel parallelSWITStages(false, null, false)
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
