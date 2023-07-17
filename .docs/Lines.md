# Lines

## Locations
Get the location of the buses of a line.
<details>
<summary>See more</summary>
To get information about the lines, you need to make a GET request to the following URL:
`{url}/v1/bus/lines/{lineCode}/locations`

The response code will be `200` or `400`.

> **Note** lineCode is the code of the line you want to get information about. It can be obtained from stops section.

### Example Request
```GET http://localhost:8080/v1/bus/lines/8__450___/locations```

### Example Response
```json
[
    {
        "lineCode": "8__450___",
        "codVehicle": "9173MCT",
        "coordinates": {
            "latitude": 40.32527160644531,
            "longitude": -3.7606699466705322
        },
        "direction": 1
    },
    {
        "lineCode": "8__450___",
        "codVehicle": "9067LJH",
        "coordinates": {
            "latitude": 40.350399017333984,
            "longitude": -3.8295199871063232
        },
        "direction": 1
    },
    {
        "lineCode": "8__450___",
        "codVehicle": "1058HHL",
        "coordinates": {
            "latitude": 40.33460998535156,
            "longitude": -3.7794899940490723
        },
        "direction": 2
    },
    {
        "lineCode": "8__450___",
        "codVehicle": "2823HHL",
        "coordinates": {
            "latitude": 40.313411712646484,
            "longitude": -3.7235000133514404
        },
        "direction": 2
    }
]
```
</details>

## Itineraries
Get the itineraries of a line.
<details>
<summary>See more</summary>
To get information about the lines, you need to make a GET request to the following URL:
`{url}/v1/bus/lines/{lineCode}/itineraries`

The response code will be `200` or `400`.

### Example Request
```GET http://localhost:8080/v1/bus/lines/8__450___/itineraries```

### Example Response
```json
[
    {
        "codItinerary": "8__450____1_-_IT_1",
        "direction": 1,
        "stops": [
            {
                "codStop": "8_15368",
                "name": "SÁNCHEZ MORATE-PZA.JESÚS JIMÉNEZ DÍAZ",
                "codMode": "8",
                "shortCodStop": "15368"
            },
            {
                "codStop": "8_08295",
                "name": "VELARDE-UNIVERSIDAD",
                "codMode": "8",
                "shortCodStop": "08295"
            },
            {
                "codStop": "8_08291",
                "name": "MADRID-PIZARRO",
                "codMode": "8",
                "shortCodStop": "08291"
            },
            {
                "codStop": "8_08290",
                "name": "SAN JOSÉ CALASANZ-SERRANILLOS",
                "codMode": "8",
                "shortCodStop": "08290"
            },
            {
                "codStop": "8_08284",
                "name": "GENERAL PINGARRÓN-COLEGIO",
                "codMode": "8",
                "shortCodStop": "08284"
            },
            {
                "codStop": "8_08242",
                "name": "PZA.ALCALDE JUAN VERGARA-LEGANÉS",
                "codMode": "8",
                "shortCodStop": "08242"
            },
            {
                "codStop": "8_08225",
                "name": "CTRA.M406-HOSPITAL DE GETAFE",
                "codMode": "8",
                "shortCodStop": "08225"
            },
            {
                "codStop": "8_08183",
                "name": "SEVERO OCHOA-EDUARDO TORROJA",
                "codMode": "8",
                "shortCodStop": "08183"
            },
            {
                "codStop": "8_07936",
                "name": "AV.MUSEO-CENTRO DE MAYORES",
                "codMode": "8",
                "shortCodStop": "07936"
            },
            {
                "codStop": "8_07896",
                "name": "AV.MUSEO-PZA.JOAN MANUEL SERRAT",
                "codMode": "8",
                "shortCodStop": "07896"
            },
            {
                "codStop": "8_12744",
                "name": "AV.GIBRALTAR-CASA DEL RELOJ",
                "codMode": "8",
                "shortCodStop": "12744"
            },
            {
                "codStop": "8_07899",
                "name": "AV.DOCTOR MARTÍN VEGUE-SAN MATEO",
                "codMode": "8",
                "shortCodStop": "07899"
            },
            {
                "codStop": "8_07905",
                "name": "AV.UNIVERSIDAD-UNIVERSIDAD",
                "codMode": "8",
                "shortCodStop": "07905"
            },
            {
                "codStop": "8_10575",
                "name": "RÍO DUERO-COLEGIO",
                "codMode": "8",
                "shortCodStop": "10575"
            },
            {
                "codStop": "8_07912",
                "name": "RÍO DUERO-RÍO MANZANARES",
                "codMode": "8",
                "shortCodStop": "07912"
            },
            {
                "codStop": "8_07884",
                "name": "AV.DR.MENDIGUCHIA CARRICHE-RÍO SEGRE",
                "codMode": "8",
                "shortCodStop": "07884"
            },
            {
                "codStop": "8_18338",
                "name": "AV.ALCORCÓN-DANUBIO",
                "codMode": "8",
                "shortCodStop": "18338"
            },
            {
                "codStop": "8_18339",
                "name": "AVDA.ALCORCÓN-RONDA OESTE",
                "codMode": "8",
                "shortCodStop": "18339"
            },
            {
                "codStop": "8_20150",
                "name": "CTRA.ALCORCÓN-FINCA SANTA FE",
                "codMode": "8",
                "shortCodStop": "20150"
            },
            {
                "codStop": "8_08519",
                "name": "AV.LEGANÉS-POL.IND.SAN JOSÉ VALDERAS",
                "codMode": "8",
                "shortCodStop": "08519"
            },
            {
                "codStop": "8_08516",
                "name": "AV.LEGANÉS-AV.DEL PINAR",
                "codMode": "8",
                "shortCodStop": "08516"
            },
            {
                "codStop": "8_08512",
                "name": "AV.LEGANÉS-PARQUE DE MONFRAGÜE",
                "codMode": "8",
                "shortCodStop": "08512"
            },
            {
                "codStop": "8_08508",
                "name": "AV.LEGANÉS-EST.PARQUE LISBOA",
                "codMode": "8",
                "shortCodStop": "08508"
            },
            {
                "codStop": "8_08506",
                "name": "AV.LEGANÉS-AV.LISBOA",
                "codMode": "8",
                "shortCodStop": "08506"
            },
            {
                "codStop": "8_09367",
                "name": "PºCASTILLA-SAN ISIDRO",
                "codMode": "8",
                "shortCodStop": "09367"
            },
            {
                "codStop": "8_08477",
                "name": "CÁCERES-TORRIJOS",
                "codMode": "8",
                "shortCodStop": "08477"
            },
            {
                "codStop": "8_08480",
                "name": "CÁCERES-PZA.SAN PEDRO BAUTISTA",
                "codMode": "8",
                "shortCodStop": "08480"
            },
            {
                "codStop": "8_08523",
                "name": "AV.OESTE-AV.JUAN RAMÓN JIMENEZ",
                "codMode": "8",
                "shortCodStop": "08523"
            },
            {
                "codStop": "8_12410",
                "name": "AV.OESTE-COLEGIO",
                "codMode": "8",
                "shortCodStop": "12410"
            },
            {
                "codStop": "8_08485",
                "name": "OLÍMPICO FCO.FDEZ.OCHOA-AV.OESTE",
                "codMode": "8",
                "shortCodStop": "08485"
            }
        ],
        "kml": "http://www.citram.es:8080/kml/itinerarios/20170504_959/interurbanoskmz/M8_L450_S1_obs[-]_TRAMO.kmz"
    },
    {
        "codItinerary": "8__450____2_-_IT_1",
        "direction": 2,
        "stops": [
            {
                "codStop": "8_08485",
                "name": "OLÍMPICO FCO.FDEZ.OCHOA-AV.OESTE",
                "codMode": "8",
                "shortCodStop": "08485"
            },
            {
                "codStop": "8_08492",
                "name": "ESCOLARES-CENTRO DE SALUD",
                "codMode": "8",
                "shortCodStop": "08492"
            },
            {
                "codStop": "8_08494",
                "name": "POLVORANCA-AV.ALCALDE JOSÉ ARANDA",
                "codMode": "8",
                "shortCodStop": "08494"
            },
            {
                "codStop": "8_08496",
                "name": "POLVORANCA-POCILLO",
                "codMode": "8",
                "shortCodStop": "08496"
            },
            {
                "codStop": "8_08499",
                "name": "POLVORANCA-JABONERIA",
                "codMode": "8",
                "shortCodStop": "08499"
            },
            {
                "codStop": "8_08505",
                "name": "AV.LEGANÉS-AV.CANTARRANAS",
                "codMode": "8",
                "shortCodStop": "08505"
            },
            {
                "codStop": "8_08507",
                "name": "AV.LEGANÉS-EST.PARQUE LISBOA",
                "codMode": "8",
                "shortCodStop": "08507"
            },
            {
                "codStop": "8_08511",
                "name": "AV.LEGANÉS-RÍO SEGRE",
                "codMode": "8",
                "shortCodStop": "08511"
            },
            {
                "codStop": "8_08517",
                "name": "AV.LEGANÉS-AV.INDUSTRIAS",
                "codMode": "8",
                "shortCodStop": "08517"
            },
            {
                "codStop": "8_08518",
                "name": "AV.LEGANÉS-POL.IND.URTINSA",
                "codMode": "8",
                "shortCodStop": "08518"
            },
            {
                "codStop": "8_08198",
                "name": "CTRA.ALCORCÓN-FINCA SANTA FE",
                "codMode": "8",
                "shortCodStop": "08198"
            },
            {
                "codStop": "8_18340",
                "name": "AVDA.ALCORCÓN-RONDA OESTE",
                "codMode": "8",
                "shortCodStop": "18340"
            },
            {
                "codStop": "8_18310",
                "name": "AV.ALCORCÓN-CANTABRIA",
                "codMode": "8",
                "shortCodStop": "18310"
            },
            {
                "codStop": "8_07883",
                "name": "AV.DR.MENDIGUCHIA CARRICHE-RÍO SEGRE",
                "codMode": "8",
                "shortCodStop": "07883"
            },
            {
                "codStop": "8_15606",
                "name": "AV.MAR MEDITERRÁNEO-RÍO URBIÓN",
                "codMode": "8",
                "shortCodStop": "15606"
            },
            {
                "codStop": "8_12701",
                "name": "RÍO MANZANARES-RÍO DUERO",
                "codMode": "8",
                "shortCodStop": "12701"
            },
            {
                "codStop": "8_07909",
                "name": "RÍO DUERO-PZA.RÍO HEREDIA",
                "codMode": "8",
                "shortCodStop": "07909"
            },
            {
                "codStop": "8_07904",
                "name": "AV.UNIVERSIDAD-POLICÍA NACIONAL",
                "codMode": "8",
                "shortCodStop": "07904"
            },
            {
                "codStop": "8_07898",
                "name": "AV.DR.MARTÍN VEGUE-AV.DE LA MANCHA",
                "codMode": "8",
                "shortCodStop": "07898"
            },
            {
                "codStop": "8_12745",
                "name": "AV.GIBRALTAR-CASA DEL RELOJ",
                "codMode": "8",
                "shortCodStop": "12745"
            },
            {
                "codStop": "8_07895",
                "name": "AV.MUSEO-PZA.JOAN MANUEL SERRAT",
                "codMode": "8",
                "shortCodStop": "07895"
            },
            {
                "codStop": "8_07937",
                "name": "LUGO-CENTRO DE MAYORES",
                "codMode": "8",
                "shortCodStop": "07937"
            },
            {
                "codStop": "8_08226",
                "name": "CTRA.M406-HOSPITAL DE GETAFE",
                "codMode": "8",
                "shortCodStop": "08226"
            },
            {
                "codStop": "8_09544",
                "name": "GLORIETA A42-HOSPITAL DE GETAFE",
                "codMode": "8",
                "shortCodStop": "09544"
            },
            {
                "codStop": "8_08241",
                "name": "LEGANÉS-BATRES",
                "codMode": "8",
                "shortCodStop": "08241"
            },
            {
                "codStop": "8_08283",
                "name": "PZA.CUESTAS-UNED",
                "codMode": "8",
                "shortCodStop": "08283"
            },
            {
                "codStop": "8_08348",
                "name": "MAGDALENA-ARBOLEDA",
                "codMode": "8",
                "shortCodStop": "08348"
            },
            {
                "codStop": "8_06348",
                "name": "MAGDALENA-PZA.CANTO REDONDO",
                "codMode": "8",
                "shortCodStop": "06348"
            },
            {
                "codStop": "8_09941",
                "name": "AV.JUAN DE LA CIERVA-PZA.ESPAÑA",
                "codMode": "8",
                "shortCodStop": "09941"
            },
            {
                "codStop": "8_15391",
                "name": "DAOIZ-MADRID",
                "codMode": "8",
                "shortCodStop": "15391"
            },
            {
                "codStop": "8_15368",
                "name": "SÁNCHEZ MORATE-PZA.JESÚS JIMÉNEZ DÍAZ",
                "codMode": "8",
                "shortCodStop": "15368"
            }
        ],
        "kml": "http://www.citram.es:8080/kml/itinerarios/20170504_959/interurbanoskmz/M8_L450_S2_obs[-]_TRAMO.kmz"
    }
]
```
</details>

## Stops
Get the stops of a line.
<details>
<summary>See more</summary>
To get the stops of a line, you have to make a GET request to the following URL:
`{url}/v1/bus/lines/{lineCode}/stops`

The response code will be `200` or `400`.

### Example Request
`GET http://localhost:8080/v1/bus/lines/8__450___/stops`

### Example Response

```json
[
    {
        "codStop": "8_06348",
        "name": "MAGDALENA-PZA.CANTO REDONDO",
        "coordinates": {
            "latitude": 40.3080352605559,
            "longitude": -3.7278995926052
        }
    },
    {
        "codStop": "8_07883",
        "name": "AV.DR.MENDIGUCHIA CARRICHE-RÍO SEGRE",
        "coordinates": {
            "latitude": 40.3331146240234,
            "longitude": -3.77378129959106
        }
    },
    {
        "codStop": "8_07884",
        "name": "AV.DR.MENDIGUCHIA CARRICHE-RÍO SEGRE",
        "coordinates": {
            "latitude": 40.3331260681152,
            "longitude": -3.77348709106445
        }
    },
    {
        "codStop": "8_07895",
        "name": "AV.MUSEO-PZA.JOAN MANUEL SERRAT",
        "coordinates": {
            "latitude": 40.3249616557709,
            "longitude": -3.76036873835007
        }
    },
    {
        "codStop": "8_07896",
        "name": "AV.MUSEO-PZA.JOAN MANUEL SERRAT",
        "coordinates": {
            "latitude": 40.3250070072092,
            "longitude": -3.76032216399265
        }
    },
    {
        "codStop": "8_07898",
        "name": "AV.DR.MARTÍN VEGUE-AV.DE LA MANCHA",
        "coordinates": {
            "latitude": 40.3290061371799,
            "longitude": -3.76182670952805
        }
    },
    {
        "codStop": "8_07899",
        "name": "AV.DOCTOR MARTÍN VEGUE-SAN MATEO",
        "coordinates": {
            "latitude": 40.3291931152344,
            "longitude": -3.76201200485229
        }
    },
    {
        "codStop": "8_07904",
        "name": "AV.UNIVERSIDAD-POLICÍA NACIONAL",
        "coordinates": {
            "latitude": 40.3309363191149,
            "longitude": -3.76558008997644
        }
    },
    {
        "codStop": "8_07905",
        "name": "AV.UNIVERSIDAD-UNIVERSIDAD",
        "coordinates": {
            "latitude": 40.3312227168665,
            "longitude": -3.76586585200627
        }
    },
    {
        "codStop": "8_07909",
        "name": "RÍO DUERO-PZA.RÍO HEREDIA",
        "coordinates": {
            "latitude": 40.3352203369141,
            "longitude": -3.76615309715271
        }
    },
    {
        "codStop": "8_07912",
        "name": "RÍO DUERO-RÍO MANZANARES",
        "coordinates": {
            "latitude": 40.3353080749512,
            "longitude": -3.77025103569031
        }
    },
    {
        "codStop": "8_07936",
        "name": "AV.MUSEO-CENTRO DE MAYORES",
        "coordinates": {
            "latitude": 40.3221207233571,
            "longitude": -3.75395734912805
        }
    },
    {
        "codStop": "8_07937",
        "name": "LUGO-CENTRO DE MAYORES",
        "coordinates": {
            "latitude": 40.3218252098337,
            "longitude": -3.75368334519219
        }
    },
    {
        "codStop": "8_08183",
        "name": "SEVERO OCHOA-EDUARDO TORROJA",
        "coordinates": {
            "latitude": 40.3176340311037,
            "longitude": -3.74564521897887
        }
    },
    {
        "codStop": "8_08198",
        "name": "CTRA.ALCORCÓN-FINCA SANTA FE",
        "coordinates": {
            "latitude": 40.3359242370757,
            "longitude": -3.78353098091136
        }
    },
    {
        "codStop": "8_08225",
        "name": "CTRA.M406-HOSPITAL DE GETAFE",
        "coordinates": {
            "latitude": 40.3132351365437,
            "longitude": -3.74041856730019
        }
    },
    {
        "codStop": "8_08226",
        "name": "CTRA.M406-HOSPITAL DE GETAFE",
        "coordinates": {
            "latitude": 40.3131628429474,
            "longitude": -3.74045308398197
        }
    },
    {
        "codStop": "8_08241",
        "name": "LEGANÉS-BATRES",
        "coordinates": {
            "latitude": 40.3105812072754,
            "longitude": -3.73698306083679
        }
    },
    {
        "codStop": "8_08242",
        "name": "PZA.ALCALDE JUAN VERGARA-LEGANÉS",
        "coordinates": {
            "latitude": 40.3108978271484,
            "longitude": -3.73672747612
        }
    },
    {
        "codStop": "8_08283",
        "name": "PZA.CUESTAS-UNED",
        "coordinates": {
            "latitude": 40.305831001728,
            "longitude": -3.73313600209697
        }
    },
    {
        "codStop": "8_08284",
        "name": "GENERAL PINGARRÓN-COLEGIO",
        "coordinates": {
            "latitude": 40.3088706604099,
            "longitude": -3.73256870065785
        }
    },
    {
        "codStop": "8_08290",
        "name": "SAN JOSÉ CALASANZ-SERRANILLOS",
        "coordinates": {
            "latitude": 40.3118986038562,
            "longitude": -3.72957690185283
        }
    },
    {
        "codStop": "8_08291",
        "name": "MADRID-PIZARRO",
        "coordinates": {
            "latitude": 40.3129201532529,
            "longitude": -3.72758721165167
        }
    },
    {
        "codStop": "8_08295",
        "name": "VELARDE-UNIVERSIDAD",
        "coordinates": {
            "latitude": 40.3163609617898,
            "longitude": -3.72625890298443
        }
    },
    {
        "codStop": "8_08348",
        "name": "MAGDALENA-ARBOLEDA",
        "coordinates": {
            "latitude": 40.3051053917591,
            "longitude": -3.72963324667632
        }
    },
    {
        "codStop": "8_08477",
        "name": "CÁCERES-TORRIJOS",
        "coordinates": {
            "latitude": 40.3474960327148,
            "longitude": -3.83285522460938
        }
    },
    {
        "codStop": "8_08480",
        "name": "CÁCERES-PZA.SAN PEDRO BAUTISTA",
        "coordinates": {
            "latitude": 40.3441162109375,
            "longitude": -3.83341407775879
        }
    },
    {
        "codStop": "8_08485",
        "name": "OLÍMPICO FCO.FDEZ.OCHOA-AV.OESTE",
        "coordinates": {
            "latitude": 40.340690612793,
            "longitude": -3.82343506813049
        }
    },
    {
        "codStop": "8_08492",
        "name": "ESCOLARES-CENTRO DE SALUD",
        "coordinates": {
            "latitude": 40.3417434692383,
            "longitude": -3.82081055641174
        }
    },
    {
        "codStop": "8_08494",
        "name": "POLVORANCA-AV.ALCALDE JOSÉ ARANDA",
        "coordinates": {
            "latitude": 40.3439025878906,
            "longitude": -3.82256746292114
        }
    },
    {
        "codStop": "8_08496",
        "name": "POLVORANCA-POCILLO",
        "coordinates": {
            "latitude": 40.3459930419922,
            "longitude": -3.82413530349731
        }
    },
    {
        "codStop": "8_08499",
        "name": "POLVORANCA-JABONERIA",
        "coordinates": {
            "latitude": 40.3486747741699,
            "longitude": -3.82552218437195
        }
    },
    {
        "codStop": "8_08505",
        "name": "AV.LEGANÉS-AV.CANTARRANAS",
        "coordinates": {
            "latitude": 40.3505331395767,
            "longitude": -3.82298222161711
        }
    },
    {
        "codStop": "8_08506",
        "name": "AV.LEGANÉS-AV.LISBOA",
        "coordinates": {
            "latitude": 40.3505701777709,
            "longitude": -3.82284136909111
        }
    },
    {
        "codStop": "8_08507",
        "name": "AV.LEGANÉS-EST.PARQUE LISBOA",
        "coordinates": {
            "latitude": 40.3487519856307,
            "longitude": -3.82007571502942
        }
    },
    {
        "codStop": "8_08508",
        "name": "AV.LEGANÉS-EST.PARQUE LISBOA",
        "coordinates": {
            "latitude": 40.349492066095,
            "longitude": -3.8211562089656
        }
    },
    {
        "codStop": "8_08511",
        "name": "AV.LEGANÉS-RÍO SEGRE",
        "coordinates": {
            "latitude": 40.3458404541016,
            "longitude": -3.81386613845825
        }
    },
    {
        "codStop": "8_08512",
        "name": "AV.LEGANÉS-PARQUE DE MONFRAGÜE",
        "coordinates": {
            "latitude": 40.3459739685059,
            "longitude": -3.81373810768127
        }
    },
    {
        "codStop": "8_08516",
        "name": "AV.LEGANÉS-AV.DEL PINAR",
        "coordinates": {
            "latitude": 40.3437272721144,
            "longitude": -3.80822903798427
        }
    },
    {
        "codStop": "8_08517",
        "name": "AV.LEGANÉS-AV.INDUSTRIAS",
        "coordinates": {
            "latitude": 40.3438939846754,
            "longitude": -3.80886682806319
        }
    },
    {
        "codStop": "8_08518",
        "name": "AV.LEGANÉS-POL.IND.URTINSA",
        "coordinates": {
            "latitude": 40.3417071589602,
            "longitude": -3.80205905636088
        }
    },
    {
        "codStop": "8_08519",
        "name": "AV.LEGANÉS-POL.IND.SAN JOSÉ VALDERAS",
        "coordinates": {
            "latitude": 40.3415853682567,
            "longitude": -3.8014336088627
        }
    },
    {
        "codStop": "8_08523",
        "name": "AV.OESTE-AV.JUAN RAMÓN JIMENEZ",
        "coordinates": {
            "latitude": 40.3402442932129,
            "longitude": -3.82936358451843
        }
    },
    {
        "codStop": "8_09367",
        "name": "PºCASTILLA-SAN ISIDRO",
        "coordinates": {
            "latitude": 40.3503952026367,
            "longitude": -3.82967615127563
        }
    },
    {
        "codStop": "8_09544",
        "name": "GLORIETA A42-HOSPITAL DE GETAFE",
        "coordinates": {
            "latitude": 40.3123016357422,
            "longitude": -3.73953199386597
        }
    },
    {
        "codStop": "8_09941",
        "name": "AV.JUAN DE LA CIERVA-PZA.ESPAÑA",
        "coordinates": {
            "latitude": 40.3098997364187,
            "longitude": -3.72509524018516
        }
    },
    {
        "codStop": "8_10575",
        "name": "RÍO DUERO-COLEGIO",
        "coordinates": {
            "latitude": 40.3353309631348,
            "longitude": -3.76680183410645
        }
    },
    {
        "codStop": "8_12410",
        "name": "AV.OESTE-COLEGIO",
        "coordinates": {
            "latitude": 40.3389434814453,
            "longitude": -3.82606267929077
        }
    },
    {
        "codStop": "8_12701",
        "name": "RÍO MANZANARES-RÍO DUERO",
        "coordinates": {
            "latitude": 40.335433681071,
            "longitude": -3.76937461465982
        }
    },
    {
        "codStop": "8_12744",
        "name": "AV.GIBRALTAR-CASA DEL RELOJ",
        "coordinates": {
            "latitude": 40.3266220092773,
            "longitude": -3.75949931144714
        }
    },
    {
        "codStop": "8_12745",
        "name": "AV.GIBRALTAR-CASA DEL RELOJ",
        "coordinates": {
            "latitude": 40.3266410827637,
            "longitude": -3.7597348690033
        }
    },
    {
        "codStop": "8_15368",
        "name": "SÁNCHEZ MORATE-PZA.JESÚS JIMÉNEZ DÍAZ",
        "coordinates": {
            "latitude": 40.3153128283467,
            "longitude": -3.72243446840415
        }
    },
    {
        "codStop": "8_15391",
        "name": "DAOIZ-MADRID",
        "coordinates": {
            "latitude": 40.3140420133199,
            "longitude": -3.72683427271598
        }
    },
    {
        "codStop": "8_15606",
        "name": "AV.MAR MEDITERRÁNEO-RÍO URBIÓN",
        "coordinates": {
            "latitude": 40.3360268716618,
            "longitude": -3.77228921633641
        }
    },
    {
        "codStop": "8_18310",
        "name": "AV.ALCORCÓN-CANTABRIA",
        "coordinates": {
            "latitude": 40.3338890075684,
            "longitude": -3.77666258811951
        }
    },
    {
        "codStop": "8_18338",
        "name": "AV.ALCORCÓN-DANUBIO",
        "coordinates": {
            "latitude": 40.3340696058302,
            "longitude": -3.77666977313099
        }
    },
    {
        "codStop": "8_18339",
        "name": "AVDA.ALCORCÓN-RONDA OESTE",
        "coordinates": {
            "latitude": 40.3348063493591,
            "longitude": -3.77964489371035
        }
    },
    {
        "codStop": "8_18340",
        "name": "AVDA.ALCORCÓN-RONDA OESTE",
        "coordinates": {
            "latitude": 40.3345816169727,
            "longitude": -3.77957167221326
        }
    },
    {
        "codStop": "8_20150",
        "name": "CTRA.ALCORCÓN-FINCA SANTA FE",
        "coordinates": {
            "latitude": 40.3361657072326,
            "longitude": -3.78379277463414
        }
    }
]
```


</details>