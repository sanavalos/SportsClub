package com.example.sportsclub.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsclub.R

class PaymentNoSocioAdapter(private val pagos: List<Pair<String, String>>) :
    RecyclerView.Adapter<PaymentNoSocioAdapter.PaymentViewHolder>() {

    inner class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val actividad: TextView = itemView.findViewById(R.id.textActividad)
        val fecha: TextView = itemView.findViewById(R.id.textFecha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment_no_socio, parent, false)
        return PaymentViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val (act, fec) = pagos[position]

        holder.actividad.text = act
        holder.fecha.text = fec
    }
    override fun getItemCount(): Int = pagos.size
}