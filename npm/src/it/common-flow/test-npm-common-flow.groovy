import java.nio.file.Paths

def baseScript = new GroovyScriptEngine( "$project.basedir/src/it" ).with {
    loadScriptByName('NpmIntegrationTest.groovy')
}
this.metaClass.mixin baseScript

println "Test test-npm-common-flow.groovy" + "\n\n"

def npmExec;

// Determine OS and appropriate commands
if (System.getProperty("os.name").toLowerCase().contains("windows")) {
    npmExec = 'cmd /c npm'
} else {
    npmExec = 'sh -c npm'
}

def targetPath = getTargetPath(project)
def executionPath = getExecutionPath(project)

runCommand(executionPath, 'cmd /c npm -v')

