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
* [**Add skill to an employee**](#add-skill-to-an-employee)
* [**Remove skill from an employee**](#remove-skill-from-an-employee)
* [**Assign company to a person**](#assign-company-to-a-person)

## List persons
List the registered persons.

```http
GET /persons
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
    "class": [ "person", "collection" ],
    "properties": {
        "pageIndex": 1,
        "pageMaxSize": 10,
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
                "state": "active",
                "roles": [ "employee" ],
                "skills": [ "water" ]
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
                { "name": "phone", "type": "string"},
                { "name": "email", "type": "string" },
                { "name": "password", "type": "string" },
                { "name": "role", "type": "number", "possibleValues": { "href": "/roles" }},
                { "name": "company", "type": "number", "possibleValues": { "href": "/companies" }},
                { "name": "skill", "type": "number", "required": false, "possibleValues": { "href": "/categories" } }
            ]
        }
    ],
    "links": [
        { "rel": [ "self" ], "href": "/persons?page=1" },
        { "rel": [ "pagination" ], "href": "/persons{?page}", "templated": true }
    ]
}
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
| `password` | string | body |  yes | Set of secret words/characters to allow access to the application. |
| `role` | string | body |  yes | Role identifier for the person that is being created. |
| `company` | number | body | no | Company identifier for the person that is being created. **Required** in case the role is an `employee` or a `manager`. |
| `skill` | number | body | no | Skill identifier for the person that is being created. **Required** in case the role is an `employee`. |

### Request body example
```json
{
    "name": "Bernardo Silva",
    "phone": "969123456",
    "email": "bernardosilva@qrreport.com",
    "password": "slb123",
    "role": "employee",
    "company": 1,
    "skill": 1
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
        "roles": [ "employee" ],
        "skills": [ "water" ],
        "companies": [ "ISEL" ],
        "state": "active"
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
        "roles": [ "user" ],
        "state": "active",
        "timestamp": "2022-05-11 14:23:56782"
    },
    "entities": [
        {
            "class": [ "ticket", "collection" ],
            "rel": [ "person-tickets" ],
            "properties": {
                "pageIndex": 1,
                "pageMaxSize": 10,
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
                        "userState": "Waiting analysis"
                    },
                    "links": [
                        { "rel": [ "self" ], "href": "/tickets/1" }
                    ]
                }
            ],
            "links": [
                { "rel": [ "self" ], "href": "/tickets" },
                { "rel": [ "pagination" ], "href": "/tickets{?page}" }
            ]
        }
    ],
    "actions": [
        {
            "name": "delete-user",
            "title": "Delete user",
            "method": "DELETE",
            "href": "/persons/cf128ed3-0d65-42d9-8c96-8ff2e05b3d08"
        },
        {
            "name": "ban-person",
            "title": "Ban person",
            "method": "POST",
            "href": "/persons/cf128ed3-0d65-42d9-8c96-8ff2e05b3d08",
            "type": "application/json",
            "properties": [ { "name": "reason", "type": "string" } ]
        },
        {
            "name": "update-person",
            "title": "Update person",
            "method": "PUT",
            "href": "/persons/cf128ed3-0d65-42d9-8c96-8ff2e05b3d08",
            "type": "application/json",
            "properties": [
                { "name": "name", "type": "string", "required": false },
                { "name": "phone", "type": "string", "required": false },
                { "name": "email", "type": "string",  "required": false  },
                { "name": "password", "type": "string",  "required": false }
            ]
        },
        {
            "name": "add-role",
            "title": "Add role",
            "method": "PUT",
            "href": "/persons/cf128ed3-0d65-42d9-8c96-8ff2e05b3d08",
            "type": "application/json",
            "properties": [
                { "name": "role", "type": "string"  },
                { "name": "company", "type": "number",  "required": false, "possibleValues": { "href": "/companies" } },
                { "name": "skill", "type": "number", "required": false, "possibleValues": { "href": "/categories" } }
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
Update the name, phone, email or password of a person.

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
| `password` | string | body | no | New password for the person. |

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
        "roles": [ "user" ],
        "state": "active"
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
* `type`: **unique-constraint**, **change-inactive-or-banned-person**

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
        "roles": [ "user" ],
        "timestamp": "2022-05-21 14:23:56782",
        "state": "inactive",
        "reason": "User deleted account"
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
* `type`: [**user-deletion**](#domain-specific-errors), **change-inactive-or-banned-person**

## Fire a person
Fire a person from a company, namely an employee or a manager.

```http
POST /companies/{companyId}/persons/{personId}/fire
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `companyId` | number | path | yes | Identifier of the company to fire the person. |
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
        "roles": [ "employee" ],
        "skills": [ "water" ],
        "companies": [ "ISEL" ],
        "timestamp": "2022-05-22 11:45:54871",
        "state": "inactive",
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
```http
Status: 409 Conflict
```
* `type`: **person-dismissal**, **change-inactive-or-banned-person**, **different-company**

## Rehire a person
Rehire a person from a company, namely an employee or a manager.

```http
POST /companies/{companyId}/persons/{personId}/rehire
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `companyId` | number | path | yes | Identifier of the company to rehire the person. |
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
        "roles": [ "employee" ],
        "skills": [ "water" ],
        "companies": [ "ISEL" ],
        "timestamp": "2022-05-21 14:23:56782",
        "state": "active"
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
```http
Status: 409 Conflict
```
* `type`: **person-dismissal**, **different-company**

## Ban a person
Ban a person from the system, namely a guest or a user but an employee or a manager can be banned as well when necessary.

```http
POST /persons/{personId}/ban
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
        "roles": [ "user" ],
        "timestamp": "2022-05-21 14:23:56782",
        "state": "banned",
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
```http
Status: 409 Conflict
```
* `type`: **change-inactive-or-banned-person**

## Unban a person
Unban a specific person.

```http
POST /persons/{personId}/unban
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
        "roles": [ "user" ],
        "timestamp": "2022-05-23 15:59:56782",
        "state": "active"
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
* `type`: **change-inactive-or-banned-person**

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
| `company` | number | body | no | Company id to add to the person in case the role added is `employee` or `manager`. |
| `skill` | number | body | no | Skill id to add to the person in case the role added is `employee`. |


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
        "roles": [ "user" , "manager" ],
        "companies": [ "ISEL" ],
        "timestamp": "2022-05-21 09:59:56782",
        "state": "active"
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
* `type`: **inactive-resource**

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
         "roles": [ "user" ],
        "timestamp": "2022-05-21 11:59:56782",
        "state": "active"
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
* `type`: **minimum-of-roles**

## Add skill to an employee
Add a new skill to an employee.

```http
PUT /persons/{personId}/add-skill
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `personId` | uuid | path | yes | Identifier of the person (employee). |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `skill` | number | body | yes | Skill id to add to the employee. |


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
        "roles": [ "employee" ],
        "skills": [  "water", "electricity" ],
        "state": "active"
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
* `type`: **inactive-resource**

## Remove skill from an employee
Remove a skill from an employee in case he has at least two skills.

```http
PUT /persons/{personId}/remove-skill
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `personId` | uuid | path | yes | Identifier of the person. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `skill` | number | body | yes | Skill id to remove from the employee. |


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
        "roles": [ "employee" ],
        "skills": [ "water" ],
        "state": "active"
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
* `type`: **minimum-of-skills**

## Assign company to a person
Assign a company to a person with the role of employee or manager.

```http
POST /persons/{personId}/assign-company
```

### Parameters
| Name | Type | In | Required | Description |
|:-:|:-:|:-:|:-:|:-:|
| `personId` | uuid | path | yes | Identifier of the person. |
| `accept` | string | header | no | Setting to `application/vnd.qrreport+json` is recommended. |
| `content-type` | string | header | yes | Set to `application/json`. |
| `company` | number | body | yes | Company id to assign the person to. |

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
        "email": "joaoambrosio@qrreport.com",
        "roles": [ "employee" ],
        "skills": [ "water" ],
        "companies": [ "ISEL", "IST" ],
        "timestamp": "2022-05-30 18:14:78165",
        "state": "active"
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
* `type`: **company-persons**


## Person representations vocabulary
| Name | Type | Description |
|:-:|:-:|:-:|
| `id` | uuid | **Unique** and **stable** identifier of the person. |
| `name` | string | Name of the person. |
| `phone` | string | Phone number of the person. **Not required**, unless it's an employee. |
| `email` | string | **Unique** email of the person. |
| `state` | string | Current state of the person, the possible values are `active`, `inactive` or `banned`. |
| `reason` | string | Brief description of the reason the person was banned or put on inactive. |
| `timestamp` | string | Timestamp of the moment that the person state changed to the current state. |
| `roles` | array of strings | Roles that the person has. |
| `skills` | array of strings | Skills that the person has. Only for persons with the `employee` role. |
| `companies` | array of strings | Companies that the person belong. |

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

* `change-inactive-or-banned-person`: Happens when it's requested to update an **inactive or banned** person, such as add a skill, remove a role or update his profile. 
  * It's thrown with the HTTP status code `409 Conflict`.

```json
{
    "type": "/errors/change-inactive-or-banned-person",
    "title": "It's not possible to update an inactive or banned person.",
    "instance": "/person/cf128ed3-0d65-42d9-8c96-8ff2e05b3d12"
}
```

The **documentation** for the `media-type`, `classes`, `standard link relations` and `generic errors` used in the representations are described [**here**](../README.md).

The **vocabulary** about the `tickets` can be consulted [**here**](Ticket.md).