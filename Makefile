setup:
	make -C app setup

clean:
	make -C app clean

build:
	make -C app build

start:
	make -C app start

start-prod:
	make -C app start-prod

install:
	make -C app install

start-dist:
	make -C app start-dist

lint:
	make -C app lint

test:
	make -C app test

report:
	make -C app report

check-updates:
	make -C app check-updates

generate-migrations:
	make -C app generate-migrations

db-migrate:
	make -C app db-migrate

.PHONY: build
