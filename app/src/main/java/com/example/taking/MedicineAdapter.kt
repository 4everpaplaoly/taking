package com.example.taking

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class MedicineAdapter(
    private val context: Context,
    private val list: List<MedicineEntity>
) : BaseAdapter() {

    override fun getCount(): Int = list.size

    override fun getItem(position: Int): MedicineEntity = list[position]

    override fun getItemId(position: Int): Long = list[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_medicine, parent, false)

        val nameText = view.findViewById<TextView>(R.id.txtMedicineName)
        val symptomText = view.findViewById<TextView>(R.id.txtMedicineSymptom)

        val item = list[position]

        nameText.text = item.name
        symptomText.text = item.symptom

        return view
    }
}
