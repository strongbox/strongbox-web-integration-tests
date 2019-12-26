import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.Paths


def baseScript = new GroovyScriptEngine("$project.basedir/src/it").with
{ loadScriptByName('PypiIntegrationTest.groovy') }

this.metaClass.mixin baseScript

println "Pypi Integration Test started...."
println "Executing test-pypi-common-flows.groovy"

def pipCommandPrefix
def packageUploadCommand
def packageBuildCommand

//TODO :: Check why --config-file not picking .pypirc file
def repositoryUrl = "http://localhost:48080/storages/storage-pypi/pypi-releases"
def username = "admin"
def password = "password"

// Determine OS and appropriate commands
if (System.getProperty("os.name").toLowerCase().contains("windows")) {
    pipCommandPrefix = "cmd /c pip3"
    packageUploadCommand = "cmd /c python3 -m twine upload --repository-url " + repositoryUrl + " --username " + username + " --password " + password+ " dist/*"
    packageBuildCommand = "cmd /c python3 setup.py sdist bdist_wheel"
} else {
    pipCommandPrefix = "pip3"
    packageUploadCommand = "python3 -m twine upload --repository-url " + repositoryUrl + " --username " + username + " --password " + password+ " dist/*"
    packageBuildCommand = "python3 setup.py sdist bdist_wheel"
}

def executionBasePath = getExecutionBasePath(project)
println "Base path is " + executionBasePath + "\n"

// Resolve path for package build/upload using pip
def uploadPackageDirectoryPath = executionBasePath.resolve("pip-package-upload-test")

// Build package to be uploaded
runCommand(uploadPackageDirectoryPath, packageBuildCommand)

// Assert directory with package created
Path packageDirectoryPath = uploadPackageDirectoryPath.resolve("dist");
boolean pathExists =
        Files.isDirectory(packageDirectoryPath,
        LinkOption.NOFOLLOW_LINKS);

assert pathExists == true

// upload python package using pip command and assert sucesss
runCommand(uploadPackageDirectoryPath, packageUploadCommand)


println "Pypi Integration Test completed.!!"