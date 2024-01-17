package apps.eduraya.e_parking.data.db

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
class Converters(
    private val jsonParser: JsonParser
) {
    @TypeConverter
    fun toStringJson(listOfString: List<String>) : String {
        return jsonParser.toJson(
            listOfString,
            object : TypeToken<List<String>>(){}.type
        ) ?: "[]"
    }

    @TypeConverter
    fun fromStringsJson(listOfString: String): List<String>{
        return jsonParser.fromJson<List<String>>(
            listOfString,
            object: TypeToken<List<String>>(){}.type
        ) ?: emptyList()
    }
}