* !!! upcoming activities; suche ermoeglichen (zb "acro yoga")
* BUG: 21:51:35.276 ERROR [tornadofx-thread-1              ] c.g.c.urclubs.service.sync.FinishedActivitySyncer - Could not find partner for: FinishedActivityHtmlModel(date=2017-12-06T16:40, category=Kampfkunst, title=Wing Chun, locationHtml=Center 6<br>Kurzgasse 6, 1060 Wien)
*  global notes feld machen. 
* verify bugfix: when new finished activity, then partner updated, then changing addresses should happen on FX thread
* ! BUG: adresse loeschen funkt nicht gscheit bei dubletten
* wenn textfield, text zu lang und "..." rendered => tooltip mit vollem text zeigen
* sync report: possibility to open full report (which partners got inserted/deleted, etc...)
* partner creation data einfuehren, sortierbar in UI machen als column (wenn neuer partner synced, sofort identifizieren koennen)
* regelmaessig auf platte die DB syncen (wegen dropbox sync)

# Grob Plan

1. Upcoming Activities
    * implement myclubs fetch/parser logic
    * implement sync logic
        - persisted as stand alone entities (OneMany and ManyOne)
    * implement UI list/table
    * implement filter
    * BUT: when fetch for partners, always only fetch future activities (not those happened in past)
1. GCal Integration
    * create dates (prefilled metadata)
        - store reference in local database
    * use calendar component, show 1) gcal and 2) upcoming events (scroll through single days)
1. Book Activities

# Detailed Plan

## Must

* UI: icons for categories (in table, in combobox)
* ! proper exception handling
* when sync is done, request focus for confirm panel
* UI: improve sync progress dialog UI (show indeterministic progress Bar; NO listener/event thing)
* UI: ad partner table fill FULL colored rows based on rating
* UI: colorize remaining credits (green-red much-few)
* UI: get mac menubar working ... javafx and macapp bundle dont play well together :-/ 

## Big Ideas

* think about how to "smart filter"
    * predefined queries a la SQL, stored by name
    * like itunes smart playlists (see omov)
    * explicitly hit "search" button VS implicit live filtering
    * remember last filter after app restart (store in prefs; if version mismatch, simply clear, no migration!)
* Recommend activities
    * Use DB and sync partners/workouts (on startup)
    * Store some metadata (preferred partner)
    * Categories themselves can be rated
    * Simple suggestion (pseudo-swipe) of workouts based on metadata
* Per-category dynamic fields:
    * description where location is; zb near subway station
    * EMS: opening hours, phone number
    * Gym: opening hours, got room Y/N

## High

* when enter search text, not only look for 'name' but + note + addresses (+ link?)
* UI: on change, then table row order changes? by default, if rows are equal, should order by name
* show save confirm dialog on changes (detect dirty view)
* BIZ: sync more Partner details (description, ...)
* add new properties: 
    * locationNote (render in table next to static location address)
    * add secondary comment for partners (not visible in table but in detail view)
* global notes
* support MacMenuBar events (quit, about, prefs)
* show number of displayed partners "10/170" based on current filter

## Med

* NOPE YET: filter by credits left
* UI: rating as rendered star icons
* rethink myclubs API for searching activity (isnt activityID only enough? how does API work?)
* UX: on successfully saved, give some feedback (blink/flash)
* UI change table vertical borders color
* in choose image file dialog dont display hidden items

## Low

* always show vertical scrollbar in partners table
* create custom about dialog (Stage class, reuse for mac and non-mac)
* code quality: https://app.codacy.com
* UI: style: scrollbar
* UI: send notifications via MacOs (e.g. after sync)
* resolve: org.hibernate.orm.connections.pooling - HHH10001002: Using Hibernate built-in connection pool (not for production use!)
    * resolve o.h.e.j.c.internal.DriverManagerConnectionProviderImpl - Connection leak detected: there are 1 unclosed connections upon shutting down pool jdbc:hsqldb:file:/Users/wu/.urclubs_dev/database/database
* @parseCourses: parse proper time ???
* BIZ: support activity types: fixed-time, book-now, drop-in
    * @parseInfrastructure: "Book Now", "Drop In" => used to infer type (OPEN, RESERVATION_NEEDED => show phone number)
* how to get older events from myclubs api?
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
