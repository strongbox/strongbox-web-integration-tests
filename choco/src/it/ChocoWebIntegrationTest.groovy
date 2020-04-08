import java.nio.file.Paths
import org.apache.commons.io.FileUtils

class ChocoWebIntegrationTest
{

    def getExecutionBasePath(project)
    {
        return Paths.get(project.build.directory)
    }

    def runCommand(targetPath, strList)
    {

        assert (strList instanceof String || (strList instanceof List && strList.each { it instanceof String }))

        def output = new StringBuffer()

        def path = targetPath.toFile()

        print "Executing command[s]: ---> "
        if (strList instanceof List)
        {
            strList.each { println "${it} " }
        } else
        {
            println strList
        }

        def proc = strList.execute(null, path)
        proc.in.eachLine
        { line ->
            output.append(line).append("\n")
            println line
        }
        proc.out.close()
        proc.waitFor()

        println "\n"

        if (proc.exitValue())
        {
            println "gave the following error: "
            println "[ERROR] ${proc.getErrorStream()}"
        }

        assert !proc.exitValue()

        return output.toString()
    }

}
