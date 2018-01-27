import org.carlspring.strongbox.client.RestClient
import java.nio.file.Paths
import java.nio.file.Files

class GradleIntegrationTest {

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

        def targetPath = Paths.get(targetDir).resolve('gradle-it')
        Files.createDirectories(targetPath)
        return targetPath;
    }

    def getStorageUrl()
    {
        def client = RestClient.getTestInstanceLoggedInAsAdmin()
        def storageUrl = String.format("%s/storages/storage0/snapshots/", client.getContextBaseUrl())
        println "Storage URL:  $storageUrl\n\n"
        return storageUrl
    }

}
