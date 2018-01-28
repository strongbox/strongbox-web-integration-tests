import java.nio.file.Paths
import org.apache.commons.io.FileUtils

class BaseSbtWebIntegrationTest {
    def getTargetPath(project) {
        return Paths.get(System.getProperty("user.dir") + "/src/sbt-it")
    }

    def cleanIvyMavenCache() {
        def userHome = new File(System.getProperty("user.home")) as File
        def mavenCache = Paths.get(userHome.toURI()).resolve(".m2").resolve("repository").toFile()
        def ivyCache = Paths.get(userHome.toURI()).resolve(".ivy2").resolve("cache").toFile()

        FileUtils.deleteDirectory mavenCache
        FileUtils.deleteDirectory ivyCache
    }

    def validateOutput(output) {
        assert output.contains("success")
        assert !output.contains("FAILURE")
    }

    def runCommand(targetPath, strList) {
        assert (strList instanceof String || (strList instanceof List && strList.each { it instanceof String }))

        def output = new StringBuffer()

        def path = targetPath.toFile()

        println "Execute command[s]: "
        if (strList instanceof List) {
            strList.each { println "${it} " }
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
}