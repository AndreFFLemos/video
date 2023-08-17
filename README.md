# VideoWatch
A Restfull API to watch videos, built in Java with a TDD philosophy, MVC architectural pattern,using design patterns(Builder,Adapter,Bridge,Façade,Flyweight,etc), SQL and MySql, Hibernate, Spring MVC, Spring Boot 3, Spring Security, Junit5, Mockito, Postman.
First Delivery-15/08/2023

## Requirements

1. Java Development Kit (JDK) 17
2.Spring MVC,Spring Boot, Spring Security, Spring Data,Spring Mail
3.Lombok
4.MySql connector-j
5.Model Mapper
6.JUnit Jupiter 5.10.0
7.Jsonwebtoken

## Compilation Instructions

You will need a RDBMS to manage the Database, i used MySQL. You will need to set the values inside a application.properties file to establish the connection between the object world (the API) and the relational world (MySQL, for example)

source.username="database user"
spring.datasource.password="database password"
spring.datasource.url=jdbc:mysql://localhost:'databaseport'/'databasename'?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
jwt.secretKey='Generate a secret password with >80 characters'


Navigate to the directory where the pom.xml file of your project is located. Then run the following command:
1. mvn clean compile

After successful compilation, you can run the program using the following command inside the directory where the pom.xml file is
2. .\mvnw.cmd spring-boot: run

or

If you are using Intellij IDE
1.Import the project as a Maven project.
2.Navigate to BlockbusterApplication.java.
3.Right-click and select "Run" or "Debug" to start the application.


