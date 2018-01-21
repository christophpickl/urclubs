# MyClubs Client

This is a custom client to manage [MyClubs](https://www.myclubs) offers.

## TODOs

### Business

* List upcoming workouts
* List past workouts
* List available workouts
* Concept of datamodel based on google docs doc
* Workout recommendation
    * Use DB and sync partners/workouts (on startup)
    * Store some metadata (preferred partner)
    * Simple suggestion of workout based on metadata

### Technical

* Use htt4pk lib
* Configure: Travis, Versioneye
* Use tornadoFX UI
* use Guice
* Integrate in GCal (introduce gcal4k)

# API reverse engineered

* Base URL: https://www.myclubs.com/api

## General remarks

* There is no proper API, but rather it returns pre-rendered HTML (which needs to be parsed)
* Sometimes there is JSON returned (login response)
* Requests are all POST
* Response got plain-text text " success" or " fail" (including leading whitespace)

## User Management

### Login

* `POST /login`
* Form data:
    * email: ...
    * password: ...
    * staylogged: true
* Response:
    * " fail" on error
    * otherwise store session cookie for successful subsequent calls

### Current user

* `POST /getLoggedUser` returns "0" on error, otherwise proper JSON payload

## Partners

* `POST /activities-get-partners`
* Form data:
    * country: at
    * city: wien
    * language: de

## Activities

* `POST /activities-list-response`
* Form data:
    * filters: (JSON string)
        * categories:[]
        * date 20.01.2018
        * time 16:00, 23:00
        * favourite: false
        * city: wien
        * partner:
        * type: infrastructure, course
    * country: at
    * language: de

## Other endpoints

* `/categories-response`
* `/cities-response`
