
def baseScript = new GroovyScriptEngine("$project.basedir/src/it").with {
    loadScriptByName('PypiIntegrationTest.groovy')
}

this.metaClass.mixin baseScript

println "Pypi Integration Test started...."
println "Executing test-pypi-common-flows.groovy"

def pipCommandPrefix
// Determine OS and appropriate commands
if (System.getProperty("os.name").toLowerCase().contains("windows")) {
    pipCommandPrefix = "cmd /c pip3 "
} else {
    pipCommandPrefix = "pip3 "
}

println pipCommandPrefix + "\n"

def executionPath = getBaseExecutionPath(project)
println executionPath


println "Pypi Integration Test completed.!!"