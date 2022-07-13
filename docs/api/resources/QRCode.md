# QR Code
The **QR Code** is associated to a room device and is used to store the URL to make a report of the problem found in the device which the QR Code was scanned. A QR Code can also be associated to room and not only a specific or set of devices. 

The **QR Code API** allows the manager of the building to create, view and change all the QR Codes associated to a room device. 

All the **vocabulary** used in the QR Code representations is described [**here**](#qr-code-representations-vocabulary).

## QR Code API contents
* [**Create or change a QR Code**](#create-or-change-a-qr-code)
* [**Get a QR Code**](#get-a-qr-code)
* [**Get report form**](#get-report-form)

## Create or change a QR Code
Create or change a QR Code for a specific room device.

```http
POST /companies/{companyId}/buildings/{buildingId}/rooms/{roomId}/devices/{deviceId}/qrcode
```

### Parameters:
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `companyId` | integer | path | yes | Identifier of the company. |
| `buildingId` | integer | path | yes | Identifier of the building. |
| `roomId` | integer | path | yes | Identifier of the room. Must be greater than 0. |
| `deviceId` | integer | path | yes | Identifier of the device. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 201 Created
Location: /companies/{companyId}/buildings/{buildingId}/rooms/{roomId}/devices/{deviceId}/qrcode
```

```http
Status: 400 Bad Request
```
```http
Status: 401 Unauthorized
```
```http
Status: 403 Forbidden
```
```http
Status: 404 Not Found
```

## Get a QR Code
Get the QR Code of a room device.

```http
GET /companies/{companyId}/buildings/{buildingId}/rooms/{roomId}/devices/{deviceId}/qrcode
```

### Parameters:
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `companyId` | integer | path | yes | Identifier of the company. |
| `buildingId` | integer | path | yes | Identifier of the building. |
| `roomId` | integer | path | yes | Identifier of the room. Must be greater than 0. |
| `deviceId` | integer | path | yes | Identifier of the device. Must be greater than 0. |
| `accept` | string | header | yes | Set to `image/png`. |

### Response
```http
Status: 200 OK
```

```http
Status: 400 Bad Request
```
```http
Status: 401 Unauthorized
```
```http
Status: 403 Forbidden
```
```http
Status: 404 Not Found
```

## Get report form
Get the report form after reading a QR Code.

```http
GET /report/{hash}
```

### Parameters:
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `hash` | string | path | yes | **Unique** hash that is associated to a room device. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 200 OK
```
```json
{
    "class": [ "report" ],
    "properties": {
        "company": "ISEL",
        "building": "A",
        "room": "1 - Bathroom",
        "device": "Toilet1"
    },
    "actions": [
        {
            "name": "report",
            "title": "Report",
            "method": "POST",
            "href": "/tickets",
            "type": "application/json",
            "properties": [
                { "name": "subject", "type": "string" },
                { "name": "description", "type": "string" },
                { "name": "hash", "type": "string" },
                { "name": "name", "type": "string" },
                { "name": "phone", "type": "string", "required": false },
                { "name": "email", "type": "string" }
            ]
        },
        {
            "name": "logout",
            "title": "Logout",
            "method": "POST",
            "href": "/logout"
        }
    ],
    "links": [
        { "rel": [ "self" ], "href": "/report/5abd4089b7921fd6af09d1cc1cbe5220" }
    ]
}
```

```http
Status: 400 Bad Request
```
```http
Status: 401 Unauthorized
```
```http
Status: 403 Forbidden
```
```http
Status: 404 Not Found
```

## QR Code representations vocabulary
| Name | Type | Description |
|:-:|:-:|:-:|
| `hash` | string | **Unique** hash associated to a room device. |
| `company` | string | Company name where the QR Code is localized. |
| `building` | string | Building name where the QR Code is localized. |
| `room` | string | Room name where the QR Code is localized. |
| `device` | string | Device name that is associated to the QR Code used. |

The **documentation** for the `media-type`, `classes`, `standard link relations` and `generic errors` used in the representations are described [**here**](../README.md).