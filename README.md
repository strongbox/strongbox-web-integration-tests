strongbox-web-integration-tests
===
[![Master Build Status](https://dev.carlspring.org/jenkins/buildStatus/icon?job=strongbox/strongbox-web-integration-tests/master)](https://dev.carlspring.org/jenkins/job/strongbox/job/strongbox-web-integration-tests/job/master/)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/strongbox/strongbox?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## Introduction

This project contains various integration tests for the Strongbox project and the different repository layout formats supported by it. The following build tools have Groovy-based integration tests:
* Gradle
* Maven (these tests are using the `maven-invoker-plugin` that starts Maven during the build and runs various build operations)
* NPM
* NuGet
* SBT

## Docker

To make everybody's lives easier we have created docker images for all of the tools we are testing with so that 
you don't have to go through the process of installing all of the by hand.

For this to work - you need to have installed `docker` and `docker-compose` on your machine. 
Afterwards, just go to the respective sub-project (i.e. `cd maven`) and execute `docker-compose up`.

#### On windows

If you are using windows you need to install all of the [tools](#testing) below and make them available in your `PATH`.
[Nuget](https://dist.nuget.org/win-x86-commandline/v3.4.4/nuget.exe) requires `.Net Framework v4` and you need to 
set the `NUGET_V3_EXEC` environment variable with value `c:/path/to/nuget.exe`.

#### On linux

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
