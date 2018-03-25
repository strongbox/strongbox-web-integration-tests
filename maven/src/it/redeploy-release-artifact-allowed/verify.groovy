import org.carlspring.strongbox.client.ArtifactClient

def client = ArtifactClient.testInstance;

def path = "/storages/storage0/releases-with-redeployment/org/carlspring/maven/test-project/1.0.9/test-project-1.0.9.jar"

return client.pathExists(path);
