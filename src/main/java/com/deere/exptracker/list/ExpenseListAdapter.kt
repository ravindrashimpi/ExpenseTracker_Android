package com.deere.exptracker.list

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.deere.exptracker.R
import com.deere.exptracker.expense.Expense
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ExpenseListAdapter(val expList: List<Expense>) : RecyclerView.Adapter<ExpenseListAdapter.ExpenseViewHolder>() {
    var TAG = "ExpenseListAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        Log.d(TAG, "onCreateViewHolder started")
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_expense, parent, false) as ConstraintLayout
        return ExpenseViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount ${expList.size}")
        return expList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expList[position]
        Log.d(TAG, "onBindViewHolder: ${expense.note}")
        holder.expNote.text = expense.note
        holder.expDate.text = dateFmtConverter(expense.expDate)
        holder.expAmt.text = expense.amount.toString()
        holder.catImg.setImageResource(when (expense.categoryId) {
            "1" -> R.drawable.ic_cloths
            "2" -> R.drawable.ic_eating_out
            "3" -> R.drawable.ic_entertainment
            "4" -> R.drawable.ic_fuel
            "5" -> R.drawable.ic_general
            "6" -> R.drawable.ic_gift
            "7" -> R.drawable.ic_holiday
            "8" -> R.drawable.ic_kids
            "9" -> R.drawable.ic_shopping
            "10" -> R.drawable.ic_sports
            "11" -> R.drawable.ic_travel
            else -> R.drawable.ic_no_image
        })
    }

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val expNote: TextView = itemView.findViewById(R.id.expNote)
        val expDate: TextView = itemView.findViewById(R.id.expDate)
        val expAmt: TextView = itemView.findViewById(R.id.expAmount)
        val catImg: ImageView = itemView.findViewById(R.id.categoryImg)

        init {
            itemView.setOnClickListener{ v: View ->
                //val position: Int = adapterPosition
                val navController = Navigation.findNavController(v)
                //navController.navigate(R.id.action_expenseList_to_expenseFragment)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun dateFmtConverter(expDate: String): String {
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH)
        val date = LocalDate.parse(expDate, formatter)
        val sdf = DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy")
        return sdf.format(date)
    }


}