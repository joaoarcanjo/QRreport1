# Device
A **Device** is used either to refer to a toilet, a set of these or even an entire room. 

The **Device API** allows the administrator to create, view and manage all the registered devices. 

All the **vocabulary** used in the devices representations is described [**here**](#devices-representations-vocabulary).

## Device API contents
* [**List devices**](#list-devices)
* [**Create a device**](#create-a-device)
* [**Get a device**](#get-a-device)
* [**Update a device**](#update-a-device)
* [**Change a device category**](#change-a-device-category)
* [**Deactivate a device**](#deactivate-a-device)
* [**Activate a device**](#activate-a-device)
* [**List room devices**](#list-room-devices)
* [**Get a room device**](#get-a-room-device)

## List devices
List the registered devices.

```http
GET /devices
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `page` | integer | query | no | Page number of the results to fetch. **Default:** `1` |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "device", "collection" ],
    "properties": {
        "pageIndex": 0,
        "pageMaxSize": 10,
        "collectionSize": 1
    },
    "entities": [
        {
            "class": [ "device" ],
            "rel": [ "item" ],
            "properties": {
                "id": 1,
                "name": "Toilet1",
                "category": "water",
                "state": "active",
                "timestamp": "2022-05-12 20:54:32452"
            },
            "links": [
                { "rel": [ "self" ], "href": "/devices/1" }
            ]
        }
    ],
    "actions": [
        {
            "name": "create-device",
            "title": "Create a device",
            "method": "POST",
            "href": "/devices",
            "type": "application/json",
            "properties": [
                { "name": "name", "type": "string" },
                { "name": "category", "type": "number" }
            ]
        }
    ],
    "links": [
        { "rel": [ "self" ], "href": "/devices?page=1" },
        { "rel": [ "pagination" ], "href": "/devices{?page}" }
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

## Create a device
Create a new device.

```http
POST /devices
```

### Parameters:
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `name` | string | body |  yes | **Unique** device name. |
| `category` | number | body |  yes | Category identifier for the device. |

### Request body example
```json
{
    "name": "Toilet1",
    "category": 1
}
```

### Response
```http
Status: 201 Created
Location: /devices/{deviceId}
```
```json
{
    "class": [ "device" ],
    "properties": {
        "id": 1,
        "name": "Toilet1",
        "category": "water",
        "state": "active",
        "timestamp": "2022-05-12 20:54:32452"
    },
    "links": [
        { "rel": [ "self" ], "href": "/devices/1" }
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
Status: 409 Conflict
```
* `type`: **unique-constraint**

## Get a device
Get a specific device.

```http
GET /devices/{deviceId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `deviceId` | integer | path | yes | Identifier of the device. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "device" ],
    "properties": {
        "id": 1,
        "name": "Toilet 1",
        "category": "water",
        "state": "active",
        "timestamp": "2022-05-12 20:54:32452"
    },
    "entities": [
        {
            "class": [ "anomaly", "collection" ],
            "rel": [ "device-anomalies" ],
            "properties": {
                "pageIndex": 0,
                "pageMaxSize": 10,
                "collectionSize": 1
            },
            "entities": [
                {
                    "class": [ "anomaly" ],
                    "rel": [ "item" ],
                    "properties": {
                        "id": 1,
                        "anomaly": "Broken toilet"
                    },
                    "actions": [ 
                        {
                            "name": "update-anomaly",
                            "title": "Update anomaly",
                            "method": "PUT",
                            "href": "/devices/1/anomalies/1",
                            "type": "application/json",
                            "properties": [
                                { "name": "anomaly", "type": "string" }
                            ]
                        },
                        {
                            "name": "delete-anomaly",
                            "title": "Delete anomaly",
                            "method": "DELETE",
                            "href": "/devices/1/anomalies/1"
                        }
                    ],
                    "links": [
                        { "rel": [ "self" ], "href": "/devices/1/anomalies/1" }
                    ]
                }
            ],
            "actions": [
                {
                    "name": "create-anomaly",
                    "title": "Create new anomaly",
                    "method": "POST",
                    "href": "/devices/1/anomalies",
                    "type": "application/json",
                    "properties": [
                        { "name": "anomaly", "type": "string" }
                    ]
                }
            ],
            "links": [
                { "rel": [ "self" ], "href": "/devices/1/anomalies?page=1"},
                { "rel": [ "pagination" ], "href": "/devices/1/anomalies{?page}"}
            ]
        }
    ],
    "actions": [
        {
            "name": "deactivate-device",
            "title": "Deactivate device",
            "method": "POST",
            "href": "/devices/1"
        },
        {
            "name": "update-device",
            "title": "Update device",
            "method": "PUT",
            "href": "/devices/1",
            "type": "application/json",
            "properties": [
                { "name": "name", "type": "string" }
            ]
        },
        {
            "name": "change-device-category",
            "title": "Change device category",
            "method": "PUT",
            "href": "/devices/1",
            "type": "application/json",
            "properties": [
                { "name": "category", "type": "number" }
            ]
        }
    ],
    "links": [
        { "rel": [ "self" ], "href": "/devices/1" },
        { "rel": [ "devices" ], "href": "/devices" }
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

## Update a device
Update the name of a specific device.

```http
PUT /device/{deviceId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `deviceId` | integer | path | yes | Identifier of the device. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `name` | string | body | yes | New **unique** name for the device. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "device" ],
    "properties": {
        "id": 1,
        "name": "Toilet1",
        "category": "water",
        "state": "active",
        "timestamp": "2022-05-12 20:54:32452"
    },
    "links": [
        { "rel": [ "self" ], "href": "/devices/1" }
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
* `types`: **unique-constraint**, **inactive-entity**

## Change a device category
Change the device category.

```http
PUT /device/{deviceId}/category
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `deviceId` | integer | path | yes | Identifier of the device. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `category` | number | body | yes | New category for the device. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "device" ],
    "properties": {
        "id": 1,
        "name": "Toilet1",
        "category": "canalization",
        "state": "active",
        "timestamp": "2022-05-12 20:54:32452"
    },
    "links": [
        { "rel": [ "self" ], "href": "/devices/1" }
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
* `type`: **inactive-entity**

## Deactivate a device
Deactivate a specific device.

```http
POST /devices/{deviceId}/deactivate
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:
| `deviceId` | integer | path | yes | Identifier of the device. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "device" ],
    "properties": {
        "id": 1,
        "name": "Toilet 1",
        "category": "water",
        "state": "inactive",
        "timestamp": "2022-05-12 20:54:32452"
    },
    "links": [
        { "rel": [ "self" ], "href": "/devices/1" },
        { "rel": [ "devices" ], "href": "/devices" }
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

## Activate a device
Activate a specific device.

```http
POST /devices/{deviceId}/activate
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:
| `deviceId` | integer | path | yes | Identifier of the device. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "device" ],
    "properties": {
        "id": 1,
        "name": "Toilet 1",
        "category": "water",
        "state": "active",
        "timestamp": "2022-05-14 14:23:56788"
    },
    "links": [
        { "rel": [ "self" ], "href": "/devices/1" }
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

## List room devices
List the devices of a specific room.

```http
GET /companies/{companyId}/buildings/{buildingId}/rooms/{roomId}/devices
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `companyId` | integer | path | yes | Identifier of the company. |
| `buildingId` | integer | path | yes | Identifier of the building. |
| `roomId` | integer | path | yes | Identifier of the room. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "device", "collection" ],
    "properties": {
        "pageIndex": 0,
        "pageMaxSize": 10,
        "collectionSize": 1
    },
    "entities": [
        {
            "class": [ "device" ],
            "rel": [ "item" ],
            "properties": {
                "id": 1,
                "name": "Toilet 1",
                "category": "water",
                "state": "active",
                "timestamp": "2022-05-14 14:23:56788"
            },
            "links": [
                { "rel": [ "self" ], "href": "/companies/1/buildings/1/rooms/1/devices/1" }
            ]
        }
    ],
    "actions": [
        {
            "name": "add-room-device",
            "title": "Add device",
            "method": "POST",
            "href": "/companies/1/buildings/1/rooms/1/devices",
            "type": "application/json",
            "properties": [
                { "name": "device", "type": "number", "possibleValues": { "href": "/devices" } }
            ]
        }
    ],
    "links": [
        { "rel": [ "self" ], "href": "/companies/1/buildings/1/rooms/1/devices?page=1" },
        { "rel": [ "pagination" ], "href": "/companies/1/buildings/1/rooms/1/devices{?page}" }
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

## Get a room device
Get the room device with the associated QR Code.

```http
GET /companies/{companyId}/buildings/{buildingId}/rooms/{roomId}/devices/{deviceId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `companyId` | integer | path | yes | Identifier of the company. |
| `buildingId` | integer | path | yes | Identifier of the building. |
| `roomId` | integer | path | yes | Identifier of the room. |
| `deviceId` | integer | path | yes | Identifier of the device. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "device" ],
    "properties": {
        "id": 1,
        "name": "Toilet1",
        "category": "water",
        "state": "active"
    },
    "entities": [
        {
            "class": [ "qrcode" ],
            "rel": [ "room-device-qrcode" ],
            "properties": {
                "qrcode": "/company/1/building/1/rooms/1/devices/1/qrcode"
            },
            "actions": [
                {
                    "name": "generate-new-qrcode",
                    "title": "Generate new QR Code",
                    "method": "POST",
                    "href": "/company/1/building/1/rooms/1/devices/1/qrcode"
                }
            ]
        }
    ],
    "actions": [
        {
            "name": "remove-room-device",
            "title": "Remove device",
            "method": "DELETE",
            "href": "/company/1/building/1/rooms/1/devices/1"
        }
    ],
    "links": [
        { "rel": [ "self" ], "href": "/company/1/building/1/rooms/1/devices/1" },
        { "rel": [ "room" ], "href": "/company/1/building/1/rooms/1" }
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

## Device representations vocabulary
| Name | Type | Description |
|:-:|:-:|:-:|
| `id` | number | **Unique** and **stable** identifier of the device. Must be greater than 0. |
| `name` | string | **Unique** name of the device. |
| `category` | string | Name of the device category . |
| `state` | string | Current state of the device, the possible values are `active` or `inactive`. |
| `timestamp` | string | Timestamp of the moment that the device state changed to the current state. |

### Domain specific link relations
| Name | Description |
|:-:|:-:|
| `room-device-qrcode` | QR Code associated to the room device. |
| `device-anomalies` | Set of anomalies associated to the device. |
| `devices` | Resource with the representation of all the devices registered in the system. |

The **documentation** for the `media-type`, `classes`, `standard link relations` and `generic errors` used in the representations are described [**here**](../README.md).

The **vocabulary** about the `categories` can be consulted [**here**](Category.md).