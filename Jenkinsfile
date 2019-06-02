@Library('jenkins-shared-libraries') _

// Notification settings for "master" and "branch/pr"
def notifyMaster = [notifyAdmins: true, recipients: [culprits(), requestor()]]
def notifyBranch = [recipients: [brokenTestsSuspects(), requestor()]]

def BUILD_COMMAND = "mvn -B -U clean install -DskipTests"
def BUILD_STAGE_M2_REPO = workspace().getM2LocalRepoPath("strongbox-build")
def MAVEN_OPTS = "-XX:+TieredCompilation -XX:TieredStopAtLevel=1 -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn"
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
                label 'alpine-jdk8-mvn-3.5'
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
            // NB: Agents with multiple containers share the same /home/jenkins which causes problems!!!
            parallel {
                stage('Gradle') {
                    agent {
                        label 'alpine-jdk8-gradle-4.5'
                    }
                    options {
                        timeout(time: 15, unit: 'MINUTES')
                        checkoutToSubdirectory "strongbox-web-integration-tests"
                    }
                    steps {
                        script {
                            runIt('gradle', 'mvn gradle', STAGE_NAME.toLowerCase(), MAVEN_OPTS, params.STRONGBOX_BRANCH.equals("master"))
                        }
                    }
                    post {
                        unsuccessful {
                            script {
                                runItArchive('gradle')
                            }
                        }
                        always {
                            // This is necessary, because Jenkins sometimes gets confused what's the CWD!
                            dir("") {
                                // Cleanup
                                script {
                                    workspace().clean()
                                }
                            }
                        }
                    }
                }

                stage('Maven') {
                    agent {
                        label 'alpine-jdk8-mvn-3.5'
                    }
                    options {
                        timeout(time: 15, unit: 'MINUTES')
                        checkoutToSubdirectory "strongbox-web-integration-tests"
                    }
                    steps {
                        script {
                            runIt('maven', 'mvn', STAGE_NAME.toLowerCase(), MAVEN_OPTS, params.STRONGBOX_BRANCH.equals("master"))
                        }
                    }
                    post {
                        unsuccessful {
                            script {
                                runItArchive('maven')
                            }
                        }
                        always {
                            // This is necessary, because Jenkins sometimes gets confused what's the CWD!
                            dir("") {
                                // Cleanup
                                script {
                                    workspace().clean()
                                }
                            }
                        }
                    }
                }

                stage('NPM') {
                    agent {
                        label 'alpine-jdk8-node-10'
                    }
                    options {
                        timeout(time: 15, unit: 'MINUTES')
                        checkoutToSubdirectory "strongbox-web-integration-tests"
                    }
                    steps {
                        script {
                            runIt('npm', 'mvn npm node yarn', STAGE_NAME.toLowerCase(), MAVEN_OPTS, params.STRONGBOX_BRANCH.equals("master"))
                        }
                    }
                    post {
                        unsuccessful {
                            script {
                                runItArchive('npm')
                            }
                        }
                        always {
                            // This is necessary, because Jenkins sometimes gets confused what's the CWD!
                            dir("") {
                                // Cleanup
                                script {
                                    workspace().clean()
                                }
                            }
                        }
                    }
                }

                stage('Nuget') {
                    agent {
                        label 'alpine-jdk8-nuget-3.4-mono'
                    }
                    options {
                        timeout(time: 15, unit: 'MINUTES')
                        checkoutToSubdirectory "strongbox-web-integration-tests"
                    }
                    steps {
                        script {
                            runIt('nuget', 'mvn mono', STAGE_NAME.toLowerCase(), MAVEN_OPTS, params.STRONGBOX_BRANCH.equals("master"))
                        }
                    }
                    post {
                        unsuccessful {
                            script {
                                runItArchive('nuget')
                            }
                        }
                        always {
                            // This is necessary, because Jenkins sometimes gets confused what's the CWD!
                            dir("") {
                                // Cleanup
                                script {
                                    workspace().clean()
                                }
                            }
                        }
                    }
                }

                stage('SBT') {
                    agent {
                        label 'alpine-jdk8-sbt-1.1'
                    }
                    options {
                        timeout(time: 15, unit: 'MINUTES')
                        checkoutToSubdirectory "strongbox-web-integration-tests"
                    }
                    steps {
                        script {
                            runIt('sbt', 'mvn', STAGE_NAME.toLowerCase(), MAVEN_OPTS, params.STRONGBOX_BRANCH.equals("master"))
                        }
                    }
                    post {
                        unsuccessful {
                            script {
                                runItArchive('sbt')
                            }
                        }
                        always {
                            // This is necessary, because Jenkins sometimes gets confused what's the CWD!
                            dir("") {
                                // Cleanup
                                script {
                                    workspace().clean()
                                }
                            }
                        }
                    }
                }

                stage('Raw') {
                    agent {
                        label 'alpine:jdk8-mvn-3.5'
                    }
                    options {
                        timeout(time: 15, unit: 'MINUTES')
                        checkoutToSubdirectory "strongbox-web-integration-tests"
                    }
                    steps {
                        script {
                            runIt('raw', 'mvn', STAGE_NAME.toLowerCase(), MAVEN_OPTS, params.STRONGBOX_BRANCH.equals("master"))
                        }
                    }
                    post {
                        unsuccessful {
                            script {
                                runItArchive('raw')
                            }
                        }
                        always {
                            // This is necessary, because Jenkins sometimes gets confused what's the CWD!
                            dir("") {
                                // Cleanup
                                script {
                                    workspace().clean()
                                }
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


def runIt(buildTool, infoTools, stageName, mavenOpts, isMasterBranch)
{
    timeout(time: 20, unit: 'MINUTES') {
        container(buildTool) {
            nodeInfo(infoTools)

            if (buildTool == 'nuget')
            {
                sh "\$NUGET_V3_EXEC | head -n1"
            }

            script {
                def mvnLocalRepo = workspace().getM2LocalRepoPath("it-" + stageName)
                def strongboxGroupPath = "${mvnLocalRepo}/org/carlspring/strongbox"

                if(!isMasterBranch)
                {
                    sh label: "Creating .m2 path to unstash artifacts",
                       script: "mkdir -p ${strongboxGroupPath}"

                    dir(strongboxGroupPath) {
                        unstash name: 'strongboxArtifacts'
                    }
                }

                dir("strongbox-web-integration-tests/" + buildTool) {
                    // Build
                    withMavenPlus(mavenLocalRepo: mvnLocalRepo,
                                  mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833',
                                  mavenOpts: mavenOpts,
                                  publisherStrategy: 'EXPLICIT') {
                        // This is a special patch, which fixes some mysterious issue with SBT's bash script which is unable
                        // to pick the environment variables for some unknown reason.
                        // Removing this will result in the following build failure:
                        //  gave the following error:
                        //  [ERROR] /java/sbt-1.1.6/bin/sbt-launch-lib.bash: line 258: java: command not found
                        //   mkdir: cannot create directory ??????: No such file or directory
                        //   java/sbt-1.1.6/bin/sbt-launch-lib.bash: line 73: java: command not found
                        //   java/sbt-1.1.6/bin/sbt-launch-lib.bash: line 73: java: command not found
                        if (buildTool == 'sbt')
                        {
                            sh label: "Fixing startup script...",
                               script: 'sed -i \'s/^declare java_cmd=java/declare java_cmd=\\/java\\/jdk8u202-b08\\/bin\\/java/\' /java/sbt-*/bin/sbt-launch-lib.bash'
                        }

                        // add -X -e when debugging.
                        sh label: "Running integration tests for ${buildTool}",
                           script: "mvn clean install -T 2 -Daether.connector.resumeDownloads=false"
                    }
                }
            }
        }
    }
}

def runItArchive(buildTool)
{
    container(buildTool) {
        dir("") {
            archiveArtifacts "strongbox-web-integration-tests/" + buildTool + "/target/**"
        }
    }
}

