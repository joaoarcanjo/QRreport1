# Person
A **Person** is a human being regarded as an individual and each one has at least one role depending on the functions performed. 

The **Person API** allows the API user, depending on his functions, to create, view and manage all the registered persons. 

All the **vocabulary** used in the persons representations is described [**here**](#person-representations-vocabulary).

## Company API contents
* [**List persons**](#list-persons)
* [**Create a person**](#create-a-person)
* [**Get a person**](#get-a-person)
* [**Update a person**](#update-a-person)
* [**Delete a user**](#delete-a-user)
* [**Fire a person**](#fire-a-person)
* [**Rehire a person**](#rehire-a-person)
* [**Ban a person**](#ban-a-person)
* [**Unban a person**](#unban-a-person)
* [**Add role to a person**](#add-role-to-a-person)
* [**Remove role from a person**](#remove-role-from-a-person)
* [**Add skill to a person**](#add-skill-to-a-person)
* [**Remove skill from a person**](#remove-skill-from-a-person)

## List persons
List the registered persons.

```http
GET /persons
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `page` | integer | query | no | Page number of the results to fetch. **Default:** `0` |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "person", "collection" ],
    "properties": {
        "pageIndex": 0,
        "pageSize": 1,
        "collectionSize": 1
    },
    "entities": [
        {
            "class": [ "person" ],
            "rel": [ "item" ],
            "properties": {
                "id": "cf128ed3-0d65-42d9-8c96-8ff2e05b3d08",
                "name": "Bernardo Silva",
                "phone": "969123456",
                "email": "bernardosilva@qrreport.com",
                "state": "Active"
            },
            "links": [
                { "rel": [ "self" ], "href": "/persons/cf128ed3-0d65-42d9-8c96-8ff2e05b3d08" }
            ]
        }
    ],
    "actions": [
        {
            "name": "create-person",
            "title": "Create a person",
            "method": "POST",
            "href": "/persons",
            "type": "application/json",
            "properties": [
                { "name": "name", "type": "string" },
                { "name": "phone", "type": "string", "required": false },
                { "name": "email", "type": "string" },
                { "name": "password", "type": "string" }
            ]
        }
    ],
    "links": [
        { "rel": [ "self" ], "href": "/persons?page=0" }
    ]
}
```
```http
Status: 401 Unauthorized
```
```http
Status: 403 Forbidden
```

## Create a person
Create a new person.

```http
POST /persons
```

### Parameters:
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `name` | string | body |  yes | Name of the person. |
| `phone` | string | body |  no | Phone number. |
| `email` | string | body |  yes | **Unique** email. |
| `password` | string | body |  yes | Set of secret words to allow access to the application. |

### Request body example
```json
{
    "name": "Bernardo Silva",
    "phone": "969123456",
    "email": "bernardosilva@qrreport.com",
    "password": "slb123"
}
```

### Response
```http
Status: 201 Created
Location: /persons/{personId}
```
```json
{
    "class": [ "person" ],
    "properties": {
        "id": "cf128ed3-0d65-42d9-8c96-8ff2e05b3d08",
        "name": "Bernardo Silva",
        "phone": "969123456",
        "email": "bernardosilva@qrreport.com",
        "roles": [
            { "name": "User" }
        ],
        "state": "Active"
    },
    "links": [
        { "rel": [ "self" ], "href": "/persons/cf128ed3-0d65-42d9-8c96-8ff2e05b3d08" }
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

## Get a person
Get a specific person.

```http
GET /persons/{personId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `personId` | uuid | path | yes | Identifier of the person. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "person" ],
    "properties": {
        "id": "cf128ed3-0d65-42d9-8c96-8ff2e05b3d08",
        "name": "Bernardo Silva",
        "phone": "969123456",
        "email": "bernardosilva@qrreport.com",
        "roles": [
            { "name": "User" }
        ],
        "state": "Active",
        "timestamp": "2022-05-11 14:23:56782"
    },
    "entities": [
        {
            "class": [ "ticket", "collection" ],
            "rel": [ "person-tickets" ],
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
                        "subject": "Broken toilet",
                        "description": "The toilet doesn't work properly.",
                        "userState": "Waiting analysis",
                        "timestamp": "2022-05-10 15:12:34564"
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
    ],
    "actions": [
        {
            "name": "ban-person",
            "title": "Ban person",
            "method": "DELETE",
            "href": "/persons/cf128ed3-0d65-42d9-8c96-8ff2e05b3d08"
        },
        {
            "name": "update-person",
            "title": "Update person",
            "method": "PUT",
            "href": "/persons/cf128ed3-0d65-42d9-8c96-8ff2e05b3d08",
            "type": "application/json",
            "properties": [
                { "name": "name", "type": "string" },
                { "name": "phone", "type": "string" },
                { "name": "email", "type": "string" }
            ]
        }
    ],
    "links": [
        { "rel": [ "self" ], "href": "/persons/cf128ed3-0d65-42d9-8c96-8ff2e05b3d08" }
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

## Update a person
Update the name, phone and email of the person.

```http
PUT /persons/{personId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `personId` | uuid | path | yes | Identifier of the person. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `name` | string | body | no | New name for the person. |
| `phone` | string | body | no | New phone for the person. |
| `email` | string | body | no | New **unique** email for the person. |

**Notice:** At least one of the body parameters **should** be inserted.

### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "person" ],
    "properties": {
        "id": "cf128ed3-0d65-42d9-8c96-8ff2e05b3d08",
        "name": "Bernardo Ronaldo",
        "phone": "969123456",
        "email": "bernardoronaldo@qrreport.com",
        "roles": [
            { "name": "User" }
        ],
        "state": "Active"
    },
    "links": [
        { "rel": [ "self" ], "href": "/persons/cf128ed3-0d65-42d9-8c96-8ff2e05b3d08" }
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

## Delete a user
Delete a specific user by changing his profile informations for fake ones.

```http
DELETE /persons/{personId}
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `personId` | uuid | path | yes | Identifier of the person. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |


### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "person" ],
    "properties": {
         "id": "cf128ed3-0d65-42d9-8c96-8ff2e05b3d08",
        "name": "Francisco Gervásio",
        "phone": "654456987",
        "email": "franciscogervasio@qrreport.com",
        "roles": [
            { "name": "User" }
        ],
        "state": "Inactive",
        "reason": "User deleted account."
    },
    "links": [
        { "rel": [ "self" ], "href": "/persons/cf128ed3-0d65-42d9-8c96-8ff2e05b3d08" }
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
* `type`: [**user-deletion**](#domain-specific-errors)

## Fire a person
Fire a person from a company, namely an employee or a manager.

```http
PUT /persons/{personId}/fire
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `personId` | uuid | path | yes | Identifier of the person. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `reason` | string | body | yes | Reason behind the dismissal. |


### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "person" ],
    "properties": {
        "id": "cf128ed3-0d65-42d9-8c96-8ff2e05b3d89",
        "name": "António Ramalho",
        "phone": "969456789",
        "email": "andreramalho@qrreport.com",
        "roles": [
            { "name": "Employee" }
        ],
        "state": "Inactive",
        "reason": "Left the company by his will."
    },
    "links": [
        { "rel": [ "self" ], "href": "/persons/cf128ed3-0d65-42d9-8c96-8ff2e05b3d89" }
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

## Rehire a person
Rehire a person from a company, namely an employee or a manager.

```http
PUT /persons/{personId}/rehire
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `personId` | uuid | path | yes | Identifier of the person. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |


### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "person" ],
    "properties": {
        "id": "cf128ed3-0d65-42d9-8c96-8ff2e05b3d89",
        "name": "António Ramalho",
        "phone": "969456789",
        "email": "andreramalho@qrreport.com",
        "roles": [
            { "name": "Employee" }
        ],
        "state": "Active"
    },
    "links": [
        { "rel": [ "self" ], "href": "/persons/cf128ed3-0d65-42d9-8c96-8ff2e05b3d89" }
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

## Ban a person
Ban a person from the system, namely a guest or a user but an employee or a manager can be banned as well when necessary.

```http
PUT /persons/{personId}/ban
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `personId` | uuid | path | yes | Identifier of the person. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `reason` | string | body | yes | Reason behind the ban. |


### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "person" ],
    "properties": {
        "id": "cf128ed3-0d65-42d9-8c96-8ff2e05b3d08",
        "name": "Bernardo Silva",
        "phone": "969123456",
        "email": "bernardosilva@qrreport.com",
        "roles": [
            { "name": "User" }
        ],
        "state": "Banned",
        "reason": "Keeps opening useless tickets every day."
    },
    "links": [
        { "rel": [ "self" ], "href": "/persons/cf128ed3-0d65-42d9-8c96-8ff2e05b3d08" }
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

## Unban a person
Unban a specific person.

```http
PUT /persons/{personId}/unban
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `personId` | uuid | path | yes | Identifier of the person. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |


### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "person" ],
    "properties": {
        "id": "cf128ed3-0d65-42d9-8c96-8ff2e05b3d08",
        "name": "Bernardo Silva",
        "phone": "969123456",
        "email": "bernardosilva@qrreport.com",
        "roles": [
            { "name": "User" }
        ],
        "state": "Active"
    },
    "links": [
        { "rel": [ "self" ], "href": "/persons/cf128ed3-0d65-42d9-8c96-8ff2e05b3d08" }
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

## Add role to a person
Add a new role to a person.

```http
PUT /persons/{personId}/add-role
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `personId` | uuid | path | yes | Identifier of the person. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `role` | string | body | yes | Role name to add to the person. |


### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "person" ],
    "properties": {
        "id": "cf128ed3-0d65-42d9-8c96-8ff2e05b3d08",
        "name": "Bernardo Silva",
        "phone": "969123456",
        "email": "bernardosilva@qrreport.com",
        "roles": [
            { "name": "User" },
            { "name": "Manager" }
        ],
        "state": "Active"
    },
    "links": [
        { "rel": [ "self" ], "href": "/persons/cf128ed3-0d65-42d9-8c96-8ff2e05b3d08" }
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
* `type`: [**update-inactive-or-banned-person**](#domain-specific-errors)

## Remove role from a person
Remove role from a person.

```http
PUT /persons/{personId}/remove-role
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `personId` | uuid | path | yes | Identifier of the person. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `role` | string | body | yes | Role name to remove from the person. |


### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "person" ],
    "properties": {
        "id": "cf128ed3-0d65-42d9-8c96-8ff2e05b3d08",
        "name": "Bernardo Silva",
        "phone": "969123456",
        "email": "bernardosilva@qrreport.com",
        "roles": [
            { "name": "User" }
        ],
        "state": "Active"
    },
    "links": [
        { "rel": [ "self" ], "href": "/persons/cf128ed3-0d65-42d9-8c96-8ff2e05b3d08" }
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
* `type`: [**update-inactive-or-banned-person**](#domain-specific-errors)

## Add skill to a person
Add a new skill to a person.

```http
PUT /persons/{personId}/add-skill
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `personId` | uuid | path | yes | Identifier of the person. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `skill` | string | body | yes | Skill name to add to the person. |


### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "person" ],
    "properties": {
        "id": "cf128ed3-0d65-42d9-8c96-8ff2e05b3d12",
        "name": "João Ambrósio",
        "phone": "93345987",
        "email": "joaoambrosio@qrreport.com",
        "roles": [
            { "name": "Employee" }
        ],
        "skills": [
            { "name": "Electricity" },
            { "name": "Software" }
        ],
        "state": "Active"
    },
    "links": [
        { "rel": [ "self" ], "href": "/persons/cf128ed3-0d65-42d9-8c96-8ff2e05b3d12" }
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
* `type`: [**update-inactive-or-banned-person**](#domain-specific-errors)

## Remove skill from a person
Remove a new skill from a person.

```http
PUT /persons/{personId}/add-skill
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `personId` | uuid | path | yes | Identifier of the person. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |


### Response
```http
Status: 200 OK 
```

```json
{
    "class": [ "person" ],
    "properties": {
        "id": "cf128ed3-0d65-42d9-8c96-8ff2e05b3d12",
        "name": "João Ambrósio",
        "phone": "933459874",
        "email": "joaoambrosio@qrreport.com",
        "roles": [
            { "name": "Employee" }
        ],
        "skills": [
            { "name": "Electricity" }
        ],
        "state": "Active"
    },
    "links": [
        { "rel": [ "self" ], "href": "/persons/cf128ed3-0d65-42d9-8c96-8ff2e05b3d12" }
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
* `type`: [**update-inactive-or-banned-person**](#domain-specific-errors)

## Person representations vocabulary
| Name | Type | Description |
|:-:|:-:|:-:|
| `id` | uuid | **Unique** and **stable** identifier of the person. |
| `name` | string | Name of the person. |
| `phone` | string | Phone number of the person. **Not required.** |
| `email` | string | **Unique** email of the person. |
| `state` | string | Current state of the person, the possible values are `active`, `inactive` or `banned`. |
| `reason` | string | Brief description of the reason the person was banned or put on inactive. |
| `timestamp` | string | Timestamp of the moment that the person state changed to the current state. |
| `roles` | array of objects | Roles that the person has. |
| `skills` | array of objects | Skills that the person has. Only for persons with the `employee` role. |

### Domain specific link relations
| Name | Description |
|:-:|:-:|
| `person-tickets` | Set of tickets that belong to the person. |

### Domain specific errors
* `user-deletion`: Happens when it's requested to delete a person that doesn't have the user role. 
  * It's thrown with the HTTP status code `409 Conflict`.

```json
{
    "type": "/errors/user-deletion",
    "title": "It's not possible to delete a person who is not a user.",
    "instance": "/person/cf128ed3-0d65-42d9-8c96-8ff2e05b3d12"
}
```

* `update-inactive-or-banned-person`: Happens when it's requested to update an **inactive or banned** person, such as add a skill, remove a role or update his profile. 
  * It's thrown with the HTTP status code `409 Conflict`.

```json
{
    "type": "/errors/update-inactive-or-banned-person",
    "title": "It's not possible to update an inactive or banned person.",
    "instance": "/person/cf128ed3-0d65-42d9-8c96-8ff2e05b3d12"
}
```

The **documentation** for the `media-type`, `classes`, `standard link relations` and `generic errors` used in the representations are described [**here**](../README.md).

The **vocabulary** about the `tickets` can be consulted [**here**](Ticket.md).