import org.carlspring.strongbox.client.RestClient
import org.carlspring.strongbox.forms.configuration.RepositoryForm
import org.carlspring.strongbox.providers.layout.Maven2LayoutProvider
import org.carlspring.strongbox.providers.storage.FileSystemStorageProvider
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

    def repositoryId = "releases-with-redeployment"

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
        repository.setAllowsRedeployment(true)
        repository.setStorageProvider(FileSystemStorageProvider.ALIAS)
        repository.setType(RepositoryTypeEnum.HOSTED.type)
        repository.setStatus(RepositoryStatusEnum.IN_SERVICE.status)

        client.addRepository(repository, storage.getId())

        System.out.println()
        System.out.println()
        System.out.println("Repository '" + repositoryId + "' successfully created!")
        System.out.println()
        System.out.println()

        def s = configuration.getStorage("storage0")
        def r = client.getRepository("storage0", repositoryId)

        System.out.println()
        System.out.println()
        System.out.println("storage0: " + s.toString())
        System.out.println("releases-with-redeployment: " + r.toString())
        System.out.println()
        System.out.println()
    }

}
catch (Exception e)
{
    e.printStackTrace()
    return false
}
