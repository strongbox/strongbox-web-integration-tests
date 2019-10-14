GroovyShell shell = new GroovyShell()
def utils = shell.parse(new File('/workspace/utils.groovy'))

import org.carlspring.strongbox.client.ArtifactClient

import javax.ws.rs.core.MediaType

def client = ArtifactClient.getTestInstance()
utils.format(client.getHost(), client.getUsername(), client.getPassword())


def response = client.pathExists("/storages/storage0/snapshots/" +
  "org/carlspring/maven/large-artifact/1.0-SNAPSHOT/large-artifact-1.0-SNAPSHOT.jar")
utils.format("Path exists? " + response.toString())

def is = client.getResource("/storages/storage0/snapshots/" +
  "org/carlspring/maven/large-artifact/1.0-SNAPSHOT/large-artifact-1.0-SNAPSHOT.jar")

def fos = new FileOutputStream("target/large-artifact-1.0-SNAPSHOT.jar")

def readLength
def bytes = new byte[4096]
while ((readLength = is.read(bytes, 0, bytes.length)) != -1)
{
    fos.write(bytes, 0, readLength)
    fos.flush()
}

def file = new File("target/large-artifact-1.0-SNAPSHOT.jar");
utils.format("File size: " + (file.length() / 1048576L) + "MB")

return file.length() == 1073741824L
