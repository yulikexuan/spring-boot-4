# Spring Data JDBC

## Key Design Principles

The design follows Spring Data JDBC's philosophy of simpler, 
more explicit aggregate management compared to JPA.

### Aggregate Pattern 

- `Student` and `Course` are aggregate roots 
- `Enrollment` is part of both aggregates via `@MappedCollection` 

### `AggregateReference` 

- Prevents loading entire entity graphs 
  - maintaining performance and clear boundaries 

### Composite Key 

- `EnrollmentId` embeds both IDs as the natural primary key 
  - for the many-to-many relationship