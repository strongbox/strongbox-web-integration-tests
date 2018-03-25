import org.carlspring.strongbox.client.ArtifactClient

def client = ArtifactClient.testInstance;

client.delete("storage0", "releases-with-trash", "org/carlspring/maven/test-project/1.0.5");

return !client.pathExists("storages/storage0/releases-with-trash/org/carlspring/maven/test-project/1.0.5/test-project-1.0.5.jar") &&
       !client.pathExists("storages/storage0/releases-with-trash/org/carlspring/maven/test-project/1.0.5/test-project-1.0.5.jar.md5") &&
       client.pathExists("storages/storage0/releases-with-trash/.trash/org/carlspring/maven/test-project/1.0.5/test-project-1.0.5.jar") &&
       client.pathExists("storages/storage0/releases-with-trash/.trash/org/carlspring/maven/test-project/1.0.5/test-project-1.0.5.jar.md5");
