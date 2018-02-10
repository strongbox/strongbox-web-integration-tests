import org.carlspring.maven.commons.util.ArtifactUtils
import org.carlspring.strongbox.client.ArtifactClient


def artifact = ArtifactUtils.getArtifactFromGAV("org.carlspring.maven:test-project:1.0.5");

def client = ArtifactClient.testInstance;

client.deleteArtifact(artifact, "storage0", "releases-with-trash");

return !client.pathExists("storages/storage0/releases-with-trash/org/carlspring/maven/test-project/1.0.5/test-project-1.0.5.jar") &&
       !client.pathExists("storages/storage0/releases-with-trash/org/carlspring/maven/test-project/1.0.5/test-project-1.0.5.jar.md5") &&
       client.pathExists("storages/storage0/releases-with-trash/.trash/org/carlspring/maven/test-project/1.0.5/test-project-1.0.5.jar") &&
       client.pathExists("storages/storage0/releases-with-trash/.trash/org/carlspring/maven/test-project/1.0.5/test-project-1.0.5.jar.md5");
