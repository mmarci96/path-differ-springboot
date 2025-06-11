# Variables
PG_CONTAINER_NAME := pg_local
PG_IMAGE := docker.io/library/postgres:15
PG_PORT := 5432
PG_USER := postgres
PG_PASSWORD := postgres
PG_DATA_DIR := ./pgdata

APP_IMAGE_NAME := demo-app:latest
APP_CONTAINER1 := app1
APP_CONTAINER2 := app2
APP_PORT1 := 8080
APP_PORT2 := 8081

NETWORK_NAME := demo-net

ifneq (,$(wildcard ./.env))
    include .env
    export
endif

.PHONY: build start stop restart status clean logs \
        start-db stop-db restart-db \
        start-app1 stop-app1 restart-app1 logs-app1 \
        start-app2 stop-app2 restart-app2 logs-app2

build:
	@echo "Building application image with Podman..."
	podman build -t $(APP_IMAGE_NAME) .

create-network:
	@echo "Creating network if not exists..."
	- podman network create $(NETWORK_NAME)

start-db: create-network ensure-db-dir
	@echo "Starting PostgreSQL container..."
	- podman rm -f $(PG_CONTAINER_NAME) || true
	podman run -d --name $(PG_CONTAINER_NAME) \
		--network $(NETWORK_NAME) \
		-p $(PG_PORT):5432 \
		-e POSTGRES_USER=$(PG_USER) \
		-e POSTGRES_PASSWORD=$(PG_PASSWORD) \
		-v $(PG_DATA_DIR):/var/lib/postgresql/data:Z \
		$(PG_IMAGE)

ensure-db-dir:
	@mkdir -p $(PG_DATA_DIR)

stop-db:
	@echo "Stopping and removing PostgreSQL..."
	- podman stop $(PG_CONTAINER_NAME) || true
	- podman rm $(PG_CONTAINER_NAME) || true

restart-db: stop-db start-db

start-app1: create-network build
	@echo "Starting application container 1 on port $(APP_PORT1)..."
	- podman rm -f $(APP_CONTAINER1) || true
	podman run -d --name $(APP_CONTAINER1) \
		--network $(NETWORK_NAME) \
		-p $(APP_PORT1):8080 \
		-e SPRING_DATASOURCE_URL=jdbc:postgresql://$(PG_CONTAINER_NAME):5432/postgres \
		-e SPRING_DATASOURCE_USERNAME=$(PG_USER) \
		-e SPRING_DATASOURCE_PASSWORD=$(PG_PASSWORD) \
		$(APP_IMAGE_NAME)

stop-app1:
	@echo "Stopping and removing container 1..."
	- podman stop $(APP_CONTAINER1) || true
	- podman rm $(APP_CONTAINER1) || true

restart-app1: stop-app1 start-app1

logs-app1:
	@podman logs -f $(APP_CONTAINER1)

start-app2: create-network build
	@echo "Starting application container 2 on port $(APP_PORT2)..."
	- podman rm -f $(APP_CONTAINER2) || true
	podman run -d --name $(APP_CONTAINER2) \
		--network $(NETWORK_NAME) \
		-p $(APP_PORT2):8080 \
		-e SPRING_DATASOURCE_URL=jdbc:postgresql://$(PG_CONTAINER_NAME):5432/postgres \
		-e SPRING_DATASOURCE_USERNAME=$(PG_USER) \
		-e SPRING_DATASOURCE_PASSWORD=$(PG_PASSWORD) \
		$(APP_IMAGE_NAME)

stop-app2:
	@echo "Stopping and removing container 2..."
	- podman stop $(APP_CONTAINER2) || true
	- podman rm $(APP_CONTAINER2) || true

restart-app2: stop-app2 start-app2

logs-app2:
	@podman logs -f $(APP_CONTAINER2)

start: start-db start-app1 start-app2
stop: stop-app1 stop-app2 stop-db
restart: stop start
clean: stop
	@echo "Removing network $(NETWORK_NAME)..."
	- podman network rm $(NETWORK_NAME) || true
	@echo "Done."

status:
	@podman ps -a --filter "name=$(PG_CONTAINER_NAME)"
	@podman ps -a --filter "name=$(APP_CONTAINER1)"
	@podman ps -a --filter "name=$(APP_CONTAINER2)"

logs: logs-app1

