import org.apache.commons.io.FileUtils

import java.nio.file.Paths

def baseScript = new GroovyScriptEngine("$project.basedir/src/it").with {
    loadScriptByName('BaseSbtWebIntegrationTest.groovy')
}
this.metaClass.mixin baseScript

println "Test test-sbt-common-flow.groovy" + "\n\n"

def static getLogbackCoreDirectory() {
    def userHome = new File(System.getProperty("user.home")) as File
    def ivyCache = Paths.get(userHome.toURI()).resolve(".ivy2").resolve("cache")
    return ivyCache.resolve("ch.qos.logback").resolve("logback-core").toFile()
}

def static getLogbackJarFile() {
    return getLogbackCoreDirectory().toPath().resolve("jars")
            .resolve("logback-core-1.2.3.jar").toFile()
}

if (getLogbackCoreDirectory().exists())
    FileUtils.deleteDirectory getLogbackCoreDirectory()

assert !getLogbackCoreDirectory().exists()

def executionPath = getExecutionPath(project).resolve('common-flow')

// Keep -no-colors so it outputs the downloaded artifacts in the log.
validateOutput runCommand(executionPath, "sbt -no-colors compile")
validateOutput runCommand(executionPath, "sbt -no-colors assembly")
validateOutput runCommand(executionPath, "sbt -no-colors publish")

assert getLogbackCoreDirectory().exists()
assert getLogbackJarFile().exists()
