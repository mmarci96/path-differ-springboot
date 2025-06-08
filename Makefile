# Variables
PG_CONTAINER_NAME = pg_local
PG_IMAGE = docker.io/library/postgres:15
PG_PORT = 5432
PG_USER = postgres
PG_PASSWORD = postgres
PG_DATA_DIR = ./pgdata

APP_IMAGE_NAME = demo-app:latest
APP_CONTAINER1 = app1
APP_CONTAINER2 = app2
APP_PORT1 = 8080
APP_PORT2 = 8081

NETWORK_NAME = demo-net

.PHONY: build start stop restart status clean logs

build:
	@echo "Building application image with Podman..."
	podman build -t $(APP_IMAGE_NAME) ./demo/

start: build
	@echo "Creating network if not exists..."
	- podman network create $(NETWORK_NAME)

	@echo "Starting PostgreSQL container..."
	- podman rm -f $(PG_CONTAINER_NAME) || true
	podman run -d --name $(PG_CONTAINER_NAME) \
		--network $(NETWORK_NAME) \
		-p $(PG_PORT):5432 \
		-e POSTGRES_USER=$(PG_USER) \
		-e POSTGRES_PASSWORD=$(PG_PASSWORD) \
		-v $(PG_DATA_DIR):/var/lib/postgresql/data:Z \
		$(PG_IMAGE)

	@echo "Starting application container 1 on port $(APP_PORT1)..."
	- podman rm -f $(APP_CONTAINER1) || true
	podman run -d --name $(APP_CONTAINER1) \
		--network $(NETWORK_NAME) \
		-p $(APP_PORT1):8080 \
		-e SPRING_DATASOURCE_URL=jdbc:postgresql://$(PG_CONTAINER_NAME):5432/postgres \
		-e SPRING_DATASOURCE_USERNAME=$(PG_USER) \
		-e SPRING_DATASOURCE_PASSWORD=$(PG_PASSWORD) \
		$(APP_IMAGE_NAME)

	@echo "Starting application container 2 on port $(APP_PORT2)..."
	- podman rm -f $(APP_CONTAINER2) || true
	podman run -d --name $(APP_CONTAINER2) \
		--network $(NETWORK_NAME) \
		-p $(APP_PORT2):8080 \
		-e SPRING_DATASOURCE_URL=jdbc:postgresql://$(PG_CONTAINER_NAME):5432/postgres \
		-e SPRING_DATASOURCE_USERNAME=$(PG_USER) \
		-e SPRING_DATASOURCE_PASSWORD=$(PG_PASSWORD) \
		$(APP_IMAGE_NAME)

stop:
	@echo "Stopping and removing containers..."
	- podman stop $(APP_CONTAINER1) $(APP_CONTAINER2) $(PG_CONTAINER_NAME) || true
	- podman rm $(APP_CONTAINER1) $(APP_CONTAINER2) $(PG_CONTAINER_NAME) || true

restart: stop start

status:
	@podman ps -a --filter "name=$(APP_CONTAINER1)"
	@podman ps -a --filter "name=$(APP_CONTAINER2)"
	@podman ps -a --filter "name=$(PG_CONTAINER_NAME)"

clean: stop
	@echo "Removing network $(NETWORK_NAME)..."
	- podman network rm $(NETWORK_NAME) || true
	@echo "Done."

logs:
	@podman logs -f $(APP_CONTAINER1)

