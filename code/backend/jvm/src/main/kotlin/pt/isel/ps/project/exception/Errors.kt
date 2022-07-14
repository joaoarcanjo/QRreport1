package pt.isel.ps.project.exception

import org.springframework.http.HttpStatus
import pt.isel.ps.project.exception.Errors.InvalidCompany.Message.BUILDING_MANAGER_COMPANY
import pt.isel.ps.project.exception.Errors.InvalidRole.Message.BUILDING_MANAGER_ROLE
import java.net.URI

object Errors {
    object Unauthorized {
        val TYPE = URI("/errors/unauthorized")
        val STATUS = HttpStatus.UNAUTHORIZED
        const val SQL_TYPE = "invalid-credentials"
        const val WWW_AUTH_HEADER = "WWW-Authenticate"
        const val WWW_AUTH_HEADER_VALUE = "Bearer realm=\"qrreport\", charset=\"UTF-8\""

        object Message {
            const val REQUIRES_AUTH = "The resource requires authentication to access."
            const val AUTHORIZATION_HEADER_MISSING = "Please provide the authorization header or login."
            const val INVALID_TYPE = "Invalid authorization type."
            const val INVALID_TOKEN = "Invalid authentication token."
            const val INVALID_CREDENTIALS = "Invalid authentication credentials."
            const val INVALID_ORIGIN = "Invalid origin request."
            const val MISSING_SESSION_COOKIE = "Session cookie is missing."
        }
    }

    object Forbidden {
        val TYPE = URI("/errors/forbidden")
        val STATUS = HttpStatus.FORBIDDEN
        const val SQL_TYPE = "resource-permission-denied"
        object Message {
            const val ACCESS_DENIED = "Forbidden access, not enough permissions to access the required resource."
            const val CHANGE_DENIED = "Forbidden access, not enough permissions to change the required resource."
            const val CREATION_DENIED = "Forbidden access, not enough permissions to create the required resource."
            const val MANAGER_CREATE_PERSON = "A manager can only add another manager or an employee."
            const val MANAGER_CREATE_PERSON_COMPANY = "A manager can only add a person to a company that belongs."
            const val UPDATE_PERSON = "You can only update your own profile."
        }
    }

    object NotFound {
        val TYPE = URI("/errors/not-found")
        val STATUS = HttpStatus.NOT_FOUND
        const val SQL_TYPE = "resource-not-found"

        object Message {
            const val RESOURCE_NOT_FOUND = "The resource was not found."
            const val RESOURCE_DETAIL_NOT_FOUND_TEMPLATE = "The {} was not found."
        }
    }

    object BadRequest {
        val TYPE = URI("/errors/validation-error")
        val STATUS = HttpStatus.BAD_REQUEST
        const val SQL_TYPE = "null-update-parameters"

        object Locations {
            const val PATH = "path"
            const val HEADERS = "headers"
            const val QUERY_STRING = "query_string"
            const val BODY = "body"
        }

        object Message {
            const val INVALID_REQ_PARAMS = "One or more request parameters are not valid."
            const val MISSING_MISMATCH_REQ_BODY = "One or more request body parameters are missing or have a type mismatch."

            const val UPDATE_NULL_PARAMS = "All updatable parameters can't be null."
            const val UPDATE_NULL_PARAMS_DETAIL = " Please insert one of the possible parameters in order to update."

            const val BLANK_PARAMS = "Parameter can't be a blank value."
            const val BLANK_PARAMS_DETAIL = "Please insert one of the possible parameters in order to update."

            const val CREATE_EMPLOYEE_WITHOUT_SKILL = "The employee must be linked to a skill."
            const val EMPLOYEE_NULL_SKILL = "The skill id for the employee cannot be null."

            const val TYPE_MISMATCH_REQ_QUERY = "Type mismatch of request query parameter."

            const val ADD_EMPLOYEE_MANAGER_ROLE_WITHOUT_COMPANY = "The company for the employee or manager is missing."
            const val NULL_EMPLOYEE_MANAGER_COMPANY = "The company id for the person must be provided."

            object Templated {
                const val MUST_HAVE_TYPE = "The value must be of the {} type."
            }

            object Pagination {
                const val PAGE_TYPE_MISMATCH = "The value must be an integer > 0."
            }

            object Device {
                const val INVALID_DEVICE_NAME_LENGTH = "The name can have a maximum of 50 characters."

                object Anomaly {
                    const val INVALID_ANOMALY_ANOMALY_LENGTH = "The anomaly can have a maximum of 150 characters."
                }
            }

            object Category {
                const val INVALID_CATEGORY_NAME_LENGTH = "The name can have a maximum of 50 characters."
            }

            object Company {
                const val INVALID_NAME_LENGTH = "The name can have a maximum of 50 characters."

                object Building {
                    const val INVALID_BUILDING_NAME_LENGTH = "The name can have a maximum of 50 characters."
                    const val INVALID_BUILDING_FLOOR_NUMBER = "The max number of floors is 600"

                    object Room {
                        const val INVALID_ROOM_NAME_LENGTH = "The name can have a maximum of 50 characters."
                        const val INVALID_ROOM_FLOOR_NUMBER = "The floor number must be between -100 and 500"
                    }
                }
            }

            object Ticket {
                const val INVALID_HASH_LENGTH = "The hash size must be equal to 64 characters."
                const val INVALID_SUBJECT_LENGTH = "The subject can have a maximum of 50 characters."
                const val INVALID_DESCRIPTION_LENGTH = "The description can have a maximum of 200 characters."
                const val INVALID_RATE = "The ticket rate must be between 0 and 5 inclusive."

                object Comment {
                    const val INVALID_COMMENT_LENGTH = "The comment can have a maximum of 200 characters."
                }
            }

            object Auth {
                const val PASSWORD_MISMATCH_TITLE = "Password and confirm password don't match, please make sure they are equal ."
                const val PASSWORD_MISMATCH_REASON = "The passwords don't match."
            }
        }
    }

    object InternalServerError {
        val TYPE = URI("/errors/internal-server-error")
        val STATUS = HttpStatus.INTERNAL_SERVER_ERROR

        object Message {
            const val INTERNAL_ERROR = "An internal server error occurred, please try again later."
            const val DB_CREATION_ERROR = "It was not possible to create the requested resource, please try again later."
            const val UNKNOWN_ERROR = "An error occurred, please verify if your passing the right values in the request."
        }
    }

    object MethodNotAllowed {
        val TYPE = URI("/errors/method-not-allowed")
        val STATUS = HttpStatus.METHOD_NOT_ALLOWED

        object Message {
            const val METHOD_NOT_ALLOWED = "The request method is not supported for the requested instance."
        }
    }

    object UniqueConstraint {
        val TYPE = URI("/errors/unique-constraint")
        val STATUS = HttpStatus.CONFLICT
        const val SQL_TYPE = "unique-constraint"

        fun buildNotUniqueMessage(property: String, value: String): String {
            return "The $property '$value' already exists. Please try another one."
        }
    }

    object UnknownErrorWritingResource {
        val TYPE = URI("/errors/database-write-error")
        val STATUS = HttpStatus.INTERNAL_SERVER_ERROR
        const val SQL_TYPE = "unknown-error-writing-resource"

        object Message {
            const val DB_WRITE_ERROR_TEMPLATE = "An error occurred {} the resource, please try again later."
        }
    }

    object InactiveResource {
        val TYPE = URI("/errors/inactive-resource")
        val STATUS = HttpStatus.CONFLICT
        const val SQL_TYPE = "inactive-resource"

        object Message {
            const val INACTIVE_RESOURCE = "It's not possible to change an inactive resource."
            const val INACTIVE_RESOURCE_DETAIL = "To change it, you need to activate it first."
        }
    }

    object InactiveBannedPerson {
        val TYPE = URI("/errors/inactive-or-banned-person")
        val STATUS = HttpStatus.CONFLICT
        const val SQL_TYPE = "change-inactive-or-banned-person"
        object Message {
            const val INACTIVE_BANNED_PERSON = "It's not possible to change an inactive or banned person."
        }
    }

    object PersonDismissal {
        val TYPE = URI("/errors/person-dismissal")
        val STATUS = HttpStatus.CONFLICT
        const val SQL_TYPE = "person-dismissal"
        object Message {
            const val WRONG_PERSON_DISMISSAL = "Only other employees or managers can be fired or rehired."
        }
    }

    object PersonBan {
        val TYPE = URI("/errors/person-ban")
        val STATUS = HttpStatus.CONFLICT
        const val SQL_TYPE_MANAGER_PERMS = "manager-ban-permission"
        object Message {
            const val WRONG_PERSON_BAN = "Only other persons can be banned or unbanned."
            const val MANAGER_BAN_PERMS = "A manager can only ban guests or users."
        }
    }

    object MinimumRolesSkills {
        val TYPE = URI("/errors/minimum-roles-skills")
        val STATUS = HttpStatus.CONFLICT
        const val SQL_TYPE_ROLES = "minimum-roles"
        const val SQL_TYPE_SKILLS = "minimum-skills"
        object Message {
            const val MINIMUM_ROLES = "A person must have, at least, one role associated."
            const val MINIMUM_SKILLS = "An employee must have, at least, one skill associated."
        }
    }

    object CompanyPersonsRoles {
        val TYPE = URI("/errors/company-person-roles")
        val STATUS = HttpStatus.CONFLICT
        const val SQL_TYPE = "company-persons"
        object Message {
            const val COMPANY_PERSON_ROLES = "Only an employee or a manager can be assigned to a company."
        }
    }

    object InvalidRole {
        val TYPE = URI("/errors/invalid-role")
        val STATUS = HttpStatus.CONFLICT
        const val SQL_TYPE = "invalid-role"

        object Message {
            const val BUILDING_MANAGER_ROLE = "The building manager must have the manager role."
        }

        val mapDetails = HashMap<String, String>().apply {
            put("building-manager", BUILDING_MANAGER_ROLE)
        }
    }

    object InvalidCompany {
        val TYPE = URI("/errors/invalid-company")
        val STATUS = HttpStatus.CONFLICT
        const val SQL_TYPE = "invalid-company"

        object Message {
            const val BUILDING_MANAGER_COMPANY = "The building manager must belong to the building company."
        }

        val mapDetails = HashMap<String, String>().apply {
            put("building-manager", BUILDING_MANAGER_COMPANY)
        }
    }

    object CategoryBeingUsed {
        val TYPE = URI("/errors/category-being-used")
        val STATUS = HttpStatus.CONFLICT
        const val SQL_TYPE = "category-being-used"

        object Message {
            const val CATEGORY_BEING_USED = "To deactivate a category, it must not be linked to a device or an employee."
        }
    }

    object ArchivedTicket {
        val TYPE = URI("/errors/archived-ticket")
        val STATUS = HttpStatus.CONFLICT
        const val SQL_TYPE = "archived-ticket"

        object Message {
            const val ARCHIVED_TICKET = "It's not possible to change an archived ticket."
        }
    }

    fun buildMessage(message: String, value: Any): String {
        return message.replace("{}", value.toString())
    }
}