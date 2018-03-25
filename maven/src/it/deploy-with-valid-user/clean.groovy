import org.carlspring.strongbox.client.ArtifactClient

def client = new ArtifactClient();

def path = "/storages/storage0/releases/org/carlspring/maven/test-project/1.0.6"

if (client.pathExists(path))
{
    client.delete("storage0", "releases", "org/carlspring/maven/test-project/1.0.6");
}
