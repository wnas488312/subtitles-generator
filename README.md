# Subtitles Generator

Subtitles Generator is a user-friendly Spring Boot application designed to help you easily append subtitles to your videos. With an intuitive web interface, users can paste their subtitle text, specify the duration for each subtitle chunk, and generate videos seamlessly. The application produces two outputs: the original video with the subtitles appended and a separate video featuring only the subtitles with a transparent background.

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
    - [Running the Application](#running-the-application)
    - [Accessing the UI](#accessing-the-ui)
- [Configuration](#configuration)
- [Database](#database)
- [Troubleshooting](#troubleshooting)
- [License](#license)

## Features

- **Append Subtitles to Videos**: Easily paste subtitle text and specify the display duration for each segment.
- **Generate Two Video Outputs**:
    - The original video with the appended subtitles.
    - A video containing only the subtitles with a transparent (alpha) background.
- **Simple Web Interface**: An intuitive UI for easy subtitle management and video generation.
- **Automatic Video Cleanup**: Generated videos are automatically deleted every day at 12:00 PM (noon).
- **Dual Database Support**: Automatically uses PostgreSQL if available, or falls back to an embedded H2 database.

## Technologies

- **Java**: 22
- **Spring Boot**: 3.3.2
- **Maven**: Dependency management and build tool.
- **PostgreSQL**: Primary database.
- **H2 Database**: Fallback embedded database.

## Prerequisites

Before running the application, ensure you have the following installed:

- Java 22
- Maven 3.8+
- PostgreSQL (optional, only if you want to use an external database)

## Getting Started

### Running the Application

1. **Clone the repository**:

   ```bash
   git clone https://github.com/wnas488312/subtitles-generator.git
   cd subtitles-generator
   ```

2. **Build the application**:
    ```bash
    mvn clean install
   ```
   
3. **Run the application**:
    ```bash
   mvn spring-boot:run
   ```

    Alternatively, you can run the application using the generated JAR file:
    ```bash
    java -jar target/subtitles-generator-0.1.0-SNAPSHOT.jar
    ```

## Accessing the UI
Once the application is running, you can access the frontend interface by navigating to the following URL in your web browser:
```bash
http://localhost:8080
```
This address is the default provided by Spring Boot.

## Configuration
The application is pre-configured to work with either PostgreSQL or H2, depending on your environment.

### Database Configuration
- **PostgreSQL**: If PostgreSQL is available, the application will automatically connect to it using the properties defined in application.properties.
- **H2 Database**: If PostgreSQL is not available, the application will fall back to the embedded H2 database.
### Configuring PostgreSQL
To configure PostgreSQL, edit the src/main/resources/application.yaml file with your PostgreSQL credentials:
```yaml
spring:
  # PostgreSQL settings
  datasource:
    url: jdbc:postgresql://localhost:5432/sgapp
    username: <your_username>
    password: <your_password>
    driver-class-name: org.postgresql.Driver
```
"sgapp" database needs to be created before starting application.

### Configuring H2 (Fallback)
No additional configuration is needed for H2. If PostgreSQL is not available, the application will use H2 by default.

### Database
- **PostgreSQL:** Ensure that the PostgreSQL server is running and accessible.
- **H2:** The H2 database will be created in-memory and will be available as long as the application is running.
## Troubleshooting
- **Application fails to start:** Ensure the application is running on the default port 8080, and that no other application is using this port.

## License
This project is licensed under the Mozilla Public License 2.0. See the [LICENSE](LICENSE.txt) file for details.