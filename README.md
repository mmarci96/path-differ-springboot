# Path Differ Spring-Boot  
![image](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![image](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![image](https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)


## Overview
This project provides a REST API for comparing file paths and tracking comparison history. It uses:
Spring Boot built with Gradle and connecting to PostgreSQL database.  

## Requirements
![image](https://img.shields.io/badge/podman-892CA0?style=for-the-badge&logo=podman&logoColor=white)

You must have Podman or Docker installed to run this application. 

## Starting the app
1. Run the following command to setup the runtime environment with default variables:

```bash
cp .env.sample .env 
```

2. Start the application with the following command:

```bash
make start
```

3. After downloading the images and starting the contrainers run to verify
       the containers are running:

```bash
make status

CONTAINER ID  IMAGE                          COMMAND   CREATED        STATUS        PORTS                   NAMES
74xxxxxxxxxx  localhost/demo-app:latest                1 minutes ago  Up 1 minutes  0.0.0.0:8080->8080/tcp  app1
8bxxxxxxxxxx  localhost/demo-app:latest                1 minutes ago  Up 1 minutes  0.0.0.0:8081->8080/tcp  app2
fexxxxxxxxxx  docker.io/library/postgres:15  postgres  1 minutes ago  Up 1 minutes  0.0.0.0:5432->5432/tcp  pg_local
```
4. For logging run:

```bash
make logs
```

## Features

### Java Docs
GET /doc
For javadocks open the browser at <a href="http://localhost:8080/doc">http://localhost:8080/doc</a>.

### Get-Diff:
GET /api/files/get-diff/{username}?pathA=...&pathB=...
Example response:
```json
{
  "filePathA": "/home/app-data/dirA",
  "filePathB": "/home/app-data/dirB",
  "onlyPathA": [
    {
      "name": "child/hello.txt",
      "size": 12
    },
    {
      "name": "file2.txt",
      "size": 0
    }
  ],
  "onlyPathB": [],
  "shared": [
    {
      "name": "data.json",
      "size": 0
    }
  ]
}
```
### History:
GET /api/files/history
Example response:
```json
[
  {
    "username": "user123",
    "results": {
      "filePathA": "/app",
      "filePathB": "/app",
      "onlyPathA": [],
      "onlyPathB": [],
      "shared": [
        {
          "name": "app.jar",
          "size": 54182419
        }
      ]
    },
    "createdAt": "2025-06-08T19:04:18.604435"
  },
  {
    ...
  },
]
```

