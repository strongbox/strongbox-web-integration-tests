import org.carlspring.strongbox.client.ArtifactClient


def client = ArtifactClient.testInstance;

return client.pathExists("storages/storage0/snapshots/org/carlspring/maven/test-project/1.0.8-SNAPSHOT/maven-metadata.xml");
