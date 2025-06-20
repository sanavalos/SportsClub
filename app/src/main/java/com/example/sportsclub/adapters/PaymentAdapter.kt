package com.example.sportsclub.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsclub.R
import com.example.sportsclub.models.Pago
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

class PaymentAdapter(
    private val pagos: List<Pair<Pago, Date>>,
    private val pagoDinamico: Pair<Pago, Date>?,
    private val estadoPagoDinamico: Int?,
    private val onClick: (Pago, Int) -> Unit
): RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>() {
    private var pagoSeleccionado: Pago? = null

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
        val isDinamico = (pagoDinamico != null && position == 0)
        val (pago, fechaVisual) = when {
            pagoDinamico != null && position == 0 -> pagoDinamico
            pagoDinamico != null -> pagos[position - 1]
            else -> pagos[position]
        }
        val estadoPago = if (isDinamico) estadoPagoDinamico!! else -1

        val mes = SimpleDateFormat("MMMM yyyy", Locale("es", "ES")).format(fechaVisual)
        holder.mes.text = mes

        holder.estado.text = when (estadoPago) {
            1 -> "Primera cuota"
            2 -> "Vencida"
            3 -> "Por vencer"
            4 -> "Pagar Cuota"
            else -> "Pagado"
        }

        holder.check.visibility = if (isDinamico) View.VISIBLE else View.GONE

        if (isDinamico) {
            val (bgRes, iconRes) = when (estadoPago) {
                1, 4 ->  R.drawable.background_circle_blue to R.drawable.icon_pay_blue
                3 -> R.drawable.background_circle_yellow to R.drawable.icon_warning_yellow
                2 -> R.drawable.background_circle_red to R.drawable.icon_warning_red
                else -> R.drawable.background_circle_green to R.drawable.icon_check_green
            }
            holder.backgroundImage.setImageResource(bgRes)
            holder.icon.setImageResource(iconRes)
        } else {
            holder.backgroundImage.setImageResource(R.drawable.background_circle_green)
            holder.icon.setImageResource(R.drawable.icon_check_green)
        }

        holder.itemView.setOnClickListener {
            onClick(pago, estadoPago)
        }

        holder.check.setOnCheckedChangeListener(null)
        holder.check.isChecked = (pagoSeleccionado == pago)

        holder.check.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                pagoSeleccionado = pago
                notifyDataSetChanged()
            } else if (pagoSeleccionado == pago) {
                pagoSeleccionado = null
                notifyDataSetChanged()
            }
        }
    }
    fun obtenerPagoSeleccionado(): Pair<Pago, Int>? {
        return if (pagoSeleccionado != null && pagoDinamico != null && pagoSeleccionado == pagoDinamico.first) {
            Pair(pagoSeleccionado!!, estadoPagoDinamico!!)
        } else {
            null
        }
    }

    override fun getItemCount(): Int {
        return pagos.size + if (pagoDinamico != null) 1 else 0
    }
}
