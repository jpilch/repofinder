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

  The only prerequisite is having **docker compose** installed on your system.
  
  To check if you have it installed you can run `docker compose version` (the following output shows which version I used):
  ```shell
  ➜  repofinder git:(master) docker compose version  
  Docker Compose version v2.20.2
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
  
    app:
      config:
        entry-ttl: 600
   ```
   Where `<YOUR-TOKEN>` is your personal access token for the GitHub's API.

   You can also adjust the `app.config.entry-ttl` to change cache entry ttl, in the above example it's equal to 10 minutes (60 -> 1 minute).

### Building and Running

1. Start up the cache service required for testing and running the API.

   ```shell
   docker compose up -d cache
   ```

2. Build the application using Maven wrapper.

   ```shell
   ./mvnw clean install
   ```

3. Run the application.

    The easiest way to get this API up and running is by using docker compose:
    
    ```
    docker compose up -d
    ```
    
    Or alternatively you can use maven spring boot plugin:
    
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
    "message": "User not found"
}
```

- If you specify an accept header for content type other than `application/json`, you will receive a 406 response in the following format:
```
{
    "status": 406,
    "message": "Not Acceptable"
}
```

## Tools Used

- **Java**: The programming language
- **Maven**: Dependency management tool
- **Spring Boot**: Framework for implementing the required API functionality
- **Redis**: In memory caching for improved performance and reducing the amount of requests to GitHub
- **Docker**: Containerization for easy development and reproducible environment



