
# Grob Plan

1. must do
    1. Caching for myclubs API
    
1. immediately after
    1. DevMode rework: by default use .dev folder, only when -Dprod defined use prod db
    1. add new properties: 
        * locationNote (render in table next to static location address)
        * add secondary comment for partners (not visible in table but in detail view)
        * global notes
    1. style evertyhing; use myclubs colors (black bg, yellow font)
    1. enhance search:
        * rating (is bigger/lower/equals)
        * remainingCredits/totalVisits (lower, equal, bigger, not)

1. more-over
    * List upcoming workouts
    * Create gcal entries
    * improved search: think about how to "smart filter" => predefined queries (SQL?)

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
* colorize location (=distance) => property Partner.locationRating (rename regular rating to "partnerRating")
* colorize remaining credits (green-red much-few)
* finished activities visualize count as "baelkchen"
* ad table: FULL colored lines based on rating
* show number of displayed partners "10/170" based on current filter

### Long term:

* Book activities
* Recommend activities
    * Use DB and sync partners/workouts (on startup)
    * Store some metadata (preferred partner)
    * Simple suggestion of workout based on metadata

## Technical

* myclubs testng group, doing HTTP requests + parse, run on travis
* resolve: org.hibernate.orm.connections.pooling - HHH10001002: Using Hibernate built-in connection pool (not for production use!)
* Configure: Versioneye
* debug HTTP traffic on android
    - reverse engineer requests, e.g.: count of available credits

