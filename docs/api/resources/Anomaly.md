# Anomaly
An **Anomaly** is something that deviates from what is standard, normal, or expected.

The **Anomaly API** allows the administrator to create, view and manage a set of common anomalies for each device. 

All the **vocabulary** used in the anomalies representations is described [**here**](#anomalies-representations-vocabulary).

## Anomaly API contents
* [**List device anomalies**](#list-device-anomalies)
* [**Create a device anomaly**](#create-a-device-anomaly)
* [**Update an anomaly**](#update-an-anomaly)
* [**Delete an anomaly**](#delete-an-anomaly)

## List device anomalies
List the anomalies of a specific device.

```http
GET /devices/{deviceId}/anomalies
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `deviceId` | integer | path | yes | Identifier of the device. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `page` | integer | query | no | Page number of the results to fetch. **Default:** `0` |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "anomaly", "collection" ],
    "properties": {
        "pageIndex": 0,
        "pageSize": 1,
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
        { "rel": [ "self" ], "href": "/devices/1/anomalies?page=0"}
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

## Create a device anomaly
Create a new anomaly for a specific device.

```http
POST /devices/{deviceId}/anomalies
```

### Parameters:
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `deviceId` | integer | path | yes | Identifier of the device. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `anomaly` | string | body |  yes | **Unique** anomaly subject. |

### Response
```http
Status: 201 Created
Location: /devices/{deviceId}/anomalies/{anomalyId}
```
```json
{
    "class": [ "anomaly" ],
    "properties": {
        "id": 1,
        "anomaly": "Broken toilet"
    },
    "links": [
        { "rel": [ "device-anomalies" ], "href": "/devices/1/anomalies" }
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

## Update an anomaly
Update the anomaly subject.

```http
PUT /devices/{deviceId}/anomalies/{anomalyId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `deviceId` | integer | path | yes | Identifier of the device. |
| `anomalyId` | integer | path | yes | Identifier of the anomaly. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `anomaly` | string | body | yes | New **unique** anomaly for the device. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "anomaly" ],
    "properties": {
        "id": 1,
        "name": "Broken toilet with water problem"
    },
    "links": [
        { "rel": [ "device-anomalies" ], "href": "/devices/1/anomalies" }
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

## Delete an anomaly
Delete an anomaly on a specific device.

```http
DELETE /devices/{deviceId}/anomalies/{anomalyId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:
| `deviceId` | integer | path | yes | Identifier of the device. |
| `anomalyId` | integer | path | yes | Identifier of the anomaly. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "anomaly" ],
    "properties": {
        "id": 1,
        "anomaly": "Broken toilet"
    },
    "links": [
        { "rel": [ "device-anomalies" ], "href": "/devices/1/anomalies" }
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
| `id` | number | Identifier of the anomaly. Must be greater than 0. **Unique for each device, but not globally.**  |
| `anomaly` | string | **Unique** subject for an anomaly of a specific device. |

### Domain specific link relations
| Name | Description |
|:-:|:-:|
| `device-anomalies` | Set of anomalies that belong to the device. |

The **vocabulary** about the `devices` can be consulted [**here**](Device.md).

The **documentation** for the `media-type`, `classes`, `standard link relations` and `generic errors` used in the representations are described [**here**](../README.md).