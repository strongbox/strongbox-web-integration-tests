import org.carlspring.strongbox.client.RestClient
import java.nio.file.Paths
import java.nio.file.Files

class BaseNugetWebIntegrationTest {
    
    def runCommand(targetPath, strList)
    {
        assert (strList instanceof String || (strList instanceof List && strList.each{ it instanceof String } ))
    
        def output = new StringBuffer()
        
        def path = targetPath.toFile()
            
        println "Execute command[s]: "
        if(strList instanceof List) {
          strList.each{ println "${it} " }
        } else {
          println strList
        }
        
        def proc = strList.execute(null, path)
        proc.in.eachLine { line ->
            output.append(line).append("\n")
            println line
        }
        proc.out.close()
        proc.waitFor()
      
        println "\n"
        
        if (proc.exitValue()) {
          println "gave the following error: "
          println "[ERROR] ${proc.getErrorStream()}"
        }
        
        assert !proc.exitValue()
        
        return output.toString()
    }
    
    def getTargetPath(project)
    {
        def targetDir = project.build.directory
        println "Target directory: $targetDir\n\n"
        
        def targetPath = Paths.get(targetDir).resolve('it')
        Files.createDirectories(targetPath)
        return targetPath;
    }
    
    def getApiKey()
    {
        def client = RestClient.getTestInstanceLoggedInAsAdmin()
        
        println "Host name: " + client.getHost()
        println "Username:  " + client.getUsername()
        println "Password:  " + client.getPassword() + "\n\n"
        
        def nugetApiKey = client.generateUserSecurityToken();
        println "ApiKey: $nugetApiKey\n\n"
        return nugetApiKey
    }
    
    def getStorageUrl()
    {
        def client = RestClient.getTestInstanceLoggedInAsAdmin()
        def storageUrl = String.format("%s/storages/storage-nuget/nuget-releases", client.getContextBaseUrl())
        println "Storage URL:  $storageUrl\n\n"
        return storageUrl
    }
    
    def generateConfig(targetPath, nugetApiKey, nugetExec)
    {
        def baseDir = targetPath.toString()
        def client = RestClient.getTestInstanceLoggedInAsAdmin()
        
        def storageUrl = getStorageUrl()
        
        def configPath = "$baseDir/NuGet.config"
        
        new File(configPath).newWriter().withWriter { w ->
          w << ("<?xml version=\"1.0\" encoding=\"utf-8\"?><configuration></configuration>")
        }
        
        def output;
        
        runCommand(targetPath, String.format(
            "$nugetExec sources Add -Name %s -Source %s -UserName %s -Password %s -ConfigFile %s",
            "strongbox",
            storageUrl,
            "admin",
            "password",
            configPath))
        
        runCommand(targetPath, String.format(
            "$nugetExec config -set DefaultPushSource=%s -ConfigFile %s",
            storageUrl,
            configPath))
        
        runCommand(targetPath, String.format(
            "$nugetExec setApiKey %s -Source %s -ConfigFile %s",
            nugetApiKey,
            storageUrl,
            configPath))
    }
    
}