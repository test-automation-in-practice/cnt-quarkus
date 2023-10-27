# Scheduler in Quarkus

This example implements and tests two ways of scheduling.

1. "io.quarkus:quarkus-scheduler"
    - The first example is implemented with annotations which allow the developer to run a very basic scheduler configured
      in the application-properties
2. "io.quarkus:quarkus-quartz"
    - The second example is the programmatic way to register a scheduler.
      This will give the developer the freedom to do whatever he/she wants to do with a scheduler.

## Test

Each scheduler implementation is covered with an *integration test* to verify the scheduled task itself with a running app
and a *Unit Test* to cover its plain functionality