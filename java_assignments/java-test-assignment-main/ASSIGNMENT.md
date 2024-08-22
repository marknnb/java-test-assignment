# Java Test Assignment

You need to create a service that provides REST API endpoints that allow you to add new clients in the service’s db with information about their databases and run the pg_dump command for those databases (like `customer_db` from docker-compose.yml) and upload the resulting dump file to an S3-compatible object storage service such as Minio and allows it to be downloaded by service API. The project should be developed using the latest Java and Spring Boot versions. This task involves integrating Docker to improve the portability and scalability of an existing application.

*Projected time to complete this task is approximately 8-10 hours.*


# Requirements

1. The API should have five endpoints (see below).
2. The backup process should create a pg_dump file of the customer_db and upload it to an S3-compatible object storage service such as Minio.
3. The candidate should determine the S3 bucket structure.
4. The API should return appropriate JSON responses for each endpoint.
5. The code should be optimized, readable, understandable, and runnable.
6. Feel free to use any libraries or frameworks you want.
7. Feel free to change the current project structure **except API responses**.
8. Create a Dockerfile in the root of your project directory.
    1. The Dockerfile should specify all necessary dependencies and configuration needed to run the application.
    2. Use an appropriate base image.
    3. Include a docker-compose.yml file if your application requires multiple services to run.
    4. Environment Variables: Utilize environment variables for configuration that may vary between deployments.


# Instructions

1. Download the provided archive and extract it to your local machine.
2. Implement the requirements listed above.
3. Once the implementation is complete, create an archive.
4. Send the archive to a recruiter.

# Evaluation
The code will be evaluated based on the following criteria:

1. Correctness: The implementation must meet the specified requirements.
2. Code quality: The code should be optimized, read, understood, and runnable.
3. Best practices: The implementation should follow best practices for Java and Spring Boot.
4. Docker Practices: Best practices for Docker usage are followed, including minimal base images, efficient layering, and proper resource allocation.
5. Documentation: The code should be documented appropriately, with clear comments and a README file explaining how to run the project.

# REST API Requirements

## The API should have four endpoints:

### Create a new client
This method creates a new client and returns its id.

 **`POST /client`**

Request

```json
{
    "credentials": {
        "username": "username",
        "password": "password",
        "database": "database",
        "host": "host",
        "port": "port"
    },
    "tariff": "FREE"
}
```

Tariffs: `FREE`, `STANDARD`, `PREMIUM`

 Response

```json
{
    "id": "client_id",
    "status": "ok"
}
```

Statuses: `ok`, `error`


### Submit backup request
This method submits a backup request for a client.

**`POST /backup`**

Request

```json
{
    "client_id": "client_id"
}
```

Response

```json
{
    "id": "backup_id",
    "start_date": "start_date",
    "status": "IN_PROGRESS"
}
```

Statuses: `IN_PROGRESS`, `DONE`, `FAILED`

### Get backup status
This method returns the status of a backup request.

**`GET /backup/{client_id}/{backup_id}`**
Response

```json
{
    "id": "backup_id",
    "start_date": "start_date",
    "end_date": "end_date",
    "database_size": "database_size",
    "backup_time": "backup_time",
    "status": "FAILED"
}
```

Statuses: `IN_PROGRESS`, `DONE`, `FAILED`


### Download backup file
This method returns the backup file.

**`GET /backup/{client_id}/{backup_id}/download`**

Response (upstream)

Response Upstream file.


#
*We’ve deliberately left the specific implementation details out - we want to
give you space to be creative with your approach to the problem, and impress us
with your skills and experience.*