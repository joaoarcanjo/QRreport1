# QR Code
The **QR Code** is associated to a room device and is used to store the URL to make a report of the problem found in the device which the QR Code was scanned. A QR Code can also be associated to room and not only a specific or set of devices. 

The **QR Code API** allows the manager of the building to create, view and change all the QR Codes associated to a room device. 

All the **vocabulary** used in the QR Code representations is described [**here**](#qr-code-representations-vocabulary).

## QR Code API contents
* [**Create or change a QR Code**](#create-or-change-a-qr-code)
* [**Get a QR Code**](#get-a-qr-code)

## Create or change a QR Code
Create or change a QR Code for a specific room device.

```http
POST /rooms/{roomId}/devices/{deviceId}/qr-code
```

### Parameters:
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `roomId` | integer | path | yes | Identifier of the room. Must be greater than 0. |
| `deviceId` | integer | path | yes | Identifier of the device. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 201 Created
Location: /rooms/{roomId}/devices/{deviceId}/qr-code
```
```json
{
    "class": [ "qrcode" ],
    "properties": {
        "qrcode": "http://api.qrreport.com/qr-code/ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    },
    "links": [
        { "rel": [ "self" ], "href": "/rooms/1/devices/1/qr-code" }
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
```http
Status: 409 Conflict
```
* `type`: **unique-constraint**

## Get a QR Code
Get the QR Code of a room device.

```http
GET /rooms/{roomId}/devices/{deviceId}/qr-code
```

### Parameters:
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `roomId` | integer | path | yes | Identifier of the room. Must be greater than 0. |
| `deviceId` | integer | path | yes | Identifier of the device. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 200 OK
```
```json
{
    "class": [ "qrcode" ],
    "properties": {
        "qrcode": "http://api.qrreport.com/qr-code/ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    },
    "links": [
        { "rel": [ "self" ], "href": "/rooms/1/devices/1/qr-code" }
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
| `qrcode` | string | URL to obtain the QR Code with a **unique** hash for each room device. |

The **documentation** for the `media-type`, `classes`, `standard link relations` and `generic errors` used in the representations are described [**here**](../README.md).