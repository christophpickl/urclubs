# MyClubs API reverse engineered

* Base URL: https://www.myclubs.com

## General remarks

* There is no proper API, but rather it returns pre-rendered HTML (which needs to be parsed)
* Sometimes there is JSON returned (login response)
* Requests are all POST
* Response got plain-text text " success" or " fail" (including leading whitespace)

## User Management

### Login

* `POST /api/login`
* Form data:
    * email: ...
    * password: ...
    * staylogged: true
* Response:
    * " fail" on error
    * otherwise store session cookie for successful subsequent calls

### Current user

* `POST /api/getLoggedUser` returns "0" on error, otherwise proper JSON payload

## Partners

### List all

* `POST /api/activities-get-partners`
* Form data:
    * country: at
    * city: wien
    * language: de

### Details

* `GET /at/de/partner/{shortName}`
* returns big HTML file

## Activities

### List all

* `POST /api/activities-list-response`
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

### Detail

* `POST /api/activityDetail`
* Form data:
    * activityData: Pf5FowjC0n
    * type: course
    * date: 1516705200
    * country: at
    * language: de

## Profile

* `GET /at/de/profile`
* Parse that HTML ...
* Contains:
    * Finished Activities
    * ...

## Feedback

* `POST /api/submitFeedback`
* Form data:
    * bookingid: PDTCBLnzPX (the activity ID)
    * value: 4 (the rating)

## Other endpoints

* `/api/categories-response`
* `/api/cities-response`
