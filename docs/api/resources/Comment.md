# Comment
A **Comment** is an opinion or reaction regarding a specific ticket. 

The comment can be made by the manager of the corresponding building where the ticket was created, by the administrator and by the delegated employee. The **Comment API** allows these roles to create, view and manage all the comments made in a ticket. 

All the **vocabulary** used in the comment representations is described [**here**](#comment-representations-vocabulary).

## Comment API contents
* [**List comments**](#list-comments)
* [**Create a comment**](#create-a-comment)
* [**Get a comment**](#get-a-comment)
* [**Update a comment**](#update-a-comment)
* [**Delete a comment**](#delete-a-comment)

## List comments
List all comments of the corresponding ticket. 

```http
GET /tickets/{ticketId}/comments
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `ticketId` | integer | path | yes | Identifier of the ticket. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `page` | integer | query | no| Page number of the results to fetch. **Default:** `1` |
| `direction` | string | query | no | Direction of the date order. **Possible values:** `asc` and `desc`. **Default:** `desc` |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "comment", "collection" ],
    "properties": {
        "pageIndex": 1,
        "pageMaxSize": 10,
        "collectionSize": 1
    },
    "entities": [
        {
            "class": [ "comment" ],
            "rel": [ "item" ],
            "properties": {
                "id": 1,
                "comment": "Ticket comment",
                "timestamp": "2022-04-08 21:52:47.012620"
            },
            "entities": [
                {
                    "class": [ "person" ],
                    "rel": [ "comment-author" ],
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
                    "name": "delete-comment",
                    "title": "Delete comment",
                    "method": "DELETE",
                    "href": "/tickets/1/comments/1"
                },
                {
                    "name": "edit-comment",
                    "title": "Edit comment",
                    "method": "PUT",
                    "href": "/tickets/1/comments/1",
                    "type": "application/json",
                    "properties": [
                        { "name": "comment", "type": "string" }
                    ]
                }            
            ]
        }
    ],
    "actions": [
        {
            "name": "create-comment",
            "title": "Create comment",
            "method": "POST",
            "href": "/tickets/1/comments",
            "type": "application/json",
            "properties": [
                { "name": "comment", "type": "string" }
            ]
        }
    ],
    "links": [
        { "rel": [ "self" ], "href": "/tickets/1/comments?page=1" },
        { "rel": [ "pagination" ], "href": "/tickets/1/comments{?page}", "templated": true }
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

## Create a comment
Create a new comment.

```http
POST /tickets/{ticketId}/comments
```

### Parameters:
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `ticketId` | integer | path | yes | Identifier of the ticket.  Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `comment` | string | body |  yes | Written remark expressing an opinion or reaction about the ticket. |

### Request body example
```json
{
    "comment": "New ticket comment."
}
```

### Response
```http
Status: 201 Created
Location: /tickets/{ticketId}/comments
```

```json
{
    "class": [ "comment" ],
    "properties": {
        "id": 1,
        "comment": "Ticket comment",
        "timestamp": "2022-09-12 09:32:53.123654"
    },
     "links": [
        { "rel": [ "self" ], "href": "/tickets/1/comments" }
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
* `type`: [**archived-ticket**](Ticket.md#domain-specific-errors)
```http
Status: 415 Unsupported Media Type
```

## Get a comment
Get a specific comment. 

```http
GET /tickets/{ticketId}/comments/{commentId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `ticketId` | integer | path | yes | Identifier of the ticket. Must be greater than 0. |
| `commentId` | integer | path | yes | Identifier of the comment. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "comment" ],
    "properties": {
        "id": 1,
        "comment": "Ticket comment",
        "timestamp": "2022-04-08 21:52:47.012620"
    },
    "entities": [
        {
            "class": [ "person" ],
            "rel": [ "comment-author" ],
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
            "name": "delete-comment",
            "title": "Delete comment",
            "method": "DELETE",
            "href": "/tickets/1/comments/1"
        },
        {
            "name": "edit-comment",
            "title": "Edit comment",
            "method": "PUT",
            "href": "/tickets/1/comments/1",
            "type": "application/json",
            "properties": [
                { "name": "comment", "type": "string" }
            ]
        }
    ],
    "links": [
        { "rel": [ "self" ], "href": "/tickets/1/comments/1" }
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

## Update a comment
Update a specific comment.

```http
PUT /tickets/{ticketId}/comments/{commentId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `ticketId` | integer | path | yes | Identifier of the ticket. Must be greater than 0. |
| `commentId` | integer | path | yes | Identifier of the ticket comment. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `comment` | string | body |  yes | New comment. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "comment" ],
    "properties": {
        "id": 1,
        "comment": "Comment edited",
        "timestamp": "2022-04-08 21:55:47.012620"
    },
    "links": [
        { "rel": [ "self" ], "href": "/ticket/1/comments" }
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
* `type`: [**archived-ticket**](Ticket.md#domain-specific-errors)
```http
Status: 415 Unsupported Media Type
```

## Delete a comment
Delete a certain comment.

```http
DELETE /tickets/{ticketId}/comments/{commentId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `ticketId` | integer | path | yes | Identifier of the ticket. Must be greater than 0. |
| `commentId` | integer | path | yes | Identifier of the ticket comment. Must be greater than 0. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "comment" ],
    "properties": {
        "id": 1,
        "comment": "Ticket comment",
        "timestamp": "2022-04-08 21:52:47.012620"
    },
    "links": [
        { "rel": [ "self" ], "href": "/tickets/1/comments/1" },
        { "rel": [ "comments" ], "href": "/tickets/1/comments" }
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
* `type`: [**archived-ticket**](Ticket.md#domain-specific-errors)

## Comment representations vocabulary
| Name | Type | Description |
|:-:|:-:|:-:|
| `id` | number | **Stable** identifier of the comment and **unique inside each ticket**, but **not globally**. |
| `comment` | string | Written remark expressing an opinion or reaction about the ticket. |
| `timestamp` | string | Date and time that the comment was created. |

### Domain specific link relations
| Name | Description |
|:-:|:-:|
| `comments` | Representation of all the comments in a specific ticket. |
| `comment-author` | Representation of the author of the comment. |

The **vocabulary** for each external class represented in this documented can be consulted by clicking in one of the following links:
* [**Person**](Person.md)
* [**Ticket**](Ticket.md)

The **documentation** for the `media-type`, `classes`, `standard link relations` and `generic errors` used in the representations are described [**here**](../README.md).
