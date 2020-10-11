@Library('jenkins-shared-libraries') _

// Notification settings for "master" and "branch/pr"
def notifyMaster = [notifyAdmins: true, recipients: [culprits(), requestor()]]
def notifyBranch = [recipients: [brokenTestsSuspects(), requestor()]]

def BUILD_COMMAND = "mvn -B -U clean install -DskipTests"
def BUILD_STAGE_M2_REPO = workspace().getM2LocalRepoPath("strongbox-build")
def MAVEN_OPTS = "-XX:+TieredCompilation -XX:TieredStopAtLevel=1"
def CHECKOUT_DIR = "strongbox-build"

// Properties are set here, because declarative pipelines don't allow for custom classes in the parameters {} block.
properties([
    parameters([
        [
            $class: 'ChoiceParameter',
            choiceType: 'PT_SINGLE_SELECT',
            description: 'Use a specific branch/pr of strongbox/strongbox? (use only when the current PR requires another one from strongbox/strongbox)',
            filterLength: 1,
            filterable: true,
            name: 'STRONGBOX_BRANCH',
            randomName: 'choice-parameter-609896998232463',
            script: [
                $class: 'GroovyScript',
                fallbackScript: [
                    classpath: [],
                    sandbox: true,
                    script: 'return ["ERROR"]'
                ],
                script: [
                    classpath: [],
                    sandbox: true,
                    script: "return jenkins.model.Jenkins.getInstance().getItemByFullName('/strongbox/builds/strongbox').getAllItems()*.name.sort{a,b->a.equalsIgnoreCase('master') ? -1 : 0}"
                ]
            ]
        ],
        [
            $class: 'ChoiceParameter',
            choiceType: 'PT_SINGLE_SELECT',
            description: 'The docker image version to use while testing (check ci-build-images job)',
            filterLength: 1,
            filterable: true,
            name: 'CI_IMAGE_VERSION',
            randomName: 'choice-parameter-609896998232363',
            script: [
                $class: 'GroovyScript',
                fallbackScript: [
                    classpath: [],
                    sandbox: true,
                    script: 'return ["ERROR"]'
                ],
                script: [
                    classpath: [],
                    sandbox: true,
                    script: "return jenkins.model.Jenkins.getInstance().getItemByFullName('/strongbox/builds/ci-build-images').getAllItems()*.name.sort{a,b->a.equalsIgnoreCase('master') ? -1 : 0}"
                ]
            ]
        ],
        booleanParam(defaultValue: true, description: 'Enable debug logging?', name: 'STRONGBOX_DEBUG'),
        booleanParam(defaultValue: true, description: 'Send email notification?', name: 'NOTIFY_EMAIL')
    ])
])

def STRONGBOX_BRANCH = params.getOrDefault('STRONGBOX_BRANCH', 'master')
def STRONGBOX_DEBUG = params.getOrDefault('STRONGBOX_DEBUG', false)
def CI_IMAGE_VERSION = params.getOrDefault('CI_IMAGE_VERSION', 'master')

pipeline {
    agent none
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
                                // strongbox-distribution is not needed for tool integration tests since they depend
                                // only on sb-web-core
                                stash name: 'strongboxArtifacts', includes: '**/*', excludes: '**/strongbox-distribution/**'

                                // Include only strongbox-distribution of type `.zip/tar.gz/etc` (depending on the exclusion list)
                                // stash name: 'strongboxArtifacts', includes: '**/*', excludes: '**/strongbox-distribution*.zip, **/strongbox-distribution*.deb, **/strongbox-distribution*.rpm'
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
                    // CI images are deployed to docker hub. Sometimes we tag testing images with additional suffix
                    // for testing purposes. The additional suffix is usually `-BRANCH_NAME` || `-PR-NUMBER`
                    def suffix = CI_IMAGE_VERSION == "master" || CI_IMAGE_VERSION == null ? "" : "-${CI_IMAGE_VERSION}"

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
                            choco: {
                                agent = "alpine:jdk8-mvn3.6-mono5-nuget3.4-choco0.10${suffix}"
                                tools = 'mvn mono choco'
                                mavenArgs = '-U'
                            },
                            gradle: {
                                agent = "alpine:jdk8-mvn3.6-gradle6.6${suffix}"
                                tools = 'mvn gradle'
                                mavenArgs = '-U'
                            },
                            maven: {
                                agent = "alpine:jdk8-mvn3.6${suffix}"
                                mavenArgs = '-U'
                            },
                            npm: {
                                agent = "alpine:jdk8-mvn3.6-node14${suffix}"
                                tools = 'mvn npm node yarn'
                                mavenArgs = '-U'
                            },
                            nuget: {
                                agent = "alpine:jdk8-mvn3.6-mono5-nuget3.4${suffix}"
                                tools = 'mvn mono'
                                mavenArgs = '-U'
                            },
                            sbt: {
                                agent = "alpine:jdk8-mvn3.6-sbt1.3${suffix}"
                                tools = 'mvn'
                                mavenArgs = '-U'
                            },
                            pypi: {
                                agent = "alpine:jdk8-mvn3.6-pip19.3${suffix}"
                                tools = 'mvn python pip'
                                mavenArgs = '-U'
                            },
                            raw: {
                                agent = "alpine:jdk8-mvn3.6${suffix}"
                                container = 'maven'
                                tools = 'mvn'
                                mavenArgs = '-U'
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
