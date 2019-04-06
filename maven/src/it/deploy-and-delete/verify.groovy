import org.carlspring.strongbox.client.ArtifactClient

def client = ArtifactClient.testInstanceLoggedInAsAdmin;

client.delete("storage0", "releases","org/carlspring/maven/test-project/1.0.1/test-project-1.0.1.jar");

def artifactFile = new File("target/storages/storage0/releases/" +
                            "org/carlspring/maven/test-project/1.0.1/test-project-1.0.1.jar").getAbsoluteFile();

def path = "/storages/storage0/releases/org/carlspring/maven/test-project/1.0.1/test-project-1.0.1.jar"

return !client.pathExists(path) && !artifactFile.exists();
