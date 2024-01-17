package apps.eduraya.e_parking.ui.reservation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import apps.eduraya.e_parking.data.responses.reservations.DataReservations
import apps.eduraya.e_parking.databinding.ListItemReservationBinding

class ReservationAdapter (val items: ArrayList<DataReservations>?):RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder>(){
    private var onItemClickCallback: OnItemClickCallback? = null

    inner class ReservationViewHolder(itemView: ListItemReservationBinding): RecyclerView.ViewHolder(itemView.root) {
        var tvPlace: TextView
        var tvVehicle : TextView
        var tvDate: TextView
        var tvStatus: TextView

        init {
            tvPlace = itemView.tvPlace
            tvVehicle = itemView.tvVehicle
            tvDate = itemView.tvDate
            tvStatus = itemView.status
        }

        fun bind(listReservation: DataReservations){
            itemView.setOnClickListener{
                onItemClickCallback?.onItemClicked(listReservation)
            }
        }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback{
        fun onItemClicked(dataReservation: DataReservations)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val view = ListItemReservationBinding.inflate(LayoutInflater.from(parent.context), parent, false )
        return ReservationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {

        val listReservation= items!![position]

        holder.bind(listReservation)
        holder.tvPlace.text = listReservation.place?.name
        if (listReservation.vehicleId == 1){
            holder.tvVehicle.text = "Sepeda Motor"
        }else if (listReservation.vehicleId == 2){
            holder.tvVehicle.text = "Mobil"
        }
        holder.tvDate.text = listReservation.checkIn
        holder.tvStatus.text = listReservation.status
    }

    override fun getItemCount(): Int = items!!.size
}