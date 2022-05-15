# Room
A **Room** is a division of a building. 

The **Room API** allows the administrator and the building manager to create, view and manage all the rooms of a specific company building. 

All the **vocabulary** used in the rooms representations is described [**here**](#room-representations-vocabulary).

## Company API contents
* [**List rooms**](#list-rooms)
* [**Create a room**](#create-a-room)
* [**Get a room**](#get-a-room)
* [**Update a room**](#update-a-room)
* [**Add a device to a room**](#add-a-device-to-a-room)
* [**Remove a device from a room**](#remove-a-device-from-a-room)
* [**Deactivate a room**](#deactivate-a-room)
* [**Activate a room**](#activate-a-room)

## List rooms
List the rooms of a company building.

```http
GET /companies/{companyId}/buildings/{buildingId}/rooms
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `companyId` | integer | path | yes | Identifier of the company. |
| `buildingId` | integer | path | yes | Identifier of the building. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `page` | integer | query | no | Page number of the results to fetch. **Default:** `0` |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "room", "collection" ],
    "properties": {
        "pageIndex": 0,
        "pageSize": 1,
        "collectionSize": 1
    },
    "entities": [
        {
            "class": [ "room" ],
            "rel": [ "item" ],
            "properties": {
                "id": 1,
                "name": "9",
                "floor": 1,
                "state": "Active"
            },
            "links": [
                { "rel": [ "self" ], "href": "/rooms/1" }
            ]
        }
    ],
    "actions": [
        {
            "name": "create-room",
            "title": "Create a room",
            "method": "POST",
            "href": "/companies/1/buildings/1/rooms",
            "type": "application/json",
            "properties": [
                { "name": "name", "type": "string" },
                { "name": "floor", "type": "number" }
            ]
        }
    ],
    "links": [
        { "rel": [ "self" ], "href": "/companies/1/buildings/1/rooms?page=0" }
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

## Create a room
Create a new room for a company building.

```http
POST /companies/{companyId}/buildings/{buildingId}/rooms
```

### Parameters:
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `companyId` | integer | path | yes | Identifier of the company. Must be greater than 0. |
| `buildingId` | integer | path | yes | Identifier of the building. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `name` | string | body |  yes | **Unique** name for the room inside the building. |
| `floor` | integer | body |  yes | Floor number of the room. |

### Response
```http
Status: 201 Created
Location: /rooms/{roomId}
```
```json
{
    "class": [ "room" ],
    "properties": {
        "id": 1,
        "name": "9",
        "floor": 1,
        "state": "Active"
    },
    "links": [
        { "rel": [ "self" ], "href": "/rooms/1" }
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

## Get a room
Get a specific room.

```http
GET /rooms/{roomId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `roomyId` | integer | path | yes | Identifier of the room. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "room" ],
    "properties": {
        "id": 1,
        "name": "9",
        "floor": 1,
        "state": "Active",
        "timestamp": "2022-05-12 20:26:87632"
    },
    "entities": [
        {
            "class": [ "device", "collection" ],
            "rel": [ "room-devices" ],
            "properties": {
                "pageIndex": 0,
                "pageSize": 1,
                "collectionSize": 1
            },
            "entities": [
                {
                    "class": [ "device" ],
                    "rel": [ "item" ],
                    "properties": {
                        "id": 1,
                        "name": "Toilet 1",
                        "state": "Active"
                    },
                    "links": [
                        { "rel": [ "self" ], "href": "/rooms/1/devices/1" }
                    ]
                }
            ],
            "actions": [
                {
                    "name": "add-device-to-room",
                    "title": "Add a device to the room",
                    "method": "POST",
                    "href": "/rooms/1/devices",
                    "type": "application/json",
                    "properties": [
                        { "name": "device", "type": "number" }
                    ]
                }
            ],
            "links": [
                { "rel": [ "self" ], "href": "/rooms/1/devices?page=0" }
            ]
        }
    ],
    "actions": [
        {
            "name": "deactivate-room",
            "title": "Deactivate room",
            "method": "PUT",
            "href": "/rooms/1"
        },
        {
            "name": "update-room",
            "title": "Update room",
            "method": "PUT",
            "href": "/rooms/1",
            "type": "application/json",
            "properties": [
                { "name": "name", "type": "string" }
            ]
        }
    ],
    "links": [
        { "rel": [ "self" ], "href": "/rooms/1" },
        { "rel": [ "rooms" ], "href": "/companies/1/buildings/1/rooms" }
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

## Update a room
Update the name of a specific room.

```http
PUT /rooms/{roomId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `roomId` | integer | path | yes | Identifier of the room. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `name` | string | body | yes | New **unique** name for the room. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "room" ],
    "properties": {
        "id": 1,
        "name": "9 - Restroom",
        "floor": 1,
        "state": "Active"
    },
    "links": [
        { "rel": [ "self" ], "href": "/rooms/1" }
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

## Add a device to a room
Add a device to a specific room of a company building.

```http
POST /room/{roomId}/devices
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:
| `roomId` | integer | path | yes | Identifier of the room. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `deviceId` | integer | body | yes | Identifier of the device to add to the room. |

### Response
```http
Status: 201 Created
Location: /room/{roomId}/devices/{deviceId}
```

```json
{
    "class": [ "room" ],
    "properties": {
        "id": 1,
        "name": "9",
        "floor": 1,
        "state": "Active",
        "timestamp": "2022-05-12 23:20:32452"
    },
    "entities": [
        {
            "class": [ "device" ],
            "rel": [ "room-device" ],
            "properties": {
                "id": 2,
                "name": "Toilet 2",
                "state": "Active"
            },
            "links": [
                { "rel": [ "self" ], "href": "/devices/2" }
            ]
        }
    ],
    "links": [
        { "rel": [ "self" ], "href": "/rooms/1/devices/2" }
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

## Remove a device from a room
Remove a device from a specific room of a company building.

```http
DELETE /room/{roomId}/devices/{deviceId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:
| `roomId` | integer | path | yes | Identifier of the room. |
| `deviceId` | integer | path | yes | Identifier of the room device to remove. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 200 OK
```

```json
{
    "class": [ "room" ],
    "properties": {
        "id": 1,
        "name": "9",
        "floor": 1,
        "state": "Active",
        "timestamp": "2022-05-12 23:20:32452"
    },
    "entities": [
        {
            "class": [ "device" ],
            "rel": [ "room-device-removed" ],
            "properties": {
                "id": 2,
                "name": "Toilet 2",
                "state": "Active"
            },
            "links": [
                { "rel": [ "self" ], "href": "/devices/2" }
            ]
        }
    ],
    "links": [
        { "rel": [ "self" ], "href": "/rooms/1/devices" }
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

## Deactivate a room
Deactivate a specific room.

```http
PUT /room/{roomId}/deactivate
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:
| `roomId` | integer | path | yes | Identifier of the room. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "room" ],
    "properties": {
        "id": 1,
        "name": "9",
        "floor": 1,
        "state": "Inactive",
        "timestamp": "2022-05-12 23:20:32452"
    },
    "links": [
        { "rel": [ "self" ], "href": "/rooms/1" },
        { "rel": [ "rooms" ], "href": "/companies/1/buildings/1/rooms" }
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

## Activate a room
Activate a specific room.

```http
PUT /rooms/{roomId}/activate
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:
| `roomId` | integer | path | yes | Identifier of the room. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "room" ],
    "properties": {
        "id": 1,
        "name": "9",
        "floor": 1,
        "state": "Active",
        "timestamp": "2022-05-13 10:15:87312"
    },
    "links": [
        { "rel": [ "self" ], "href": "/rooms/1" }
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

## Company representations vocabulary
| Name | Type | Description |
|:-:|:-:|:-:|
| `id` | number | **Unique** and **stable** identifier of the room. Must be greater than 0. |
| `name` | string | **Unique** name of the room inside a specific building. |
| `floor` | number | Number of the floor the room is in. The floor can be greater, less or equal to zero. |
| `state` | string | Current state of the room, the possible values are `Active` or `Inactive`. |
| `timestamp` | string | Timestamp of the moment that the room state changed to the current state. |

### Domain specific link relations
| Name | Description |
|:-:|:-:|
| `room-devices` | Set of devices that exist in the room. |
| `room-device-added` | Device added to the room. |
| `room-device-removed` | Device removed from the room. |
| `rooms` | Resource with the representation of all the rooms of a company building. |

The **documentation** for the `media-type`, `classes`, `standard link relations` and `generic errors` used in the representations are described [**here**](../README.md).

The **vocabulary** about the `devices` can be consulted [**here**](Device.md).