def baseScript = new GroovyScriptEngine( "$project.basedir/src/gradle-it" ).with {
    loadScriptByName( 'GradleIntegrationTest.groovy' )
}
this.metaClass.mixin baseScript

println "Test test-gradle-common-flow.groovy" + "\n\n"

def gradleExec = System.getenv("GRADLE_HOME")
assert gradleExec?.trim() : "\"GRADLE_HOME\" environment variable need to be set"

def targetPath = getTargetPath(project)
def baseDir = targetPath.toString()
def gradlePath = getGradlePath(project)

def $gradle = System.getenv("gradle")

System.out.println($gradle)

def username = "admin"
def password = "password"

def storageUrl = getStorageUrl()


runCommand(gradlePath, String.format("$gradleExec clean upload -Dcredentials.username=%s -Dcredentials.password=%s",
                                    username,
                                    password))

assert targetPath.resolve('strongbox-vault\\storages\\storage0\\snapshots\\org\\carlspring\\strongbox\\' +
                         'examples\\hello-strongbox-gradle\\1.0-SNAPSHOT').resolve("hello-strongbox-gradle-1.0-SNAPSHOT.jar")
                         .toFile().exists()
