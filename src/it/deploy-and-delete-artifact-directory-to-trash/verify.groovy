import org.carlspring.strongbox.client.ArtifactClient


def client = ArtifactClient.testInstance;

client.delete("storage0", "releases-with-trash", "org/carlspring/maven/test-project/1.0.3");

return !client.pathExists("storages/storage0/releases-with-trash/org/carlspring/maven/test-project/1.0.3");
