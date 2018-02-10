def baseScript = new GroovyScriptEngine( "$project.basedir/" ).with {
    loadScriptByName( 'GradleIntegrationTest.groovy' )
}
this.metaClass.mixin baseScript

println "Test test-gradle-common-flow.groovy" + "\n\n"

def targetPath = getTargetPath(project)
def gradlePath = getGradlePath(project)

def gradlewName;

// Determine OS and appropriate gradlew executable
if (System.getProperty("os.name").toLowerCase().contains("windows")) {
    gradlewName = "gradlew.bat"
} else {
    gradlewName = "gradlew"
}

def gradleExec = gradlePath.resolve(gradlewName).toString();

def username = "admin"
def password = "password"

System.out.println(gradleExec)

runCommand(gradlePath, String.format("$gradleExec upload -Dcredentials.username=%s -Dcredentials.password=%s",
                                    username,
                                    password))

// check if dependency was resolved from Strongbox
assert targetPath.resolve('strongbox-vault/storages/storage-common-proxies/carlspring/com/fasterxml/jackson/core/' +
                          '/jackson-databind/2.9.4/').resolve('jackson-databind-2.9.4.jar').toFile().exists();

// check if artifact was uploaded to Strongbox
assert targetPath.resolve('strongbox-vault/storages/storage0/snapshots/org/carlspring/strongbox/' +
                         'examples/hello-strongbox-gradle/1.0-SNAPSHOT')
                         .resolve('hello-strongbox-gradle-1.0-SNAPSHOT.jar')
                         .toFile().exists()

