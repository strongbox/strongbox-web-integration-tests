import org.carlspring.strongbox.client.RestClient

def baseScript = new GroovyScriptEngine("$project.basedir/src/it").with
{ 
    loadScriptByName('ChocoWebIntegrationTest.groovy') 
}

this.metaClass.mixin baseScript

println "\n"
println "Choco Integration Test started....\n"
println "Executing test-choco-auth-config-flows.groovy\n"


def API_KEY = getApiKey()
def REPO_URL = "http://localhost:48080/storages/storage-nuget/nuget-releases"

def executionBasePath = getExecutionBasePath(project)
println "Base path is " + executionBasePath + "\n"

// Add API_KEY to choco
def saveApiKeyCommand = "choco apikey -k " + API_KEY + " -s " + REPO_URL
commandOutput = runCommand(executionBasePath, saveApiKeyCommand)
def expectedOutput = "Added ApiKey for " + REPO_URL
assert commandOutput.contains(expectedOutput) || commandOutput.contains("Updated ApiKey for " + REPO_URL) 

// Add Strongbox repository as source to choco
def addSourceCommand = "choco source add -n=strongbox -s " + REPO_URL + " --priority=1"
commandOutput = runCommand(executionBasePath, addSourceCommand)
expectedOutput = "Added strongbox - " + REPO_URL + " (Priority 1)"
assert commandOutput.contains(expectedOutput) || commandOutput.contains("Updated strongbox - " + REPO_URL + " (Priority 1)")

def getApiKey()
{
    def client = RestClient.getTestInstanceLoggedInAsAdmin()

    println "Host name: " + client.getHost()
    println "Username:  " + client.getUsername()
    println "Password:  " + client.getPassword() + "\n\n"

    def apiKey = client.generateUserSecurityToken();
    println "ApiKey for choco: $apiKey\n\n"
    return apiKey
}