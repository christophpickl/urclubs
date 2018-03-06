
```-Durclubs.email=foo@bar.com -Durclubs.password=xxx -Durclubs.development```

# TODOs

## New Order

* minor UI
    * detail pack height

1. Replace google docs
    * ad table:
        * colored lines based on rating
        * context menu: ignore partner
    * ad detail view:
        * display idMyc + shortName
        * link to two pages
        * change rating
        * add note
        * checkboxes: favourite, wishlist
1. Fix search!
1. (Caching)
1. Add credits leftover
1. Activities list

### Long term:

* custom pictures for partners
* Per-category additional fields:
    * description where location is; zb near subway station
    * EMS: opening hours
    * Gym: got room Y/N, opening hours
* richtext format notes
* Book activities
* Recommend activities
* Google cal integration

## High Prio

* observable entities
* MyClubs API caching + deferred login
* MenuBar => Develop / Create dummy data
* BUG: when filter, update meanwhile => change observable in background

## Business

* List upcoming workouts
* List past workouts
* List available workouts
* Concept of datamodel based on google docs doc
* Workout recommendation
    * Use DB and sync partners/workouts (on startup)
    * Store some metadata (preferred partner)
    * Simple suggestion of workout based on metadata
* show number of displayed partners "10/170" based on current filter 

## Technical

* introduce some kind of PROFILE
    * DEV ... use other directory, enable critical shortcuts
    * PROD (must be explicitly declared) ... using real folder/db
    * (TEST)
* Configure: Versioneye
* Use http4k lib
* Integrate in GCal (introduce gcal4k)
* debug HTTP traffic on android


* create own view model? => https://github.com/edvin/tornadofx/wiki/Type-Safe-Builders
