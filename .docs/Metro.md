# Metro
To get information about the metro, you need to make a GET request to the following URL:
- `{url}/v1/metro/times/{id}` to get the next metro times of one station.
- `{url}/v1/metro/times` to get the next metro times of all stations.

`{id}` is the auto numeric id of the metro station.

## Example Request
```GET http://localhost:8080/v1/metro/times/209```

## Example Reponse
```json
[
    {
        "id": 209,
        "nombreEstacion": "Ópera",
        "linea": 0,
        "anden": 1,
        "sentido": "Príncipe Pío",
        "proximos": [
            0
        ]
    },
    {
        "id": 209,
        "nombreEstacion": "Ópera",
        "linea": 2,
        "anden": 1,
        "sentido": "Cuatro Caminos",
        "proximos": [
            5
        ]
    },
    {
        "id": 209,
        "nombreEstacion": "Ópera",
        "linea": 2,
        "anden": 2,
        "sentido": "Las Rosas",
        "proximos": [
            3,
            9
        ]
    },
    {
        "id": 209,
        "nombreEstacion": "Ópera",
        "linea": 5,
        "anden": 1,
        "sentido": "Casa de Campo",
        "proximos": [
            4
        ]
    },
    {
        "id": 209,
        "nombreEstacion": "Ópera",
        "linea": 5,
        "anden": 2,
        "sentido": "Alameda de Osuna",
        "proximos": [
            1
        ]
    }
]
```