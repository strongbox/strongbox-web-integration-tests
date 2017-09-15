strongbox-web-integration-tests
===
[![Master Build Status](https://dev.carlspring.org/jenkins/buildStatus/icon?job=strongbox/strongbox-web-integration-tests/master)](https://dev.carlspring.org/jenkins/job/strongbox/job/strongbox-web-integration-tests/job/master/)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/strongbox/strongbox?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

####Introducion

This project contains the tests for the Strongbox Webapp Assembly and different  Repository layouts types.
Followting Layouts supported for now:
- Maven tests, based on `maven-invoker-plugin`;
- Nuget tests, which are just Groovy scripts;

####Maven tests

You will need the common environment to execute `Maven` Integration tests, this is only `Java 1.8` and `Maven 3.x` installed.
These tests active by default, however you can skip them using following profile:
 - `-P\!run-maven-it-tests` for Linux;
 - `-P!run-maven-it-tests` for Windows;
 
