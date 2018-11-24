strongbox-web-integration-tests
===
[![Master Build Status](https://dev.carlspring.org/jenkins/buildStatus/icon?job=strongbox/strongbox-web-integration-tests/master)](https://dev.carlspring.org/jenkins/job/strongbox/job/strongbox-web-integration-tests/job/master/)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/strongbox/strongbox?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# Introduction

This project contains various integration tests for the Strongbox project and the different repository layout formats supported by it. The following build tools have Groovy-based integration tests:
* Gradle
* Maven (these tests are using the `maven-invoker-plugin` that starts Maven during the build and runs various build operations)
* NPM
* NuGet
* SBT

Each of the modules in the project is built as a Maven project and:
1. Starts the Strongbox Spring Boot application
2. Runs the code against the Strongbox Spring Boot application

The code used to start the Spring Boot application is either retrieved from our repository, or, if you've made local modifications, it uses them directly from your local Maven cache.

# Building and Testing

## Docker

To make everybody's lives easier, we've created Docker images for all of the tools we are testing against, so that 
you don't have to go through the process of installing all of them manually.

For this to work, you need to have installed `docker` and `docker-compose` on your machine.

Afterwards, just go to the respective sub-project (i.e. `cd maven`) and execute `docker-compose up`.

### Using `docker-compose`

You can run all the tests like this:

```
for tool in `ls -ap | grep \/ | grep -v -e "\."`; do cd ${tool} && docker-compose up & cd -; done
```

Alternatively, enter the respective tool’s directory and just do:
```
docker-compose up
```

This will automatically build the code after spinning up a Docker container and then switch it off when it's done.

## On Windows

If you are using windows you need to install all of the [tools](#testing) below and make them available in your `PATH`.
[Nuget](https://dist.nuget.org/win-x86-commandline/v3.4.4/nuget.exe) requires `.Net Framework v4` and you need to 
set the `NUGET_V3_EXEC` environment variable with value `c:/path/to/nuget.exe`.

## On Linux

If you are using linux you need to install all of the [tools](#testing) below and make them available in your `PATH`.
To run Nuget tests here you will need `mono` to be installed. 
There were many problems with the compatibility of `nuget.exe` and `mono` versions, and the sutable combination is the following:

- `Mono JIT compiler version 5.2.0.215 (tarball Mon Aug 14 15:46:23 UTC 2017)`
- `nuget.exe` v3.4.4 ( [link](https://dist.nuget.org/win-x86-commandline/v3.4.4/nuget.exe) )
- `NUGET_V3_EXEC` need to be set with value `mono \path\to\nuget.exe`

<a href="#testing"></a>

## Testing

We are using `Maven 3.x` and `jdk 1.8` to execute the tests for the respective tool.

* [Maven 3.x](./maven)
* [SBT 1.1.0](./sbt)
* [NuGet 3.4.4](./nuget)
* [NPM](./npm)
