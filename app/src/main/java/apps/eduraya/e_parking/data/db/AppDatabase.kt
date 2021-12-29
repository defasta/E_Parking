package apps.eduraya.e_parking.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import apps.eduraya.e_parking.data.responses.UserInfo

@Database(entities = [UserInfo::class], version =1, exportSchema = false)
abstract class AppDatabase :RoomDatabase(){

    abstract fun getDao(): AppDao
    companion object{
        private var dbINSTANCE : AppDatabase? = null

        fun getAppDB(context: Context):AppDatabase{
            if(dbINSTANCE == null){
                dbINSTANCE = Room.databaseBuilder<AppDatabase>(
                    context.applicationContext, AppDatabase::class.java, "EParkingDB"
                )
                    .allowMainThreadQueries()
                    .build()
            }
            return dbINSTANCE!!
        }
    }
}