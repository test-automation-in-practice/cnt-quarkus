# quarkus JPA Repository Pattern

## defining profiles

in our examples we use 2 profiles,  `test`, `h2-test`

to define a profile you implement the interface `io.quarkus.test.junit.QuarkusTestProfile` see
example `util.H2TestProfile`

after this you can define each property in `application.properties`

Luckily it is only needed to define the database kind as h2 and everything else is provided by quarkus.

## testcontainers via dev services

quarkus comes with a handy dev service for mostly all databases.

this allow us to only set `quarkus.datasource.db-kind=postgresql`

everything else is provided by quarkus

## h2 testing vs postgres testcontainers

h2 database is a bit faster then testcontainers. thats why we have it here as an example.

Normally you would use testcontainers with your database which you use in production
