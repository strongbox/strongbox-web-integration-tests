import org.carlspring.strongbox.client.ArtifactClient

def client = ArtifactClient.testInstanceLoggedInAsAdmin;

def path = "/storages/storage0/snapshots/org/carlspring/maven/test-project/1.0.8"

if (!client.pathExists(path))
{
    System.out.println("The remote (SNAPSHOT) repository successfully declined the deployment of a release artifact.")

    return true;
}

return false;
