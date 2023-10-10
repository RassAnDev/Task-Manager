# Task Manager

### Tests, linter and maintainability statuses:
[![Java CI](https://github.com/RassAnDev/java-project-73/actions/workflows/main.yml/badge.svg)](https://github.com/RassAnDev/java-project-73/actions/workflows/main.yml)
[![Maintainability](https://api.codeclimate.com/v1/badges/54442414c25fd35773db/maintainability)](https://codeclimate.com/github/RassAnDev/java-project-73/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/54442414c25fd35773db/test_coverage)](https://codeclimate.com/github/RassAnDev/java-project-73/test_coverage)

## About
This application is a task management system that allows you to create tasks, change their statuses and assign executors for them, as well as set tags for convenient grouping of tasks by them.

## Requirements:
Before using this application, you must install and configure:
* JDK 20;
* Gradle 8.2
* Node.js 16.13.1

## Stack:
* Java 20;
* Gradle 8.2;
* Spring Boot, Spring Web, Spring Data JPA, Spring Security;
* JWT;
* Liquibase;
* QueryDSL;
* SQL DB H2, PostgreSQL;
* Lombok;
* Swagger;
* MockMvc;
* JUnit;
* GitHub Actions;
* CodeClimate;
* Jacoco

## Build Application

```bash
make build
```

## Start Application on localhost

```bash
make start
```

#### [An example of a deployed application](https://task-manager-45fe.onrender.com)
#### See the API documentation [there](http://localhost:5000/swagger-ui/index.html#)
