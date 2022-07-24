# Errors documentation

This document contains all the business logic errors that the API may send to the client as a response when an error occurs.

## Errors
* [**archived-ticket**](#archived-ticket)
* [**category-being-used**](#category-being-used)
* [**company-person-roles**](#company-person-roles)
* [**database-write-error**](#database-write-error)
* [**fixing-ticket**](#fixing-ticket)
* [**inactive-or-banned-person**](#inactive-or-banned-person)
* [**inactive-resource**](#inactive-resource)
* [**invalid-company**](#invalid-company)
* [**invalid-role**](#invalid-role)
* [**minimum-roles-skills**](#minimum-roles-skills)
* [**person-ban**](#person-ban)
* [**person-dismissal**](#person-dismissal)
* [**ticket-employee-skill-mismatch**](#ticket-employee-skill-mismatch)
* [**ticket-rate**](#ticket-rate)
* [**unique-constraint**](#unique-constraint)

### Unique Constraint
Thrown when a property value must be unique and the value inserted it is not.

* **Type:** `/errors/unique-constraint`
* **SQL Type:** `unique-constraint`

```http
Status: 409 Conflict
```
```json
{
    "type": "/errors/unique-constraint",
    "title": "The company 'ISEL' already exists. Please try another one.",
    "instance": "/companies"
}
```

### Database Write Error
Thrown when occurred an error in a write operation in the PostgreSQL database.

* **Type:** `/errors/database-write-error`
* **SQL Type:** `unknown-error-writing-resource`

```http
Status: 500 Internal Server Error
```
```json
{
    "type": "/errors/database-write-error",
    "title": "An error occurred writing the resource, please try again later.",
    "instance": "/companies"
}
```

### Inactive Resource
Thrown when it is requested to change a resource that is on an inactive state.

* **Type:** `/errors/inactive-resource`
* **SQL Type:** `inactive-resource`

```http
Status: 409 Conflict
```
```json
{
    "type": "/errors/database-write-error",
    "title": "It's not possible to change an inactive resource.",
    "detail": "To change it, you need to activate it first.",
    "instance": "/companies/1"
}
```

### Inactive or Banned Person
Thrown when it is requested to change an information of a person on an inactive or banned state.

* **Type:** `/errors/inactive-or-banned-person`
* **SQL Type:** `change-inactive-or-banned-person`

```http
Status: 409 Conflict
```
```json
{
    "type": "/errors/inactive-or-banned-person",
    "title": "It's not possible to change an inactive or banned person.",
    "instance": "/persons/c2b393be-d720-4494-874d-43765f5116cb"
}
```

### Person Dismissal
Thrown when the person to fire or rehire it is not an employee or a manager.

* **Type:** `/errors/person-dismissal`
* **SQL Type:** `person-dismissal`

```http
Status: 409 Conflict
```
```json
{
    "type": "/errors/person-dismissal",
    "title": "Only other employees or managers can be fired or rehired.",
    "instance": "/persons/c2b393be-d720-4494-874d-43765f5116cb"
}
```

### Person Ban
Thrown when a managers tries to ban a person that is not a guest or a user.

* **Type:** `/errors/person-ban`
* **SQL Type:** `manager-ban-permission`

```http
Status: 409 Conflict
```
```json
{
    "type": "/errors/person-ban",
    "title": "A manager can only ban guests or users.",
    "instance": "/persons/c2b393be-d720-4494-874d-43765f5116cb"
}
```

### Minimum Roles Skills
Thrown when is requested to remove a role or a skill from a person when they are already in the limit. The persons must have at least one.

* **Type:** `/errors/minimum-roles-skills`
* **SQL Types:** `minimum-roles`, `minimum-skills`

```http
Status: 409 Conflict
```
```json
{
    "type": "/errors/minimum-roles-skills",
    "title": "A person must have, at least, one role associated.",
    "instance": "/persons/c2b393be-d720-4494-874d-43765f5116cb"
}
```

### Company Person Roles
Thrown when a person tries to assign another person that is not an employee or a manager to a company.

* **Type:** `/errors/company-person-roles`
* **SQL Type:** `company-persons`

```http
Status: 409 Conflict
```
```json
{
    "type": "/errors/company-person-roles",
    "title": "Only an employee or a manager can be assigned to a company.",
    "instance": "/persons/c2b393be-d720-4494-874d-43765f5116cb"
}
```

### Invalid Role
Thrown when a person does not have the required role to complete the request.

* **Type:** `/errors/invalid-role`
* **SQL Type:** `invalid-role`

```http
Status: 409 Conflict
```
```json
{
    "type": "/errors/invalid-role",
    "title": "You don't have the required role assigned.",
    "instance": "/persons/c2b393be-d720-4494-874d-43765f5116cb"
}
```

### Invalid Company
Thrown when a person that does not belong to a company tries to access some resource of that company.

* **Type:** `/errors/invalid-company`
* **SQL Type:** `invalid-company`

```http
Status: 409 Conflict
```
```json
{
    "type": "/errors/invalid-company",
    "title": "The manager must belong to the ticket company to access or make changes.",
    "instance": "/companies/1"
}
```

### Category Being Used
Thrown when is made a request to deactivate a category that is in use.

* **Type:** `/errors/category-being-used`
* **SQL Type:** `category-being-used`

```http
Status: 409 Conflict
```
```json
{
    "type": "/errors/category-being-used",
    "title": "To deactivate a category, it must not be linked to a device or an employee.",
    "instance": "/categories/1"
}
```

### Archived Ticket
Thrown when is made a request to change some ticket information when it is on the archived state.

* **Type:** `/errors/archived-ticket`
* **SQL Type:** `archived-ticket`

```http
Status: 409 Conflict
```
```json
{
    "type": "/errors/archived-ticket",
    "title": "It's not possible to change an archived ticket.",
    "instance": "/tickets/1"
}
```

### Fixing Ticket
Thrown when is made a request to update the subject or the description of a ticket that is already being fixed, i.e. that in the fixing state.

* **Type:** `/errors/fixing-ticket`
* **SQL Type:** `fixing-ticket`

```http
Status: 409 Conflict
```
```json
{
    "type": "/errors/fixing-ticket",
    "title": "It's not possible to update a ticket that is already being fixed.",
    "instance": "/tickets/1"
}
```

### Ticket Employee Skill Mismatch
Thrown when a ticket is delivered to an employee that does not have the required skills to solve the ticket.

* **Type:** `/errors/ticket-employee-skill-mismatch`
* **SQL Type:** `ticket-employee-skill-mismatch`

```http
Status: 409 Conflict
```
```json
{
    "type": "/errors/ticket-employee-skill-mismatch",
    "title": "The employee doesn't have the skills to solve the ticket.",
    "instance": "/tickets/1/employee"
}
```

### Ticket Rate
Thrown when is made a request to set the rate of a ticket that is not on the archived state yet.

* **Type:** `/errors/ticket-rate`
* **SQL Type:** `ticket-rate`

```http
Status: 409 Conflict
```
```json
{
    "type": "/errors/ticket-rate",
    "title": "The ticket must be archived to be able to receive a rating.",
    "instance": "/tickets/1/rate"
}
```