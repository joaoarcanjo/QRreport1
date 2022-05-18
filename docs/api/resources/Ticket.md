# Ticket
A **Ticket** is where anyone who founds an anomaly can report the problem found.

The ticket can be accessed by the person who submitted it, by the managers of the corresponding building, by the administrator and by the delegated employee for the ticket. The **Ticket API** allows all these accesses and others related to ticket management, such as update or delete a ticket.

All the **vocabulary** used in the representations below is described [**here**](#ticket-representations-vocabulary).

## Ticket API contents
* [**List tickets**](#list-tickets)
* [**Create a ticket**](#create-a-ticket)
* [**Get a ticket**](#get-a-ticket)
* [**Change a ticket state**](#change-a-ticket-state)
* [**Update a ticket**](#update-a-ticket)
* [**Delete a ticket**](#delete-a-ticket)
* [**Add ticket rate**](#add-ticket-rate)
* [**Set employee to fix a ticket**](#set-employee-to-fix-a-ticket)
* [**Remove employee from a ticket**](#remove-employee-from-a-ticket)

## List tickets
List all tickets.

```http
GET /tickets
``` 

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `page` | integer | query | no | Page number of the results to fetch. **Default:** `0` |
| `sort_by` | string | query | no | Sorts the tickets by one of the possible values. **Possible values:** `name` and `date`. **Default:** `date` |
| `direction` | string | query | no | Direction of the sorting. **Possible values:** `asc` and `desc`. **Default:** `desc` |
| `company` | string | query | no | Company name to filter the tickets. |
| `building` | string | query | no | Building name to filter the tickets. |
| `room` | string | query | no | Room name to filter the tickets. |
| `category` | string | query | no | Category name to filter the tickets. |
| `search` | string | query | no | Keyword to search for a specific or a set of tickets subjects. |
| `personId` | uuid | query | no | Identifier of the person to fetch his tickets. |

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
                "description": "Faucet does not work, water doesn't come out.",
                "employeeState": "Unassigned",
                "userState": "Waiting analysis"
            },
            "links": [
                { "rel": [ "self" ], "href": "/tickets/1" }
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

## Create a ticket 
Create a new ticket.

```http
POST /tickets
```
### Parameters:
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `subject` | string | body | yes | Subject of the problem found. |
| `description` | string | body | yes | Brief description of the problem found. |
| `hash` | string | body | yes | Hash associated to the company, building, room and device of the problem found. |

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
        "description": "Faucet does not work, water doesn't come out.",
        "userState": "Waiting analysis"
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

## Get a ticket
Get a specific ticket.

```http
GET /tickets/{ticketId}
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
        "description": "Faucet does not work, water doesn't come out.",
        "creationTimestamp": "2022-04-08 21:52:47012",
        "employeeState": "Unassigned",
        "userState": "Waiting analysis",
        "possibleTransitions": [
            { "id": 1, "name": "Fixing" },
            { "id": 2, "name": "Paused" },
            { "id": 3, "name": "Waiting for material" },
            { "id": 4, "name": "Concluded" }
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
                        "comment": "I think it's just one missing screw.",
                        "timestamp": "2022-04-09 12:52:47012"
                    },
                    "entities": [
                        {
                            "class": [ "person" ],
                            "rel": [ "comment-author" ],
                            "properties": {
                                "id": "cf128ed3-0d65-42d9-8c96-8ff2e05b3d12",
                                "name": "João Ambrósio",
                                "phone": "93345987",
                                "email": "joaoambrosio@qrreport.com"
                            },
                            "links": [
                                { "rel": [ "self" ], "href": "/persons/cf128ed3-0d65-42d9-8c96-8ff2e05b3d12" }
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
                },
                {
                    "class": [ "company" ],
                    "rel": [ "ticket-company" ],
                    "properties": {
                        "id": 1,
                        "name": "ISEL",
                        "state": "Active"
                    },
                    "links": [
                        { "rel": [ "self" ], "href": "/companies/1" }
                    ]
                },
                {
                    "class": [ "building" ],
                    "rel": [ "ticket-building" ],
                    "properties": {
                        "id": 1,
                        "name": "A",
                        "state": "Active"
                    },
                    "links": [
                        { "rel": [ "self" ], "href": "/buildings/1" }
                    ]
                },
                {
                    "class": [ "room" ],
                    "rel": [ "ticket-room" ],
                    "properties": {
                        "id": 1,
                        "name": "Restroom 1",
                        "state": "Active"
                    },
                    "links": [
                        { "rel": [ "self" ], "href": "/rooms/1" }
                    ]
                },
                {
                    "class": [ "device" ],
                    "rel": [ "ticket-device" ],
                    "properties": {
                        "id": 1,
                        "name": "Faucet",
                        "state": "Active"
                    },
                    "links": [
                        { "rel": [ "self" ], "href": "/devices/1" }
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
            "rel": [ "ticket-author" ],
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
                { "name": "description", "type": "string" }
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

## Update a ticket
Update the subject or the description of a specific ticket.

```http
PUT /tickets/{ticketId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `ticketId` | integer | path | yes | Identifier of the ticket. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `subject` | string | body | no | New subject for the ticket. |
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
        "subject": "Broken faucet updated",
        "description": "Faucet does not work, water doesn't come out updated.",
        "userState": "Waiting analysis "
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
```http
Status: 403 Forbidden
```
```http
Status: 404 Not Found
```
```http
Status: 409 Conflict
```
* `types`: [**archived-ticket**](#domain-specific-errors), [**update-fixing-ticket**](#domain-specific-errors)

## Change a ticket state
Change the ticket state to one of the possible next states.

```http
PUT /tickets/{ticketId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `ticketId` | integer | path | yes | Identifier of the ticket. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `state` | integer | body | yes | New state for the ticket. |

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
        "description": "Faucet does not work, water doesn't come out.",
        "employeeState": "Fixing"
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
```http
Status: 403 Forbidden
```
```http
Status: 404 Not Found
```
```http
Status: 409 Conflict
```
* `type`: [**archived-ticket**](#domain-specific-errors)

## Delete a ticket
Delete a specific ticket.

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
        "description": "Faucet does not work, water doesn't come out.",
        "employeeState": "Refused",
        "userState": "Refused"
    },
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

## Add ticket rate
Assign a rate to a concluded ticket.

```http
PUT /tickets/{ticketId}/rate
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `ticketId` | integer | path | yes | Identifier of the ticket. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `rate` | integer | body | yes | Rate for the ticket. **Possible values:** `1-5` |

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
        "description": "Faucet does not work, water doesn't come out.",
        "employeeState": "Concluded",
        "userState": "Completed",
        "rate": 4
    },
    "links": [
        { "rel": [ "ticket" ], "href": "/tickets/1" }
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

## Set employee to fix a ticket
Set employee responsible for fixing the problem associated to the ticket.

```http
POST /tickets/{ticketId}/employee
```

### Parameters:
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `employeeId` | number | body | yes | Identifier of the employee to fix the ticket problem. |

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
        "description": "Faucet does not work, water doesn't come out.",
        "employeeState": "Not started"
    },
    "entities": [
        {
            "class": [ "person" ],
            "rel": [ "ticket-employee" ],
            "properties": {
                "id": "cf128ed3-0d65-42d9-8c96-8ff2e05b3d12",
                "name": "João Ambrósio",
                "email": "joaoambrosio@qrreport.com"
            },
            "links": [
                { "rel": [ "self" ], "href": "/persons/cf128ed3-0d65-42d9-8c96-8ff2e05b3d12" }
            ]
        }
    ],
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
```http
Status: 403 Forbidden
```
```http
Status: 404 Not Found
```

## Remove employee from a ticket
Set employee responsible for fixing the problem associated to the ticket.

```http
POST /tickets/{ticketId}/employee
```

### Parameters:
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `employeeId` | number | body | yes | Identifier of the employee to fix the ticket problem. |

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
        "description": "Faucet does not work, water doesn't come out.",
        "employeeState": "Not started"
    },
    "entities": [
        {
            "class": [ "person" ],
            "rel": [ "ticket-employee" ],
            "properties": {
                "id": "cf128ed3-0d65-42d9-8c96-8ff2e05b3d12",
                "name": "João Ambrósio",
                "email": "joaoambrosio@qrreport.com"
            },
            "links": [
                { "rel": [ "self" ], "href": "/persons/cf128ed3-0d65-42d9-8c96-8ff2e05b3d12" }
            ]
        }
    ],
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
```http
Status: 403 Forbidden
```
```http
Status: 404 Not Found
```
```http
Status: 409 Conflict
```
* `type`: [**archived-ticket**](#domain-specific-errors)

## Ticket representations vocabulary
| Name | Type | Description |
|:-:|:-:|:-:|
| `id` | number |  **Stable** and **unique** identifier of the ticket. |
| `subject` | string | Subject of the problem found. |
| `description` | string | Brief description of the problem found. |
| `creationTimestamp` | string | Timestamp associated to the creation of a ticket. |
| `closeTimestamp` | string | Timestamp associated to the closing of a ticket. |
| `employeeState` | string | Detailed state of the ticket, not visible to users. |
| `userState` | string | User state correspondent to the employee state. |
| `possibleTransitions` | array of objects | Possible states transitions based in the current employee state of the ticket. The objects are composed by the identifier and the name of the state that is possible to transition into. |

### Domain specific link relations
| Name | Description |
|:-:|:-:|
| `comment-author` | Person that created a specific ticket comment. |
| `ticket-author` | Person that created the ticket. |
| `ticket-comments` | Set of comments that belong to the ticket. |
| `ticket-company` | Company on which the ticket was created. |
| `ticket-building` | Building on which the ticket was created. |
| `ticket-room` | Room which on the ticket was created. |
| `ticket-device` | Malfunctioning device on which the ticket was created. |
| `tickets` | Resource with the representation of all the tickets. |

### Domain specific errors
* `archived-ticket`: Happens when it's requested to update an archived ticket. 
  * It's thrown with the HTTP status code `409 Conflict`.

```json
{
    "type": "/errors/archived-ticket",
    "title": "It's not possible to update an archived ticket.",
    "instance": "/tickets/1"
}
```
* `update-fixing-ticket`: Happens when a person with a user role requests an update to a ticket that is already under analysis or is been fixed.
  * It's thrown with the HTTP status code `409 Conflict`.

```json
{
    "type": "/errors/update-fixing-ticket",
    "title": "It's not possible to update a ticket that is already being fixed.",
    "instance": "/tickets/1"
}
```

The **vocabulary** for each external class represented in this documented can be consulted by clicking in one of the following links: 
* [**Company**](Company.md) 
* [**Building**](Building.md) 
* [**Room**](Room.md) 
* [**Device**](Device.md)
* [**Person**](Person.md)
* [**Comment**](Comment.md)

The **documentation** for the `media-type`, `classes`, `standard link relations` and `generic errors` used in the representations are described [**here**](../README.md).