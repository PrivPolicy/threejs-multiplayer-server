# Three.JS Multiplayer Server

Backend implementation of a server for [Three.JS Multiplayer Client](https://github.com/PrivPolicy/threejs-multiplayer-client).

## Table of Contents
- [Introduction](#introduction)
- [Installation and setup](#installation-and-setup)
  - [Database schema](#database-schema)
- [Future Development](#future-development)

## Introduction
Backend implementation for communicating with clients and database, written in Kotlin + PostgreSQL.

Project works with conjunction with respective [client](https://github.com/PrivPolicy/threejs-multiplayer-client).

## Installation and setup
- Clone or download the repository
- Build the project

This will create a `\target` directory with `.jar` file inside

### Database schema
#### Levels
| Field name     | Type       | Attributes                               | Default |
|----------------|------------|------------------------------------------|---------|
|id              |int(11)     | NOT NULL, PRIMARY KEY, AUTO_INCREMENT    |None     |
|difficulty      |difficulty  | NOT NULL                                 |None     |
|data            |text        | NOT NULL                                 |None     |
#### Difficulty
enum('easy', 'medium', 'hard');

## Future Development
There are no plans regarding future development of this project