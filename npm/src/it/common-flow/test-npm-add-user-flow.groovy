def baseScript = new GroovyScriptEngine("$project.basedir/src/it").with
{
    loadScriptByName('NpmIntegrationTest.groovy')
}

this.metaClass.mixin baseScript

println "Test test-npm-add-user-flow.groovy" + "\n\n"

def npmExec
def strongboxUsername = "admin"
def nonStrongboxUsername = "notarealuser"
def successMsg = "logged in as "

// Determine OS and appropriate commands
if (System.getProperty("os.name").toLowerCase().contains("windows"))
{
    npmExec = "cmd /c npm"
}
else
{
    npmExec = "npm"
}

def executionPath = getExecutionPathRoot(project)
def adduserDependencyRoot = executionPath.resolve("npm-add-user-test")

def strongboxUser = new Credentials(strongboxUsername,"password","a@a.com")
def nonStrongboxUser = new Credentials(nonStrongboxUsername,"zipZapZooey","derp@derpy.com")

//test that npm adduser works for strongbox user
def proc = createNpmAdduserProcess(npmExec, adduserDependencyRoot.toString())
assert loginToNpm(proc, strongboxUser, successMsg+strongboxUsername)

proc.destroy()

//test that npm adduser does not work for nonstrongbox user
proc = createNpmAdduserProcess(npmExec, adduserDependencyRoot.toString())
assert !loginToNpm(proc, nonStrongboxUser, successMsg+nonStrongboxUsername)

proc.destroy()

Process createNpmAdduserProcess(String npmExec, String directory)
{
    def processBuilder = new ProcessBuilder(npmExec, "adduser")
    processBuilder.directory(new File(directory))
    return processBuilder.start()
}

boolean loginToNpm(Process proc, Credentials creds, String returnMessage)
{
    def npmPromptHandler = new NpmPromptHandler(proc.getInputStream(), proc.getOutputStream(), creds)

    npmPromptHandler.start()

    synchronized (npmPromptHandler)
    {
        npmPromptHandler.wait()
    }

    return npmPromptHandler.getReturnMessage()
                           .toString()
                           .toLowerCase()
                           .contains(returnMessage)
}

class Credentials
{
    String username
    String password
    String email

    Credentials(String username, String password, String email)
    {
        this.username = username
        this.password = password
        this.email = email
    }

    String getUsername()
    {
        return username
    }

    String getPassword()
    {
        return password
    }

    String getEmail()
    {
        return email
    }
}

class NpmPromptHandler extends Thread
{
    InputStream input
    OutputStreamWriter output
    Credentials creds
    StringBuilder returnMessage

    NpmPromptHandler(InputStream input, OutputStream output, Credentials creds)
    {
        this.input = input
        this.output = new OutputStreamWriter(output)
        this.creds = creds
        returnMessage = new StringBuilder()
    }

    String getReturnMessage()
    {
        return returnMessage
    }

    @Override
    void run()
    {

        synchronized (this)
        {

            byte[] inputBuffer = new byte[1024]

            try
            {
                while (input.read(inputBuffer) != -1)
                {
                    def token = new String(inputBuffer)
                    println token

                    if (token.toLowerCase().contains("username:"))
                    {
                        output.write(creds.getUsername() + "\n")
                        output.flush()
                    }
                    else if (token.toLowerCase().contains("password:"))
                    {
                        output.write(creds.getPassword() + "\n")
                        output.flush()
                    }
                    else if (token.toLowerCase().contains("email:"))
                    {
                        output.write(creds.getEmail() + "\n")
                        output.flush()
                        output.close()
                    }
                    else
                    {
                        returnMessage.append(token)
                    }
                }
                input.close()
                notify()
            }
            catch (IOException e)
            {
                e.printStackTrace()
            }
        }
    }
}
