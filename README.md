strongbox-web-integration-tests
===
[![Master Build Status](https://dev.carlspring.org/jenkins/buildStatus/icon?job=strongbox/strongbox-web-integration-tests/master)](https://dev.carlspring.org/jenkins/job/strongbox/job/strongbox-web-integration-tests/job/master/)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/strongbox/strongbox?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## Introduction

This project contains various integration tests for the Strongbox project and the different repository layout formats supported by it. The following build tools have Groovy-based integration tests:
* Gradle
* Maven (these tests are using the `maven-invoker-plugin` that starts Maven during the build and runs various build operations)
* NuGet
* SBT

## Maven tests

You will need the common environment to execute `Maven` Integration tests, this is only `Java 1.8` and `Maven 3.x` installed.

## SBT tests

You will need SBT `1.1.0` installed in order to execute the `SBT` Integration tests.

## NuGet tests

You will need NuGet `3.4.4` in order to be able to run the tests. You can get it from [here](https://dist.nuget.org/win-x86-commandline/v3.4.4/nuget.exe).

### Windows

Here you just need to install `.Net Framework v4` and set the `NUGET_V3_EXEC` environment variable with value `c:/path/to/nuget.exe`.

### Linux

To run Nuget tests here you will need `mono` to be installed. There were many problems with the compatibility of `nuget.exe` and `mono` versions, and the sutable combination is the following:

-  `Mono JIT compiler version 5.2.0.215 (tarball Mon Aug 14 15:46:23 UTC 2017)`
- `nuget.exe` v3.4.4 ( [link](https://dist.nuget.org/win-x86-commandline/v3.4.4/nuget.exe) )
- `NUGET_V3_EXEC` need to be set with value `mono \path\to\nuget.exe`
