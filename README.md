# Ziggy Backend

A Scala 3 backend prototype for a food delivery platform built with Apache Pekko, Slick, PostgreSQL, and Kafka.

> Status: active work in progress. The codebase already shows the intended architecture and core flows, but the current branch still has compilation issues and a few incomplete integrations.

## Overview

Ziggy models the backend of a delivery platform with:
- customer registration and login
- cookie-based authentication
- order placement APIs
- actor-driven order orchestration
- delivery partner assignment based on city and serviceable pin codes
- PostgreSQL persistence through Slick
- Kafka scaffolding for restaurant-related events

## Tech Stack

- Scala 3.8.1
- sbt 1.12.4
- Apache Pekko HTTP, Actors, Streams
- Slick + HikariCP
- PostgreSQL
- Apache Kafka + Pekko Kafka Connector
- Circe
- Guice
- BCrypt
- Custom JWT-based auth
- Docker Compose

## Current State

This repository is best described as a backend prototype / architecture draft.

What is already present:
- auth routes for register and login
- order placement flow
- restaurant and delivery actors
- database schemas/tables for users, restaurants, partners, orders, items, and addresses
- Kafka producer/consumer/event-mapping layer

What still needs work:
- current branch does not compile successfully
- some modules are partially implemented or not wired into startup
- config values need cleanup and environment-based externalization
- automated tests are not included yet

## Architecture

1. `api` exposes HTTP endpoints under `/api`
2. `controller` handles request orchestration
3. `service` contains business logic for auth, orders, partners, and restaurants
4. `actor` models order lifecycle and delivery partner assignment
5. `database` contains Slick schemas and table access layers
6. `kafka` provides async event publishing/consuming for restaurant workflows

## Main API Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/health` | Health check |
| POST | `/api/auth/register` | Register a customer |
| POST | `/api/auth/login` | Login and set `AUTH_TOKEN` cookie |
| POST | `/api/order/place` | Place an order |
| `*` | `/api/order/delivered/{orderId}` | Mark an order as delivered |

Note: utility routes for restaurant event publishing exist in the codebase, but they are not currently mounted in the root router.

## Project Structure

```text
src/main/scala/com/ziggy
├── api/          # Pekko HTTP routes + JSON support
├── controller/   # Request orchestration
├── actor/        # Restaurant and delivery actors
├── service/      # Business logic
├── database/
│   ├── model/    # Domain models
│   ├── schema/   # Slick schemas
│   └── table/    # DB access layer
├── kafka/        # Event producer/consumer/services
├── utils/        # Config, logging, auth helpers
├── AppModule.scala
└── Main.scala

```md
### Run the app

```bash
sbt compile
sbt run
> Note: This project is ongoing and may change as development continues.
