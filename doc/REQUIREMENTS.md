# Top 3 Reasons

... why to write a custom MyClubs client.

1. Additional Metadata
    * Partners additional have got:
        - rating
        - ignored flag (only for women)
        - wishlist flag ( i want to go there some time)
        - image (pictures are easier to recognize than the textual name)
        - notes (custom text containing personal description)
        - address (link to google maps, additional comment like "near U4", distance from current position)
        - opening hours (for infrastructures like gyms/EMS)
        - credits (maximum, available this booking period)
        - category (simplified version of tags)
        - phone number (as EMS usually needs pre-registration)
        - website (myclubs and partner site as clickable links)
    * See all visited ("finished") activities so far
        - A per-partner list of the complete history
        - Compute how many days since last visit
        - Compute total number of visits
        - Figure out un-visited partners
    * Categories themselves can be rated
1. Recommendation System
    * Filter based on personal preference and given history/metadata
    * Make use of metadata (rating) to get some kind of ranking (score system)
    * Hide "bad" partners and "unwanted" categories (marked as such by customer)
    * Hide activities too far away (e.g. trans-danube)
    * Proper location based search
    * Different recommendation "styles", e.g.: "Exploration" - list all yoga partner's if not yet visited
1. Calendar Integration
    * Only display activities which are possible due to free timeslot
    * Automatically create calendar entry (location, web address)
    * Display my calendar side-by-side with potential activities (scroll through days)

## Other reasons

* Some features are only available via the smartphone app, such as:
    - Display remaining credits for a certain partner
    - "Redo": Scroll through activity history and click on it to go to the same partner (plus future activities) 
* Display long-term statistics
    - most importantly how many credits this month = fair use policy
* An embedded map is not considered to be useful enough as such a huge consumption of space would be justified
    - maybe the distance (in km) to the current location could be useful
    - the given address (and a short note like "near U4") should be enough for judgment whether to go there or not
    - an external link to google map should be enough if the address is not known 

# Google Docs Reference

This is how to original document looks/ed like:

![google_docs](https://raw.githubusercontent.com/christophpickl/urclubs/master/doc/img/google_docs.png "Google Docs")

Based on this it can be inferred what the target client should at least support.

# Big Features

## Additional Metadata for partners

* there should be a "good" overview of all relevant partners, compact, containing all relevant information, making best use of space
* a table would suit that need best, as it gives you an overview over many partners on a single screen
* there needs to be a special grouping
    * the predefined tags are not sufficient, each partner should be possible to assign a custom group
    * based on that group, other fields apply, e.g.:
        * gym => room available, wellness
        * yoga => atmosphere, training
        * ems => phone number
        * ~infrastructure (EMS, gym) => opening hours
    * some of them have an even further sub-category, e.g.:
        * wushu => WT/TJ/...
        * crossfit => all-natural/stressy-hip/coordination
        * yoga => bikram/hot/regular
        * but some partners got many different tags, which would be represented by many different categories
* the following fields should be available:
    * OK ... name (custom, and myclubs default)
    * OK ... link (myclubs site + individual site; clickable)
    * OK ... picture
    * OK ... address
    * rating + short comment
    * location + short comment
    * OK ... ignored (don't want to see anymore)
    * notes (support HTML)
    * OK ... credits (available + left)
    * OK ... how many days since last activity visited
    * OK ... how many activities in total
    * OK ... concerning activities:
        * OK ... how many credits left this month (out of X)
        * past history so far

## Recommendation System

* sophisticated algorithm which proposes only relevant activities
    * make heavy use of metadata and past history (maybe even geo location)
    * don't recommend for example EMS again, if already went there x days ago
* when simply scrolling through all activities, enable filters:
    * category
    * available because of gcal empty timeslot
    * yet unvisited (when looking for new adventure)
    * rating

## GCal integration

* in order to know which activities to propose (when "strict time filter" is enabled) because of availability
* create gcal entry automatically with all necessary metadata and URLs in description
    - for those with booking: first enter with "xxx ???" and then remove "???" when confirmed
* see personal calendar and future activities side-by-side in some (daily) calendar view
    - colorize different categories differently

# Scenarios

## I want to go to EMS

* i know when approx i want to go
    - e.g. within next 3 days (maybe even check calendar for available time slots)
* i need to have enough credits
    - filter out partners with 0 credits left
    - prefer those with higher count available
* only specific studios based on metadata
    - the ones i rated as being good/visited/wishlisted (show also "unvisted" down below)
* book a course
    - phone number is displayed
    - use regular gcal view in browser to create entry (maybe same sync as with gadsu?!)

## I want to mark an activity as "todo"

* sometimes i see something interesting but got no time right now to attend that course/partner
* marking as "todo" represented by a "wishlist" flag
* later on i want to scroll through that list and see all wishlisted partners

## I want to explore myclubs

* there are so many yoga clubs out there, i want to have tried all of them
* based on my finished activities, show me a list of all unvisited partners with category yoga
* based on that list, figure out the one which suits best
    - wishlisted first
    - closest address (distance)
* do a "check in on site" and create gcal calendar
