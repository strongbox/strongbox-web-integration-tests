import java.nio.file.Paths

class NpmIntegrationTest {

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

    def getExecutionPathRoot(project)
    {
        def projectDir = project.basedir

        def executionPath = Paths.get(projectDir.toString()).resolve('src/it/common-flow')
        println "Execution directory: $executionPath\n\n"

        return executionPath
    }

}
