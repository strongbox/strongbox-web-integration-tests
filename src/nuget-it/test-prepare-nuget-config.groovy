import groovy.transform.BaseScript
import org.carlspring.strongbox.artifact.generator.NugetPackageGenerator
import java.nio.file.Paths
import java.nio.file.Files

def baseScript = new GroovyScriptEngine( "$project.basedir/src/nuget-it" ).with {
    loadScriptByName( 'BaseNugetWebIntegrationTest.groovy' )
  }
this.metaClass.mixin baseScript

println "Test test-prepare-nuget-config.groovy" + "\n\n"

def targetPath = getTargetPath(project)

def nugetExec = System.getenv("NUGET_V3_EXEC")
assert nugetExec?.trim() : "\"NUGET_V3_EXEC\" environment variable need to be set"

def nugetApiKey = getApiKey()

generateConfig(targetPath, nugetApiKey, nugetExec)
   

