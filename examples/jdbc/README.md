# Quarkus JDBC Example

This is a simple example project demonstrating the use of Quarkus with JDBC for database access.

Disclaimer: There is currently no such thing like springs 'jdbcTemplate'. Quarkus provides at the moment only plain jdbc with a connection on the datasource or via the
panacheRepository.

# What is JDBC?

JDBC (or Java Database Connectivity) is a Java API that provides a standardized way to access relational databases.
The JDBC API allows Java applications to perform SQL-based database operations by providing an interface between the application and the database.

# Discretionary

- **Driver** JDBC drivers are specific implementations that enable communication between the Java application and the database. There are different JDBC drivers for various
  databases, and they need to be included in the Java application.
- **Connection**: The Connection interface represents a connection to the database. It is used to establish a connection, execute SQL statements, and perform other database
  operations.
- **Statement**: JDBC provides various types of statements, including Statement, PreparedStatement, and CallableStatement, to execute SQL statements. Statement is used for simple
  SQL statements, PreparedStatement for prepared statements with parameters, and CallableStatement for calling stored procedures.
- **ResultSet**: The ResultSet represents the result set of a query. It allows traversing the results and retrieving data from the database.