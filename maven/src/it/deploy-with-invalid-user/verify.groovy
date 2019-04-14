import org.carlspring.strongbox.client.ArtifactClient


def client = ArtifactClient.getTestInstance()

System.out.println()
System.out.println()
System.out.println(client.getHost())
System.out.println(client.getUsername())
System.out.println(client.getPassword())
System.out.println()
System.out.println()

def response = client.pathExists("/storages/storage0/snapshots/org/carlspring/maven/test-project/1.0-SNAPSHOT/" +
                                 "test-project-1.0-SNAPSHOT.jar")

System.out.println()
System.out.println()
System.out.println("Path exists? " + response.toString())
System.out.println()
System.out.println()

return !response
