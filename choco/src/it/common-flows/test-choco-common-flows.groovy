import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.util.ArrayList;
import java.util.List;

def baseScript = new GroovyScriptEngine("$project.basedir/src/it").with
{ 
    loadScriptByName('ChocoWebIntegrationTest.groovy') 
}

this.metaClass.mixin baseScript

println "Executing test-choco-common-flows.groovy\n"

def REPO_URL = "http://localhost:48080/storages/storage-nuget/nuget-releases"
def PACKAGE_NAME = "hello-chocolatey"

def executionBasePath = getExecutionBasePath(project)
println "Base path is " + executionBasePath + "\n"

// Create package to be pushed
def createPackageCommand = "choco new --name=" + PACKAGE_NAME + " --version=1.0.0 --force"
commandOutput = runCommand(executionBasePath, createPackageCommand)
def expectedOutput = "Successfully generated " + PACKAGE_NAME + " package specification files"
assert commandOutput.contains(expectedOutput)


// Assert directory exists for package
Path packageDirectoryPath = executionBasePath.resolve(PACKAGE_NAME);
boolean pathExists = Files.isDirectory(packageDirectoryPath,
                                       LinkOption.NOFOLLOW_LINKS);

assert pathExists == true


Path packageToolsDirectoryPath = executionBasePath.resolve(PACKAGE_NAME).resolve("tools");
pathExists = Files.isDirectory(packageToolsDirectoryPath,
                               LinkOption.NOFOLLOW_LINKS);

assert pathExists == true


Path chocolateyinstallFile = executionBasePath.resolve(PACKAGE_NAME).resolve("tools").resolve("chocolateyinstall.ps1");
pathExists = Files.isRegularFile(chocolateyinstallFile,
                                 LinkOption.NOFOLLOW_LINKS);

assert pathExists == true

Path nupsecFile = executionBasePath.resolve(PACKAGE_NAME).resolve("hello-chocolatey.nuspec");
pathExists = Files.isRegularFile(nupsecFile,
                                 LinkOption.NOFOLLOW_LINKS);

assert pathExists == true


// Add Content to file to be packaged and pushed
BufferedWriter writer = Files.newBufferedWriter(chocolateyinstallFile);
writer.write("");
writer.write("Write-Output 'Package would install here'");
writer.flush();


// Edit nuspec file
List <String> lines = Files.readAllLines(nupsecFile);
List <String> replacedText =  new ArrayList<>();
for(String line : lines)
{
    replacedText.add(line.replace("<file src=\"tools\\**\" target=\"tools\" />", "<file src=\"tools/**\" target=\"tools\" />"))
}
Files.write(nupsecFile, replacedText);


// Make Chocolatey package
def pushPackageDirectoryPath = executionBasePath.resolve(PACKAGE_NAME)
def makePackageCommand = "choco pack"
commandOutput = runCommand(pushPackageDirectoryPath, makePackageCommand)
expectedOutput = "Successfully created package"
assert commandOutput.contains(expectedOutput)


// Push package
def pushPackageCommand = "choco push --source " + REPO_URL + " --force"
commandOutput = runCommand(pushPackageDirectoryPath, pushPackageCommand)
expectedOutput = PACKAGE_NAME + " 1.0.0 was pushed successfully to " + REPO_URL
assert commandOutput.contains(expectedOutput)


// Search Package
def searchackageCommand = "choco search -s " + REPO_URL
commandOutput = runCommand(executionBasePath, searchackageCommand)
assert commandOutput.contains(PACKAGE_NAME + " 1.0.0")
assert commandOutput.contains("1 packages found.")