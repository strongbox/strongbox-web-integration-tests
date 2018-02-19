def baseScript = new GroovyScriptEngine( "$project.basedir/src/it" ).with {
    loadScriptByName('NpmIntegrationTest.groovy')
}
this.metaClass.mixin baseScript

println "Test test-npm-common-flow.groovy" + "\n\n"

def npmExec
def publishSuccessMsg = "+ @strongbox/hello-strongbox-npm@1.0.0"
def resolveFailureMsg = "npm ERR! missing: @strongbox/hello-strongbox-npm"

// Determine OS and appropriate commands
if (System.getProperty("os.name").toLowerCase().contains("windows")) {
    npmExec = "cmd /c npm "
} else {
    npmExec = "npm "
}

def executionPath = getExecutionPath(project)
def commandOutput

// Publish package to Strongbox and check output for success
commandOutput = runCommand(executionPath, npmExec + "publish")

assert commandOutput.contains(publishSuccessMsg)

// Resolve dependency via Strongbox and check output for success

runCommand(executionPath, npmExec + "install")

commandOutput = runCommand(executionPath, npmExec + "ls")

assert !commandOutput.contains(resolveFailureMsg)
