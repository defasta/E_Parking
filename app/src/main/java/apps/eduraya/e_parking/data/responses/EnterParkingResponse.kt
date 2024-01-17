package apps.eduraya.e_parking.data.responses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EnterParkingResponse(
    val code: Int? = null,
    val message: String? = null,
    val success: Boolean? = null
): Parcelable