# Flashcard Requirements

## Before Everything 

1. Please inspect module `./rest-mvc` to understand its structure and tech-stack.
2. Please also refer to [guidelines.md](./.junie/guidelines.md) for the full list of Spring Boot and Java development standards.

## There is a new added domain called `ocp` in 
`./rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/domain/ocp`

### In this new `ocp` module, A new model `Flashcard` already added

Could you please add SQL statements to create a database table for this new model
in file `./rest-mvc/src/main/resources/schema_renew.sql`,
and make it compatible with the current PostgeSQL database schema 

### Make `Flashcard` Entity with Spring Data JDBC

After added SQL Statements for `Flashcard` model, could you also help me modify 
record `Flashcard` to make it become an Entity with Spring Data JDBC?
For example, add annotations to specify table name, primary key, etc. 

### Add Repository for `Flashcard` Entity

After making `Flashcard` Entity with Spring Data JDBC, could you also help me add a repository interface for `Flashcard` entity
in `./rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/domain/ocp/repository`
For example, create an interface that extends `ListCrudRepository` to perform CRUD operations on `Flashcard` entities.

### Create a Service for `Flashcard` Entity, name it `FlashcardService`

After making Repository for `Flashcard` Entity, please create a service interface for `Flashcard` entity
in `./rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/domain/ocp/service`
And implement this new interface in the same place

### Create a REST Controller `FlashcardController` for `Flashcard` Entity 

Please also create REST Endpoints by creating a REST Controller with Spring Web MVC for `Flashcard` Entity
in `./rest-mvc/src/main/java/spring/boot/sfg7/rest/mvc/web/controller`,
the controller should have an instance field of type `FlashcardService` you just created previously.

### Please also add unit tests for the new code you created 

### Please also create integration test with `@WebMvcTest` and `MockMvc` for testing
the controller you just created, you can inspect other controllers already created in 
`./rest-mvc/src/test/java/spring/boot/sfg7/rest/mvc/web/controller`






