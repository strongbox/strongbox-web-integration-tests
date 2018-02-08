import org.carlspring.commons.io.RandomInputStream
import org.carlspring.strongbox.client.ArtifactClient


def client = ArtifactClient.getTestInstance()

System.out.println()
System.out.println()
System.out.println(client.getHost())
System.out.println(client.getUsername())
System.out.println(client.getPassword())
System.out.println()
System.out.println()

def is = new RandomInputStream(536870912L)

def url = client.getContextBaseUrl() + "/storages/storage0/snapshots/" +
          "org/carlspring/maven/large-artifact/1.0-SNAPSHOT/large-artifact-1.0-SNAPSHOT.jar"

client.deployFile(is, url, "large-artifact-1.0-SNAPSHOT.jar")

def response = client.pathExists("/storages/storage0/snapshots/" +
                                 "org/carlspring/maven/large-artifact/1.0-SNAPSHOT/large-artifact-1.0-SNAPSHOT.jar")

System.out.println()
System.out.println()
System.out.println("Path exists? " + response.toString())
System.out.println()
System.out.println()

return response
