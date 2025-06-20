package com.example.sportsclub.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sportsclub.R
import com.example.sportsclub.models.DatosSocio

class ListarVencimientosAdapter(
    private val socios: List<DatosSocio>
) : RecyclerView.Adapter<ListarVencimientosAdapter.UsuarioViewHolder>() {

    inner class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagePerfil: ImageView = itemView.findViewById(R.id.imagenPerfil)
        val textNombre: TextView = itemView.findViewById(R.id.textNombre)
        val textDocumento: TextView = itemView.findViewById(R.id.textDocumento)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lista_vencimiento, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val socio = socios[position]
        holder.textNombre.text = "${socio.nombre} ${socio.apellido}"
        holder.textDocumento.text = formatearDNI(socio.documento)

        if (socio.imagenCarnet != null) {
            val bitmap = BitmapFactory.decodeByteArray(socio.imagenCarnet, 0, socio.imagenCarnet.size)
            holder.imagePerfil.setImageBitmap(bitmap)
        } else {
            holder.imagePerfil.setImageResource(R.drawable.perfil_image)
        }
    }

    private fun formatearDNI(dni: String): String {
        return dni.reversed()
            .chunked(3)
            .joinToString(".")
            .reversed()
    }

    override fun getItemCount(): Int = socios.size
}