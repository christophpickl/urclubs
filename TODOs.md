
# Grob Plan

1. must do
    1. prompt for user/pass on myclubs login; inject some kind of CredentialsProvider into MyclubsApi
    1. Fix search! BUG: when filter, update meanwhile => change observable in background
    1. Caching
    1. sync past activities
    1. display:
        * remaining credits (colorize green-red much-few)
        * count past activities (mehr platz einnehmende "baelkchen")
    
1. immediately after
    * colorize location (=distance) => property Partner.locationRating (rename regular rating to "partnerRating")
        + add new property: locationNote (render in table next to static location address)
    1. enhance search:
        * rating (is bigger/lower/equals)
        * remainingCredits/totalVisits (lower, equal, bigger, not)
    1. DevMode rework: by default use .dev folder, only when -Dprod defined use prod db

1. Activities list in detail
    * List upcoming workouts
    * List past workouts

# TODOs

## Coming up:

* Per-category dynamic fields:
    * description where location is; zb near subway station
    * EMS: opening hours, phone number
    * Gym: opening hours, got room Y/N
* richtext format notes

### Minor:

* MacMenuBar
* send notifications via MacOs (e.g. after sync)
* myclubs testng group, doing HTTP requests + parse, run on travis
* think about how to "smart filter" => predefined queries (SQL?)
* ad table: FULL colored lines based on rating
* show number of displayed partners "10/170" based on current filter

### Long term:

* Book activities
* Recommend activities
    * Use DB and sync partners/workouts (on startup)
    * Store some metadata (preferred partner)
    * Simple suggestion of workout based on metadata
* Google cal integration

## Technical

* resolve: org.hibernate.orm.connections.pooling - HHH10001002: Using Hibernate built-in connection pool (not for production use!)
* introduce some kind of PROFILE
    * DEV ... use other directory, enable critical shortcuts
    * PROD (must be explicitly declared) ... using real folder/db
    * (TEST)
* Configure: Versioneye
* Use http4k lib
* debug HTTP traffic on android
* create own view model? => https://github.com/edvin/tornadofx/wiki/Type-Safe-Builders
* maybe introduce myclubsMetadata object?? categoryMyc?
