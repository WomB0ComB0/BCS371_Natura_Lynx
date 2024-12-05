interface FamilyService {
    suspend fun createFamily(name: String, parentId: String): Family
    suspend fun addFamilyMember(familyId: String, userId: String, role: FamilyRole)
    suspend fun removeFamilyMember(familyId: String, userId: String)
    suspend fun getFamilyMembers(familyId: String): List<FamilyMember>
}

data class Family(
    val id: String,
    val name: String,
    val parentId: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class FamilyMember(
    val userId: String,
    val familyId: String,
    val role: FamilyRole
)

enum class FamilyRole {
    PARENT,
    CHILD
} 