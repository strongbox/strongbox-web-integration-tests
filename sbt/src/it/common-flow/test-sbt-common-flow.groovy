import org.apache.commons.io.FileUtils

import java.nio.file.Path
import java.nio.file.Paths

def baseScript = new GroovyScriptEngine("$project.basedir/src/it").with {
    loadScriptByName('BaseSbtWebIntegrationTest.groovy')
}
this.metaClass.mixin baseScript

println "Test test-sbt-common-flow.groovy" + "\n\n"

def static userHome() {
    return Paths.get(System.getProperty("user.home")) as Path
}

def static sbtCache()  {
    return userHome().resolve(".cache")
                     .resolve("coursier")
                     .resolve("v1")
                     .resolve("http")
                     .resolve("localhost")
}

def static getLogbackCoreDirectory() {
    return sbtCache().resolve("ch")
                     .resolve("qos")
                     .resolve("logback")
                     .resolve("logback-core")
                     .toFile()
}

def static getLogbackJarFile() {
    return getLogbackCoreDirectory().toPath()
                                    .resolve("1.2.3")
                                    .resolve("logback-core-1.2.3.jar")
                                    .toFile()
}

def static getCommonsHttpDirectory() {
    return sbtCache().resolve("org")
                     .resolve("carlspring")
                     .resolve("commons")
                     .resolve("commons-http")
                     .toFile()
}

def static getCommonsHttpJarFile() {
    return getCommonsHttpDirectory().toPath().resolve("1.3").resolve("commons-http-1.3.jar").toFile()
}

if (getLogbackCoreDirectory().exists())
    FileUtils.deleteDirectory getLogbackCoreDirectory()

if (getCommonsHttpDirectory().exists())
    FileUtils.deleteDirectory getCommonsHttpDirectory()

assert !getLogbackCoreDirectory().exists()
assert !getCommonsHttpDirectory().exists()

def executionPath = getExecutionPath(project).resolve('common-flow')

// Keep -no-colors so it outputs the downloaded artifacts in the log.
validateOutput runCommand(executionPath, "sbt -no-colors compile")
validateOutput runCommand(executionPath, "sbt -no-colors package")
validateOutput runCommand(executionPath, "sbt -no-colors publish")

assert getLogbackCoreDirectory().exists()
assert getLogbackJarFile().exists()

assert getCommonsHttpDirectory().exists()
assert getCommonsHttpJarFile().exists()
