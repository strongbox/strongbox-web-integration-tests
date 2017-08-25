import org.carlspring.maven.commons.util.ArtifactUtils
import org.carlspring.strongbox.client.ArtifactClient
import org.carlspring.strongbox.client.RestClient
import org.carlspring.strongbox.storage.repository.Repository
import org.carlspring.strongbox.storage.repository.RepositoryLayoutEnum
import org.carlspring.strongbox.storage.repository.RepositoryPolicyEnum


def artifact = ArtifactUtils.getArtifactFromGAV("org.carlspring.maven:test-project:1.0.3");
def artifactClient = ArtifactClient.testInstance;
def restClient = RestClient.getTestInstanceLoggedInAsAdmin()

System.out.println()
System.out.println()
System.out.println("Host name: " + restClient.getHost())
System.out.println("Username:  " + restClient.getUsername())
System.out.println("Password:  " + restClient.getPassword())
System.out.println()
System.out.println()

try
{
    def configuration = restClient.getConfiguration()

    System.out.println()
    System.out.println("configuration = " + configuration)
    System.out.println()

    def storage = configuration.getStorage("storage0")

    System.out.println()
    System.out.println("storage = " + storage.toString())
    System.out.println()

    def repositoryId = "releases-with-trash"

    if (storage.getRepository(repositoryId) == null)
    {

        System.out.println()
        System.out.println()
        System.out.println("Creating a new repository with ID '" + repositoryId + "'...")
        System.out.println()
        System.out.println()

        // Create the test repository:
        def repository = new Repository(repositoryId)
        repository.setLayout(RepositoryLayoutEnum.MAVEN_2.layout)
        repository.setPolicy(RepositoryPolicyEnum.RELEASE.policy)
        repository.setTrashEnabled(true)
        repository.setStorage(storage)

        restClient.addRepository(repository)

        System.out.println()
        System.out.println()
        System.out.println("Repository '" + repositoryId + "' successfully created!")
        System.out.println()
        System.out.println()
    } else {
        if (artifactClient.artifactExists(artifact, "storage0", "releases-with-trash"))
        {
            artifactClient.delete("storage0", "releases-with-trash", "org/carlspring/maven/test-project/1.0.3");
        }
        artifactClient.deleteTrash("storage0", "releases-with-trash");
    }
    
}
catch (Exception e)
{
    e.printStackTrace()
    return false
}