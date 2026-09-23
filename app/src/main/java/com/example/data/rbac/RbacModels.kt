package com.example.data.rbac

enum class RbacRole(
    val title: String,
    val description: String,
    val tierLevel: Int
) {
    SUPER_ADMIN(
        title = "Super Admin",
        description = "Root authority with unrestricted governance and audit privileges.",
        tierLevel = 0
    ),
    SECURITY_ADMIN(
        title = "Security Admin",
        description = "Manages user bans, cryptographic key rotations, and session security.",
        tierLevel = 1
    ),
    CONTENT_MODERATOR(
        title = "Content Moderator",
        description = "Reviews reported posts, quaranines toxic media, and resolves tickets.",
        tierLevel = 2
    ),
    ADS_MANAGER(
        title = "Ads Manager",
        description = "Manages sponsored campaigns, creator payouts, and ad quality approval.",
        tierLevel = 2
    ),
    CREATOR_PARTNER(
        title = "Creator Partner",
        description = "Verified partner with collaborative canvas and live studio priorities.",
        tierLevel = 3
    ),
    STANDARD_USER(
        title = "Standard User",
        description = "Basic platform access for feed browsing, posting, and encrypted messaging.",
        tierLevel = 4
    )
}

enum class RbacPermission(
    val code: String,
    val displayName: String,
    val description: String
) {
    MANAGE_ROLES(
        code = "role:manage",
        displayName = "Manage Roles",
        description = "Promote, demote users and reassign administrative role privileges."
    ),
    MANAGE_USERS(
        code = "user:manage",
        displayName = "Manage Users",
        description = "Ban, suspend, unban, and adjust user reputation / aura levels."
    ),
    MODERATE_POSTS(
        code = "post:moderate",
        displayName = "Moderate Content",
        description = "Quarantine, censor, or remove reported posts and visual media."
    ),
    MODERATE_COMMENTS(
        code = "comment:moderate",
        displayName = "Moderate Comments",
        description = "Purge toxic comments and mute disruptive participants."
    ),
    MANAGE_ADS(
        code = "ads:manage",
        displayName = "Manage Ads",
        description = "Review, approve, or halt sponsored campaigns and adjust impression rates."
    ),
    VIEW_AUDIT_LOGS(
        code = "audit:read",
        displayName = "View Audit Logs",
        description = "Inspect cryptographic immutable audit logs of all administrative actions."
    ),
    SYSTEM_CONFIG(
        code = "sys:config",
        displayName = "System Config",
        description = "Toggle emergency feed killswitch and modify global algorithm governance."
    ),
    SECURITY_KEYS(
        code = "sec:keys",
        displayName = "Security & Keys",
        description = "Execute master key rotation and terminate compromised E2EE sessions."
    )
}

object RbacPolicy {
    val rolePermissions: Map<RbacRole, Set<RbacPermission>> = mapOf(
        RbacRole.SUPER_ADMIN to RbacPermission.values().toSet(),
        RbacRole.SECURITY_ADMIN to setOf(
            RbacPermission.MANAGE_USERS,
            RbacPermission.VIEW_AUDIT_LOGS,
            RbacPermission.SECURITY_KEYS,
            RbacPermission.SYSTEM_CONFIG
        ),
        RbacRole.CONTENT_MODERATOR to setOf(
            RbacPermission.MODERATE_POSTS,
            RbacPermission.MODERATE_COMMENTS,
            RbacPermission.VIEW_AUDIT_LOGS
        ),
        RbacRole.ADS_MANAGER to setOf(
            RbacPermission.MANAGE_ADS,
            RbacPermission.VIEW_AUDIT_LOGS
        ),
        RbacRole.CREATOR_PARTNER to emptySet(),
        RbacRole.STANDARD_USER to emptySet()
    )

    fun hasPermission(role: RbacRole, permission: RbacPermission): Boolean {
        return rolePermissions[role]?.contains(permission) == true
    }

    fun getPermissionsForRole(role: RbacRole): Set<RbacPermission> {
        return rolePermissions[role] ?: emptySet()
    }
}

data class AdminPersona(
    val id: String,
    val name: String,
    val handle: String,
    val role: RbacRole,
    val avatarDrawable: String,
    val titleBadge: String
)

data class RbacCheckResult(
    val isAllowed: Boolean,
    val requiredPermission: RbacPermission,
    val actorRole: RbacRole,
    val errorMessage: String? = null
)
