# Simple REST API for Money Transfer

* Very simple implementation - just a toy project
* No heavy frameworks
* Good code quality
* Good unit tests

## REST API

1. Create Account

```
curl -X POST -H "Content-Type: application/json" \
 -d '{"name": "John Smith", "currency": "EUR", "balance": "1000.0"}' \
 http://localhost:7000/v1/accounts/create
```

2. Find Account by id

```
curl -X GET http://localhost:7000/v1/accounts/1
```

3. Transfer Money

```
curl -X POST -H "Content-Type: application/json" \
 -d '{"from": "1", "to": "2", "currency": "EUR", "amount": "1000.0"}' \
 http://localhost:7000/v1/transfers
```

## Running the tests

```
mvn clean test
```

## Running

Standalone application. Run sidomik.samples.transfermoney.App::main

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management