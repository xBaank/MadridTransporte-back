# Stops
## Estimations
Get the estimations of bus lines in a bus stop.
<details>
<summary>See more</summary>
To get the estimations of bus lines in a bus stop, you need make a GET request to the following endpoint:

- `{url}/v1/bus/stops/{stopCodes}/estimations`

The response code will be `200` or `400`.

### Example Request
```GET http://localhost:8080/v1/bus/stops/08242/estimations```

### Example Response
```json
{
    "data": {
        "name": "PZA.ALCALDE JUAN VERGARA-LEGANÉS",
        "estimatedTimes": [
            {
                "lineCode": "8__447___",
                "codMode": "8",
                "codVehicle": "",
                "time": 1689534688000
            },
            {
                "lineCode": "9__3__065_",
                "codMode": "9",
                "codVehicle": "",
                "time": 1689535381000
            },
            {
                "lineCode": "9__3__065_",
                "codMode": "9",
                "codVehicle": "",
                "time": 1689535440000
            },
            {
                "lineCode": "9__2__065_",
                "codMode": "9",
                "codVehicle": "",
                "time": 1689535602000
            },
            {
                "lineCode": "8__448___",
                "codMode": "8",
                "codVehicle": "",
                "time": 1689535956000
            },
            {
                "lineCode": "9__4__065_",
                "codMode": "9",
                "codVehicle": "",
                "time": 1689536114000
            },
            {
                "lineCode": "8__450___",
                "codMode": "8",
                "codVehicle": "",
                "time": 1689536204000
            },
            {
                "lineCode": "8__468___",
                "codMode": "8",
                "codVehicle": "",
                "time": 1689536249000
            },
            {
                "lineCode": "8__428___",
                "codMode": "8",
                "codVehicle": "",
                "time": 1689536251000
            },
            {
                "lineCode": "8__462___",
                "codMode": "8",
                "codVehicle": "",
                "time": 1689536732000
            },
            {
                "lineCode": "8__450___",
                "codMode": "8",
                "codVehicle": "",
                "time": 1689538011000
            },
            {
                "lineCode": "9__3__065_",
                "codMode": "9",
                "codVehicle": "",
                "time": 1689538080000
            },
            {
                "lineCode": "8__447___",
                "codMode": "8",
                "codVehicle": "",
                "time": 1689538140000
            },
            {
                "lineCode": "9__2__065_",
                "codMode": "9",
                "codVehicle": "",
                "time": 1689538260000
            },
            {
                "lineCode": "8__428___",
                "codMode": "8",
                "codVehicle": "",
                "time": 1689538420000
            },
            {
                "lineCode": "8__448___",
                "codMode": "8",
                "codVehicle": "",
                "time": 1689538785000
            },
            {
                "lineCode": "9__4__065_",
                "codMode": "9",
                "codVehicle": "",
                "time": 1689539220000
            },
            {
                "lineCode": "8__468___",
                "codMode": "8",
                "codVehicle": "",
                "time": 1689539568000
            },
            {
                "lineCode": "8__450___",
                "codMode": "8",
                "codVehicle": "",
                "time": 1689539760000
            },
            {
                "lineCode": "8__447___",
                "codMode": "8",
                "codVehicle": "",
                "time": 1689540240000
            },
            {
                "lineCode": "9__2__065_",
                "codMode": "9",
                "codVehicle": "",
                "time": 1689540300000
            },
            {
                "lineCode": "8__428___",
                "codMode": "8",
                "codVehicle": "",
                "time": 1689540520000
            },
            {
                "lineCode": "8__468___",
                "codMode": "8",
                "codVehicle": "",
                "time": 1689541260000
            },
            {
                "lineCode": "8__448___",
                "codMode": "8",
                "codVehicle": "",
                "time": 1689541440000
            },
            {
                "lineCode": "9__4__065_",
                "codMode": "9",
                "codVehicle": "",
                "time": 1689541860000
            }
        ]
    },
    "lastTime": 1689534681714
}
```
</details>

## Times
Get the times of bus lines in a bus stop.
<details>
<summary>See more</summary>
To get the times of bus lines in a bus stop, you need make a GET request to the following endpoint:

- `{url}/v1/bus/stops/{stopCodes}/times`
- `{url}/v1/bus/stops/{stopCodes}/times/cached` (cached response, this endpoint is faster but can be outdated if no one has made a request in minutes)
- `{url}/v1/bus/stops/{stopCodes}/times/subscribe` (websocket endpoint, you can subscribe to a bus stop and receive the times in real time)

The response code will be `200` or `400`.

### Example Request
```GET http://localhost:8080/v1/bus/stops/08242/times```

### Example Response
```json
{
    "data": {
        "name": "PZA.ALCALDE JUAN VERGARA-LEGANÉS",
        "times": [
            {
                "lineCode": "8__447___",
                "codMode": "8",
                "destination": "MADRID (Plaza de Legazpi)-GETAFE (Hospital)",
                "codVehicle": "",
                "time": 1689535140000
            },
            {
                "lineCode": "9__3__065_",
                "codMode": "9",
                "destination": "AV.LOS ÁNGELES-AMBULATORIO",
                "codVehicle": "",
                "time": 1689535400000
            },
            {
                "lineCode": "9__3__065_",
                "codMode": "9",
                "destination": "AV.LOS ÁNGELES-AMBULATORIO",
                "codVehicle": "",
                "time": 1689535440000
            },
            {
                "lineCode": "9__2__065_",
                "codMode": "9",
                "destination": "PºJUAN JOSÉ ROSÓN-EST.ARROYO CULEBRO",
                "codVehicle": "",
                "time": 1689535614000
            },
            {
                "lineCode": "8__448___",
                "codMode": "8",
                "destination": "MADRID (Pza.de Legazpi)-GETAFE (Hospital)",
                "codVehicle": "",
                "time": 1689536003000
            },
            {
                "lineCode": "9__4__065_",
                "codMode": "9",
                "destination": "VIENTO-CASERÍO DE PERALES",
                "codVehicle": "",
                "time": 1689536115000
            },
            {
                "lineCode": "8__450___",
                "codMode": "8",
                "destination": "GETAFE (Dr. Sanchez Morate, 35)-ALCORCON (Av. Olímpico Fco. Fdez Ochoa)",
                "codVehicle": "",
                "time": 1689536206000
            },
            {
                "lineCode": "8__468___",
                "codMode": "8",
                "destination": "GETAFE (Est. Getafe Centro)-GRIÑON",
                "codVehicle": "",
                "time": 1689536250000
            },
            {
                "lineCode": "8__428___",
                "codMode": "8",
                "destination": "VALDEMORO (Avenida de España)-GETAFE (c/ Leganes/Hospital)",
                "codVehicle": "",
                "time": 1689536509000
            },
            {
                "lineCode": "8__462___",
                "codMode": "8",
                "destination": "PARLA (calle Pinto)-GETAFE PASO APROXIMADO AV.LIBERTAD-HOSPITAL DE GETAFE",
                "codVehicle": "",
                "time": 1689536901000
            },
            {
                "lineCode": "8__447___",
                "codMode": "8",
                "destination": "MADRID (Plaza de Legazpi)-GETAFE (Hospital)",
                "codVehicle": "",
                "time": 1689537330000
            },
            {
                "lineCode": "9__2__065_",
                "codMode": "9",
                "destination": "PºJUAN JOSÉ ROSÓN-EST.ARROYO CULEBRO",
                "codVehicle": "",
                "time": 1689537718000
            },
            {
                "lineCode": "8__450___",
                "codMode": "8",
                "destination": "GETAFE (Dr. Sanchez Morate, 35)-ALCORCON (Av. Olímpico Fco. Fdez Ochoa)",
                "codVehicle": "",
                "time": 1689538012000
            },
            {
                "lineCode": "9__3__065_",
                "codMode": "9",
                "destination": "AV.LOS ÁNGELES-AMBULATORIO",
                "codVehicle": "",
                "time": 1689538080000
            },
            {
                "lineCode": "8__428___",
                "codMode": "8",
                "destination": "VALDEMORO (Avenida de España)-GETAFE (c/ Leganes/Hospital)",
                "codVehicle": "",
                "time": 1689538619000
            },
            {
                "lineCode": "8__448___",
                "codMode": "8",
                "destination": "MADRID (Pza.de Legazpi)-GETAFE (Hospital)",
                "codVehicle": "",
                "time": 1689538783000
            },
            {
                "lineCode": "9__4__065_",
                "codMode": "9",
                "destination": "VIENTO-CASERÍO DE PERALES",
                "codVehicle": "",
                "time": 1689539105000
            },
            {
                "lineCode": "8__468___",
                "codMode": "8",
                "destination": "GETAFE (Est. Getafe Centro)-GRIÑON",
                "codVehicle": "",
                "time": 1689539568000
            },
            {
                "lineCode": "8__450___",
                "codMode": "8",
                "destination": "GETAFE (Dr. Sanchez Morate, 35)-ALCORCON (Av. Olímpico Fco. Fdez Ochoa)",
                "codVehicle": "",
                "time": 1689539760000
            },
            {
                "lineCode": "8__447___",
                "codMode": "8",
                "destination": "MADRID (Plaza de Legazpi)-GETAFE (Hospital)",
                "codVehicle": "",
                "time": 1689540240000
            },
            {
                "lineCode": "9__2__065_",
                "codMode": "9",
                "destination": "PºJUAN JOSÉ ROSÓN-EST.ARROYO CULEBRO",
                "codVehicle": "",
                "time": 1689540300000
            },
            {
                "lineCode": "8__428___",
                "codMode": "8",
                "destination": "VALDEMORO (Avenida de España)-GETAFE (c/ Leganes/Hospital)",
                "codVehicle": "",
                "time": 1689540520000
            },
            {
                "lineCode": "8__468___",
                "codMode": "8",
                "destination": "GETAFE (Est. Getafe Centro)-GRIÑON",
                "codVehicle": "",
                "time": 1689541260000
            },
            {
                "lineCode": "8__448___",
                "codMode": "8",
                "destination": "MADRID (Pza.de Legazpi)-GETAFE (Hospital)",
                "codVehicle": "",
                "time": 1689541440000
            },
            {
                "lineCode": "9__4__065_",
                "codMode": "9",
                "destination": "VIENTO-CASERÍO DE PERALES",
                "codVehicle": "",
                "time": 1689541860000
            }
        ]
    },
    "lastTime": 1689534982807
}
```
</details>