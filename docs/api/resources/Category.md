# Category

A **Category** is a entity that describe the group where the device is inserted or a skill that can be owned by an employee.

The **Category API** allows the administrator to create, view and manage all the categories.

All the **vocabulary** used in the categories representations is described [**here**](#building-representations-vocabulary).

## Category API contents
* [**List categories**](#list-categories)
* [**Create a category**](#create-a-category)
* [**Update a category**](#update-a-category)
* [**Deactivate a category**](#deactivate-a-category)
* [**Activate a category**](#activate-a-category)

## List categories
List categories present in the system.

```http
GET /categories
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `page` | integer | query | no| Page number of the results to fetch. **Default:** `0` |

### Response
```http
Status: 200 OK
```

```json
{
    "class": [ "category", "collection" ],
    "properties": {
        "pageIndex": 0,
        "pageSize": 1,
        "collectionSize": 1
    },
    "entities": [ 
        {
            "class": [ "category" ],
            "rel": [ "item" ],
            "properties": {
                "id": 1,
                "name": "Climatization",
                "state": "Active",
                "timestamp": "2022-04-08 21:52:47.012620"
            },
            "actions": [
                {
                    "name": "deactivate-category",
                    "title": "Deactivate category",
                    "method": "DELETE",
                    "href": "/categories/1"
                },
                {
                    "name": "update-category",
                    "title": "Update category",
                    "method": "PUT",
                    "href": "/categories/1",
                    "type": "application/json",
                    "properties": [
                        { "name": "name", "type": "string" }
                    ]
                }
            ]
        }
    ],
    "actions": [
        {
            "name": "create-category",
            "title": "Create a category",
            "method": "POST",
            "href": "/categories",
            "type": "application/json",
            "properties": [
                { "name": "name", "type": "string" }
            ]
        }
    ],
    "links": [
        { "rel": [ "self" ], "href": "/categories?page=0" }
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

## Create a category
Create a new category.

```http
POST /categories
```

### Parameters:
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `name` | string | body |  yes | Name of the category. |

### Request body example
```json
{
    "name": "Climatization"
}
```

### Response
```http
Status: 201 Created
Location: /categories/1
```

```json
{
    "class": [ "category" ],
    "properties": {
        "id": 1,
        "name": "Climatization",
        "state": "Active",
        "timestamp": "2022-04-08 21:52:47.012620"
    },
     "links": [
        { "rel": [ "self" ], "href": "/categories/1" }
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
Status: 415 Unsupported Media Type
```

## Update a category
Update the name of a specific category. This action just can be performed if doesn't exist any device or employee with this category.

```http
PUT /categories/{categoryId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `categoryId` | integer | path | yes | Identifier of the category. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `name` | string | body | yes | New name for the category. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "category" ],
    "properties": {
        "id": 1,
        "name": "New Climatization",
        "state": "Active",
        "timestamp": "2022-05-14 14:23:56788"
    },
     "links": [
        { "rel": [ "self" ], "href": "/categories/1" }
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
* `types`: [**inactive-category**](#domain-specific-errors), [**used-category**](#domain-specific-errors)
```http
Status: 415 Unsupported Media Type
```

## Deactivate a category
Deactivate a determined category.

```http
DELETE /categories/{categoryId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `categoryId` | integer | path | yes | Identifier of the category. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "category" ],
    "properties": {
        "id": 1,
        "name": "Climatization",
        "state": "Inactive",
        "timestamp": "2022-05-14 14:23:56788"
    },
     "links": [
        { "rel": [ "self" ], "href": "/categories/1" },
        { "rel": [ "categories" ], "href": "/categories" }
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

## Activate a category
Activate a determined category.

```http
PUT /categories/{categoryId}/activate
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `categoryId` | integer | path | yes | Identifier of the category. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "category" ],
    "properties": {
        "id": 1,
        "name": "Climatization",
        "state": "Active",
        "timestamp": "2022-05-14 14:23:56788"
    },
     "links": [
        { "rel": [ "self" ], "href": "/categories/1" }
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

## Category representations vocabulary
| Name | Type | Description |
|:-:|:-:|:-:|
| `id` | integer | **Unique** and **stable** identifier of the category. |
| `name` | string | Name of the category. |
| `state` | string | Current state of the category, the possible values are `Active` or `Inactive`. |
| `timestamp` | string | Timestamp of the moment that the category state changed to the current state. |

### Domain specific errors
* `inactive-category`: Happens when it's requested to update an **inactive** category. 
  * It is thrown with the HTTP status code `409 Conflict`.
```json
{
    "type": "/errors/inactive-category",
    "title": "It's not possible to update an inactive category.",
    "instance": "/categories/1"
}
```
* `used-category`: Happens when it's requested to deactivate a category that is in use. 
  * It is thrown with the HTTP status code `409 Conflict`.
```json
{
    "type": "/errors/used-category",
    "title": "It's not possible to deactivate a category that is in use.",
    "instance": "/categories/1"
}
```

The **documentation** for the `media-type`, `classes`, `standard link relations` and `generic errors` used in the representations are described [**here**](../README.md).