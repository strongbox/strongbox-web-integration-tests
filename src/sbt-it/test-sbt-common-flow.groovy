def baseScript = new GroovyScriptEngine("$project.basedir/src/sbt-it").with {
    loadScriptByName('BaseSbtWebIntegrationTest.groovy')
}
this.metaClass.mixin baseScript

println "Test test-sbt-common-flow.groovy" + "\n\n"

def targetPath = getTargetPath(project)

runCommand(targetPath, "sbt clean")

output = runCommand(targetPath, "sbt compile")

assert output.contains("success")
assert !output.contains("FAILURE")

output = runCommand(targetPath, "sbt assembly")

assert output.contains("success")
assert !output.contains("FAILURE")

output = runCommand(targetPath, "sbt publish")

assert output.contains("success")
assert !output.contains("FAILURE")