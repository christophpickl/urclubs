
# TODOs

## Business

* List upcoming workouts
* List past workouts
* List available workouts
* Concept of datamodel based on google docs doc
* Workout recommendation
    * Use DB and sync partners/workouts (on startup)
    * Store some metadata (preferred partner)
    * Simple suggestion of workout based on metadata

## Technical

* split into several modules (myclubs SDK, persistence, logic (recommend, gcal), UI)
* database migration concept (when adding new property, when changing package/typename, ...)
* Use http4k lib
* Configure: Travis, Versioneye
* Use tornadoFX UI
* Integrate in GCal (introduce gcal4k)
* debug HTTP traffic on android
