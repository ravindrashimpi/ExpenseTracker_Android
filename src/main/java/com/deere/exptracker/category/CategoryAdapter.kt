package com.deere.exptracker.category


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.deere.exptracker.R
import com.deere.exptracker.entity.CategoryEntity


class CategoryAdapter(catList: MutableList<CategoryEntity>, isDelete: Boolean, listner: OnListInteractionListener) :RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    val TAG = "CategoryAdapter"
    var categoryList = catList
    var deleteItem = isDelete
    lateinit var view: LinearLayout
    var mListner: OnListInteractionListener = listner

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        Log.d(TAG, "onCreateViewHolder started")
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.list_categories, parent, false) as LinearLayout

        //Enable the delete button
        if(deleteItem) {
            var delImg = view.findViewById<ImageView>(R.id.deleteCategory)
            delImg.visibility = View.VISIBLE
        }

        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        //Log.d(TAG, "onCreateViewHolder CategorySize : ${categoryList.size}")
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val cat: CategoryEntity = categoryList[position]
        holder.categoryName.text = cat.categoryName
        holder.img.setImageResource(when (cat.categoryImg) {
            "ic_travel" -> R.drawable.ic_travel
            "ic_cloths" -> R.drawable.ic_cloths
            "ic_eating_out" -> R.drawable.ic_eating_out
            "ic_entertainment" -> R.drawable.ic_entertainment
            "ic_fuel" -> R.drawable.ic_fuel
            "ic_general" -> R.drawable.ic_general
            "ic_gift" -> R.drawable.ic_gift
            "ic_holiday" -> R.drawable.ic_holiday
            "ic_kids" -> R.drawable.ic_kids
            "ic_shopping" -> R.drawable.ic_shopping
            "ic_sports" -> R.drawable.ic_sports
            "ic_bills" -> R.drawable.ic_bills
            "ic_drinks" -> R.drawable.ic_drinks
            "ic_mobile" -> R.drawable.ic_mobile
            "ic_phone" -> R.drawable.ic_phone
            "ic_tea" -> R.drawable.ic_tea
            "ic_medical" -> R.drawable.ic_medical
            "ic_wifi" -> R.drawable.ic_wifi
            "ic_television" -> R.drawable.ic_television
            else -> R.drawable.ic_no_image
        })
        holder.delImg.setImageResource(R.drawable.ic_delete)

        //Below is used to remove the item when clicked
        holder.delImg.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "Position: ${position}")
            mListner.onListInteraction(categoryList[position])
            removeItem(categoryList[position])
        })
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val delImg: ImageView = itemView.findViewById(R.id.deleteCategory)
        val img: ImageView = itemView.findViewById(R.id.catImage)
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
    }

    private fun removeItem(category: CategoryEntity) {
        var position = categoryList.indexOf(category)
        categoryList.removeAt(position)
        notifyDataSetChanged()
    }

    interface OnListInteractionListener {
        fun onListInteraction(category: CategoryEntity)
    }

}

