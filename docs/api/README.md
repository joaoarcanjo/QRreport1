# API Documentation
Documentation of all the resources available through the API.

The base URI for all Web API requests is `http://localhost:8000/v1`.

## Table of contents
* [**API resources and documentation**](#api-resources-and-documentation)
* [**Media-type**](#media-type)
* [**Classes**](#classes)
* [**Standard link relations**](#standard-link-relations)
* [**Generic Errors**](#generic-errors)
* [**Authentication**](#authentication)

## API endpoints and documentation
* [**Anomaly**](resources/Anomaly.md)
* [**Building**](resources/Building.md)
* [**Category**](resources/Category.md)
* [**Comment**](resources/Comment.md)
* [**Company**](resources/Company.md)
* [**Device**](resources/Device.md)
* [**Person**](resources/Person.md)
* [**QRCode**](resources/QRCode.md)
* [**Room**](resources/Room.md)
* [**Ticket**](resources/Ticket.md)

## Media-type
The media-type that we recommend to be used in the representations of each resource is `application/vnd.qrreport+json`. This media-type is based in the hypermedia specification for representing entities - Siren, in which we did some little changes.

All the properties used in the representations that are in the JSON Siren format, such as, `entities`, `class`, `links`, `actions`, etc, are described in their documentation [here](https://github.com/kevinswiber/siren).

Before, in the actions section, when you wanted to do an action that would require a payload, the field types of this payload had to be an [input type specified in HTML5](https://html.spec.whatwg.org/#the-input-element), such as, text, number, hidden, etc. Therefore we added to our media-type that when the content-type of the action is `application/json` it has to be followed by the property `properties` which represents an object with all the data that will be in the body request, which types can be `string`, `number`, `array` or `object`. Each property can have an extra element called `required` to inform if the property must be inserted or not, when it is not required its value is set to false, otherwise it is hidden, and another element `possibleValues`, that represents an URI to fetch the possible values that can be inserted to that property.
```json
{
    "type": "application/json",
    "properties": [
        { "name": "names", "type": "array", "itemsType": "string" }
    ]
}
```
```json
{
    "type": "application/json",
    "properties": [
        { 
            "name": "names", 
            "type": "object", 
            "properties": [ { "name": "firstName", "type": "string" } ] 
        }
    ]
}
```
```json
{
    "type": "application/json",
    "properties": [
        { 
            "name": "skill", 
            "type": "number", 
            "possibleValues": { "href": "/categories" }
        }
    ]
}
```

#### `itemsType`
When the parent property type is an **array**, *itemsType* indicates the type of each value in the array. 

#### `properties`
When the parent property type is an **object**, *properties* represents an array that contains all the properties names and values.

#### `possibleValues`
Represents an object with two properties: **i)** `href` to the URI to fetch the possible values to insert in the property; **ii)** `templated` in case the URI is templated, when omitted the default value is false.

## Classes
The classes, as mentioned in the [Siren documentation](https://github.com/kevinswiber/siren), describe the nature of an entity's content based on the current representation.

| Name | Description |
|:-:|:-:|
| `collection` | The representation contains a collection, such as, a collection of companies, tickets, persons, etc. |
| `company` | The representations of the projects. |
| `building` | The representations of the issues. |
| `room` | The representations of the comments. |
| `person` | The representations of the persons. |
| `category` | The representations of the categories. |
| `device` | The representations of the devices. |
| `anomaly` | The representations of the anomalies. |
| `comment` | The representations of the comments. |
| `report` | The representation contains the report form. |
| `ticket` | The representations of the tickets. |

#### `collection`
When the representation contains a collection, it is always included three more properties:
* `collectionSize` - Integer used to represent the total collection size.
* `pageMaxSize` - Integer used to represent the maximum number of items in the page. 
* `pageIndex` - Integer used to represent the page number. In the system, by default, corresponds to **1**.

## Standard link relations
A link relation is a descriptive attribute attached to a hyperlink in order to define the type of the link, or the relationship between the source and destination resources.

In our domain, we mainly use standard link relations, which are described in [IANA's Link Relation Types documentation](https://www.iana.org/assignments/link-relations/link-relations.xhtml), and the ones we use that are not standard are always described in the document where they're used. The standard link relation types used are: `author`, `item` and `self`.

## Generic errors
Each HTTP request can throw a set of errors. The error information format used in the Web API is the one described in the [RFC7807 - Problem Details for HTTP APIs](https://datatracker.ietf.org/doc/html/rfc7807) specification. As such the **content-type** of the errors representations is `application/problem+json`.

Each class can have their domain specific errors but mostly have generic errors, this is, errors that have almost everything in common with each other, the properties that can differ are the `detail` message, `instance` URI and the `data` or `invalidParams` that caused the error. Below are described all these errors and an example of its usage.

### Examples of the properties data and invalidParams
The examples are not specific to the domain of this project, its objective is to illustrate the usage of these properties.

#### `data`
Represents an array of objects, in which each object describe the properties names and values that caused the error. The properties inside the object can vary depending on the class.
```json
{
    "type": "/errors/out-of-credit",
    "title": "You do not have enough credit.",
    "detail": "Your current balance is 30, but that costs 50.",
    "instance": "/account/12345/msgs/abc",
    "data": [
        { "balance": 30, "accounts": ["/account/12345", "/account/67890"] }
    ]
}
```

#### `invalid-params`
Only used in the HTTP error Bad Request and it's composed by an array of objects, in which each object represents the error found, describing the `name` of the parameter, the local (`local`) where it was found and the `reason` for this error to occur.
```json
{
   "type": "/errors/validation-error",
   "title": "Your request parameters didn't validate.",
   "invalidParams": [ 
       { "name": "age", "local": "body", "reason": "must be a positive integer" },
       { "name": "color", "local": "body", "reason": "must be 'green', 'red' or 'blue'" }
    ]
   }
```

### Errors documentation
All the errors that can happen in the system are documented, where all the generic errors that can happen in almost every resource are described down below and the errors that are more domain specific and more connected to the business logic of the project are described [**here**](resources/errors/README.md).

### Bad Request
Thrown in requests that have some error inside the request, such as, an error in the path, headers or body.

#### Type
* `validation-error`

```http
Status: 400 Bad Request
```
```json
{
    "type": "/errors/validation-error",
    "title": "Your request parameters are invalid.",
    "instance": "/companies/abc",
    "invalid-params": [
        { "name": "companyId", "in": "path", "reason": "Must be an integer > 0." }
    ]
}
```

### Unauthorized
Thrown in requests that need an authorization token to be carried out.

#### Type
* `unauthorized`

```http
Status: 401 Unauthorized
```
```json
{
    "type": "/errors/unauthorized",
    "title": "The resource requires authentication to access.",
    "instance": "/companies/1"
}
```

### Forbidden
Thrown in requests where a person is authenticated but does not have the required permissions to access the resource.

#### Type
* `forbidden`

```http
Status: 403 Forbidden
```
```json
{
    "type": "/errors/forbidden",
    "title": "Forbidden access, not enough permissions to access the required resource.",
    "instance": "/devices/1"
}
```

### Not Found
Thrown in requests that the required resource was not found.

#### Type
* `not-found`

```http
Status: 404 Not Found
```
```json
{
    "type": "/errors/not-found",
    "title": "The resource was not found.",
    "instance": "/companies/99999"
}
```

### Method Not Allowed
Thrown in requests that have an HTTP method that is not allowed.

#### Type
* `method-not-allowed`

```http
Status: 405 Method Not Allowed
```
```json
{
    "type": "/errors/method-not-allowed",
    "title": "The request method is not supported for the requested instance.",
    "instance": "/companies/1"
}
```

### Internal Server Error
Thrown when happens an unexpected error in the database or in the server.

#### Type
* `internal-server-error`

```http
Status: 500 Internal Server Error
```
```json
{
    "type": "/errors/internal-server-error",
    "title": "An internal server error occurred. Please try again later.",
    "instance": "/devices"
}
```

#### Observations
In the documentation, where is mentioned that the parameters or the properties of the representations receive an integer, it is **always a non-negative integer greater that 0**. And whenever is not mentioned that a property **is or is not unique and/or stable** it's because **it is not**.

## Authentication

The project uses [**JSON Web Tokens**](https://jwt.io/) to authenticate all the requests.
