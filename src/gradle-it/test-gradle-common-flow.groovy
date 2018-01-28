def baseScript = new GroovyScriptEngine( "$project.basedir/src/gradle-it" ).with {
    loadScriptByName( 'GradleIntegrationTest.groovy' )
}
this.metaClass.mixin baseScript

println "Test test-gradle-common-flow.groovy" + "\n\n"

def gradleExec = System.getenv("GRADLE_HOME")
assert gradleExec?.trim() : "\"GRADLE_HOME\" environment variable need to be set"

def targetPath = getTargetPath(project)
def baseDir = targetPath.toString()

def $gradle = System.getenv("gradle")

System.out.println($gradle)

def username = "maven"
def password = "password"

def storageUrl = getStorageUrl()

runCommand(targetPath, String.format(
           "$gradleExec clean upload -Dcredentials.username=%s -Dcredentials.password=%s",
           username,
           password))
