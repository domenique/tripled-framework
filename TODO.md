## Todo

### Prepare for introduction of transactional outbox pattern style eventing

the EventBus should be able to use a persistent store so that it can leverage this pattern to guarantee event delivery. The basic idea is that 
events are stored in an outbox table inside the same transaction as the command handling. An async process then iterates through the outbox to process the 
events.

In order for this to work, An Invoker should be responsible for creating an interceptor chain if it wants to. This will allow us to create an Invoker which 
persists the events in the outbox table, and then, when processing processing it, wrap it inside the interceptor chain.
