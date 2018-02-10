import org.carlspring.strongbox.artifact.generator.NugetPackageGenerator

def baseScript = new GroovyScriptEngine( "$project.basedir/src/it" ).with {
    loadScriptByName( 'BaseNugetWebIntegrationTest.groovy' )
  }
this.metaClass.mixin baseScript

println "Test test-nuget-common-flow.groovy" + "\n\n"

def targetPath = getTargetPath(project)
def baseDir = targetPath.toString()
def configPath = "$baseDir/NuGet.config"

def nugetExec = System.getenv("NUGET_V3_EXEC")
assert nugetExec?.trim() : "\"NUGET_V3_EXEC\" environment variable need to be set"

def packageId = "Org.Carlspring.Strongbox.Examples.Nuget.Mono" 
def packageVersion = "1.0.0"
def packageFileName = packageId + "." + packageVersion + ".nupkg";

def nugetPackageGenerator = new NugetPackageGenerator(baseDir);
nugetPackageGenerator.generateNugetPackage(packageId, packageVersion);

def storageUrl = getStorageUrl()

runCommand(targetPath, String.format(
    "$nugetExec push %s/%s/%s -ConfigFile %s",
    packageId,
    packageVersion,
    packageFileName,
    configPath))

output = runCommand(targetPath, String.format(
    "$nugetExec list Org.Carlspring -ConfigFile %s",
    configPath))
assert output.contains("Org.Carlspring.Strongbox.Examples.Nuget.Mono")

output = runCommand(targetPath, String.format(
    "$nugetExec delete Org.Carlspring.Strongbox.Examples.Nuget.Mono 1.0.0 -Source %s -NonInteractive -ConfigFile %s",
    storageUrl,
    configPath))
output = runCommand(targetPath, String.format(
    "$nugetExec list Org.Carlspring -ConfigFile %s",
    configPath))
assert !output.contains("Org.Carlspring.Strongbox.Examples.Nuget.Mono")