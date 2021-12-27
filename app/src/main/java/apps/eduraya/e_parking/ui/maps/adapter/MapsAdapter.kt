package apps.eduraya.e_parking.ui.maps.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import apps.eduraya.e_parking.data.responses.ListDataPlace
import apps.eduraya.e_parking.data.responses.getplace.ListDataQuotasByPlace
import apps.eduraya.e_parking.databinding.ListItemLocationBinding
import apps.eduraya.e_parking.ui.valet.ValetBookingActivity

class MapsAdapter(private val context: Context) : RecyclerView.Adapter<MapsAdapter.MapsViewHolder>() {
//
//    private val modelResultArrayList = ArrayList<ModelResults>()

    private val placeResultArrayList = ArrayList<ListDataPlace?>()
    private val quotasResultArrayList = ArrayList<ListDataQuotasByPlace?>()

//    fun setLocationAdapter(items: ArrayList<ModelResults>) {
//        modelResultArrayList.clear()
//        modelResultArrayList.addAll(items)
//        notifyDataSetChanged()
//    }

    fun setLocationAdapter(items: ArrayList<ListDataPlace?>?) {
        placeResultArrayList.clear()
        placeResultArrayList.addAll(items!!)
        notifyDataSetChanged()
    }

    fun setQuotasAdapter(items: ArrayList<ListDataQuotasByPlace>?){
        quotasResultArrayList.clear()
        quotasResultArrayList.addAll(items!!)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapsViewHolder {
        val view = ListItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MapsViewHolder(view)
    }

//    override fun onBindViewHolder(holder: MapsViewHolder, position: Int) {
//        val modelResult = modelResultArrayList[position]
//
//        //set rating
//        val newValue = modelResult.rating.toDouble()
//        holder.ratingBar.numStars = 5
//        holder.ratingBar.stepSize = 0.5.toDouble().toFloat()
//        holder.ratingBar.rating = newValue.toFloat()
//        holder.tvNamaJalan.text = modelResult.vicinity
//        holder.tvNamaLokasi.text = modelResult.name
//        holder.tvRating.text = "(" + modelResult.rating + ")"
//
//        //set data to share & intent
//        val strPlaceId = modelResultArrayList[position].placeId
//        val strNamaLokasi = modelResultArrayList[position].name
//        val strNamaJalan = modelResultArrayList[position].vicinity
//        val strLat = modelResultArrayList[position].modelGeometry.modelLocation.lat
//        val strLong = modelResultArrayList[position].modelGeometry.modelLocation.lng
//
//        //send data to another activity
//        holder.linearRute.setOnClickListener {
//            val intent = Intent(context, ValetBookingActivity::class.java)
//            intent.putExtra("placeId", strPlaceId)
//            intent.putExtra("vicinity", strNamaJalan)
//            intent.putExtra("lat", strLat)
//            intent.putExtra("lng", strLong)
//            context.startActivity(intent)
//        }
//
//        //intent to share location
////        holder.imageShare.setOnClickListener {
////            val strUri = "http://maps.google.com/maps?saddr=$strLat,$strLong"
////            val intent = Intent(Intent.ACTION_SEND)
////            intent.type = "text/plain"
////            intent.putExtra(Intent.EXTRA_SUBJECT, strNamaLokasi)
////            intent.putExtra(Intent.EXTRA_TEXT, strUri)
////            context.startActivity(Intent.createChooser(intent, "Bagikan :"))
////        }
//    }

    override fun onBindViewHolder(holder: MapsViewHolder, position: Int) {
        val placeResult = placeResultArrayList[position]
        val quotasResult = quotasResultArrayList[position]

        //set rating
//        val newValue = modelResult.rating.toDouble()
//        holder.ratingBar.numStars = 5
//        holder.ratingBar.stepSize = 0.5.toDouble().toFloat()
//        holder.ratingBar.rating = newValue.toFloat()
        holder.tvNamaJalan.text = placeResult?.address
        holder.tvNamaLokasi.text = placeResult?.name
        holder.tvSpace.text = "Tersedia "+quotasResult?.quotaValet.toString() + " tempat parkir kosong"

        //set data to share & intent
        val strPlaceId = placeResultArrayList[position]?.id
        val strNamaLokasi = placeResultArrayList[position]?.name
        val strNamaJalan = placeResultArrayList[position]?.address
        val strLat = placeResultArrayList[position]?.lat
        val strLong = placeResultArrayList[position]?.lng

        //send data to another activity
        holder.linearRute.setOnClickListener {
            val intent = Intent(context, ValetBookingActivity::class.java)
            intent.putExtra("placeId", strPlaceId)
            intent.putExtra("address", strNamaJalan)
            intent.putExtra("name", strNamaLokasi)
//            intent.putExtra("lng", strLong)
            context.startActivity(intent)
        }

        //intent to share location
//        holder.imageShare.setOnClickListener {
//            val strUri = "http://maps.google.com/maps?saddr=$strLat,$strLong"
//            val intent = Intent(Intent.ACTION_SEND)
//            intent.type = "text/plain"
//            intent.putExtra(Intent.EXTRA_SUBJECT, strNamaLokasi)
//            intent.putExtra(Intent.EXTRA_TEXT, strUri)
//            context.startActivity(Intent.createChooser(intent, "Bagikan :"))
//        }
    }

    override fun getItemCount(): Int {
        return placeResultArrayList.size
    }

    class MapsViewHolder(itemView: ListItemLocationBinding) : RecyclerView.ViewHolder(itemView.root) {
        var linearRute: LinearLayout
        var tvNamaJalan: TextView
        var tvNamaLokasi: TextView
        var tvSpace: TextView
//        var imageShare: ImageView
//        var ratingBar: RatingBar

        init {
            linearRute = itemView.linearRute
            tvNamaJalan = itemView.tvNamaJalan
            tvNamaLokasi = itemView.tvNamaLokasi
//            imageShare = itemView.imageShare
            tvSpace = itemView.tvSpace
        }
    }

}