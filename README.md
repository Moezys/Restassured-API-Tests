# API Automation with RestAssured & Java

This project demonstrates API test automation using RestAssured, JUnit 5, and test data driven by a MySQL table.

## Prerequisites
- Java 11 or higher
- Maven (or Gradle)
- MySQL server (with database `qa_api`)
- JUnit 5, RestAssured dependencies in `pom.xml`
- MySQL Connector configured

## Setup
1. Clone this repo:
2. Create the MySQL database and table:
```sql
CREATE DATABASE qa_api;
USE qa_api;
CREATE TABLE api_tests (
  id INT AUTO_INCREMENT PRIMARY KEY,
  method VARCHAR(6) NOT NULL,
  endpoint VARCHAR(255) NOT NULL,
  payload JSON,
  expected_status INT NOT NULL,
  expected_json_path VARCHAR(255),
  expected_value VARCHAR(255)
);

INSERT INTO api_tests (method, endpoint, payload, expected_status, expected_json_path, expected_value)
  VALUES
    ('GET',  'https://jsonplaceholder.typicode.com/posts/1', NULL, 200, '$.id', '1'),
    ('GET',  'https://jsonplaceholder.typicode.com/posts/99999', NULL, 404, NULL, NULL),
    ('POST', 'https://jsonplaceholder.typicode.com/posts', '{"title":"foo","body":"bar","userId":1}', 201, '$.title', 'foo');
3. Update the jdbcURL, dbUser, and dbPass in ApiTests.java to match your local MySQL credentials.

