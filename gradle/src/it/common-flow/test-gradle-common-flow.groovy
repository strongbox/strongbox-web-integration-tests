def baseScript = new GroovyScriptEngine( "$project.basedir/src/it" ).with {
    loadScriptByName( 'GradleIntegrationTest.groovy' )
}
this.metaClass.mixin baseScript

println "Test test-gradle-common-flow.groovy" + "\n\n"

def targetPath = getTargetPath(project)
def gradlePath = getGradlePath(project)
def executionPath = gradlePath.resolve('src').resolve('it').resolve('common-flow')


def gradlewName

// Determine OS and appropriate gradlew executable
if (System.getProperty("os.name").toLowerCase().contains("windows")) {
    gradlewName = "gradlew.bat"
} else {
    gradlewName = "gradlew"
}

def gradleExec = gradlePath.resolve(gradlewName).toString()

def username = "deployer"
def password = "password"

System.out.println(gradleExec)

runCommand(executionPath, String.format("$gradleExec upload -Dcredentials.username=%s -Dcredentials.password=%s",
                                        username,
                                        password))

// check if artifact was uploaded to Strongbox
def artifactFilenames = new FileNameByRegexFinder()
                        .getFileNames(project.build.directory +
                                      '/strongbox-vault/storages/storage0/snapshots/org/carlspring/strongbox/' +
                                      'examples/hello-strongbox-gradle/1.0-SNAPSHOT', 'hello-strongbox-gradle-1.0-\\d{8}\\.\\d{6}-\\d\\.jar$')
assert artifactFilenames.size() == 1
assert targetPath.resolve('strongbox-vault/storages/storage0/snapshots/org/carlspring/strongbox/' +
                          'examples/hello-strongbox-gradle/1.0-SNAPSHOT')
                 .resolve(artifactFilenames[0])
                 .toFile().exists()

