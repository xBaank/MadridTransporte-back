To set up the API you need to configure the following environment variables:

| Environment Variable    | Default Value                          | Description                             |
|-------------------------|----------------------------------------|-----------------------------------------|
| PORT                    | 8080                                   | Port                                    |
| MONGO_CONNECTION_STRING | NONE                                   | Database connection string              |
| MONGO_DATABASE_NAME     | NONE                                   | Database name                           |
| SMTP_HOST               | NONE                                   | Email host server                       |
| SMTP_PORT               | NONE                                   | Email host port                         |
| SMTP_USERNAME           | NONE                                   | Email username                          |
| SMTP_PASSWORD           | NONE                                   | Email password                          |
| JWT_SECRET              | NONE                                   | JWT secret key                          |
| JWT_AUDIENCE            | NONE                                   | JWT audience                            |
| JWT_ISSUER              | NONE                                   | JWT issuer                              |

This variables can be loaded from a .env file in the root of the project or can be set in the environment variables of the system.

Execute the following command to install the dependencies:

```bash
 .gradlew buildFatJar
 ```

Execute the following command to run the application:

```bash
 java --jar busTrackerApi/build/libs/busTrackerApi.jar
 ```

> **WARNING**: The application doesn't allow using SSL, so it is recommended to use a reverse proxy to handle the SSL connection.