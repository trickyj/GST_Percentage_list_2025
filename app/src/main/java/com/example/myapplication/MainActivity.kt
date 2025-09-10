package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import org.json.JSONArray
import java.io.IOException

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
data class GstItem(val name: String, val category: String, val gstRate: Int)

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GstAdapter
    private val gstItems = mutableListOf<GstItem>()
    private val filteredItems = mutableListOf<GstItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = GstAdapter(filteredItems)
        recyclerView.adapter = adapter

        loadGstData()

        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterItems(newText.orEmpty())
                return true
            }
        })
    }

    private fun loadGstData() {
        try {
            val jsonString = assets.open("gst_data.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            val electronicsItems = mutableListOf<GstItem>()
            val foodItems = mutableListOf<GstItem>()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val item = GstItem(
                    name = jsonObject.getString("name"),
                    category = jsonObject.getString("category"),
                    gstRate = jsonObject.getInt("gstRate")
                )
                when (item.category) {
                    "Electronics" -> electronicsItems.add(item)
                    "Food" -> foodItems.add(item)
                }
            }
            // Add a single aggregated Electronics tile if there are electronics items
            if (electronicsItems.isNotEmpty()) {
                gstItems.add(GstItem(name = "Electronics", category = "Electronics", gstRate = electronicsItems[0].gstRate))
            }
            // Add a single aggregated Food tile if there are food items
            if (foodItems.isNotEmpty()) {
                gstItems.add(GstItem(name = "Food", category = "Food", gstRate = foodItems[0].gstRate))
            }
            // Add other non-electronics items
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val item = GstItem(
                    name = jsonObject.getString("name"),
                    category = jsonObject.getString("category"),
                    gstRate = jsonObject.getInt("gstRate")
                )
                if (item.category != "Electronics") {
                    gstItems.add(item)
                }
            }
            filteredItems.clear()
            filteredItems.addAll(gstItems)
            adapter.notifyDataSetChanged()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun filterItems(query: String) {
        filteredItems.clear()
        if (query.isEmpty()) {
            filteredItems.addAll(gstItems)
        } else {
            filteredItems.addAll(gstItems.filter {
                it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true)
            })
        }
        adapter.notifyDataSetChanged()
    }
}


class GstAdapter(private val items: List<GstItem>) : RecyclerView.Adapter<GstAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.itemName)
        val categoryTextView: TextView = itemView.findViewById(R.id.itemCategory)
        val gstRateTextView: TextView = itemView.findViewById(R.id.itemGstRate)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gst, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.nameTextView.text = item.name
        holder.categoryTextView.text = item.category
        holder.gstRateTextView.text = "${item.gstRate}%"
        holder.cardView.setOnClickListener {
            when (item.name) {
                "Electronics" -> {
                    val intent = Intent(holder.itemView.context, ElectronicsActivity::class.java)
                    holder.itemView.context.startActivity(intent)
                }
                "Food" -> {
                    val intent = Intent(holder.itemView.context, FoodActivity::class.java)
                    holder.itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size
}