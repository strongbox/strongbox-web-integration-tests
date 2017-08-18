import org.carlspring.strongbox.client.RestClient

System.out.println("Clean [nuget-it]")

def client = RestClient.getTestInstanceLoggedInAsAdmin()

System.out.println()
System.out.println()
System.out.println("Host name: " + client.getHost())
System.out.println("Username:  " + client.getUsername())
System.out.println("Password:  " + client.getPassword())
System.out.println()
System.out.println()


def nugetApiKey = client.generateUserSecurityToken();
System.out.println("ApiKey:  " + nugetApiKey)