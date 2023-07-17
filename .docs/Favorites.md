# Favorites
## Add a favorite
Add a favorite to the user's favorites list.
To add a favorite you need to send a POST request to the following URL:
`{url}/v1/favorites`

The body of the request must be a JSON object with the following fields:
- `name`: The name of the favorite.
- `stopType`: The type of the stop.
- `stopId`: The id of the stop.

The response code will be `201` or `401` or `400`.

## Get a favorite
Get a favorite from the user's favorites list.
To get a favorite you need to send a GET request to the following URL:
`{url}/v1/favorites/{favoriteId}`

The response code will be `200` or `401` or `400`.
The response will contain a JSON object with the following fields:
- `name`: The name of the favorite.
- `email`: The email of the user.
- `stopType`: The type of the stop.
- `stopId`: The id of the stop.

## Get all favorites
Get all favorites from the user's favorites list.
To get all favorites you need to send a GET request to the following URL:
`{url}/v1/favorites`

The response code will be `200` or `401` or `400`.
The response will contain a JSON array of objects with the following fields:
- `name`: The name of the favorite.
- `email`: The email of the user.
- `stopType`: The type of the stop.
- `stopId`: The id of the stop.

## Delete a favorite
Delete a favorite from the user's favorites list.
To delete a favorite you need to send a DELETE request to the following URL:
`{url}/v1/favorites/{favoriteId}`

The response code will be `204` or `401` or `400`.