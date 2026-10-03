.DEFAULT_GOAL := build

setup: build-frontend
	./gradlew installDist

build-frontend:
	npm ci
	npx build-frontend

start:
	./gradlew bootRun --args="--spring.profiles.active=dev"

start-prod:
	./gradlew bootRun --args="--spring.profiles.active=prod"

start-dist:
	./build/install/app/bin/app

clean:
	./gradlew clean

build:
	./gradlew clean build

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

lint:
	./gradlew spotlessCheck

lint-fix:
	./gradlew spotlessApply

check-deps:
	./gradlew dependencyUpdates -Drevision=release

.PHONY: build test lint setup start
