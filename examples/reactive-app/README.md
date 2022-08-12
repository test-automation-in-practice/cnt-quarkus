# reactive-app Example Project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Getting Started

- using `quarkus dev` or `./gradlew quarkusDev` will start your application and testcontainer as well if the docker daemon is running
- if running in the `prod` profile it will connect to a specific postgres docker container which can be started via startPostgresContainer.sh

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.
