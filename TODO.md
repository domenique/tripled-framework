## Todo

## Create an invoker that uses the outbox pattern.

The invoker should persist the event to an outbox table. A different thread should read from the outbox table to publish events.
A Cleanup thread to check for unpublished, delayed events, which should be started at application start.

A file based impl would be nice so that we can use it without DB.

