import groovy.transform.BaseScript
import org.carlspring.strongbox.client.RestClient
import org.carlspring.strongbox.artifact.generator.NugetPackageGenerator
import java.nio.file.Paths
import java.nio.file.Files

def baseScript = new GroovyScriptEngine( "$project.basedir/src/nuget-it" ).with {
    loadScriptByName( 'BaseNugetWebIntegrationTest.groovy' )
  }
this.metaClass.mixin baseScript

println "Test test-install-with-transitive-deps.groovy" + "\n\n"

def targetDir = project.build.directory
println "Target directory: $targetDir\n\n"

def targetPath = Paths.get(targetDir).resolve('nuget-it')
Files.createDirectories(targetPath)

def nugetExec = System.getenv("NUGET_V3_EXEC")
assert nugetExec?.trim() : "\"NUGET_V3_EXEC\" environment variable need to be set"

def packageId = "Org.Carlspring.Strongbox.Examples.Nuget.Mono" 
def packageVersion = "1.0.0"
def packageFileName = packageId + "." + packageVersion + ".nupkg";

def baseDir = targetPath.toString()

def nugetPackageGenerator = new NugetPackageGenerator(baseDir);
nugetPackageGenerator.generateNugetPackage(packageId, packageVersion);
def packageFilePath = Paths.get(baseDir).resolve(packageVersion).resolve(packageFileName);

def client = RestClient.getTestInstanceLoggedInAsAdmin()

println "Host name: " + client.getHost()
println "Username:  " + client.getUsername()
println "Password:  " + client.getPassword() + "\n\n"

def nugetApiKey = client.generateUserSecurityToken();
println "ApiKey: $nugetApiKey\n\n"

def storageUrl = String.format("%s/storages/nuget-common-storage/nuget-releases", client.getContextBaseUrl()) 
println "Storage URL:  $storageUrl\n\n"
   
def configPath = "$baseDir/NuGet.config"

new File(configPath).newWriter().withWriter { w ->
  w << ("<?xml version=\"1.0\" encoding=\"utf-8\"?><configuration></configuration>")
}

def output;