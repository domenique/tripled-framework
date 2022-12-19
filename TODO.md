## Todo

### Prepare for introduction of transactional outbox pattern style eventing

the EventBus should be able to use a persistent store so that it can leverage this pattern to guarantee event delivery. The basic idea is that 
events are stored in an outbox table inside the same transaction as the command handling. An async process then iterates through the outbox to process the 
events.

In order for this to work, An Invoker should be responsible for creating an interceptor chain if it wants to. This will allow us to create an Invoker which 
persists the events in the outbox table, and then, when processing processing it, wrap it inside the interceptor chain.

Thread 1
incoming adapter
  dispatch(command)
    uowFactory.create()
    uow.intialize()
    dispatchInternal(command, uow)

Thread 2
  uow.start()
  icFactory.create()
  ic.proceed(command, uow)
    ---> interceptors
    calls each Invoker (which calls the CommandHandlers)
      publish(event)
        uow.scheduleDelayedEvent()
    <--- interceptors
  returns response
  ouw.commit() or uow.rollback
    publish(delayedEvent)
  calls callback method

Thread 1
  receives result in Future.


-------

Thread 1 
incoming adapter
  dispatch(command)
    uowFactory.create()
    uow.intialize()
    dispatcherInterceptorChain.proceed(command, uow)
      ---> interceptors (set uow data)
        dispatchInternal(command, uow)
      <--- interceptors (clear uow data)

Thread 2
  invokerInterceptorChain.proceed(command, uow)
    ---> interceptors (set threadLocal info)
      calls each Invoker (which calls the CommandHandlers)
      publish(event)
        uow.scheduleDelayedEvent()
    <--- interceptors (clear threadLocal info)
  returns response
  ouw.commit() or uow.rollback
    publish(delayedEvent)
  calls callback method


Cleanuop thread to check for unpublished, delayed events ?
