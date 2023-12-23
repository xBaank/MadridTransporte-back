# Changelog

## 3.2.0

- Add `v1/lines/shapes/{id}` endpoint.

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
- Removed `v1/bus/lines/ocations`
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