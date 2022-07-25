package pt.isel.ps.project.auth

import pt.isel.ps.project.exception.Errors.Forbidden.Message.ACCESS_DENIED
import pt.isel.ps.project.exception.Errors.Forbidden.Message.CHANGE_DENIED
import pt.isel.ps.project.exception.Errors.Forbidden.Message.CREATION_DENIED
import pt.isel.ps.project.exception.ForbiddenException
import pt.isel.ps.project.model.Uris
import pt.isel.ps.project.model.Uris.UNDEFINED
import pt.isel.ps.project.model.person.CreatePersonEntity
import pt.isel.ps.project.model.person.Roles.USER
import pt.isel.ps.project.model.person.Roles.EMPLOYEE
import pt.isel.ps.project.model.person.Roles.MANAGER
import pt.isel.ps.project.model.person.Roles.ADMIN
import pt.isel.ps.project.model.person.Roles.GUEST
import pt.isel.ps.project.util.Validator.Person.verifyManagerCreationPermissions
import java.util.*

object Authorizations {
    object Person {
        fun getPersonsAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(ACCESS_DENIED)
        }

        fun createPersonsAuthorization(person: CreatePersonEntity, user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == ADMIN) return true
            if ((userRole == MANAGER)) {
                verifyManagerCreationPermissions(user, person)
                return true
            }
            throw ForbiddenException(CREATION_DENIED)
        }

        fun getPersonAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == USER || userRole == EMPLOYEE || userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(ACCESS_DENIED)
        }

        fun updatePersonAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == USER || userRole == EMPLOYEE || userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun deleteUserAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == USER || userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun firePersonAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun rehirePersonAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun banPersonAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun unbanPersonAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun addRoleToPersonAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun removeRoleFromPersonAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun addSkillToEmployeeAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun removeSkillFromEmployeeAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun assignPersonToCompanyAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun switchRoleAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == USER || userRole == EMPLOYEE || userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun getRolesAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(ACCESS_DENIED)
        }
    }

    object Company {
        fun getCompaniesAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(ACCESS_DENIED)
        }

        fun createCompanyAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == ADMIN) return true
            throw ForbiddenException(CREATION_DENIED)
        }

        fun getCompanyAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(ACCESS_DENIED)
        }

        fun updateCompanyAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun deactivateCompanyAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun activateCompanyAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }
    }

    object Building {
        fun getBuildingsAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(ACCESS_DENIED)
        }

        fun createBuildingAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CREATION_DENIED)
        }

        fun getBuildingAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(ACCESS_DENIED)
        }

        fun updateBuildingAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun deactivateBuildingAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun activateBuildingAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun changeBuildingManagerAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }
    }

    object Ticket {
        fun getTicketsAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == USER || userRole == EMPLOYEE || userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(ACCESS_DENIED)
        }

        fun getTicketAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == USER || userRole == EMPLOYEE || userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(ACCESS_DENIED)
        }

        fun updateTicketAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == USER || userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun refuseTicketAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun changeTicketStateAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == EMPLOYEE || userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun addTicketRateAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == USER) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun getSpecificEmployeesAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun setEmployeeAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun removeEmployeeAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun groupTicketAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun getEmployeeStatesAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == EMPLOYEE || userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }
    }

    object Room {
        fun getRoomsAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(ACCESS_DENIED)
        }

        fun createRoomAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CREATION_DENIED)
        }

        fun getRoomAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(ACCESS_DENIED)
        }

        fun updatRoomAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun deactivateRoomAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun activateRoomAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun addRoomDeviceAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun removeRoomDeviceAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }
    }

    object Device {
        fun getDevicesAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(ACCESS_DENIED)
        }

        fun createDeviceAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == ADMIN) return true
            throw ForbiddenException(CREATION_DENIED)
        }

        fun getDeviceAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(ACCESS_DENIED)
        }

        fun updateDeviceAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun deactivateDeviceAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun activateDeviceAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun changeDeviceCategoryAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun getRoomDevicesAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun getRoomDeviceAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }
    }

    object QRCode {
        fun getQRCodeAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(ACCESS_DENIED)
        }

        fun createQRCodeAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(ACCESS_DENIED)
        }
    }

    object Anomaly {
        fun createAnomalyAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == ADMIN) return true
            throw ForbiddenException(CREATION_DENIED)
        }

        fun updateAnomalyAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun deleteAnomalyAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }
    }

    object Category {
        fun getCategoriesAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(ACCESS_DENIED)
        }

        fun createCategoryAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == ADMIN) return true
            throw ForbiddenException(CREATION_DENIED)
        }

        fun updateCategoryAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun activateCategoryAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun deactivateCategoryAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }
    }

    object Comment {
        fun getCommentsAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == EMPLOYEE || userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(ACCESS_DENIED)
        }

        fun createCommentAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == EMPLOYEE || userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CREATION_DENIED)
        }

        fun updateCommentAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == EMPLOYEE || userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }

        fun deleteCommentAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == EMPLOYEE || userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(CHANGE_DENIED)
        }
    }
}