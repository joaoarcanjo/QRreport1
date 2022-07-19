package pt.isel.ps.project.model.person

/**
 * Name of the person representation output parameter
 */
const val PERSON_REP = "personRep"

object PersonEntity {
    const val NAME = "name"
    const val PHONE = "phone"
    const val EMAIL = "email"
    const val PASSWORD = "password"
    const val ROLE = "role"
    const val COMPANY = "company"
    const val SKILL = "skill"
}

object Roles {
    const val GUEST = "guest"
    const val USER = "user"
    const val EMPLOYEE = "employee"
    const val MANAGER = "manager"
    const val ADMIN = "admin"
}

data class CreatePersonEntity(
    val name: String,
    val phone: String,
    val email: String,
    val password: String,
    val role: String,
    val company: Long,
    val skill: Long? = null,
)

data class UpdatePersonEntity(
    val name: String?,
    val phone: String?,
    val email: String?,
    val password: String?,
)

data class FireBanPersonEntity(
    val reason: String,
)

data class AddRoleToPersonEntity(
    val role: String,
    val company: Long?,
    val skill: Long?,
)

data class RemoveRoleFromPersonEntity(
    val role: String,
)

data class AddRemoveSkillToEmployeeEntity(
    val skill: Long,
)

data class AssignPersonToCompanyEntity(
    val company: Long,
)

data class SwitchRoleEntity(
    val role: String,
)
