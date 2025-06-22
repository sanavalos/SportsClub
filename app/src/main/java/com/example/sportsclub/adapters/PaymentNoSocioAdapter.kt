package com.example.sportsclub.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsclub.R
import com.example.sportsclub.models.Pago
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class PaymentNoSocioAdapter(private val pagos: List<Pago>, private val onClick: (Pago) -> Unit) :
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
        val pago = pagos[position]
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "AR"))
        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

        holder.actividad.text = "MONTO: ${currencyFormat.format(pago.monto)}"
        holder.fecha.text = formato.format(pago.fechaPago)

        holder.itemView.setOnClickListener {
            onClick(pago)
        }
    }
    override fun getItemCount(): Int = pagos.size
}