pipeline {
    agent {
        docker {
            args '-v /mnt/ramdisk/3:/home/jenkins --cap-add SYS_ADMIN'
            image 'hub.carlspring.org/jenkins/opensuse-slave:latest'
        }
    }
    options {
        timeout(time: 2, unit: 'HOURS')
        disableConcurrentBuilds()
        skipDefaultCheckout()
    }
    stages {
        stage('Setup workspace')
        {
            steps {
                script {
                    env.HDDWS=env.WORKSPACE
                    env.RAMWS="/home/jenkins/workspace/"+ sh(returnStdout: true, script: 'basename "${HDDWS}"').trim()
                    env.RAMMOUNT=env.WORKSPACE+"/ram"

                    cleanWs deleteDirs: true
                    checkout scm

                    echo "Preparing workspace..."
                    sh "mkdir -p '$RAMWS'"
                    sh "cp -R `ls -A '$HDDWS' | grep -v .git | grep -v ram` '$RAMWS'"
                    sh "mkdir -p '$RAMMOUNT'"
                    sh "sudo mount --bind  '$RAMWS' '$RAMMOUNT'"
                }
            }
        }
        stage('Building...')
        {
            steps {
                withMaven(maven: 'maven-3.3.9', mavenSettingsConfig: 'a5452263-40e5-4d71-a5aa-4fc94a0e6833')
                {
                    sh "cd '$RAMMOUNT' && mvn -U clean install"

                    // unmount and copy back to hdd
                    sh "sudo umount --force $RAMMOUNT"
                    sh "cp -R '$RAMWS/.' '$RAMMOUNT'"
                    sh "touch '$HDDWS/copied'"
                }
            }
        }
    }
    post {
        success {
            script {
                if(BRANCH_NAME == 'master') {
                    build job: "strongbox/strongbox-web-core-pro/master", wait: false
                }
            }
        }
        changed {
            script {
                if(BRANCH_NAME == 'master') {
                    def skype = new org.carlspring.jenkins.notification.skype.Skype()
                    skype.sendNotification("admins;devs");
                }
            }
        }
        always {
            script {
                // fallback copy
                if(!fileExists(env.HDDWS+'/copied'))
                {
                    // unmount and copy back to hdd
                    sh "sudo umount --force $RAMMOUNT"
                    sh "cp -R '$RAMWS/.' '$RAMMOUNT'"
                }
            }

            // remove unnecessary directories.
            sh "(cd '$HDDWS' && find . -maxdepth 1 ! -name '.' ! -name '..' ! -name 'ram' -exec rm -rf '{}' \\;)"

            // clean up ram
            sh "rm -rf '$RAMWS'"
        }
    }
}
