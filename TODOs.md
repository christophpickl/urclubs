
# Grob Plan

1. must do
    1. Caching for myclubs API

1. immediately after
    1. proper exception handling
    1. Minor: change app icon (turn around the U)
    1. Minor: Version number by gradle resource filtering (display in About dialog)
    1. add new properties: 
        * locationNote (render in table next to static location address)
        * add secondary comment for partners (not visible in table but in detail view)
        * global notes
    1. style evertyhing; use myclubs colors (black bg, yellow font)
        * create `FakePersistenceModule` (disable migration and DB stuff for faster startup)
    1. enhance search:
        * rating (is bigger/lower/equals)
        * remainingCredits/totalVisits (lower, equal, bigger, not)

1. more-over
    * List upcoming workouts
    * improved search: think about how to "smart filter" => predefined queries (SQL?)
    * BIG: Create gcal entries
    * richtext format notes

# TODOs

## Coming up:

* Per-category dynamic fields:
    * description where location is; zb near subway station
    * EMS: opening hours, phone number
    * Gym: opening hours, got room Y/N

### Minor:

* send notifications via MacOs (e.g. after sync)
* colorize location (=distance) => property Partner.locationRating (rename regular rating to "partnerRating")
* colorize remaining credits (green-red much-few)
* finished activities visualize count as "baelkchen"
* ad table: FULL colored lines based on rating
* show number of displayed partners "10/170" based on current filter
* support MacMenuBar (handle QuitEvent, outsource menu items into system's Application item)

### Long term:

* Book activities
* Recommend activities
    * Use DB and sync partners/workouts (on startup)
    * Store some metadata (preferred partner)
    * Simple suggestion of workout based on metadata

## Technical

* myclubs testng group, doing HTTP requests + parse, run on travis
* resolve: org.hibernate.orm.connections.pooling - HHH10001002: Using Hibernate built-in connection pool (not for production use!)
* resolve o.h.e.j.c.internal.DriverManagerConnectionProviderImpl - Connection leak detected: there are 1 unclosed connections upon shutting down pool jdbc:hsqldb:file:/Users/wu/.urclubs_dev/database/database
* Configure: Versioneye
* debug HTTP traffic on android
    - reverse engineer requests, e.g.: count of available credits

