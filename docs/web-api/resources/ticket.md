# Ticket

A **ticket** is a detailed report performed by a system user where is described what problem was found. The content of a ticket can be accessed by the user who submitted  it, the managers of the building where the problem was found, by the system administrator and by the employee who was chosen to solve the problem. The **ticket API** allows you to view and manage all the tickets you can access. All the **vocabulary** used in the representations is described [**here**](#ticket-representations-vocabulary).

## Ticket API contents
* [**List tickets**](#list-tickets)
* [**Get a ticket**](#get-a-ticket)
* [**Create a ticket**](#create-a-ticket)
* [**Update a ticket**](#update-a-ticket)
* [**Delete a ticket**](#delete-a-ticket)
* [**Add ticket rate**](#add-ticket-rate)

## List tickets
List all tickets that can be accessed by the authenticated user.

```http
GET /tickets
``` 

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `page` | integer | query | no | Page number of the results to fetch. **Default:** `0` |
| `sort_by` | string | query | no | Way to sort the results to fetch. **Name** and **Date** are possible values.
| `room` | string | query | no | Filter the tickets by room.

### Response
```http
Status: 200 OK
```

```json
{
    "class": ["ticket", "collection"],
    "properties": {
        "pageIndex": 0,
        "pageSize": 1,
        "collectionSize": 1
    },
    "entities": [
        {
            "class": [ "ticket" ],
            "rel": [ "item" ],
            "properties": {
                "id": 1,
                "subject": "Broken faucet",
                "description": "Faucet does not work, seems to have not access to water",
                "employeState": "Processing",
                "userState": "Processing"
            },
            "entities": [
                {
                    "class": [ "person" ],
                    "rel": [ "author" ],
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
            "links": [
                { "rel": ["self"], "href": "tickets/1" }
            ]
        }
    ],
    "links": [
        { "rel": [ "self" ], "href": "/tickets?page=0" }
    ]
}
```
```http
Status: 400 Bad Request
```
```http
Status: 401 Unauthorized
```

## Get a ticket
Get a certain ticket.

```http
GET /tickets/{ticketId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `ticketId` | integer | path | yes | Identifier of the ticket. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `page` | integer | query | no | Page number of the results to fetch. **Default:** `0` |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "ticket" ],
    "properties": {
        "id": 1,
        "subject": "Broken faucet",
        "description": "Faucet does not work, seems to have not access to water",
        "creationTimestamp": "2022-04-08 21:52:47012",
        "employeeState": "Processing",
        "userState": "Processing",
        "room": 1,
        "device": 1,
        "employeeNextStates": [
            {"id": 1, "name": "Paused"},
            {"id": 2, "name": "Waiting for material"},
            {"id": 3, "name": "Concluded"}
        ]
    },
    "entities": [
        {
            "class": [ "comment", "collection" ],
            "rel": [ "ticket-comments" ],
            "properties": {
                "pageIndex": 0,
                "pageSize": 1,
                "collectionSize": 1
            },
            "entities": [
                {
                    "class": [ "comment" ],
                    "rel": [ "item" ],
                    "properties": {
                        "id": 1,
                        "comment": "Will be expensive"
                    },
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
                    "entities": [
                        {
                            "class": [ "person" ],
                            "rel": [ "author" ],
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
                    "links": [
                        { "rel": [ "self" ], "href": "/tickets/1/comments/1" }
                    ]
                }
            ],
            "actions": [
                {
                    "name": "create-comment",
                    "title": "Create a comment",
                    "method": "POST",
                    "href": "/tickets/1/comments",
                    "type": "application/json",
                    "properties": [
                        { "name": "comment", "type": "string" }
                    ]
                }
            ],
            "links": [
                { "rel": [ "self" ], "href": "/tickets/1/comments?page=0" }
            ]
        },
        {
            "class": [ "person" ],
            "rel": [ "author" ],
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
            "name": "delete-ticket",
            "title": "Delete ticket",
            "method": "DELETE",
            "href": "/tickets/1"
        },
        {
            "name": "edit-ticket",
            "title": "Edit ticket",
            "method": "PUT",
            "href": "/tickets/1",
            "type": "application/json",
            "properties": [
                { "name": "subject", "type": "string" },
                { "name": "description", "type": "string" },
                { "name": "employee_state", "type": "integer" }
            ]
        }
    ],
    "links": [
        { "rel": [ "self" ], "href": "/tickets/1" },
        { "rel": [ "tickets" ], "href": "/tickets" }
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

## Create a ticket 
Create a new ticket.

```http
POST /{roomDeviceHash}/new-report
```
### Parameters:
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `roomDeviceHash` | string | path | yes | Identifier of the device. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `subject` | string | body | yes | Name for the ticket. |
| `description` | string | body | yes | Description for the ticket. |

### Response
```http
Status: 201 Created
Location: /tickets/{ticketId}
```
```json
{
    "class": [ "ticket" ],
    "properties": {
        "id": 1,
        "subject": "Broken faucet",
        "description": "Faucet does not work, seems to have not access to water",
        "creationTimestamp": "2022-04-08 21:52:47012",
        "employeeState": "Processing",
        "userState": "Processing",
        "room": 1,
        "device": 1
    },
    "links": [
        { "rel": [ "self" ], "href": "/tickets/1" }
    ]
}
```
```http
Status: 400 Bad Request
```
```http
Status: 401 Unauthorized
```

## Update a ticket
Update the state, subject or the description of a certain ticket.

```http
PUT /tickets/{ticketId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `ticketId` | integer | path | yes | Identifier of the ticket. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `state` | int | body | no | New state for the ticket. |
| `subject` | string | body | no | New subject ticket. |
| `description` | string | body | no | New description for the ticket. |

 **Notice:** At least one of the body parameters **should** be inserted.

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "ticket" ],
    "properties": {
        "id": 1,
        "subject": "Broken faucet subject updated",
        "description": "Broken faucet description updated",
        "employeeState": "Broken faucet employee_state updated"
    },
    "links": [
        { "rel": [ "self" ], "href": "/room/1/tickets/1" }
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

## Delete a ticket
Delete a certain ticket.

```http
DELETE /tickets/{ticketId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `ticketId` | integer | path | yes | Identifier of the ticket. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "ticket" ],
    "properties": {
        "id": 1,
        "subject": "Broken faucet",
        "description": "Faucet does not work, seems to have not access to water"
    },
    "links": [
        { "rel": [ "self" ], "href": "/room/1/tickets/1" },
        { "rel": [ "tickets" ], "href": "/room/1/tickets" }
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

## Add ticket rate
Atribute a new avaliation to a ticket, the ticket must be concluded.

```http
PUT /tickets/{ticketId}/new-rate
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `ticketId` | integer | path | yes | Identifier of the ticket. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `rate` | int | body | yes | New ticket rate. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "rate" ],
    "properties": {
        "ticket": 1,
        "person": "cf128ed3-0d65-42d9-8c96-8ff2e05b3d08",
        "rate": 4
    },
    "links": [
        { "rel": [ "ticket" ], "href": "/room/1/tickets/1" }
    ]
}
```

## Ticket representations vocabulary
| Name | Type | Description |
|:-:|:-:|:-:|
| `id` | integer | Identifier of the ticket. **Stable** and **unique**. |
| `subject` | string | Subject of the ticket. |
| `description` | string | Description of the ticket. |
| `creationTimestamp` | string | Timestamp associated to the creation of an ticket. |
| `closeTimestamp` | string | Timestamp associated to the closing of an ticket. |
| `room` | integer | Identifier of the room where the ticket was submitted. |
| `reporter` | uuid | Identifier of the person who submit the ticket. |
| `employeeState` | int | Current employee state. |
| `device` | int | Device reported. |

### Domain specific link relations
| Name | Description |
|:-:|:-:|
| `ticket-comments` | Ticket in which the comments belong. |
| `tickets` | Representation of all ticket. |

The **documentation** for the `media-type`, `classes`, `standard link relations` and `generic errors` used in the representations are described [**here**](../README.md).