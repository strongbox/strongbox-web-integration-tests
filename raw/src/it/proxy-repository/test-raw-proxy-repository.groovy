import org.carlspring.strongbox.client.RestClient
import org.carlspring.strongbox.util.MessageDigestUtils

import java.nio.file.Paths

// Create a raw, proxy (remote) repo to https://services.gradle.org/distributions/
// Then download a gradle zip, and the shasum
// Finally, calculate a shasum for the zip and check that it matches the shasum that we downloaded

def baseScript = new GroovyScriptEngine("$project.basedir/src/it" ).with {
    loadScriptByName('RawIntegrationTest.groovy')
}
this.metaClass.mixin baseScript

println "Test test-raw-proxy-repository.groovy" + "\n\n"

def repositoryId = 'raw-gradle-distributions-proxy'
def storageId = 'storage-raw-proxies'
def remoteRepositoryUrl = 'https://services.gradle.org/distributions'
def artifactName = 'gradle-5.3-bin.zip'

// ---------------------------------------------------------------------------------------------------------------------
// Create a raw repo that proxies the specified remote

def client = RestClient.getTestInstanceLoggedInAsAdmin()

println()
println("Host name: " + client.getHost())
println("Username:  " + client.getUsername())
println()

println()
println("Creating a new storage with ID '" + storageId + "'...")
println()
createStorage(client, storageId)

println()
println("New storage = " + client.getConfiguration().getStorage(storageId))
println()

println()
println("Creating a new repository with ID '" + repositoryId + "'...")
println()
createRemoteRepo(client, repositoryId, storageId, remoteRepositoryUrl)

println()
println("New storage with new repo = " + client.getConfiguration().getStorage(storageId))
println()

def repository = client.getConfiguration().getStorage(storageId).getRepository(repositoryId)
if (repository != null) {
    println()
    println("SUCCESS: '" + repository + "' successfully created")
    println()
}
else
{
    throw new RuntimeException("Failed to create repository " + repositoryId)
}

// ---------------------------------------------------------------------------------------------------------------------
// Download the artifact via the proxy repo, verify the checksums

def address = client.getContextBaseUrl() + "/storages/$storageId/$repositoryId/$artifactName"
def addressShaSum = address + '.sha256'

File artifactShaSum = downloadArtifact(addressShaSum)
downloadedChecksum = artifactShaSum.getText('UTF-8')

File artifact = downloadArtifact(address)
calculatedChecksum = MessageDigestUtils.calculateChecksum(Paths.get(artifact.toString()), 'SHA-256')

println()
println("Downloaded sha256sum is " + downloadedChecksum)
println("Calculated checksum is " + calculatedChecksum)
println()

assert calculatedChecksum == downloadedChecksum : "The checksum calculated does not match the downloaded checksum!"
println()
println("SUCCESS: downloaded $address and the artifact matches the expected checksum")
println()

