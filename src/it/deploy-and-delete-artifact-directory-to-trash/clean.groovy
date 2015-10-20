import org.carlspring.maven.commons.util.ArtifactUtils
import org.carlspring.strongbox.client.ArtifactClient


def artifact = ArtifactUtils.getArtifactFromGAV("org.carlspring.maven:test-project:1.0.3");

def client = ArtifactClient.testInstance;

if (client.artifactExists(artifact, "storage0", "releases-with-trash"))
{
    client.delete("storage0", "releases-with-trash", "org/carlspring/maven/test-project/1.0.3");
}
