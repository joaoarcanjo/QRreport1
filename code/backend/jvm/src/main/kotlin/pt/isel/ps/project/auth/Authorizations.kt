package pt.isel.ps.project.auth

import pt.isel.ps.project.exception.Errors.Forbidden.Message.ACCESS_DENIED
import pt.isel.ps.project.exception.Errors.Forbidden.Message.CHANGE_DENIED
import pt.isel.ps.project.exception.ForbiddenException
import pt.isel.ps.project.model.person.Roles.USER
import pt.isel.ps.project.model.person.Roles.EMPLOYEE
import pt.isel.ps.project.model.person.Roles.MANAGER
import pt.isel.ps.project.model.person.Roles.ADMIN

object Authorizations {
    object Person {
        fun getPersonsAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(ACCESS_DENIED)
        }

        fun createPersonsAuthorization(user: AuthPerson): Boolean {
            val userRole = user.activeRole
            if (userRole == MANAGER || userRole == ADMIN) return true
            throw ForbiddenException(ACCESS_DENIED)
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
            throw ForbiddenException(ACCESS_DENIED)
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
            throw ForbiddenException(ACCESS_DENIED)
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
}