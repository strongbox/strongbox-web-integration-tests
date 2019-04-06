import org.carlspring.strongbox.client.ArtifactClient

def client = ArtifactClient.testInstanceLoggedInAsAdmin;

def path = "/storages/storage0/releases/org/carlspring/maven/test-project/1.0.6/test-project-1.0.6.jar"

return client.pathExists(path);
