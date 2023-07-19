# Abono
Get information about an abono.
<details>
<summary>See more</summary>
To get information about the abono, you need to make a GET request to the following URL:

` {url}/v1/abono/{id}`

The response code will be `200` or `401` or `400`.

`{id}` is the id of the abono you want to get information about.
> **Note**
> The `001` in `001 000 000 000 000000000` must be omitted, but the rest is required.

## Example Request
```GET http://localhost:8080/v1/abono/0000000010040117584```

## Example Response
```json
{
    "serialNumber": "0454211ADA3B80",
    "ttpNumber": "0010000000010040117584",
    "createdAt": "2015-11-24",
    "expireAt": "2025-11-24",
    "contracts": [
        {
            "contractCode": 1055,
            "contractName": "ABONO 30 DIAS JOVEN T. PLANA",
            "contractCompanyPropietary": 1,
            "contractUserProfileType": "03",
            "contractUserProfilePropietaryCompany": "01",
            "chargeDate": "2023-06-22",
            "firstUseDateLimit": "2023-07-01",
            "firstUseDate": "2023-06-22",
            "lastUseDate": "2023-07-21",
            "useDays": 29,
            "leftDays": 5,
            "charges": "0",
            "remainingCharges": "0"
        }
    ]
}
```
</details>