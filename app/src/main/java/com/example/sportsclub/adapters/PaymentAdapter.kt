package com.example.sportsclub.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsclub.R

class PaymentAdapter(private val pagos: List<Pair<String, Boolean>>) :
    RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>() {

    inner class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val estado: TextView = itemView.findViewById(R.id.textEstado)
        val mes: TextView = itemView.findViewById(R.id.textMes)
        val check: CheckBox = itemView.findViewById(R.id.checkBox)
        val backgroundImage: ImageView = itemView.findViewById(R.id.imageViewBg)
        val icon: ImageView = itemView.findViewById(R.id.iconEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment, parent, false)
        return PaymentViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val (fecha, pagado) = pagos[position]

        holder.mes.text = fecha
        holder.estado.text = if (pagado) "Pagado" else "Vencido"
        holder.check.visibility = if (!pagado) View.VISIBLE else View.GONE

        val bgRes = if (pagado) R.drawable.background_circle_green else R.drawable.background_circle_red
        holder.backgroundImage.setImageResource(bgRes)
        val iconRes = if (pagado) R.drawable.icon_check_green else R.drawable.icon_warning_red
        holder.icon.setImageResource(iconRes)
    }

    override fun getItemCount(): Int = pagos.size
}
