
* UI: change hover table color; selected light orange, und hover noch lighter machen
* UI: on change, then table row order changes? by default, if rows are equal, should order by name
* when tag "crosstraining" => if category not set, set to workout
* change table vertical borders color

# Grob Plan

1. Filter Partners
1. Upcoming Activities (Fetch+Filter)
1. GCal Integration
1. Book Activities

# Detailed Plan

## Must

1. !! FIX: myclubs caching
1. !! FILTER:
    * only wishlist/favourit
    * rating (is bigger/lower/equals)
    * remainingCredits/totalVisits (lower, equal, bigger, not)
    * think about how to "smart filter" => predefined queries (SQL?)
1. ! when sync is done, request focus for confirm panel
1. ! splash screen doesnt go away
1. ! UI: get mac menubar working
1. UI: improve sync progress dialog UI (show indeterministic progress Bar; NO listener/event thing)
1. UI: ad partner table fill FULL colored rows based on rating
1. UI: colorize remaining credits (green-red much-few)

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

1. proper exception handling
* show save confirm dialog on changes (detect dirty view)
* BIZ: sync more Partner details (description, ...)
* add new properties: 
    * locationNote (render in table next to static location address)
    * add secondary comment for partners (not visible in table but in detail view)
* global notes
* support MacMenuBar events (quit, about, prefs)

## Med

* rethink myclubs API for searching activity (isnt activityID only enough? how does API work?)
* UI: style: scrollbar
* UX: on save, give some feedback
* use calendar component, show 1) gcal and 2) upcoming events (scroll through single days)
* add translation (EN, DE; changeable via prefs, initially detected by OS lang)
* for address: make link clickable and go to google maps
* UI: send notifications via MacOs (e.g. after sync)
* resolve: org.hibernate.orm.connections.pooling - HHH10001002: Using Hibernate built-in connection pool (not for production use!)
    * resolve o.h.e.j.c.internal.DriverManagerConnectionProviderImpl - Connection leak detected: there are 1 unclosed connections upon shutting down pool jdbc:hsqldb:file:/Users/wu/.urclubs_dev/database/database
* wenn woanders hinclicken, dann kein dirty check... changes lost
* @parseCourses: parse proper time ???
* BIZ: support activity types: fixed-time, book-now, drop-in
    * @parseInfrastructure: "Book Now", "Drop In" => used to infer type (OPEN, RESERVATION_NEEDED => show phone number)

## Low

* in choose image file dialogÖ dont display hidden items
* show number of displayed partners "10/170" based on current filter
* UI: field labels: make em bold
* UX: image should have orange border, on hover change color
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
