# path-differ-springboot
A REST API to compare paths and save results to database as well as serving the history of each request.

## Usage
1. Run the following command to setup the runtime environment with default variables:

```bash
cp .env.sample .env 

export $(grep -v '^#' .env | xargs)
```

2. Start the application with the following command:

```bash
make Start
```

3. After downloading the images and starting the contrainers run to verify
       the containers are running:

```bash
make status

CONTAINER ID  IMAGE                          COMMAND   CREATED        STATUS        PORTS                   NAMES
74xxxxxxxxxx  localhost/demo-app:latest                1 minutes ago  Up 1 minutes  0.0.0.0:8080->8080/tcp  app1
8bxxxxxxxxxx  localhost/demo-app:latest                1 minutes ago  Up 1 minutes  0.0.0.0:8081->8080/tcp  app2
fexxxxxxxxxx  docker.io/library/postgres:15  postgres  1 minutes ago  Up 1 minutes  0.0.0.0:5432->5432/tcp  pg_local
~/Documents/path-differ-springboot
```
4. For logging run:

```bash
make logs
```

