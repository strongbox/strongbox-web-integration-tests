import org.carlspring.strongbox.client.RestClient
import org.carlspring.strongbox.artifact.generator.NugetPackageGenerator
import java.nio.file.Paths

def runCommand = { strList ->
    assert (strList instanceof String || (strList instanceof List && strList.each{ it instanceof String } ))

    def path = new File("./src/nuget-it/deploy")
        
    println "Execute command[s]: "
    if(strList instanceof List) {
      strList.each{ println "${it} " }
    } else {
      println strList
    }
    
    def proc = strList.execute(null, path)
    proc.in.eachLine { line -> println line }
    proc.out.close()
    proc.waitFor() 
  
    println "\n"
    
    if (proc.exitValue()) {
      println "gave the following error: "
      println "[ERROR] ${proc.getErrorStream()}"
    }
    
    assert !proc.exitValue()
}

println "Test common-nuget-flow.groovy" + "\n\n"

def nugetExec = System.getenv("NUGET_V2_EXEC")
assert nugetExec?.trim() : "\"NUGET_V2_EXEC\" environment variable need to be set"

def packageId = "Org.Carlspring.Strongbox.Examples.Nuget.Mono" 
def packageVersion = "1.0.0"
def packageFileName = packageId + "." + packageVersion + ".nupkg";

def baseDir = "./target/nuget-it"
new File(baseDir).mkdirs()

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
   
def configPath = "./../../../target/nuget-it/NuGet.config"

new File("./target/nuget-it/NuGet.config").newWriter().withWriter { w ->
  w << ("<?xml version=\"1.0\" encoding=\"utf-8\"?><configuration></configuration>")
}

runCommand(String.format(
    "mono --runtime=v4.0 $nugetExec sources Add -Name %s -Source '%s' -UserName %s -Password %s -ConfigFile '%s'",
    "strongbox",
    storageUrl,
    "admin",
    "password",
    configPath))
runCommand(String.format(
    "mono --runtime=v4.0 $nugetExec config -set DefaultPushSource='%s' -ConfigFile '%s'",
    storageUrl,
    configPath))
runCommand(String.format(
    "mono --runtime=v4.0 $nugetExec setApiKey %s -Source '%s' -ConfigFile '%s'",
    nugetApiKey,
    storageUrl,
    configPath))
runCommand(String.format(
    "mono --runtime=v4.0 $nugetExec push ./../../../%s/%s/%s -ConfigFile '%s'",
    baseDir,
    packageVersion,
    packageFileName,
    configPath))