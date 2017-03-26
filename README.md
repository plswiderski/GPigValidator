# GPigValidator

[![Build Status](https://travis-ci.org/pablo127/GPigValidator.svg?branch=master)](https://travis-ci.org/pablo127/GPigValidator)
  
[SonarQube](https://sonarqube.com/dashboard?id=gpigvalidator%3Amaster)

[Javadoc](https://pablo127.bitbucket.io/gpigvalidator-javadoc/)

Multiplatform Java library which validates the data from classes depending on validation annotations from `javax.validation.constraints` ([full list of javax constraints' annotations](http://docs.oracle.com/javaee/6/api/javax/validation/constraints/package-summary.html)) and `org.hibernate.validator.constraints` ([full list of hibernate constraints' annotations](https://docs.jboss.org/hibernate/validator/4.3/api/org/hibernate/validator/constraints/package-summary.html)) packages. It enables to check correctness and throw customized exceptions and messages in different languages. English and Polish are built-in languages.

Current version: `0.1`.

## [Requirements](https://bitbucket.org/pablo127/gpigvalidator/wiki/requirements)

## Licence
GPigValidator is covered by MIT licence. It is **free** for commercial and non-commercial use.

## Sample
Sample project is available [here](https://bitbucket.org/snippets/pablo127/EkKgE). All available functions are shown in action. It is fully operational gradle project shown with output.

## Add library to your project

Get started with build system:

* [gradle](https://bitbucket.org/pablo127/gpigvalidator/wiki/gradle)
* [maven](https://bitbucket.org/pablo127/gpigvalidator/wiki/maven)

or [manual](https://bitbucket.org/pablo127/gpigvalidator/wiki/manual)

## [Currently available annotations](https://bitbucket.org/pablo127/gpigvalidator/wiki/available_annotations)

## SonarQube code coverage

There are some problems with measuring code coverage on SonarQube. The problems are with jacoco plugin and powermock. They are described [here](https://github.com/powermock/powermock/wiki/Code-coverage-with-JaCoCo). A solution is approaching and it will come up with [fix in powermock](https://github.com/powermock/powermock/issues/727).

## Problems & suggestions
Look at [issues](https://bitbucket.org/pablo127/gpigvalidator/issues). If you do not find an answer, [write a new issue.](https://bitbucket.org/pablo127/gpigvalidator/issues/new)