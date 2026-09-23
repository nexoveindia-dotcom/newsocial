package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AdminDao {

    // ==================== User Roles ====================
    @Query("SELECT * FROM admin_user_roles ORDER BY assignedAt DESC")
    fun getAllUserRoles(): Flow<List<AdminUserRoleEntity>>

    @Query("SELECT * FROM admin_user_roles WHERE userId = :userId LIMIT 1")
    suspend fun getUserRole(userId: String): AdminUserRoleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserRole(entity: AdminUserRoleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserRoles(entities: List<AdminUserRoleEntity>)

    @Query("UPDATE admin_user_roles SET role = :newRole, assignedBy = :assignedBy, assignedAt = :assignedAt WHERE userId = :userId")
    suspend fun updateUserRole(userId: String, newRole: String, assignedBy: String, assignedAt: Long)

    @Query("UPDATE admin_user_roles SET isBanned = :isBanned, banReason = :banReason WHERE userId = :userId")
    suspend fun updateUserBanStatus(userId: String, isBanned: Boolean, banReason: String?)

    // ==================== Audit Logs ====================
    @Query("SELECT * FROM admin_audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AdminAuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AdminAuditLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLogs(logs: List<AdminAuditLogEntity>)

    // ==================== Moderation Tickets ====================
    @Query("SELECT * FROM moderation_tickets ORDER BY aiSeverityScore DESC, id DESC")
    fun getAllTickets(): Flow<List<ModerationTicketEntity>>

    @Query("SELECT * FROM moderation_tickets WHERE id = :ticketId LIMIT 1")
    suspend fun getTicketById(ticketId: String): ModerationTicketEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: ModerationTicketEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTickets(tickets: List<ModerationTicketEntity>)

    @Query("UPDATE moderation_tickets SET status = :status, reviewerNotes = :notes, reviewedBy = :reviewedBy, reviewedAt = :reviewedAt WHERE id = :ticketId")
    suspend fun updateTicketStatus(
        ticketId: String,
        status: String,
        notes: String?,
        reviewedBy: String?,
        reviewedAt: Long?
    )

    // ==================== System Configs ====================
    @Query("SELECT * FROM system_configs ORDER BY category ASC, `key` ASC")
    fun getAllConfigs(): Flow<List<SystemConfigEntity>>

    @Query("SELECT * FROM system_configs WHERE `key` = :key LIMIT 1")
    suspend fun getConfigByKey(key: String): SystemConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: SystemConfigEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfigs(configs: List<SystemConfigEntity>)

    @Query("UPDATE system_configs SET value = :value, lastUpdated = :lastUpdated, updatedBy = :updatedBy WHERE `key` = :key")
    suspend fun updateConfigValue(key: String, value: String, lastUpdated: Long, updatedBy: String)
}
