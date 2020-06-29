import org.carlspring.strongbox.client.RestClient
import org.carlspring.strongbox.forms.configuration.RemoteRepositoryForm
import org.carlspring.strongbox.forms.configuration.RepositoryForm
import org.carlspring.strongbox.forms.configuration.StorageForm
import org.carlspring.strongbox.providers.layout.RawLayoutProvider
import org.carlspring.strongbox.providers.storage.FileSystemStorageProvider
import org.carlspring.strongbox.storage.repository.RepositoryPolicyEnum
import org.carlspring.strongbox.storage.repository.RepositoryStatusEnum
import org.carlspring.strongbox.storage.repository.RepositoryTypeEnum

class RawIntegrationTest
{
    final String TEST_RESOURCES = "target/test-resources"

    def createStorage(RestClient client, storageId) {
        def storageForm = new StorageForm()
        storageForm.setId(storageId)
        client.addStorage(storageForm)
    }

    def createRemoteRepo(RestClient client, repositoryId, storageId, remoteRepositoryUrl) {
        def repositoryForm = new RepositoryForm()
        repositoryForm.setId(repositoryId)
        repositoryForm.setLayout(RawLayoutProvider.ALIAS)
        repositoryForm.setType(RepositoryTypeEnum.PROXY.getType())
        repositoryForm.setAllowsRedeployment(true)
        repositoryForm.setStorageProvider(FileSystemStorageProvider.ALIAS)
        repositoryForm.setStatus(RepositoryStatusEnum.IN_SERVICE.getStatus())
        repositoryForm.setPolicy(RepositoryPolicyEnum.MIXED.getPolicy())

        def remoteRepositoryForm = new RemoteRepositoryForm()
        remoteRepositoryForm.setUrl(remoteRepositoryUrl)
        remoteRepositoryForm.setCheckIntervalSeconds(5)

        repositoryForm.setRemoteRepository(remoteRepositoryForm)

        client.addRepository(repositoryForm, storageId)
    }

    def downloadArtifact(address) {
        def artifact = new File(TEST_RESOURCES, "${address.tokenize('/')[-1]}")
        if (!artifact.getParentFile().exists())
        {
            artifact.getParentFile().mkdirs()
        }

        println("Attempting to download $address")
        artifact.withOutputStream { out ->
            out << new URL(address).openStream()
        }
        println("Finished the download of $address")
        return artifact
    }
}
