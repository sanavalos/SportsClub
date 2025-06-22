package com.example.sportsclub.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.CheckedTextView
import android.widget.TextView
import com.example.sportsclub.R
import com.example.sportsclub.ui.ActividadesListaActivity

class ActividadesExpandableAdapter(
    private val context: Context,
    private val data: Map<String, List<ActividadesListaActivity.ActividadUIItem>>,
    private val selectedIds: MutableSet<Int>
) : BaseExpandableListAdapter() {

    private val groupList = data.keys.toList()

    override fun getGroupCount() = groupList.size

    override fun getChildrenCount(groupPosition: Int): Int {
        return data[groupList[groupPosition]]?.size ?: 0
    }

    override fun getGroup(groupPosition: Int): Any = groupList[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return data[groupList[groupPosition]]?.get(childPosition)
            ?: throw IllegalStateException("Actividad no encontrada")
    }

    override fun getGroupId(groupPosition: Int) = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int) = childPosition.toLong()

    override fun hasStableIds() = false

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(android.R.layout.simple_expandable_list_item_1, parent, false)
        val text = view.findViewById<TextView>(android.R.id.text1)
        text.text = getGroup(groupPosition) as String
        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_actividad_check, parent, false)

        val checkView = view.findViewById<CheckedTextView>(R.id.checkedTextView)
        val actividad = getChild(groupPosition, childPosition) as ActividadesListaActivity.ActividadUIItem

        // Seteo del texto
        checkView.text = actividad.fechaHora

        // 🔁 Seteo explícito del estado desde la fuente de verdad
        val isSelected = selectedIds.contains(actividad.actividadProgramadaId)
        checkView.isChecked = isSelected
        checkView.setCheckMarkDrawable(
            if (isSelected) android.R.drawable.checkbox_on_background
            else android.R.drawable.checkbox_off_background
        )

        // 🎯 Click listener para cambiar estado
        checkView.setOnClickListener {
            if (selectedIds.contains(actividad.actividadProgramadaId)) {
                selectedIds.remove(actividad.actividadProgramadaId)
            } else {
                selectedIds.add(actividad.actividadProgramadaId)
            }

            // Actualizá solo esta vista (esto es suficiente)
            checkView.isChecked = selectedIds.contains(actividad.actividadProgramadaId)
            checkView.setCheckMarkDrawable(
                if (checkView.isChecked) android.R.drawable.checkbox_on_background
                else android.R.drawable.checkbox_off_background
            )
        }

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true
}
