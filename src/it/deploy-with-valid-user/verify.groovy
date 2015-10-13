import org.carlspring.maven.commons.util.ArtifactUtils
import org.carlspring.strongbox.client.ArtifactClient

System.out.print();
System.out.print("**********************************");
System.out.print(System.getProperty("strongbox.host"));
System.out.print("**********************************");
System.out.print();

def artifact = ArtifactUtils.getArtifactFromGAV("org.carlspring.maven:test-project:1.0.6");

def client = new ArtifactClient();

return client.artifactExists(artifact, "storage0", "releases");
