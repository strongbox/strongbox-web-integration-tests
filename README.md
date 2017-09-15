strongbox-web-integration-tests
===
[![Master Build Status](https://dev.carlspring.org/jenkins/buildStatus/icon?job=strongbox/strongbox-web-integration-tests/master)](https://dev.carlspring.org/jenkins/job/strongbox/job/strongbox-web-integration-tests/job/master/)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/strongbox/strongbox?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## Introducion

This project contains the tests for the Strongbox Webapp Assembly and different  Repository layouts types.
Followting Layouts supported for now:
- Maven tests, based on `maven-invoker-plugin`;
- Nuget tests, which are just Groovy scripts;

## Maven tests

You will need the common environment to execute `Maven` Integration tests, this is only `Java 1.8` and `Maven 3.x` installed.
These tests active by default, however you can skip them using following profile:
 - `-P\!run-maven-it-tests` for Linux;
 - `-P!run-maven-it-tests` for Windows;
 
## Nuget tests

To run Nuget tests you will also need `Java 1.8` and `Maven 3.x` installed, and some `.Net` related environment (depends on you OS).
Regardless of operating system you will need to download `nuget.exe` v3.4.4 [here](https://dist.nuget.org/win-x86-commandline/v3.4.4/nuget.exe)


#### Windows

Here you just need to install `.Net Framework v4` and set the `NUGET_V3_EXEC` environment variable with value `c:/path/to/nuget.exe`.

#### Linux

To run Nuget tests here you will need `mono` to be installed. There were many problems with the compatibility of `nuget.exe` and `mono` versions, and the sutable combination is the following:

-  `Mono JIT compiler version 5.2.0.215 (tarball Mon Aug 14 15:46:23 UTC 2017)`
- `nuget.exe` v3.4.4 ( [link](https://dist.nuget.org/win-x86-commandline/v3.4.4/nuget.exe) )
- `NUGET_V3_EXEC` need to be set with value `mono \path\to\nuget.exe`  

Also you can skip Nuget tests using following profile:
 - `-P\!run-nuget-it-tests` for Linux;
 - `-P!run-nuget-it-tests` for Windows;
