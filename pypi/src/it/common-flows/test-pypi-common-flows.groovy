import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.Paths


def baseScript = new GroovyScriptEngine("$project.basedir/src/it").with
{ loadScriptByName('PypiIntegrationTest.groovy') }

this.metaClass.mixin baseScript

println "Pypi Integration Test started...."
println "Executing test-pypi-common-flows.groovy"

def pipInstallPackageCommand
def packageUploadCommand
def packageBuildCommand
def pipDownloadPackageCommand

//TODO :: Check why --config-file not picking .pypirc file
def repositoryUrl = "http://localhost:48080/storages/storage-pypi/pypi-releases"
def username = "admin"
def password = "password"

// Determine OS and appropriate commands
if (System.getProperty("os.name").toLowerCase().contains("windows")) {
    pipInstallPackageCommand = "cmd /c pip3 install --extra-index-url " + repositoryUrl
    pipDownloadPackageCommand = "cmd /c pip3 download --extra-index-url " + repositoryUrl
    packageUploadCommand = "cmd /c python3 -m twine upload --repository-url " + repositoryUrl + " --username " + username + " --password " + password+ " dist/*"
    packageBuildCommand = "cmd /c python3 setup.py sdist bdist_wheel"
} else {
    pipInstallPackageCommand = "pip3 install --extra-index-url " + repositoryUrl
    pipDownloadPackageCommand="pip3 download --extra-index-url " + repositoryUrl
    packageUploadCommand = "python3 -m twine upload --repository-url " + repositoryUrl + " --username " + username + " --password " + password+ " dist/*"
    packageBuildCommand = "python3 setup.py sdist bdist_wheel"
}

def executionBasePath = getExecutionBasePath(project)
println "Base path is " + executionBasePath + "\n"

// Resolve path for package build/upload using pip
def uploadPackageDirectoryPath = executionBasePath.resolve("pip-package-upload-test")

// Build package to be uploaded
runCommand(uploadPackageDirectoryPath, packageBuildCommand)

// Assert directory exists for package uploaded
Path packageDirectoryPath = uploadPackageDirectoryPath.resolve("dist");
boolean pathExists =
        Files.isDirectory(packageDirectoryPath,
        LinkOption.NOFOLLOW_LINKS);

assert pathExists == true

// upload python package using pip command
runCommand(uploadPackageDirectoryPath, packageUploadCommand)

def commandOutput
// Install uploaded python package using pip command and assert success
def uploadedPackageName = "pip_upload_test"
commandOutput = runCommand(uploadPackageDirectoryPath, pipInstallPackageCommand + " " + uploadedPackageName)
assert commandOutput.contains("Successfully installed " + uploadedPackageName.replace("_" , "-") + "-1.0")

// Resolve path for packages with dependency build/upload using pip
def uploadPackageWithDependencyDirectoryPath = executionBasePath.resolve("pip-package-with-dependency-upload-test")
def uploadDependentPackageDirectoryPath = executionBasePath.resolve("pip-dependent-package-upload-test")

// Build dependent package to be uploaded
runCommand(uploadDependentPackageDirectoryPath, packageBuildCommand)
// Build package with dependency to be uploaded
runCommand(uploadPackageWithDependencyDirectoryPath, packageBuildCommand)

// Assert directory for package with dependency created
Path packageWithDependencyDirectoryPath = uploadPackageWithDependencyDirectoryPath.resolve("dist");
boolean pathForDependencyPackageExists =
        Files.isDirectory(packageWithDependencyDirectoryPath,
        LinkOption.NOFOLLOW_LINKS);

assert pathForDependencyPackageExists == true

// Assert directory for dependent package created
Path dependentPackageDirectoryPath = uploadDependentPackageDirectoryPath.resolve("dist");
boolean pathForDependentPackageExists =
        Files.isDirectory(dependentPackageDirectoryPath,
        LinkOption.NOFOLLOW_LINKS);

assert pathForDependentPackageExists == true


// upload dependent python package using pip command
runCommand(uploadDependentPackageDirectoryPath, packageUploadCommand)
// upload python package with dependency using pip command
runCommand(uploadPackageWithDependencyDirectoryPath, packageUploadCommand)

// Install uploaded python package with dependency using pip command and assert success
def uploadedPackageWithDependency = "pip_package_with_dependency"
def uploadedDepdendentPackage = "pip-dependent-package"
commandOutput = runCommand(uploadPackageWithDependencyDirectoryPath, pipInstallPackageCommand + " " + uploadedPackageWithDependency)
assert commandOutput.contains("Successfully installed ")

// Uninstall installed packages to execute pip download command
runCommand(executionBasePath, "pip3 uninstall --yes " + uploadedPackageName)
runCommand(executionBasePath, "pip3 uninstall --yes " + uploadedPackageWithDependency)
runCommand(executionBasePath, "pip3 uninstall --yes " + uploadedDepdendentPackage)


// execute pip download commands
commandOutput = runCommand(executionBasePath, pipDownloadPackageCommand + " pip-upload-test")
assert  commandOutput.contains("Successfully downloaded pip-upload-test")
commandOutput = runCommand(executionBasePath, pipDownloadPackageCommand + " pip-package-with-dependency")
assert  commandOutput.contains("Successfully downloaded ")

println "Pypi Integration Test completed.!!"