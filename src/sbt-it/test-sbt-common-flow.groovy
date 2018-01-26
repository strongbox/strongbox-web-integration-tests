def baseScript = new GroovyScriptEngine("$project.basedir/src/sbt-it").with {
    loadScriptByName('BaseSbtWebIntegrationTest.groovy')
}
this.metaClass.mixin baseScript

println "Test test-sbt-common-flow.groovy" + "\n\n"

def targetPath = getTargetPath(project)

runCommand(targetPath, "sbt clean compile publish")