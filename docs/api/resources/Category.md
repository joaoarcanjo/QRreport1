# Category
A **Category** is an entity that describe the group where the device is inserted or a skill that can be owned by an employee.

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
| `page` | integer | query | no| Page number of the results to fetch. **Default:** `1` |

### Response
```http
Status: 200 OK
```

```json
{
    "class": [ "category", "collection" ],
    "properties": {
        "pageIndex": 1,
        "pageMaxSize": 10,
        "collectionSize": 1
    },
    "entities": [ 
        {
            "class": [ "category" ],
            "rel": [ "item" ],
            "properties": {
                "id": 1,
                "name": "garden",
                "state": "active",
                "timestamp": "2022-04-08 21:52:47.012620"
            },
            "actions": [
                {
                    "name": "deactivate-category",
                    "title": "Deactivate category",
                    "method": "POST",
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
            "title": "Create category",
            "method": "POST",
            "href": "/categories",
            "type": "application/json",
            "properties": [
                { "name": "name", "type": "string" }
            ]
        }
    ],
    "links": [
        { "rel": [ "self" ], "href": "/categories?page=1" },
        { "rel": [ "pagination" ], "href": "/categories{?page}", "templated": true }
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
    "name": "garden"
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
        "name": "garden",
        "state": "active",
        "timestamp": "2022-04-08 21:52:47.012620"
    },
     "links": [
        { "rel": [ "self" ], "href": "/categories" }
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
Update the name of a specific category.

```http
PUT /categories/{categoryId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `categoryId` | integer | path | yes | Identifier of the category. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `name` | string | body | yes | New **unique** name for the category. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "category" ],
    "properties": {
        "id": 1,
        "name": "window",
        "state": "active",
        "timestamp": "2022-05-14 14:23:56788"
    },
     "links": [
        { "rel": [ "self" ], "href": "/categories" }
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
* `type`: **inactive-resource**
```http
Status: 415 Unsupported Media Type
```

## Deactivate a category
Deactivate a determined category. This action can only be performed if doesn't exist any device or employee with this category.

```http
POST /categories/{categoryId}/deactivate
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
        "name": "garden",
        "state": "inactive",
        "timestamp": "2022-05-14 14:23:56788"
    },
     "links": [
        { "rel": [ "self" ], "href": "/categories" }
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
* `types`: **inactive-resource**, [**category-being-used**](#domain-specific-errors)

## Activate a category
Activate a determined category.

```http
POST /categories/{categoryId}/activate
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
        "name": "garden",
        "state": "active",
        "timestamp": "2022-05-14 14:23:56788"
    },
     "links": [
        { "rel": [ "self" ], "href": "/categories" }
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
* `type`: **inactive-resource**

## Category representations vocabulary
| Name | Type | Description |
|:-:|:-:|:-:|
| `id` | number | **Unique** and **stable** identifier of the category. |
| `name` | string | **Unique** name of the category. |
| `state` | string | Current state of the category, the possible values are `active` or `inactive`. |
| `timestamp` | string | Timestamp of the moment that the category state changed to the current state. |

### Domain specific errors
* `category-being-used`: Happens when it's requested to deactivate a category that is in use. 
  * It is thrown with the HTTP status code `409 Conflict`.
```json
{
    "type": "/errors/category-being-used",
    "title": "To deactivate a category, it must not be linked to a device or an employee.",
    "instance": "/categories/1/deactivate"
}
```

The **documentation** for the `media-type`, `classes`, `standard link relations` and `generic errors` used in the representations are described [**here**](../README.md).