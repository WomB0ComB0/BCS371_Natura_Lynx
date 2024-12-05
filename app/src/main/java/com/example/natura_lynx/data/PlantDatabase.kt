@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey val id: String,
    val commonName: String,
    val scientificName: String,
    val confidence: Float,
    val imageUri: String,
    val identifiedAt: Long = System.currentTimeMillis()
)

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants ORDER BY identifiedAt DESC")
    fun getAllPlants(): Flow<List<Plant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: Plant)

    @Query("SELECT * FROM plants WHERE id = :id")
    suspend fun getPlantById(id: String): Plant?
}

@Database(entities = [Plant::class], version = 1)
abstract class PlantDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao

    companion object {
        @Volatile
        private var INSTANCE: PlantDatabase? = null

        fun getDatabase(context: Context): PlantDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    PlantDatabase::class.java,
                    "plant_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
} 