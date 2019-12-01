def baseScript = new GroovyScriptEngine( "$project.basedir/src/it" ).with {
    loadScriptByName('NpmIntegrationTest.groovy')
}
this.metaClass.mixin baseScript

println "Test test-npm-common-flow.groovy" + "\n\n"

def npmExec
def publishSuccessMsg = "+ @strongbox/npm-transitive-dependency@1.0.0"
def resolveFailureMsg = "npm ERR! missing: @strongbox/npm-transitive-dependency"
def unpublishVersionSuccessMsg = "- @strongbox/unpublish-test@1.0.0"
def unpublishPackageSuccessMsg = "- @strongbox/unpublish-test"

// Determine OS and appropriate commands
if (System.getProperty("os.name").toLowerCase().contains("windows")) {
    npmExec = "cmd /c npm "
} else {
    npmExec = "npm "
}

def executionPath = getExecutionPathRoot(project)
def commandOutput

def transitiveDependencyRoot = executionPath.resolve("npm-transitive-dependency")

def unpublishFirstVersionRoot = executionPath.resolve("npm-unpublish-test/first-version-test")
def unpublishSecondVersionRoot = executionPath.resolve("npm-unpublish-test/second-version-test")

// Publish package to Strongbox and check output for success
commandOutput = runCommand(transitiveDependencyRoot, npmExec + "publish")

assert commandOutput.contains(publishSuccessMsg)

def dependencyTestRoot = executionPath.resolve("npm-dependency-test")

// Resolve dependency via Strongbox and check output for success
runCommand(dependencyTestRoot, npmExec + "install")

commandOutput = runCommand(dependencyTestRoot, npmExec + "ls")

assert !commandOutput.contains(resolveFailureMsg)


//publish versions for unpublish test
runCommand(unpublishFirstVersionRoot, npmExec + "publish")
runCommand(unpublishSecondVersionRoot, npmExec + "publish")

//unpublish single version
commandOutput = runCommand(transitiveDependencyRoot, npmExec + "unpublish @strongbox/unpublish-test@1.0.0")
assert commandOutput.contains(unpublishVersionSuccessMsg)

//unpublish package
commandOutput = runCommand(transitiveDependencyRoot, npmExec + "unpublish @strongbox/unpublish-test --force")
assert commandOutput.contains(unpublishPackageSuccessMsg)
