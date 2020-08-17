package com.deere.exptracker.expense

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.deere.exptracker.R
import com.deere.exptracker.entity.ExpenseCategoryEntity
import com.deere.exptracker.entity.ExpenseEntity
import com.deere.exptracker.util.DateUtils


class ExpenseListAdapter(val expList: MutableList<ExpenseCategoryEntity>, val resources: Resources, deleteListner: OnItemSwipeListner) :
    RecyclerView.Adapter<ExpenseListAdapter.ExpenseViewHolder>(), Filterable {
    var TAG = "ExpenseListAdapter"
    var count: Int = 0
    var mListner: OnItemSwipeListner = deleteListner
    var expListAll: MutableList<ExpenseCategoryEntity>

    init {
        expListAll = expList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        Log.d(TAG, "onCreateViewHolder started")
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_expense, parent, false) as LinearLayout

        return ExpenseViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount ${expList.size}")
        return expList.size
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expList[position]
        Log.d(TAG, "ExpCatEntity: ${expense.catImage}")
        holder.categoryName.text = expense.catName.toString()
        holder.expenseAmt.text = expense.expAmount.toString()
        holder.expenseDate.text = expense.expDate
        holder.expenseNote.text = expense.expNote.toString()
        holder.categoryImg.setImageResource(when (expense.catImage) {
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

        //Set the background of image
        if(count > 6) {
            count = 0
            holder.categoryImg.setBackgroundResource(getGradient(count))
            count += 1
        } else {
            holder.categoryImg.setBackgroundResource(getGradient(count))
            count += 1
        }

        holder.cardView.setOnClickListener {
            Log.d(TAG, "OnDragListner: ${expList[position]}")
            mListner.deleteItemOnSwipe(expList[position])
            notifyDataSetChanged()
        }

        holder.editExpImg.setOnClickListener {
            mListner.getUpdateExpenseFragmentView(true, expList[position])
        }
    }

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryImg: ImageView = itemView.findViewById(R.id.categoryImg)
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        val expenseAmt: TextView = itemView.findViewById(R.id.expenseAmt)
        val expenseDate: TextView = itemView.findViewById(R.id.expDate)
        val expenseNote: TextView = itemView.findViewById(R.id.expNote)
        var cardView: CardView = itemView.findViewById(R.id.expLstCardView)
        val editExpImg: ImageView = itemView.findViewById(R.id.editExpenseImg)

        init {
            itemView.setOnClickListener{ v: View ->
                //val position: Int = adapterPosition
                val navController = Navigation.findNavController(v)
                //navController.navigate(R.id.action_expenseList_to_expenseFragment)
            }
        }
    }

    interface OnItemSwipeListner {
        fun deleteItemOnSwipe(expenseCategoryEntity: ExpenseCategoryEntity)
        fun getUpdateExpenseFragmentView(updateFlag: Boolean, expenseCategoryEntity: ExpenseCategoryEntity)
    }

    /**
     * Method used to get the ExpenseCategoryEntity at specific position
     */
    public fun getExpenseCategoryEntity(pos: Int) : ExpenseCategoryEntity {
        return expList.get(pos)
    }



    /**
     * Method used to get the gradient atlease once
     */
    private fun getGradient(count: Int): Int {
        return when(count) {
           0 -> R.drawable.custom_circle_gradient_1
           1 -> R.drawable.custom_circle_gradient_2
           2 -> R.drawable.custom_circle_gradient_3
           3 -> R.drawable.custom_circle_gradient_4
           4 -> R.drawable.custom_circle_gradient_5
           5 -> R.drawable.custom_circle_gradient_6
            else -> R.drawable.ic_no_image
        }

    }

    override fun getFilter(): Filter {
        return object: Filter() {
            //Runs on background thread
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                var filteredExpense = ArrayList<ExpenseCategoryEntity>()

                if(charSequence.toString().isEmpty()) {
                    filteredExpense.addAll(expListAll)
                } else {
                    for(expCat in expListAll) {
                        if(expCat.catName.toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filteredExpense.add(expCat)
                        }
                    }
                }

                var filterResult = FilterResults()
                filterResult.values = filteredExpense
                return filterResult
            }

            //Runs on UI thread
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                expList.clear()
                expList.addAll(results?.values as Collection<ExpenseCategoryEntity>)
                notifyDataSetChanged()
            }
        }
    }
}