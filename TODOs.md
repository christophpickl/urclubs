
# Grob Plan

1. must do
    1. myclubs caching
    1. improve sync progress dialog UI
    1. get mac menubar working
    1. BUG: after initial import, credits left this month is not updated!
    1. render address
    * dont display short name
    * render favourite+wishlist in table (as icon) + make it a clickable icon in detail (only outlines / filled)
    * BUG: when partner updaten, dann select springt woanders in table hin
    * !!! BUG: manchmal funktioniert speichern einfach nicht (wenn woanders hinclicken, dann kein dirty check... changes lost) 

1. immediately after
    1. UI: incorporate partner detail view into main window (get rid of additional window)
    1. in partner detail view: add button to artificially increase visit counts (reason: only past 70s are synced :-()
    1. enhance search:
        * rating (is bigger/lower/equals)
        * remainingCredits/totalVisits (lower, equal, bigger, not)
        * improved search: think about how to "smart filter" => predefined queries (SQL?)
    1. proper exception handling
    1. splash screen doesnt go away

1. next big step
    1. list upcoming activities
    1. list my booked activities
    1. resolve: org.hibernate.orm.connections.pooling - HHH10001002: Using Hibernate built-in connection pool (not for production use!)
    1. resolve o.h.e.j.c.internal.DriverManagerConnectionProviderImpl - Connection leak detected: there are 1 unclosed connections upon shutting down pool jdbc:hsqldb:file:/Users/wu/.urclubs_dev/database/database

# TODOs

## Big Ideas

* create Gcal events
* book activities
* Recommend activities
    * Use DB and sync partners/workouts (on startup)
    * Store some metadata (preferred partner)
    * Simple suggestion of workout based on metadata
* Per-category dynamic fields:
    * description where location is; zb near subway station
    * EMS: opening hours, phone number
    * Gym: opening hours, got room Y/N

## High

* UI: ad partner table fill FULL colored rows based on rating
* UI: colorize remaining credits (green-red much-few)
* show number of displayed partners "10/170" based on current filter

## Med

* add new properties: 
    * locationNote (render in table next to static location address)
    * add secondary comment for partners (not visible in table but in detail view)
* global notes
* UI: send notifications via MacOs (e.g. after sync)
* support MacMenuBar (handle QuitEvent, outsource menu items into system's Application item)
* @parseCourses: parse proper time ???
* @parseInfrastructure: "Book Now", "Drop In" => used to infer type (OPEN, RESERVATION_NEEDED => show phone number)

## Low

* BIZ: support activity types: fixed-time, book-now, drop-in
* BIZ: sync more Partner details (description, flags, ...)
* richtext format notes
* colorize location (=distance) => property Partner.locationRating (rename regular rating to "partnerRating")
* UI: finished activities visualize count as "baelkchen"

## Technical

* inject MyClubsHttpApi.baseUrl, in order to make it fakeable/testable (integration tests with wiremock)
* CurrentPartnerFx.original: Partner is hacky
* inject MyclubsUtil, in order to make it fakeable/testable (integration tests with wiremock)
* MyClubsHttpApi: test for not found activity
* myclubs testng group, doing HTTP requests + parse, run on travis
* Configure: Versioneye
* debug HTTP traffic on android; reverse engineer requests, e.g.: count of available credits

