package com.deere.exptracker.expense

import android.content.res.ColorStateList
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.deere.exptracker.R
import com.deere.exptracker.entity.CategoryEntity

class ExpenseCategoryAdapter(
    catList: MutableList<CategoryEntity>,
    clickEvent: OnItemClickListner,
    caregoryId: Int
) : RecyclerView.Adapter<ExpenseCategoryAdapter.ExpenseCategoryViewHolder>() {

    val TAG = "ExpenseCategoryAdapter"
    var categoryList = catList
    lateinit var view: CardView
    var prevPosition: Int = -1
    lateinit var preColor: ColorStateList
    var onItemClickListner: OnItemClickListner = clickEvent
    var count: Int = 0
    var selectedCatId = caregoryId

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseCategoryViewHolder {
        Log.d(TAG, "onCreateViewHolder started")
        val layoutInflater = LayoutInflater.from(parent.context)
        view = layoutInflater.inflate(R.layout.expense_category_list, parent, false) as CardView

        return ExpenseCategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: ExpenseCategoryViewHolder, position: Int) {
        val cat: CategoryEntity = categoryList[position]
        holder.categoryName.text = cat.categoryName
        holder.img.setImageResource(
            when (cat.categoryImg) {
                "ic_travel" -> R.drawable.ic_travel_lg
                "ic_cloths" -> R.drawable.ic_cloths_lg
                "ic_eating_out" -> R.drawable.ic_eating_out_lg
                "ic_entertainment" -> R.drawable.ic_entertainment_lg
                "ic_fuel" -> R.drawable.ic_fuel_lg
                "ic_general" -> R.drawable.ic_general_lg
                "ic_gift" -> R.drawable.ic_gift_lg
                "ic_holiday" -> R.drawable.ic_holiday_lg
                "ic_kids" -> R.drawable.ic_kids_lg
                "ic_shopping" -> R.drawable.ic_shopping_lg
                "ic_sports" -> R.drawable.ic_sports_lg
                "ic_bills" -> R.drawable.ic_bills_lg
                "ic_drinks" -> R.drawable.ic_drinks_lg
                "ic_mobile" -> R.drawable.ic_mobile_lg
                "ic_phone" -> R.drawable.ic_phone_lg
                "ic_tea" -> R.drawable.ic_tea
                "ic_medical" -> R.drawable.ic_medical_lg
                "ic_wifi" -> R.drawable.ic_wifi_lg
                "ic_television" -> R.drawable.ic_television_lg
                else -> R.drawable.ic_no_image
            }
        )

        Log.d(TAG, "CategoryIDToBeUpdated: ${selectedCatId} : ${cat.caregoryId}")


        //Used to set the alignment of the categories
        if (prevPosition == position) {
            var layoutParam = holder.cardView.layoutParams as ViewGroup.MarginLayoutParams
            layoutParam.setMargins(15, 114, 15, 15)
            holder.cardView.requestLayout()
            setBackgroundToCardView(holder)
        } else {
            var layoutParam = holder.cardView.layoutParams as ViewGroup.MarginLayoutParams
            layoutParam.setMargins(15, 85, 15, 15)
            holder.cardView.requestLayout()
            setBackgroundToCardView(holder)
        }

        //Make the categorySelected
        if (selectedCatId == cat.caregoryId) {
            var layoutParam = holder.cardView.layoutParams as ViewGroup.MarginLayoutParams
            layoutParam.setMargins(15, 114, 15, 15)
            holder.cardView.requestLayout()
            setBackgroundToCardView(holder)
        }

        holder.cardView.setOnClickListener(View.OnClickListener {
            prevPosition = position
            count = 0
            selectedCatId = -1
            onItemClickListner.onClick(categoryList[position])
            notifyDataSetChanged()
        })
    }

    inner class ExpenseCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.catImage)
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        var cardView: CardView = itemView.findViewById(R.id.expCatCardView)
        var cardViewGradient: ImageView = itemView.findViewById(R.id.cardViewGrdient)
    }

    interface OnItemClickListner {
        fun onClick(category: CategoryEntity)
    }

    /**
     * Setting layout
     */
    private fun setBackgroundToCardView(holder: ExpenseCategoryViewHolder) {
        //Set the background of image
        if (count > 6) {
            count = 0
            holder.cardViewGradient.setBackgroundResource(getRectangleGradient(count))
            holder.img.setBackgroundResource(getCircleGradient(count))
            count += 1
        } else {
            holder.cardViewGradient.setBackgroundResource(getRectangleGradient(count))
            holder.img.setBackgroundResource(getCircleGradient(count))
            count += 1
        }
    }

    /**
     * Method used to get the gradient atlease once
     */
    private fun getRectangleGradient(count: Int): Int {
        return when (count) {
            0 -> R.drawable.custom_rectangle_gradient_1
            1 -> R.drawable.custom_rectangle_gradient_2
            2 -> R.drawable.custom_rectangle_gradient_3
            3 -> R.drawable.custom_rectangle_gradient_4
            4 -> R.drawable.custom_rectangle_gradient_5
            5 -> R.drawable.custom_rectangle_gradient_6
            else -> R.drawable.ic_no_image
        }

    }

    /**
     * Method used to get the gradient atlease once
     */
    private fun getCircleGradient(count: Int): Int {
        return when (count) {
            0 -> R.drawable.custom_circle_gradient_1
            1 -> R.drawable.custom_circle_gradient_2
            2 -> R.drawable.custom_circle_gradient_3
            3 -> R.drawable.custom_circle_gradient_4
            4 -> R.drawable.custom_circle_gradient_5
            5 -> R.drawable.custom_circle_gradient_6
            else -> R.drawable.ic_no_image
        }

    }

}