import org.carlspring.strongbox.client.RestClient
import org.carlspring.strongbox.forms.configuration.RepositoryForm
import org.carlspring.strongbox.providers.datastore.StorageProviderEnum
import org.carlspring.strongbox.providers.layout.Maven2LayoutProvider
import org.carlspring.strongbox.storage.repository.RepositoryPolicyEnum
import org.carlspring.strongbox.storage.repository.RepositoryStatusEnum
import org.carlspring.strongbox.storage.repository.RepositoryTypeEnum

def client = RestClient.getTestInstanceLoggedInAsAdmin()

System.out.println()
System.out.println()
System.out.println("Host name: " + client.getHost())
System.out.println("Username:  " + client.getUsername())
System.out.println("Password:  " + client.getPassword())
System.out.println()
System.out.println()

try
{
    def configuration = client.getConfiguration()

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
        def repository = new RepositoryForm()
        repository.setId(repositoryId)
        repository.setLayout(Maven2LayoutProvider.ALIAS)
        repository.setPolicy(RepositoryPolicyEnum.RELEASE.policy)
        repository.setTrashEnabled(true)
        repository.setImplementation(StorageProviderEnum.FILESYSTEM.description)
        repository.setType(RepositoryTypeEnum.HOSTED.type)
        repository.setStatus(RepositoryStatusEnum.IN_SERVICE.status)

        client.addRepository(repository, storage.getId())

        System.out.println()
        System.out.println()
        System.out.println("Repository '" + repositoryId + "' successfully created!")
        System.out.println()
        System.out.println()
    }
}
catch (Exception e)
{
    e.printStackTrace()
    return false
}

def path = "/storages/storage0/releases-with-trash/org/carlspring/maven/test-project/1.0.5"

if (client.pathExists(path))
{
    client.delete("storage0", "releases-with-trash", "org/carlspring/maven/test-project/1.0.5", true)
}

client.deleteTrash("storage0", "releases-with-trash")

