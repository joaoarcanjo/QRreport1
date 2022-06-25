# Company
A **Company** can be described as an association of persons operating a business. 

The **Company API** allows the administrator to create, view and manage all the registered companies. 

All the **vocabulary** used in the companies representations is described [**here**](#company-representations-vocabulary).

## Company API contents
* [**List companies**](#list-companies)
* [**Create a company**](#create-a-company)
* [**Get a company**](#get-a-company)
* [**Update a company**](#update-a-company)
* [**Deactivate a company**](#deactivate-a-company)
* [**Activate a company**](#activate-a-company)

## List companies
List the registered companies.

```http
GET /companies
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
    "class": [ "company", "collection" ],
    "properties": {
        "pageIndex": 1,
        "pageMaxSize": 10,
        "collectionSize": 1
    },
    "entities": [
        {
            "class": [ "company" ],
            "rel": [ "item" ],
            "properties": {
                "id": 1,
                "name": "ISEL",
                "state": "active",
                "timestamp": "2022-05-12 19:23:56782"
            },
            "links": [
                { "rel": [ "self" ], "href": "/companies/1" }
            ]
        }
    ],
    "actions": [
        {
            "name": "create-company",
            "title": "Create company",
            "method": "POST",
            "href": "/companies",
            "type": "application/json",
            "properties": [
                { "name": "name", "type": "string" }
            ]
        }
    ],
    "links": [
        { "rel": [ "self" ], "href": "/companies?page=1" }
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

## Create a company
Create a new company.

```http
POST /companies
```

### Parameters:
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `name` | string | body |  yes | **Unique** company name. |

### Request body example
```json
{
    "name": "ISEL"
}
```

### Response
```http
Status: 201 Created
Location: /companies/{companyId}
```
```json
{
    "class": [ "company" ],
    "properties": {
        "id": 1,
        "name": "ISEL",
        "state": "active",
        "timestamp": "2022-05-12 19:23:56782"
    },
    "links": [
        { "rel": [ "self" ], "href": "/companies/1" }
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

## Get a company
Get a specific company.

```http
GET /companies/{companyId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `companyId` | integer | path | yes | Identifier of the company. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "company" ],
    "properties": {
        "id": 1,
        "name": "ISEL",
        "state": "active",
        "timestamp": "2022-05-12 19:23:56782"
    },
    "entities": [
        {
            "class": [ "building", "collection" ],
            "rel": [ "company-buildings" ],
            "properties": {
                "pageIndex": 1,
                "pageMaxSize": 10,
                "collectionSize": 1
            },
            "entities": [
                {
                    "class": [ "building" ],
                    "rel": [ "item" ],
                    "properties": {
                        "id": 1,
                        "name": "G",
                        "floors": 2,
                        "state": "active",
                        "timestamp": "2022-05-13 17:15:76532"
                    },
                    "links": [
                        { "rel": [ "self" ], "href": "/companies/1/buildings/1" }
                    ]
                }
            ],
            "actions": [
                {
                    "name": "create-building",
                    "title": "Create building",
                    "method": "POST",
                    "href": "/companies/1/buildings",
                    "type": "application/json",
                    "properties": [
                        { "name": "name", "type": "string" },
                        { "name": "floors", "type": "number" }
                    ]
                }
            ],
            "links": [
                { "rel": [ "self" ], "href": "/companies/1/buildings?page=1" }
            ]
        }
    ],
    "actions": [
        {
            "name": "deactivate-company",
            "title": "Deactivate company",
            "method": "POST",
            "href": "/companies/1"
        },
        {
            "name": "update-company",
            "title": "Update company",
            "method": "PUT",
            "href": "/companies/1",
            "type": "application/json",
            "properties": [
                { "name": "name", "type": "string" }
            ]
        }
    ],
    "links": [
        { "rel": [ "self" ], "href": "/companies/1" },
        { "rel": [ "companies" ], "href": "/companies" }
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

## Update a company
Update the name of a specific company.

```http
PUT /companies/{companyId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `companyId` | integer | path | yes | Identifier of the project. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `name` | string | body | yes | New **unique** name for the company. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "company" ],
    "properties": {
        "id": 1,
        "name": "Instituto Superior de Engenharia de Lisboa",
        "state": "active",
        "timestamp": "2022-05-12 19:23:56782"
    },
    "links": [
        { "rel": [ "self" ], "href": "/companies/1" }
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

## Deactivate a company
Deactivate a certain company.

```http
POST /companies/{companyId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:
| `companyId` | integer | path | yes | Identifier of the company. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "company" ],
    "properties": {
        "id": 1,
        "name": "ISEL",
        "state": "inactive",
        "timestamp": "2022-05-12 20:54:32452"
    },
    "links": [
        { "rel": [ "self" ], "href": "/companies/1" },
        { "rel": [ "companies" ], "href": "/companies" }
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

## Activate a company
Activate a certain company.

```http
PUT /companies/{companyId}/activate
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:
| `companyId` | integer | path | yes | Identifier of the company. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "company" ],
    "properties": {
        "id": 1,
        "name": "ISEL",
        "state": "active",
        "timestamp": "2022-05-14 14:23:56788"
    },
    "links": [
        { "rel": [ "self" ], "href": "/companies/1" },
        { "rel": [ "companies" ], "href": "/companies" }
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
| `id` | number | **Unique** and **stable** identifier of the company. Must be greater than 0. |
| `name` | string | **Unique** name of the company. |
| `state` | string | Current state of the company, the possible values are `Active` or `Inactive`. |
| `timestamp` | string | Timestamp of the moment that the company state changed to the current state. |

### Domain specific link relations
| Name | Description |
|:-:|:-:|
| `company-buildings` | Set of buildings that belong to the company. |
| `companies` | Resource with the representation of all the companies registered in the system. |

The **documentation** for the `media-type`, `classes`, `standard link relations` and `generic errors` used in the representations are described [**here**](../README.md).

The **vocabulary** about the `buildings` can be consulted [**here**](Building.md).