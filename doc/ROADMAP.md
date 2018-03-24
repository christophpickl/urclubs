
# Grob Plan

1. One
    1. Filter
    1. Single window
1. Two
    1. Upcoming Activities
    1. GCal
1. Three
    1. Book Activities
    1. Recommend Activities

# TODOs

1. Pre One
    1. !!! BUG: manchmal funktioniert speichern einfach nicht
    1. !! FIX: myclubs caching
    1. ! style: context menu!, scrollbar 
    1. in partner detail view: add button to artificially increase visit counts (reason: only past 70s are synced :-()

1. Ad One
    1. ! UI: improve sync progress dialog UI (show indeterministic progress Bar; NO listener/event thing)
    1. UI: get mac menubar working
    1. UI: incorporate partner detail view into main window (get rid of additional window)
    1. enhance search:
        * rating (is bigger/lower/equals)
        * remainingCredits/totalVisits (lower, equal, bigger, not)
        * improved search: think about how to "smart filter" => predefined queries (SQL?)
    1. proper exception handling
    1. splash screen doesnt go away

1. Ad Two
    1. UI: ad partner table fill FULL colored rows based on rating
    1. UI: colorize remaining credits (green-red much-few)
    1. show number of displayed partners "10/170" based on current filter

## Big Ideas

* Recommend activities
    * Use DB and sync partners/workouts (on startup)
    * Store some metadata (preferred partner)
    * Simple suggestion of workout based on metadata
* Per-category dynamic fields:
    * description where location is; zb near subway station
    * EMS: opening hours, phone number
    * Gym: opening hours, got room Y/N

## High

* BIZ: sync more Partner details (description, ...)
* add new properties: 
    * locationNote (render in table next to static location address)
    * add secondary comment for partners (not visible in table but in detail view)
* global notes
* support MacMenuBar (handle QuitEvent, outsource menu items into system's Application item)

## Med

* for address: make link clickable and go to google maps
* UI: send notifications via MacOs (e.g. after sync)
* resolve: org.hibernate.orm.connections.pooling - HHH10001002: Using Hibernate built-in connection pool (not for production use!)
    * resolve o.h.e.j.c.internal.DriverManagerConnectionProviderImpl - Connection leak detected: there are 1 unclosed connections upon shutting down pool jdbc:hsqldb:file:/Users/wu/.urclubs_dev/database/database
* wenn woanders hinclicken, dann kein dirty check... changes lost
* @parseCourses: parse proper time ???
* BIZ: support activity types: fixed-time, book-now, drop-in
    * @parseInfrastructure: "Book Now", "Drop In" => used to infer type (OPEN, RESERVATION_NEEDED => show phone number)

## Low

* UI: render checkbox for favourite/wishlist as images
* richtext format notes
* colorize location (=distance) => property Partner.locationRating (rename regular rating to "partnerRating")
* UI: finished activities visualize count as "baelkchen"

## Technical

* debug HTTP traffic on android; reverse engineer requests, e.g.: count of available credits
* CurrentPartnerFx.original: Partner is hacky
* BUILD: Configure: Versioneye
* TEST: inject MyclubsUtil, in order to make it fakeable/testable (integration tests with wiremock)
* TEST: inject MyClubsHttpApi.baseUrl, in order to make it fakeable/testable (integration tests with wiremock)
* TEST: myclubs testng group, doing HTTP requests + parse, run on travis
* TEST: MyClubsHttpApi: test for not found activity
