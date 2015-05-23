# command-dispatcher [![build status](https://travis-ci.org/domenique/command-dispatcher.svg?branch=master)](https://travis-ci.org/domenique/command-dispatcher) [![Coverage Status](https://coveralls.io/repos/domenique/command-dispatcher/badge.svg?branch=master)](https://coveralls.io/r/domenique/command-dispatcher?branch=master)
A command dispatcher written java which can be used in a CQRS based application.

## Goal
CQRS stands for Command Query Responsibilty segregation. Which is just a fancy acronym to say that the actions which change the state of your application should be seperated from actions querying the state of your application. 

This framework aims to facilitate the creation and execution of commands. The idea is to be able to dispatch commands so that they can be executed in an (a)synchronous fashion.

> Note that this is just the first part. The idea would be to build some framework to facilitate development of the domain model including the repositories. For the query part of the architecture, I intend to write some framework as well.  

## Usage 
The framework is created using the Spring framework and supports Spring boot. This implies that if you add this framework jar to your classpath, it will be auto-configured with sensible defaults.

after adding the required dependencies, a command-dispatcher should be available in your application context.

You can find some examples in the test packages, they contain some tests on the basic usage of the command dispatcher.

> More information later when I've finished some more code. Currently, I have a bunch of ideas in my head, but I need some time to work them out before I can document them :P

## Contribute
The project is still in a very early stage, however if you feel like contributing or have some brilliant ideas how to make this a killer framework, just contact me! I'm open for suggestions!

