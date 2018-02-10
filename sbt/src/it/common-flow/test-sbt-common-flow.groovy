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

def targetPath = getTargetPath(project).resolve('common-flow')

validateOutput runCommand(targetPath, "sbt compile")
validateOutput runCommand(targetPath, "sbt assembly")
validateOutput runCommand(targetPath, "sbt publish")

assert getLogbackCoreDirectory().exists()
assert getLogbackJarFile().exists()
