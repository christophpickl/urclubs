# Top 3 reasons why to write a custom MyClubs client

1. Support of additional metadata
    * Partners can be rated, have images, notes, location, opening hours, 2/4 credits, multiple tags ("categories"), phone number, website
    * See complete history of all visited activities
    * Custom grouping (which can be rated in general)
1. "Better" recommendation system
    * Don't just display all activities, but rather filter based on personal preference
    * Make use of metadata (rating) to get some kind of ranking (score system)
    * Hide "bad" partners and "unwanted" categories
    * Hide activities too far away (e.g. trans-danube)
    * Proper location based search
    * Different recommendation styles, e.g.: "Exploration" - list all yoga partner's if not yet visited
1. Calendar integration
    * Only display activities which are possible due to free timeslot
    * Automatically create calendar entry (location, web address)

## Other reasons

* Some features are only available via the smartphone app, such as:
    * Display remaining credits for a certain partner
    * "Redo": Scroll through activity history and click on it to go to the same partner (plus future activities) 
* Display long-term statistics (most importantly how many credits this month = fair use policy)

## Google Docs Reference

![google_docs](https://raw.githubusercontent.com/christophpickl/urclubs/master/doc/img/google_docs.png "Google Docs")

# Additional Metadata for partners

* there should be a "good" overview of all relevant partners
* a table would suit that need best, as it gives you an overview over many partners at a single screen
* there needs to be a special grouping
    * the predefined groupings are not sufficient, each partner should be possible to assign a custom group
    * based on that group, other fields apply, e.g.:
        * gym => room available
        * yoga => atmosphere, training
        * ems => phone number
        * ~infrastructure (EMS, gym) => opening hours
* the following fields should be available:
    * name (custom, and myclubs default)
    * link (myclubs site + individual site)
    * picture
    * rating + short comment
    * location + short comment
    * notes (support HTML)
    * concerning activities:
        * how many credits left this month (out of X)
        * ~past history so far

# Recommendation System

TODO

# GCal integration

TODO
