# Stops

## Locations
Get the locations of the stops near a coordinate.
<details>
<summary>See more</summary>
To get the locations of the stops near a coordinate, you need to make a GET request to the following URL:

- `{url}/v1/stops/locations?latitude={lat}&longitude={lon}` to get the locations of the stops near a coordinate.

The response code will be `200` or `400`.

### Example Request
```GET http://localhost:8080/v1/bus/stops/locations?latitude=40.37043738780061&longitude=-3.536834949732102```

### Example Response
```json
[
    {
        "codStop": "8_12204",
        "codMode": "8",
        "name": "PºFERROCARRIL-EST.RIVAS URBANIZACIONES",
        "latitude": 40.3671531677246,
        "longitude": -3.54778170585632
    },
    {
        "codStop": "8_15947",
        "codMode": "8",
        "name": "AV.GABRIEL GARCÍA MÁRQUEZ-INSTITUTO",
        "latitude": 40.3627853393555,
        "longitude": -3.54777002334595
    },
    {
        "codStop": "8_16042",
        "codMode": "8",
        "name": "AV.GABRIEL GARCÍA MÁRQUEZ-INSTITUTO",
        "latitude": 40.3627777099609,
        "longitude": -3.54751086235046
    },
    {
        "codStop": "8_07507",
        "codMode": "8",
        "name": "AV.ALMENDROS-BOROS",
        "latitude": 40.361930847168,
        "longitude": -3.53953075408936
    },
    {
        "codStop": "8_07508",
        "codMode": "8",
        "name": "AV.ALMENDROS-URB.EL TEJAR",
        "latitude": 40.3631210327148,
        "longitude": -3.53739666938782
    },
    {
        "codStop": "8_07509",
        "codMode": "8",
        "name": "AV.ALMENDROS-ACEBO",
        "latitude": 40.3652534484863,
        "longitude": -3.5324432849884
    },
    {
        "codStop": "8_07511",
        "codMode": "8",
        "name": "AV.ALMENDROS-RÍO JARAMA",
        "latitude": 40.3654327392578,
        "longitude": -3.53239750862122
    },
    {
        "codStop": "8_07512",
        "codMode": "8",
        "name": "AV.ALMENDROS-ÓPERA",
        "latitude": 40.3632011413574,
        "longitude": -3.53753852844238
    },
    {
        "codStop": "8_07513",
        "codMode": "8",
        "name": "AV.ALMENDROS-AV.ZARZUELA",
        "latitude": 40.3620491027832,
        "longitude": -3.53964948654175
    },
    {
        "codStop": "8_11623",
        "codMode": "9",
        "name": "ENCINA VERDE-COLEGIO",
        "latitude": 40.3617095947266,
        "longitude": -3.53329873085022
    },
    {
        "codStop": "8_11624",
        "codMode": "9",
        "name": "ALOE-ACEBO",
        "latitude": 40.363208770752,
        "longitude": -3.5305073261261
    },
    {
        "codStop": "8_12610",
        "codMode": "9",
        "name": "ALOE-ACEBO",
        "latitude": 40.3632888793945,
        "longitude": -3.53059053421021
    },
    {
        "codStop": "8_15947",
        "codMode": "8",
        "name": "AV.GABRIEL GARCÍA MÁRQUEZ-INSTITUTO",
        "latitude": 40.3627853393555,
        "longitude": -3.54777002334595
    },
    {
        "codStop": "8_16042",
        "codMode": "8",
        "name": "AV.GABRIEL GARCÍA MÁRQUEZ-INSTITUTO",
        "latitude": 40.3627777099609,
        "longitude": -3.54751086235046
    },
    {
        "codStop": "8_20470",
        "codMode": "9",
        "name": "ALOE-TORCADA",
        "latitude": 40.3617324829102,
        "longitude": -3.53391122817993
    },
    {
        "codStop": "8_09012",
        "codMode": "8",
        "name": "AV.ALMENDROS-COLEGIO",
        "latitude": 40.3717498779297,
        "longitude": -3.53174066543579
    },
    {
        "codStop": "8_09069",
        "codMode": "8",
        "name": "AV.ALMENDROS-COLEGIO",
        "latitude": 40.3716430664063,
        "longitude": -3.53188109397888
    },
    {
        "codStop": "8_09070",
        "codMode": "8",
        "name": "PºCHOPERA-INSTITUTO",
        "latitude": 40.3688049316406,
        "longitude": -3.53151726722717
    },
    {
        "codStop": "8_09099",
        "codMode": "8",
        "name": "AV.COVIBAR-PZA.VALENCIA",
        "latitude": 40.3633995056152,
        "longitude": -3.54731559753418
    },
    {
        "codStop": "8_09102",
        "codMode": "8",
        "name": "PºCHOPERA-INSTITUTO",
        "latitude": 40.3686790466309,
        "longitude": -3.53151631355286
    },
    {
        "codStop": "8_11605",
        "codMode": "8",
        "name": "AV.ALMENDROS-CENTRO COMERCIAL",
        "latitude": 40.3758277893066,
        "longitude": -3.53264427185059
    },
    {
        "codStop": "8_11607",
        "codMode": "8",
        "name": "PºPROVINCIAS-COLEGIO",
        "latitude": 40.3734741210938,
        "longitude": -3.53724336624146
    },
    {
        "codStop": "8_11609",
        "codMode": "8",
        "name": "PºPROVINCIAS-VALLADOLID",
        "latitude": 40.3713874816895,
        "longitude": -3.54220938682556
    },
    {
        "codStop": "8_11610",
        "codMode": "8",
        "name": "PºPROVINCIAS-RONDA DE GIJÓN",
        "latitude": 40.3718414306641,
        "longitude": -3.54098796844482
    },
    {
        "codStop": "8_11612",
        "codMode": "8",
        "name": "PºPROVINCIAS-RONDA DE OVIEDO",
        "latitude": 40.3692321777344,
        "longitude": -3.54795169830322
    },
    {
        "codStop": "8_11614",
        "codMode": "8",
        "name": "PºCHOPERA-ROBLES",
        "latitude": 40.3675956726074,
        "longitude": -3.53413414955139
    },
    {
        "codStop": "8_11615",
        "codMode": "8",
        "name": "PºCHOPERA-ROBLES",
        "latitude": 40.3670883178711,
        "longitude": -3.53499007225037
    },
    {
        "codStop": "8_11616",
        "codMode": "8",
        "name": "PºCHOPERA-TILOS",
        "latitude": 40.3655128479004,
        "longitude": -3.5386757850647
    },
    {
        "codStop": "8_11617",
        "codMode": "8",
        "name": "PºCHOPERA-TILOS",
        "latitude": 40.3654937744141,
        "longitude": -3.53838133811951
    },
    {
        "codStop": "8_11618",
        "codMode": "8",
        "name": "NIBELUNGOS-AV.ZARZUELA",
        "latitude": 40.3640365600586,
        "longitude": -3.54179692268372
    },
    {
        "codStop": "8_11619",
        "codMode": "8",
        "name": "NIBELUNGOS-AV.ZARZUELA",
        "latitude": 40.3639640808105,
        "longitude": -3.5417492389679
    },
    {
        "codStop": "8_12060",
        "codMode": "8",
        "name": "PºLAS PROVINCIAS-SEVILLA",
        "latitude": 40.3753128051758,
        "longitude": -3.52945971488953
    },
    {
        "codStop": "8_12061",
        "codMode": "8",
        "name": "PICOS DE URBIÓN-HUELVA",
        "latitude": 40.373779296875,
        "longitude": -3.52541923522949
    },
    {
        "codStop": "8_12062",
        "codMode": "8",
        "name": "PICOS DE URBIÓN-CERPA",
        "latitude": 40.3731651306152,
        "longitude": -3.52974915504456
    },
    {
        "codStop": "8_12063",
        "codMode": "8",
        "name": "PICOS DE URBIÓN-CAZORLA",
        "latitude": 40.3730850219727,
        "longitude": -3.52959537506104
    },
    {
        "codStop": "8_12064",
        "codMode": "8",
        "name": "PICOS DE URBIÓN-CAÑADAS",
        "latitude": 40.3737373352051,
        "longitude": -3.52517151832581
    },
    {
        "codStop": "8_12065",
        "codMode": "8",
        "name": "PºLAS PROVINCIAS-SEVILLA",
        "latitude": 40.3754920959473,
        "longitude": -3.52964973449707
    },
    {
        "codStop": "8_12203",
        "codMode": "8",
        "name": "PºPROVINCIAS-CÁCERES",
        "latitude": 40.3691520690918,
        "longitude": -3.54778623580933
    },
    {
        "codStop": "8_12204",
        "codMode": "8",
        "name": "PºFERROCARRIL-EST.RIVAS URBANIZACIONES",
        "latitude": 40.3671531677246,
        "longitude": -3.54778170585632
    },
    {
        "codStop": "8_16069",
        "codMode": "8",
        "name": "PºFERROCARRIL-EST.RIVAS URBANIZACIONES",
        "latitude": 40.3673248291016,
        "longitude": -3.5477831363678
    },
    {
        "codStop": "8_18266",
        "codMode": "8",
        "name": "AV.ALMENDROS-ISADORA DUNCAN",
        "latitude": 40.3789443969727,
        "longitude": -3.53319907188416
    },
    {
        "codStop": "8_18777",
        "codMode": "8",
        "name": "AV.ALMENDROS-DÉBORA ARANGO",
        "latitude": 40.3756141662598,
        "longitude": -3.53235983848572
    },
    {
        "codStop": "8_18778",
        "codMode": "8",
        "name": "AV.ALMENDROS-ISADORA DUNCAN",
        "latitude": 40.3786735534668,
        "longitude": -3.53277277946472
    },
    {
        "codStop": "8_18779",
        "codMode": "8",
        "name": "AV.OCHO DE MARZO-CIUDAD EDUCATIVA",
        "latitude": 40.3790512084961,
        "longitude": -3.53718161582947
    },
    {
        "codStop": "8_18780",
        "codMode": "8",
        "name": "AV.TIERRA-AV.OCHO DE MARZO",
        "latitude": 40.3781394958496,
        "longitude": -3.53928303718567
    },
    {
        "codStop": "8_18781",
        "codMode": "8",
        "name": "PºPROVINCIAS-SAN SEBASTIÁN",
        "latitude": 40.3733444213867,
        "longitude": -3.53819632530212
    },
    {
        "codStop": "8_18782",
        "codMode": "8",
        "name": "AV.TIERRA-AV.OCHO DE MARZO",
        "latitude": 40.3781242370605,
        "longitude": -3.53870558738709
    },
    {
        "codStop": "8_18783",
        "codMode": "8",
        "name": "AV.OCHO DE MARZO-CIUDAD EDUCATIVA",
        "latitude": 40.3788528442383,
        "longitude": -3.53666162490845
    },
    {
        "codStop": "8_20276",
        "codMode": "8",
        "name": "AV.TIERRA-DULCE CHACÓN",
        "latitude": 40.3747901916504,
        "longitude": -3.53923273086548
    },
    {
        "codStop": "8_20277",
        "codMode": "8",
        "name": "AV.TIERRA-DULCE CHACÓN",
        "latitude": 40.3748626708984,
        "longitude": -3.53896236419678
    },
    {
        "codStop": "8_20708",
        "codMode": "8",
        "name": "PºPROVINCIAS-AV.VÍCTIMAS DEL TERRORISMO",
        "latitude": 40.3703002929688,
        "longitude": -3.54563999176025
    },
    {
        "codStop": "8_20709",
        "codMode": "8",
        "name": "PºPROVINCIAS-AV.VÍCTIMAS DEL TERRORISMO",
        "latitude": 40.3701553344727,
        "longitude": -3.54533267021179
    },
    {
        "codStop": "8_09099",
        "codMode": "8",
        "name": "AV.COVIBAR-PZA.VALENCIA",
        "latitude": 40.3633995056152,
        "longitude": -3.54731559753418
    },
    {
        "codStop": "8_09100",
        "codMode": "9",
        "name": "AV.COVIBAR-INSTITUTO",
        "latitude": 40.3634071350098,
        "longitude": -3.54751586914063
    },
    {
        "codStop": "8_07507",
        "codMode": "8",
        "name": "AV.ALMENDROS-BOROS",
        "latitude": 40.361930847168,
        "longitude": -3.53953075408936
    },
    {
        "codStop": "8_07508",
        "codMode": "8",
        "name": "AV.ALMENDROS-URB.EL TEJAR",
        "latitude": 40.3631210327148,
        "longitude": -3.53739666938782
    },
    {
        "codStop": "8_07509",
        "codMode": "8",
        "name": "AV.ALMENDROS-ACEBO",
        "latitude": 40.3652534484863,
        "longitude": -3.5324432849884
    },
    {
        "codStop": "8_07511",
        "codMode": "8",
        "name": "AV.ALMENDROS-RÍO JARAMA",
        "latitude": 40.3654327392578,
        "longitude": -3.53239750862122
    },
    {
        "codStop": "8_07512",
        "codMode": "8",
        "name": "AV.ALMENDROS-ÓPERA",
        "latitude": 40.3632011413574,
        "longitude": -3.53753852844238
    },
    {
        "codStop": "8_07513",
        "codMode": "8",
        "name": "AV.ALMENDROS-AV.ZARZUELA",
        "latitude": 40.3620491027832,
        "longitude": -3.53964948654175
    },
    {
        "codStop": "8_09012",
        "codMode": "8",
        "name": "AV.ALMENDROS-COLEGIO",
        "latitude": 40.3717498779297,
        "longitude": -3.53174066543579
    },
    {
        "codStop": "8_09069",
        "codMode": "8",
        "name": "AV.ALMENDROS-COLEGIO",
        "latitude": 40.3716430664063,
        "longitude": -3.53188109397888
    },
    {
        "codStop": "8_11605",
        "codMode": "8",
        "name": "AV.ALMENDROS-CENTRO COMERCIAL",
        "latitude": 40.3758277893066,
        "longitude": -3.53264427185059
    },
    {
        "codStop": "8_11609",
        "codMode": "8",
        "name": "PºPROVINCIAS-VALLADOLID",
        "latitude": 40.3713874816895,
        "longitude": -3.54220938682556
    },
    {
        "codStop": "8_11610",
        "codMode": "8",
        "name": "PºPROVINCIAS-RONDA DE GIJÓN",
        "latitude": 40.3718414306641,
        "longitude": -3.54098796844482
    },
    {
        "codStop": "8_11612",
        "codMode": "8",
        "name": "PºPROVINCIAS-RONDA DE OVIEDO",
        "latitude": 40.3692321777344,
        "longitude": -3.54795169830322
    },
    {
        "codStop": "8_12203",
        "codMode": "8",
        "name": "PºPROVINCIAS-CÁCERES",
        "latitude": 40.3691520690918,
        "longitude": -3.54778623580933
    },
    {
        "codStop": "8_15869",
        "codMode": "8",
        "name": "AV.ALMENDROS-PºCHOPERA",
        "latitude": 40.3693580627441,
        "longitude": -3.53134489059448
    },
    {
        "codStop": "8_18266",
        "codMode": "8",
        "name": "AV.ALMENDROS-ISADORA DUNCAN",
        "latitude": 40.3789443969727,
        "longitude": -3.53319907188416
    },
    {
        "codStop": "8_18777",
        "codMode": "8",
        "name": "AV.ALMENDROS-DÉBORA ARANGO",
        "latitude": 40.3756141662598,
        "longitude": -3.53235983848572
    },
    {
        "codStop": "8_18778",
        "codMode": "8",
        "name": "AV.ALMENDROS-ISADORA DUNCAN",
        "latitude": 40.3786735534668,
        "longitude": -3.53277277946472
    },
    {
        "codStop": "8_18779",
        "codMode": "8",
        "name": "AV.OCHO DE MARZO-CIUDAD EDUCATIVA",
        "latitude": 40.3790512084961,
        "longitude": -3.53718161582947
    },
    {
        "codStop": "8_18780",
        "codMode": "8",
        "name": "AV.TIERRA-AV.OCHO DE MARZO",
        "latitude": 40.3781394958496,
        "longitude": -3.53928303718567
    },
    {
        "codStop": "8_18782",
        "codMode": "8",
        "name": "AV.TIERRA-AV.OCHO DE MARZO",
        "latitude": 40.3781242370605,
        "longitude": -3.53870558738709
    },
    {
        "codStop": "8_18783",
        "codMode": "8",
        "name": "AV.OCHO DE MARZO-CIUDAD EDUCATIVA",
        "latitude": 40.3788528442383,
        "longitude": -3.53666162490845
    },
    {
        "codStop": "8_20276",
        "codMode": "8",
        "name": "AV.TIERRA-DULCE CHACÓN",
        "latitude": 40.3747901916504,
        "longitude": -3.53923273086548
    },
    {
        "codStop": "8_20277",
        "codMode": "8",
        "name": "AV.TIERRA-DULCE CHACÓN",
        "latitude": 40.3748626708984,
        "longitude": -3.53896236419678
    },
    {
        "codStop": "8_20708",
        "codMode": "8",
        "name": "PºPROVINCIAS-AV.VÍCTIMAS DEL TERRORISMO",
        "latitude": 40.3703002929688,
        "longitude": -3.54563999176025
    },
    {
        "codStop": "8_20709",
        "codMode": "8",
        "name": "PºPROVINCIAS-AV.VÍCTIMAS DEL TERRORISMO",
        "latitude": 40.3701553344727,
        "longitude": -3.54533267021179
    },
    {
        "codStop": "8_20710",
        "codMode": "8",
        "name": "AV.ALMENDROS-Pº CHOPERA",
        "latitude": 40.3686447143555,
        "longitude": -3.53111553192139
    }
]
```
</details>

## Estimations
Get the estimations of bus lines in a bus stop.
<details>
<summary>See more</summary>
To get the estimations of bus lines in a bus stop, you need make a GET request to the following endpoint:

- `{url}/v1/stops/{stopCodes}/estimations`

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

- `{url}/v1/stops/{stopCodes}/times`
- `{url}/v1/stops/{stopCodes}/times/cached` (cached response, this endpoint is faster but can be outdated if no one has made a request in minutes)
- `{url}/v1/stops/{stopCodes}/times/subscribe` (websocket endpoint, you can subscribe to a bus stop and receive the times in real time)

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