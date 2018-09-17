def baseScript = new GroovyScriptEngine( "$project.basedir/src/it" ).with {
    loadScriptByName('NpmIntegrationTest.groovy')
}
this.metaClass.mixin baseScript

println "Test test-npm-common-flow.groovy" + "\n\n"

def npmExec
def publishSuccessMsg = "+ @strongbox/npm-transitive-dependency@1.0.0"
def resolveFailureMsg = "npm ERR! missing: @strongbox/npm-transitive-dependency"

// Determine OS and appropriate commands
if (System.getProperty("os.name").toLowerCase().contains("windows")) {
    npmExec = "cmd /c npm "
} else {
    npmExec = "npm "
}

def executionPath = getExecutionPathRoot(project)
def commandOutput

def transitiveDependencyRoot = executionPath.resolve("npm-transitive-dependency")

// Publish package to Strongbox and check output for success
commandOutput = runCommand(transitiveDependencyRoot, npmExec + "publish")

assert commandOutput.contains(publishSuccessMsg)

def dependencyTestRoot = executionPath.resolve("npm-dependency-test")

// Resolve dependency via Strongbox and check output for success
runCommand(dependencyTestRoot, npmExec + "install")

commandOutput = runCommand(dependencyTestRoot, npmExec + "ls")

assert !commandOutput.contains(resolveFailureMsg)
