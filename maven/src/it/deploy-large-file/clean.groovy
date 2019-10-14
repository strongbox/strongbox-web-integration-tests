GroovyShell shell = new GroovyShell()
def utils = shell.parse(new File('/workspace/utils.groovy'))

import org.carlspring.commons.io.RandomInputStream
import org.carlspring.strongbox.client.ArtifactClient


def client = ArtifactClient.getTestInstance()
utils.format(client.getHost(), client.getUsername(), client.getPassword())

def is = new RandomInputStream(1073741824L)

def url = client.getContextBaseUrl() + "/storages/storage0/snapshots/" +
  "org/carlspring/maven/large-artifact/1.0-SNAPSHOT/large-artifact-1.0-SNAPSHOT.jar"

client.deployFile(is, url, "large-artifact-1.0-SNAPSHOT.jar")

def response = client.pathExists("/storages/storage0/snapshots/" +
  "org/carlspring/maven/large-artifact/1.0-SNAPSHOT/large-artifact-1.0-SNAPSHOT.jar")
utils.format("File size: " + (is.getLength() / 1048576L) + "MB",
  "Path exists? " + response.toString())

return response
