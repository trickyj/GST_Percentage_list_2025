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

class FoodActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FoodAdapter
    private val foodItems = mutableListOf<GstItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)

        recyclerView = findViewById(R.id.foodRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FoodAdapter(foodItems)
        recyclerView.adapter = adapter

        loadFoodData()
    }

    private fun loadFoodData() {
        try {
            val jsonString = assets.open("gst_data.json").bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val category = jsonObject.getString("category")
                val name = jsonObject.getString("name")
                val gstRate = jsonObject.getInt("gstRate")
                if (category == "Food") {
                    foodItems.add(GstItem(name = name, category = category, gstRate = gstRate))
                }
            }
            adapter.notifyDataSetChanged()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

class FoodAdapter(private val items: List<GstItem>) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.foodItemName)
        val gstRateTextView: TextView = itemView.findViewById(R.id.foodItemGstRate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.nameTextView.text = item.name
        holder.gstRateTextView.text = "${item.gstRate}%"
    }

    override fun getItemCount(): Int = items.size
}