package ru.avtomaton.irz.app.client.api.users.models

/**
 * @author Anton Akkuzin
 */
object UserRoles {
    const val admin: String = "Admin"
    const val superAdmin: String = "SuperAdmin"
    const val cabinetsManager: String = "CabinetsManager"
    const val support: String = "Support"

    fun containsRole(possibleRole: String) : Boolean {
        return possibleRole.equals(admin, ignoreCase = true)
                || possibleRole.equals(superAdmin, ignoreCase = true)
                || possibleRole.equals(cabinetsManager, ignoreCase = true)
                || possibleRole.equals(support, ignoreCase = true)
    }
}