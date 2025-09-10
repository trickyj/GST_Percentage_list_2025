package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import java.io.IOException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class ElectronicsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ElectronicsAdapter
    private val electronicsItems = mutableListOf<GstItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_electronics)

        recyclerView = findViewById(R.id.electronicsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ElectronicsAdapter(electronicsItems)
        recyclerView.adapter = adapter

        loadElectronicsData()
    }

    private fun loadElectronicsData() {
        try {
            val jsonString = assets.open("gst_data.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val category = jsonObject.getString("category")
                val name = jsonObject.getString("name")
                val gstRate = jsonObject.getInt("gstRate")
                if (category == "Electronics") {
                    electronicsItems.add(GstItem(name = name, category = category, gstRate = gstRate))
                }
            }
            adapter.notifyDataSetChanged()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

class ElectronicsAdapter(private val items: List<GstItem>) : RecyclerView.Adapter<ElectronicsAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.electronicsItemName)
        val gstRateTextView: TextView = itemView.findViewById(R.id.electronicsItemGstRate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_electronics, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.nameTextView.text = item.name
        holder.gstRateTextView.text = "${item.gstRate}%"
    }

    override fun getItemCount(): Int = items.size
}