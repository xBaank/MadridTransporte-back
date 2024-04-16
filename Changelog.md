# Changelog

## 6.2.0

- Updated dependencies
- Added `/stops/{type}/times/{stopId}/planned` (Only available for type `bus` in this update)

## 6.1.14

- Fix notifications not working, caused
  by [Fold restriction not allowing any Function<*> to be returned is too strict](https://github.com/arrow-kt/arrow/issues/3391)

## 6.1.13

- Added retry policy when extracting emt data as an attempt to fix transient errors.

## 6.1.12

- Added more logging

## 6.1.11

- Fix emt exception on cases where it can't extract the url of incidents

## 6.1.10

- Add unsafe http for trains

## 6.1.9

- Updated mongo driver
- Added soap error logs

## 6.1.8

- Added gzip and deflate support
- Updated dependencies

## 6.1.7

- Fixed abono notification typo

## 6.1.6

- Fixed abono notifications bug that caused to receive the notification every second for an hour
- Updated dependencies

## 6.1.5

- Fixed EMT exception when getting times
- Updated dependencies

## 6.1.4

- Fixed metro stop times problem
- Fixed emt locations not being correct

## 6.1.3

- Fixed some cases that caused a 500 to be thrown with no response

## 6.1.2

- Improved stop times precision

## 6.1.1

- Fixed tran stops times

## 6.1.0

- Fixed some `500` status codes when extracting emt data.
- Added abono notifications.

## 6.0.0

- Readded `/lines/bus/{lineCode}/locations/{direction}?stopCode={stopCode}`
- Readded `/lines/emt/{lineCode}/locations/{direction}?stopCode={stopCode}`
- Readded `/lines/bus/{lineCode}/itineraries/{direction}?stopCode={stopCode}`
- Readded `/lines/emt/{lineCode}/itineraries/{direction}?stopCode={stopCode}`
- Removed `/lines/bus/kml/{itineraryCode}`
- Removed `/lines/emt/kml/{itineraryCode}`

This is because times does not return codItinerary, and we had to guess the codItinerary, this caused too many false
matches.
Now we just get the itinerary that has the same direction and stopCode.

## 5.1.0

- Added `/lines/bus/kml/{itineraryCode}`
- Added `/lines/emt/kml/{itineraryCode}`

## 5.0.0

- Removed `/lines/bus/{lineCode}/locations/{direction}`
- Removed `/lines/emt/{lineCode}/locations/{direction}`
- Removed `/lines/bus/{lineCode}/itineraries/{direction}`
- Removed `/lines/emt/{lineCode}/itineraries/{direction}`
- Added `distance` property to `/lines/bus/shapes/{itineraryCode}`
- Added `distance` property to `/lines/emt/shapes/{itineraryCode}`

## 4.2.0

- Added `/lines/bus/locations/{itineraryCode}`
- Added `/lines/emt/locations/{itineraryCode}`
- Added `itineraryCode` property for  `/stops/bus/{stopCode}/times`
- Added `itineraryCode` property for  `/stops/emt/{stopCode}/times`

## 4.1.5

- Fix train times not being parsed properly.

## 4.1.4

- Add notifications to Android.

## 4.1.3

- Fix train times.

## 4.1.2

- Remove duplicates stops.

## 4.1.1

- Return lineCode when getting emt times.

## 4.1.0

- Added `/stops/train/{stopCode}/times` endpoint

## 4.0.2

- Fix `abono` endpoint

## 4.0.1

- Update dependencies

## 4.0.0

- Remove `v1` from path
- Change `lines/bus/locations` return type with more info

## 3.2.5

- Added emt locations

## 3.2.4

- Fix emt times to always be in UTC format

## 3.2.3

- Fix notifications

## 3.2.2

- Add `SOAP_TIMEOUT` and `SUBSCRIPTIONS_LIMIT` as environment variables

## 3.2.1

- Remove repeated stops when getting itineraries

## 3.2.0

- Add `v1/lines/shapes/{itineraryCode}` endpoint

## 3.1.2

- Index data after inserting it to db and not before (Improves loading times)

## 3.1.1

- Memory optimization when loading db data

## 3.1.0

- Performance optimizations
- Re-add  `v1/stops/all` endpoint

## 3.0.1

- Fixed a bug that didn't allow the user to see certain line locations

## 3.0.0

- Removed `v1/bus/lines/stops`
- Removed `v1/bus/lines/locations`
- Removed `v1/bus/lines/itineraries`
- Added `v1/lines/bus/locations`
- Added `v1/lines/bus/itineraries`

## 2.0.1

- Improve performance

## 2.0.0

- Removed `v1/stops/all` endpoint
- Improve performance

## 1.0.15

- Improve performance

## 1.0.14

- Improve calculation of time left for metro

## 1.0.13

- Improve calculation of time left for metro

## 1.0.12

- Reduce cache time for metro and emt

## 1.0.11

- Update status codes to work with nginx cache stale

## 1.0.10

- Disable cache for 424 responses

## 1.0.9

- If a dependency fails for the route `times`, the app will respond with a 424 instead of a 200. But arrives will be
  null like before. This change is for cache purposes as nginx or other proxies will not cache the response if the
  dependency fails.

## 1.0.8

- Make soap **Fully async**

## 1.0.7

- Update dependencies

## 1.0.6

- Fix deadlock when CRTM Soap Server gives a timeout

## 1.0.6-RC

- Make all soap request with dispatcher IO

## 1.0.5

- Global scope for notifications

## 1.0.4

- Timeout for soap requests

## 1.0.3

- Use Dispatcher IO for notifications

## 1.0.2

- Log ALL

## 1.0.1

- Improve cache

## 1.0.0

- Release version 1.0.0

## 1.0.0-RC13

- Fixed an issue where the app would not return stop info if the soap request failed instead of giving a timeout

## 1.0.0-RC12

- Make arrives nullable to know if times couldn't be retrieved
- Check if stop exist when subscribing for notifications

## 1.0.0-RC11

- Remove invalid device tokens when sending push notifications