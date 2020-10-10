def baseScript = new GroovyScriptEngine( "$project.basedir/src/it" ).with {
    loadScriptByName( 'BaseNugetWebIntegrationTest.groovy' )
  }
this.metaClass.mixin baseScript

println "Test test-prepare-nuget-config.groovy" + "\n\n"

def targetPath = getTargetPath(project)

def nugetExec = System.getenv("NUGET_EXEC")
assert nugetExec?.trim() : "\"NUGET_EXEC\" environment variable need to be set"

def nugetApiKey = getApiKey()

generateConfig(targetPath, nugetApiKey, nugetExec)
   

