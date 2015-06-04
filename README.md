# TripleD [![build status](https://travis-ci.org/domenique/command-dispatcher.svg?branch=master)](https://travis-ci.org/domenique/command-dispatcher) [![Coverage Status](https://coveralls.io/repos/domenique/command-dispatcher/badge.svg?branch=master)](https://coveralls.io/r/domenique/command-dispatcher?branch=master)
A very opinionated framework to build applications using CQRS, event sourcing and domain driven design.

## Goal
The TripleD framework aims to take away as much boiler plate code as possible when building CQRS based applications which want to take advantage of event sourcing and domain driven design.

This framework aims to facilitate the creation and execution of commands. The idea is to be able to dispatch commands so that they can be executed in an (a)synchronous fashion.

> Note that this is just the first part. The idea would be to build some framework to facilitate development of the domain model including the  repositories.  

## Usage 
The framework supports Spring boot. This implies that if you add this framework jar to your classpath, it will be auto-configured with sensible defaults.

after adding the required dependencies, an `EventPublisher` and `EventSubscriber`  should be available in your application context.
Alternatively, When annotatating an `@Configuration` class with `@EnableEventHandlerSupport` you should be able to annotate any spring service with `@EventHandler` and it will be registered automatically to the `EventBus`.

You can find some examples in the test packages, they contain some tests on the basic usage of the command dispatcher.

> More information later when I've finished some more code.

## Contribute
The project is still in a very early stage, however if you feel like contributing or have some brilliant ideas how to make this a killer framework, just contact me! I'm open for suggestions!

