package apps.eduraya.e_parking.data.responses

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginResponse(
    val data: Data? = null,
    val message: String? = null,
    val success: Boolean? = null,
    val code: Int? = null
):Parcelable

@Parcelize
data class Data (
    val token: String? = null,
    val user: UserInfo? = null): Parcelable

@Parcelize
@Entity(tableName = "user_info")
data class UserInfo(

    @PrimaryKey
    val id: Int? = null,

    @ColumnInfo(name = "name")
    val name: String? = null,

    @ColumnInfo(name = "email")
    val email: String? = null,

    @ColumnInfo(name = "email_verified_at")
    val email_verified_at: String? = null,

    @ColumnInfo(name = "phone")
    val phone: Int? = null,

    @ColumnInfo(name = "avatar")
    val avatar: String? = null,

    @ColumnInfo(name = "balance")
    val balance: Int? = null,

    @ColumnInfo(name = "status")
    val status: String? = null,

    @ColumnInfo(name = "activation_token")
    val activation_token: String? = null,

    @ColumnInfo(name = "created_at")
    val created_at: String? = null,

    @ColumnInfo(name = "updated_at")
    val updated_at: String? = null,

//    @ColumnInfo(name = "roles")
//    val roles: List<String>? = null
):Parcelable