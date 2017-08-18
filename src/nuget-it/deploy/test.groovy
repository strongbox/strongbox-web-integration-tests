import org.carlspring.strongbox.client.RestClient

def path = new File("./src/nuget-it/deploy")

def runCommand = { strList ->
    assert (strList instanceof String || (strList instanceof List && strList.each{ it instanceof String } ))
    
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

def sout = new StringBuilder(), serr = new StringBuilder()
def client = RestClient.getTestInstanceLoggedInAsAdmin()

println "Host name: " + client.getHost()
println "Username:  " + client.getUsername()
println "Password:  " + client.getPassword() + "\n\n"

def nugetApiKey = client.generateUserSecurityToken();
println "ApiKey: $nugetApiKey\n\n"

def storageUrl = String.format("%s/storages/nuget-common-storage/nuget-releases", client.getContextBaseUrl()) 
println "Storage URL:  $storageUrl\n\n"

new File("./src/nuget-it/deploy/NuGet.config").newWriter().withWriter { w ->
  w << ("<?xml version=\"1.0\" encoding=\"utf-8\"?><configuration></configuration>")
}

runCommand(String.format(
    "mono --runtime=v4.0 ./../nuget_v2.exe sources Add -Name %s -Source %s -UserName %s -Password %s -ConfigFile ./NuGet.config",
    "strongbox",
    storageUrl,
    "admin",
    "password"))
runCommand(String.format(
    "mono --runtime=v4.0 ./../nuget_v2.exe config -set DefaultPushSource=%s -ConfigFile ./NuGet.config",
    storageUrl))
runCommand(String.format(
    "mono --runtime=v4.0 ./../nuget_v2.exe setApiKey %s -Source %s -ConfigFile ./NuGet.config",
    nugetApiKey,
    storageUrl))
runCommand(String.format(
    "mono --runtime=v4.0 ./../nuget_v2.exe push ./Org.Carlspring.Strongbox.Examples.Nuget.Mono.1.0.0.nupkg -ConfigFile ./NuGet.config"))