# Repofinder
Repofinder is a RESTful web service written in Java that allows you to list all non-forked GitHub repositories associated with a given user (user is either an owner or a member of a repository). 

It provides information about the repository name, owner login, and for each branch, its name and last commit SHA.

Additionally, it handles error responses, returning 404 for non-existing users and 406 for unsupported content types.

## Table of Contents

- [Local Setup](#local-setup)
- [API Usage](#api-usage)
  - [List Repositories](#list-repositories)
  - [Error Handling](#error-handling)
- [Tools Used](#tools-used)

## Local Setup

To set up Repofinder locally, follow these steps:

### Prerequisites

  - **docker compose v2.20.2**
  
    To check if you have it installed you can run `docker compose version`, if not installed take a look at the docs on how to install it.

  - **java 17.0.7**

    For me, the easiest way to install java is by using sdkman. With sdkman you can run the following command to install the jdk that was used during the development of this project:

    ```
    sdk install java 17.0.7-amzn
    ```

  

### Configuration

1. Clone the Repofinder repository to your local machine.

   ```shell
   git clone https://github.com/jpilch/repofinder.git

2. Navigate to the project directory.

   ```shell
   cd repofinder

3. Create an `application.yml` file under `src/main/resources` with the following contents.
   ```
    github:
      client:
        config:
          apiVersion: 2022-11-28
          authToken: <YOUR-TOKEN>
          rootUri: https://api.github.com
   ```
   Where `<YOUR-TOKEN>` is your personal access token for the GitHub's API.

### Building and Running

1. Build the application using Maven wrapper.

   ```shell
   ./mvnw clean install
   ```

2. Build the app image.

   ```shell
   docker compose build
   ```

3. Run the application.

    By using docker compose:
    
    ```
    docker compose up -d
    ```
    
    Or by using maven spring boot plugin:
    
    ```
    ./mvnw spring-boot:run
    ```

   The API should now be running locally on port 8080.

## API Usage

### List repositories

To list all GitHub repositories for a specified username, make a GET request with the username as the path variable and an `Accept: application/json` header:

```
curl -H"Accept: application/json" http://localhost:8080/{username}
```

Sample response:

```
[
    {
        "name": "repofinder",
        "ownerLogin": "jpilch",
        "branches": [
            {
                "name": "master",
                "lastCommitSha": "123456"
            }
        ]
    }
]
```

### Error Handling

- For a non-existing GitHub user, you will receive a 404 response in the following format:

```
{
    "status": 404,
    "message": "Not Found"
}
```

- If you specify an accept header for content type other than `application/json`, you will receive a 406 response in the following format:
```
{
    "status": 406,
    "message": "No acceptable representation"
}
```

## Tools Used

- **Java**: The programming language
- **Maven**: Dependency management tool
- **Spring Boot**: Framework for implementing the required API functionality
- **Docker**: Containerization for easy development and reproducible environment

## Todo

- write new `GithubService` implementation using non-blocking approach
- use `WebClient` instead of `RestTemplate`
- add redis for caching along with test containers



