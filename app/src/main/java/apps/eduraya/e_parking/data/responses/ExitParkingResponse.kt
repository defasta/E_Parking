package apps.eduraya.e_parking.data.responses.exitparking


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExitParkingResponse(
    val code: Int? = null,
    val `data`: DataExitParking? = null,
    val message: String? = null,
    val success: Boolean? = null
): Parcelable

@Parcelize
data class DataExitParking(
    @SerializedName("basic_price")
    val basicPrice: Int? = null,
    @SerializedName("check_in")
    val checkIn: String? = null,
    @SerializedName("check_in_photo")
    val checkInPhoto: String? = null,
    @SerializedName("check_out")
    val checkOut: String? = null,
    @SerializedName("check_out_photo")
    val checkOutPhoto: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("gate_id")
    val gateId: Int? = null,
    val id: Int? = null,
    @SerializedName("insurance_price")
    val insurancePrice: Int? = null,
    @SerializedName("is_insurance")
    val isInsurance: Int? = null,
    @SerializedName("officer_id")
    val officerId: Int? = null,
    @SerializedName("place_id")
    val placeId: Int? = null,
    @SerializedName("plate_number")
    val plateNumber: String? = null,
    @SerializedName("progressive_price")
    val progressivePrice: Int? = null,
    val status: String? = null,
    @SerializedName("total_bill")
    val totalBill: Int? = null,
    @SerializedName("total_pay")
    val totalPay: Int? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("user_id")
    val userId: Int? = null,
    @SerializedName("valet_area_id")
    val valetAreaId: Int? = null,
    @SerializedName("vehicle_id")
    val vehicleId: Int? = null
):Parcelable