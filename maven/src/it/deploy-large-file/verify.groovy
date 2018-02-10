import org.carlspring.strongbox.client.ArtifactClient

import javax.ws.rs.core.MediaType

return true

def client = ArtifactClient.getTestInstance()

System.out.println()
System.out.println()
System.out.println(client.getHost())
System.out.println(client.getUsername())
System.out.println(client.getPassword())
System.out.println()
System.out.println()

def response = client.pathExists("/storages/storage0/snapshots/" +
                                 "org/carlspring/maven/large-artifact/1.0-SNAPSHOT/large-artifact-1.0-SNAPSHOT.jar")

System.out.println()
System.out.println()
System.out.println("Path exists? " + response.toString())
System.out.println()
System.out.println()

def headers = new LinkedHashMap<String, String>()
headers.put(HEADER_NAME_USER_AGENT, HEADER_VALUE_MAVEN)

def is = client.getResource("/storages/storage0/snapshots/" +
                            "org/carlspring/maven/large-artifact/1.0-SNAPSHOT/large-artifact-1.0-SNAPSHOT.jar",
                            0,
                            MediaType.APPLICATION_OCTET_STREAM_TYPE,
                            headers)

def fos = new FileOutputStream("target/large-artifact-1.0-SNAPSHOT.jar")

def readLength
def bytes = new byte[4096]
while ((readLength = is.read(bytes, 0, bytes.length)) != -1)
{
    // Write the artifact
    fos.write(bytes, 0, readLength)
    fos.flush()
}

def file = new File("target/large-artifact-1.0-SNAPSHOT.jar");

return file.length() == 536870912L
