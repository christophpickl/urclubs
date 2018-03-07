
```-Durclubs.email=foo@bar.com -Durclubs.password=xxx -Durclubs.development```

# Grob Plan

1. must do
    * custom pictures for partners
    * colorize location (=distance)
    1. Fix search! BUG: when filter, update meanwhile => change observable in background
    1. (Caching)
    1. sync past activities
    1. Add remaining credits
    
1. immediately after
    1. enhance search:
        * rating (is bigger/lower/equals)
        * remainingCredits/totalVisits (lower, equal, bigger, not)
    1. DevMode rework: by default use .dev folder, only when -Dprod defined use prod db

1. Activities list in detail
    * List upcoming workouts
    * List past workouts
    * List available workouts

# TODOs

## Coming up:

* Per-category dynamic fields:
    * description where location is; zb near subway station
    * EMS: opening hours, phone number
    * Gym: opening hours, got room Y/N
* richtext format notes

### Minor:

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
