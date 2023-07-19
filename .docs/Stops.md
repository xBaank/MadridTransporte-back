# Stops
## Locations
Get the locations of the stops near a coordinate.
<details>
<summary>See more</summary>
To get the locations of the stops near a coordinate, you need to make a GET request to the following URL:

- `{url}/v1/stops/locations?latitude={lat}&longitude={lon}` to get the locations of the stops near a coordinate.

The response code will be `200` or `400`.

### Example Request
```GET http://localhost:8080/v1/stops/locations?latitude=40.37043738780061&longitude=-3.536834949732102```

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

## Search
Get the stops that match the search criteria.
<details>
<summary>See more</summary>

To get the stops that match the search criteria, you must use the following endpoint:

`{url}/v1/stops/search?query={query}`

Where `{query}` is the search criteria.

The response code will be `200` or `400`.

### Example Request
`GET http://localhost:8080/v1/stops/search?query=Leganes` 

### Example Response

```json
[
    {
        "codStop": "4_235",
        "codMode": "4",
        "name": "LEGANES CENTRAL",
        "latitude": 40.32899,
        "longitude": -3.77154
    },
    {
        "codStop": "5_41",
        "codMode": "5",
        "name": "LEGANES",
        "latitude": 40.32899,
        "longitude": -3.77148
    },
    {
        "codStop": "6_2445",
        "codMode": "6",
        "name": "Abrantes-Camino Viejo Leganés",
        "latitude": 40.3767928883653,
        "longitude": -3.73323881313048
    },
    {
        "codStop": "6_2465",
        "codMode": "6",
        "name": "Camino Viejo Leganés-Pelícano",
        "latitude": 40.3837567401341,
        "longitude": -3.72898485890995
    },
    {
        "codStop": "6_2470",
        "codMode": "6",
        "name": "Camino Viejo Leganés-Pelícano",
        "latitude": 40.38393649013,
        "longitude": -3.72905343274949
    },
    {
        "codStop": "6_2471",
        "codMode": "6",
        "name": "Avenida de Oporto-Camino Viejo Leganés",
        "latitude": 40.3860918834771,
        "longitude": -3.72726700546961
    },
    {
        "codStop": "6_3033",
        "codMode": "6",
        "name": "Avenida de Oporto-Camino Viejo Leganés",
        "latitude": 40.3859704849952,
        "longitude": -3.72794831517096
    },
    {
        "codStop": "6_3034",
        "codMode": "6",
        "name": "Avenida de Oporto-Camino Viejo Leganés",
        "latitude": 40.3860438253496,
        "longitude": -3.7277466019699
    },
    {
        "codStop": "6_3036",
        "codMode": "6",
        "name": "Camino Viejo Leganés-Falcinelo",
        "latitude": 40.3823151216349,
        "longitude": -3.73044089634744
    },
    {
        "codStop": "6_3037",
        "codMode": "6",
        "name": "Camino Viejo Leganés-Falcinelo",
        "latitude": 40.3820943258358,
        "longitude": -3.73045250564645
    },
    {
        "codStop": "6_3038",
        "codMode": "6",
        "name": "Camino Viejo Leganés-Carrero Juan Ramón",
        "latitude": 40.3809521898439,
        "longitude": -3.73156012256177
    },
    {
        "codStop": "6_3039",
        "codMode": "6",
        "name": "Camino Viejo Leganés-Carrero Juan Ramón",
        "latitude": 40.3805233548584,
        "longitude": -3.73170312137737
    },
    {
        "codStop": "6_3040",
        "codMode": "6",
        "name": "Camino Viejo Leganés-Carcastillo",
        "latitude": 40.3787663406615,
        "longitude": -3.73316592691836
    },
    {
        "codStop": "6_3041",
        "codMode": "6",
        "name": "Camino Viejo Leganés-Carcastillo",
        "latitude": 40.3785908758617,
        "longitude": -3.73313236763929
    },
    {
        "codStop": "6_3060",
        "codMode": "6",
        "name": "Camino Viejo Leganés-Valle de Oro",
        "latitude": 40.3884947675424,
        "longitude": -3.7269540666913
    },
    {
        "codStop": "6_3061",
        "codMode": "6",
        "name": "Camino Viejo Leganés-Alejandro Sánchez",
        "latitude": 40.3905864511784,
        "longitude": -3.72526545189378
    },
    {
        "codStop": "6_3062",
        "codMode": "6",
        "name": "General Ricardos-Camino Viejo Leganés",
        "latitude": 40.3931236176988,
        "longitude": -3.72364740572698
    },
    {
        "codStop": "6_334",
        "codMode": "6",
        "name": "General Ricardos-Camino Viejo Leganés",
        "latitude": 40.3937532733512,
        "longitude": -3.72308301558623
    },
    {
        "codStop": "6_4595",
        "codMode": "6",
        "name": "Abrantes-Camino Viejo Leganés",
        "latitude": 40.3768941238876,
        "longitude": -3.7343236968952
    },
    {
        "codStop": "6_4609",
        "codMode": "6",
        "name": "Camino Viejo Leganés-Pelícano",
        "latitude": 40.3838630269886,
        "longitude": -3.7292742819214
    },
    {
        "codStop": "6_5549",
        "codMode": "6",
        "name": "Camino Viejo Leganés-Valle de Oro",
        "latitude": 40.3877790702293,
        "longitude": -3.72687053653569
    },
    {
        "codStop": "6_5560",
        "codMode": "6",
        "name": "General Ricardos-Camino Viejo Leganés",
        "latitude": 40.3938506994387,
        "longitude": -3.72335102058993
    },
    {
        "codStop": "8_07751",
        "codMode": "8",
        "name": "LEGANÉS-HUMILLADERO",
        "latitude": 40.2846360697668,
        "longitude": -3.79403171089726
    },
    {
        "codStop": "8_07754",
        "codMode": "8",
        "name": "LEGANÉS-CENTRO DE SALUD MENTAL",
        "latitude": 40.2948951721191,
        "longitude": -3.79017019271851
    },
    {
        "codStop": "8_07755",
        "codMode": "8",
        "name": "LEGANÉS-CENTRO DE SALUD MENTAL",
        "latitude": 40.295223236084,
        "longitude": -3.78975033760071
    },
    {
        "codStop": "8_07756",
        "codMode": "8",
        "name": "AV.LOS ANDES-LEGANÉS",
        "latitude": 40.2952497278076,
        "longitude": -3.78862609146449
    },
    {
        "codStop": "8_07757",
        "codMode": "8",
        "name": "AV.LOS ANDES-LEGANÉS",
        "latitude": 40.2954550756965,
        "longitude": -3.78889908940926
    },
    {
        "codStop": "8_07763",
        "codMode": "8",
        "name": "LEGANÉS-MURCIA",
        "latitude": 40.2894368518681,
        "longitude": -3.79155849950191
    },
    {
        "codStop": "8_07764",
        "codMode": "8",
        "name": "LEGANÉS-CUZCO",
        "latitude": 40.2895545242067,
        "longitude": -3.79147751986261
    },
    {
        "codStop": "8_07771",
        "codMode": "8",
        "name": "LEGANÉS-LOS ÁNGELES",
        "latitude": 40.286998226576,
        "longitude": -3.79245942169942
    },
    {
        "codStop": "8_07772",
        "codMode": "8",
        "name": "LEGANÉS-LOS ÁNGELES",
        "latitude": 40.286835431041,
        "longitude": -3.79255163376751
    },
    {
        "codStop": "8_07780",
        "codMode": "8",
        "name": "TESILLO-LEGANÉS",
        "latitude": 40.2840270996094,
        "longitude": -3.79443144798279
    },
    {
        "codStop": "8_07800",
        "codMode": "8",
        "name": "LEGANÉS-FUENLABRADA",
        "latitude": 40.2424827203384,
        "longitude": -3.77402318385526
    },
    {
        "codStop": "8_07802",
        "codMode": "8",
        "name": "LEGANÉS-FUENLABRADA",
        "latitude": 40.2423861423391,
        "longitude": -3.77364587630631
    },
    {
        "codStop": "8_07880",
        "codMode": "8",
        "name": "SANTA ROSA-EST.LEGANÉS CENTRAL",
        "latitude": 40.3289985656738,
        "longitude": -3.77070903778076
    },
    {
        "codStop": "8_08240",
        "codMode": "8",
        "name": "ESTUDIANTES-LEGANÉS",
        "latitude": 40.3085210256175,
        "longitude": -3.73655426323097
    },
    {
        "codStop": "8_08241",
        "codMode": "8",
        "name": "LEGANÉS-BATRES",
        "latitude": 40.3104468140654,
        "longitude": -3.73689292943845
    },
    {
        "codStop": "8_08242",
        "codMode": "8",
        "name": "PZA.ALCALDE JUAN VERGARA-LEGANÉS",
        "latitude": 40.3108712612135,
        "longitude": -3.73673278575825
    },
    {
        "codStop": "8_08505",
        "codMode": "8",
        "name": "AV.LEGANÉS-AV.CANTARRANAS",
        "latitude": 40.3505331395767,
        "longitude": -3.82298222161711
    },
    {
        "codStop": "8_08506",
        "codMode": "8",
        "name": "AV.LEGANÉS-AV.LISBOA",
        "latitude": 40.3505701777709,
        "longitude": -3.82284136909111
    },
    {
        "codStop": "8_08507",
        "codMode": "8",
        "name": "AV.LEGANÉS-EST.PARQUE LISBOA",
        "latitude": 40.3487519856307,
        "longitude": -3.82007571502942
    },
    {
        "codStop": "8_08508",
        "codMode": "8",
        "name": "AV.LEGANÉS-EST.PARQUE LISBOA",
        "latitude": 40.349492066095,
        "longitude": -3.8211562089656
    },
    {
        "codStop": "8_08511",
        "codMode": "8",
        "name": "AV.LEGANÉS-RÍO SEGRE",
        "latitude": 40.3458404541016,
        "longitude": -3.81386613845825
    },
    {
        "codStop": "8_08512",
        "codMode": "8",
        "name": "AV.LEGANÉS-PARQUE DE MONFRAGÜE",
        "latitude": 40.3459739685059,
        "longitude": -3.81373810768127
    },
    {
        "codStop": "8_08516",
        "codMode": "8",
        "name": "AV.LEGANÉS-AV.DEL PINAR",
        "latitude": 40.3437272721144,
        "longitude": -3.80822903798427
    },
    {
        "codStop": "8_08517",
        "codMode": "8",
        "name": "AV.LEGANÉS-AV.INDUSTRIAS",
        "latitude": 40.3438939846754,
        "longitude": -3.80886682806319
    },
    {
        "codStop": "8_08518",
        "codMode": "8",
        "name": "AV.LEGANÉS-POL.IND.URTINSA",
        "latitude": 40.3417071589602,
        "longitude": -3.80205905636088
    },
    {
        "codStop": "8_08519",
        "codMode": "8",
        "name": "AV.LEGANÉS-POL.IND.SAN JOSÉ VALDERAS",
        "latitude": 40.3415853682567,
        "longitude": -3.8014336088627
    },
    {
        "codStop": "8_09545",
        "codMode": "8",
        "name": "LEGANÉS-HOSPITAL DE GETAFE",
        "latitude": 40.3120468686275,
        "longitude": -3.73886390544241
    },
    {
        "codStop": "8_11887",
        "codMode": "8",
        "name": "AV.CARLOS V-CAMINO DE LEGANES",
        "latitude": 40.3233985900879,
        "longitude": -3.85013246536255
    },
    {
        "codStop": "8_12703",
        "codMode": "8",
        "name": "BALEARES-CºDE LEGANÉS",
        "latitude": 40.3235054016113,
        "longitude": -3.85625457763672
    },
    {
        "codStop": "8_12791",
        "codMode": "8",
        "name": "AV.ESPAÑA-LEGANÉS",
        "latitude": 40.293621567468,
        "longitude": -3.7909014070065
    },
    {
        "codStop": "8_12876",
        "codMode": "8",
        "name": "AV.LEGANÉS-AV.BELLAS VISTAS",
        "latitude": 40.3517990112305,
        "longitude": -3.82481837272644
    },
    {
        "codStop": "8_12877",
        "codMode": "8",
        "name": "AV.LEGANÉS-AV.LOS CARABANCHELES",
        "latitude": 40.351432800293,
        "longitude": -3.82443714141846
    },
    {
        "codStop": "8_13097",
        "codMode": "8",
        "name": "LEGANÉS-EST.PARLA",
        "latitude": 40.2415472732675,
        "longitude": -3.76974497751406
    },
    {
        "codStop": "8_18538",
        "codMode": "8",
        "name": "AV.FELIPE II-CAMINO DE LEGANÉS",
        "latitude": 40.323616027832,
        "longitude": -3.85237169265747
    },
    {
        "codStop": "8_19097",
        "codMode": "8",
        "name": "HUMANES-LEGANÉS",
        "latitude": 40.3081201770643,
        "longitude": -3.73441991947936
    },
    {
        "codStop": "8_20167",
        "codMode": "8",
        "name": "AV.LEGANÉS-EST.PUERTA DEL SUR",
        "latitude": 40.3447685241699,
        "longitude": -3.81110978126526
    },
    {
        "codStop": "8_20483",
        "codMode": "8",
        "name": "LEGANÉS-CENTRO CULTURAL",
        "latitude": 40.2929835808584,
        "longitude": -3.79065866384469
    }
]
```
</details>

