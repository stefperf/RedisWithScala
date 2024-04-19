# RedisDemo Scala Project

## Description
This Scala project demonstrates various operations using the Scala-Redis client library. It includes examples of working with different Redis data structures such as strings, lists, hashes, sets, sorted sets, HyperLogLog, and geospatial indexes.

## Features
Using various Redis data types:
- Connection to Redis with authentication.
- Operations on different Redis data types.
- Usage of Redis expiration features for keys.
- Geospatial operations to handle location-based data.
- Handling of time and expiration outputs in a human-readable format.

Using cross-cutting Redis features:
- Key expiration
- Persistence

## Prerequisites
- Scala 2.13.13 or higher
- SBT (Scala Build Tool) for building and running the project
- Redis server running on `localhost` with the default port `6379`

## Dependencies
This project uses the following primary dependencies:
- `scala-redis` client for interfacing with Redis.
- Java Time API for handling date and time.

Make sure your Redis server is configured with a password that matches the one used in the project (`insertAStrongRedisPasswordFromASecureStorage`).

## Setup and Running
1. Clone the repository:
   git clone <repository-url>
2. Navigate to the project directory:
   cd redis_with_scala
3. To bring up the needed Redis microservice, simply execute:
    docker-compose -f docker/docker-compose.yaml up -d 
4. Compile and run the project using SBT:
   sbt run

## Code Overview
- `RedisDemo`: Contains the main method which demonstrates Redis operations.
- `connectToRedisDb`: Helper method to connect to different Redis databases.
- `printExpiration`: Utility method to output key expiration details.

## Redis Configuration
Ensure your Redis server is up and running. If using a different host or port, adjust the `redisHost` and `redisPort` values in the `RedisDemo` object accordingly.

## Security Note
The Redis password is hardcoded for demonstration purposes. In a production environment, consider securing credentials using environment variables or configuration files.
