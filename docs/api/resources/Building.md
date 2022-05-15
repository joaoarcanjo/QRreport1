# Building
A **Building** is a property of a company and can be described as a set of rooms. 

The **Building API** allows the administrator and the building manager to create, view and manage all the registered buildings.

All the **vocabulary** used in the buildings representations is described [**here**](#building-representations-vocabulary).

## Building API contents
* [**List buildings**](#list-buildings)
* [**Create a building**](#create-a-building)
* [**Get a building**](#get-a-building)
* [**Update a building**](#update-a-building)
* [**Deactivate a building**](#deactivate-a-building)
* [**Activate a building**](#activate-a-building)
* [**Change manager**](#change-manager)

## List buildings
List buildings of a determined company. 

```http
GET /companies/{companyId}/buildings
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `companyId` | integer | path | yes | Identifier of the company. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `page` | integer | query | no| Page number of the results to fetch. **Default:** `0` |

### Response
```http
Status: 200 OK
```

```json
{
    "class": [ "building", "collection" ],
    "properties": {
        "pageIndex": 0,
        "pageSize": 1,
        "collectionSize": 1
    },
    "entities": [ 
        {
            "class": [ "building" ],
            "rel": [ "item" ],
            "properties": {
                "id": 1,
                "name": "Amoreiras",
                "floors": 6,
                "state": "Active",
                "timestamp": "2022-05-12 19:23:56782"
            },
            "links": [
                { "rel": [ "self" ], "href": "/companies/1/buildings/1" }
            ]
        }
    ],
    "actions": [
        {
            "name": "create-building",
            "title": "Create a building",
            "method": "POST",
            "href": "/companies/1/buildings",
            "type": "application/json",
            "properties": [
                { "name": "name", "type": "string" },
                { "name": "floors", "type": "number" },
                { "name": "manager", "type": "uuid" },
            ]
        }
    ],
    "links": [
        { "rel": [ "self" ], "href": "/companies/1/buildings?page=0" },
        { "rel": [ "company" ], "href": "/companies/1" }
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

## Create a building
Create a new building.

```http
POST /companies/{companyId}/buildings
```

### Parameters:
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `companyId` | integer | path | yes | Identifier of the company. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `name` | string | body |  yes | Name of the building. |
| `floors` | integer | body | yes | Number of floors in a building. Must be greater than 0. |
| `manager` | uuid | body | yes | Identifier of the manager. |

### Request body example
```json
{
    "name": "LabCenter",
    "floors": 3,
    "manager": "cf128ed3-0d65-42d9-8c96-8ff2e05b3d08"
}
```

### Response
```http
Status: 201 Created
Location: /companies/1/buildings/1
```

```json
{
    "class": [ "building" ],
    "properties": {
        "id": 1,
        "name": "Amoreiras",
        "floors": 3,
        "state": "Active",
        "timestamp": "2022-04-08 21:52:47.012620"
    },
     "links": [
        { "rel": [ "self" ], "href": "/companies/1/buildings/1" }
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
* `types`: [**inactive-company**](#domain-specific-errors)

```http
Status: 415 Unsupported Media Type
```

## Get a building
Get a specific building. 

```http
GET /companies/{companyId}/buildings/{buildingId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `companyId` | integer | path | yes | Identifier of the company. Must be greater than 0. |
| `buildingId` | integer | path | yes | Identifier of the building. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "building" ],
     "properties": {
        "id": 1,
        "name": "Amoreiras",
        "floor": 6,
        "state": "Active",
        "timestamp": "2022-04-08 21:52:47.012620",
        "company": "ISEL"
    },
    "entities": [
        {
            "class": [ "room", "collection" ],
            "rel": [ "building-rooms" ],
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
                        "name": "lab",
                        "state": "Active"
                    },
                    "links": [
                        { "rel": [ "self" ], "href": "/companies/1/buildings/1/rooms/1"}
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
                { "rel": [ "self" ], "href": "/companies/1/buildings/1/rooms?page=0"}
            ]
        },
        {
            "class": [ "person" ],
            "rel": [ "building-manager" ],
            "properties": {
                "id": "cf128ed3-0d65-42d9-8c96-8ff2e05b3d08",
                "name": "José Bonifácio",
                "email": "joca@gmail.com"
            },
            "links": [
                { "rel": [ "self" ], "href": "/persons/cf128ed3-0d65-42d9-8c96-8ff2e05b3d08" }
            ]
        }
    ],
    "actions": [
        {
            "name": "deactivate-building",
            "title": "Deactivate issue",
            "method": "DELETE",
            "href": "/companies/1/buildings/1"
        },
        {
            "name": "update-building",
            "title": "Update building",
            "method": "PUT",
            "href": "/companies/1/buildings/1",
            "type": "application/json",
            "properties": [
                { "name": "name", "type": "string" },
                { "name": "floors", "type": "number" }
            ]
        },
        {
            "name": "change-manager",
            "title": "Change manager",
            "method": "PUT",
            "href": "/companies/1/buildings/1/manager",
            "type": "application/json",
            "properties": [
                { "name": "manager", "type": "uuid" }
            ]
        }

    ],
    "links": [
        { "rel": [ "self" ], "href": "/companies/1/buildings/1" },
        { "rel": [ "buildings" ], "href": "/companies/1/buildings" }
        { "rel": [ "company" ], "href": "/companies/1" }
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

## Update a building
Update the name or the number of floors of a specific building.

```http
PUT /companies/{companyId}/buildings/{buildingId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `companyId` | integer | path | yes | Identifier of the company. Must be greater than 0. |
| `buildingId` | integer | path | yes | Identifier of the building. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `name` | string | body | no | New name for the person. |
| `floors` | integer | body | no | New number of floors. |

**Notice:** At least one of the body parameters **should** be inserted.

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "building" ],
    "properties": {
        "id": 1,
        "name": "New Amoreiras",
        "floors": 6,
        "state": "Active",
        "timestamp": "2022-05-14 14:23:56788"
    },
    "links": [
        { "rel": [ "self" ], "href": "/companies/1/buildings/1" }
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
* `types`: [**inactive-building**](#domain-specific-errors)

```http
Status: 415 Unsupported Media Type
```

## Deactivate a building
Deactivate a certain building.

```http
DELETE /companies/{companyId}/buildings/{buildingId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `companyId` | integer | path | yes | Identifier of the company. Must be greater than 0. |
| `buildingId` | integer | path | yes | Identifier of the building. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "building" ],
    "properties": {
        "id": 1,
        "name": "Amoreiras",
        "floors": 6,
        "state": "Inactive",        
        "timestamp": "2022-05-14 14:23:56788"
    },
    "links": [
        { "rel": [ "self" ], "href": "/companies/1/buildings/1" },
        { "rel": [ "companies" ], "href": "/companies/1/buildings" }
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

## Activate a building
Activate a certain building.

```http
PUT /companies/{companyId}/buildings/{buildingId}/activate
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:
| `companyId` | integer | path | yes | Identifier of the company. Must be greater than 0. |
| `buildingId` | integer | path | yes | Identifier of the company. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "building" ],
    "properties": {
        "id": 1,
        "name": "Amoreiras",
        "floors": 6,
        "state": "Active",        
        "timestamp": "2022-06-20 12:42:12415"
    },
    "links": [
        { "rel": [ "self" ], "href": "/companies/1/buildings/1" }
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

## Change manager
Change manager of a certain building.

```http
PUT /companies/{companyId}/buildings/{buildingId}/manager
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:
| `companyId` | integer | path | yes | Identifier of the company. Must be greater than 0. |
| `buildingId` | integer | path | yes | Identifier of the company. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `manager` | uuid | body | yes | New manager for the building. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "building" ],
    "properties": {
        "id": 1,
        "name": "Amoreiras",
        "manager": "bbd344ff-37f3-4649-8068-bd9ff6535c6e"
    },
    "links": [
        { "rel": [ "self" ], "href": "/companies/1/buildings/1" }
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
* `types`: [**inactive-building**](#domain-specific-errors)
```http
Status: 415 Unsupported Media Type
```

## Building representations vocabulary
| Name | Type | Description |
|:-:|:-:|:-:|
| `id` | integer | **Unique** and **stable** identifier of the building. **Unique for each company, but not globally.** |
| `name` | string | Name of the building. |
| `floors` | integer | Number of floors in the building. |
| `state` | string | Current state of the building, the possible values are `Active` or `Inactive`. |
| `timestamp` | string | Timestamp of the moment that the building state changed to the current state. |
| `company` | integer | Identifier of the company owner of the building.
| `manager` | integer | Identifier of the building manager.

### Domain specific link relations
| Name | Description |
|:-:|:-:|
| `building-rooms` | Set of rooms that belong to the building. |
| `building-manager` | Representation of the manager of the building. |
| `buildings` | Resource with the representation of all the companies registered in the system. |

### Domain specific errors
* `inactive-company`: Happens when it's requested to create a building for an **inactive** company. 
  * It is thrown with the HTTP status code `409 Conflict`.
```json
{
    "type": "/errors/inactive-company",
    "title": "It's not possible to create a building for an inactive company.",
    "instance": "/companies/1/buildings/1"
}
```
* `inactive-building`: Happens when it's requested to update an **inactive** building. 
  * It is thrown with the HTTP status code `409 Conflict`.
```json
{
    "type": "/errors/inactive-building",
    "title": "It's not possible to update an inactive building.",
    "instance": "/companies/1/buildings/1"
}
```

The **documentation** for the `media-type`, `classes`, `standard link relations` and `generic errors` used in the representations are described [**here**](../README.md).

The **vocabulary** for each external class represented in this documented can be consulted by clicking in one of the following links:
* [**Person**](Person.md)
* [**Company**](Company.md)
* [**Room**](Room.md)