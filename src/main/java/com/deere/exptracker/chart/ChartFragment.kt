package com.deere.exptracker.chart

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.deere.exptracker.DAO.ExpenseDAO
import com.deere.exptracker.DAO.IncomeDAO
import com.deere.exptracker.databinding.FragmentChartBinding
import com.deere.exptracker.entity.IncomeEntity
import com.deere.exptracker.entity.UserEntity
import com.deere.exptracker.model.ExpenseViewModel
import com.deere.exptracker.model.ExpenseViewModelFactory
import com.deere.exptracker.model.IncomeViewModel
import com.deere.exptracker.model.IncomeViewModelFactory
import com.deere.exptracker.repository.ExpenseRepository
import com.deere.exptracker.repository.IncomeRepository
import com.deere.exptracker.util.ExpenseTrackerDB
import com.deere.exptracker.util.SessionManagement
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ChartFragment : Fragment() {

    lateinit var binding: FragmentChartBinding
    var TAG = "ChartFragment"

    //Below variable is used for PieChart
    lateinit var piChart: PieChart
    lateinit var barChart: BarChart
    lateinit var sessionManagement: SessionManagement
    lateinit var userEntity: UserEntity
    lateinit var incomeViewModel: IncomeViewModel
    lateinit var expenseViewModel: ExpenseViewModel
    var barEntries: ArrayList<BarEntry>? = null

    val BAR_COLOR = intArrayOf(
        Color.parseColor("#63459E"),
        Color.parseColor("#AA4838"),
        Color.parseColor("#009CA7"),
        Color.parseColor("#4B88FF"),
        Color.parseColor("#BD4787"),
        Color.parseColor("#F2A335")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        binding = FragmentChartBinding.inflate(inflater, container, false)

        //Inisitalize Session Management Object
        val application = requireNotNull(this.activity).application
        sessionManagement = SessionManagement(application.applicationContext)
        userEntity = sessionManagement.getSession()!!

        //initialize Model and DB
        initializeModel()

        var chartDateViewFormat = SimpleDateFormat("MMM-yyyy");
        binding.chartDateView.setText("Expense for " + chartDateViewFormat.format(Date()))

        //Read the income and expense till date for the user
        CoroutineScope(Dispatchers.IO).launch {
            var incomeDetails = readIncomeForCurrentMonth()
            var expenseDetails = readExpenseForCurrentMonth()
            var remainingBudget = if(incomeDetails != null) incomeDetails.incomeAmt - expenseDetails else 0
            withContext(Dispatchers.Main) {
                if(incomeDetails != null) {
                    binding.totalIncome.setText(incomeDetails.incomeAmt.toString())
                }

                if(expenseDetails != null) {
                    binding.totalExpense.setText(remainingBudget.toString())
                }
            }
        }

        //Get yesterday and todays expense
        CoroutineScope(Dispatchers.IO).launch {
            getYesterdayTodaysExpesne()
        }

        //Initialize PieChart
        //piChart = binding.piChart
        //Build Pie Chart
        //buildPieChart()


        //Setting Data

        CoroutineScope(Dispatchers.IO).launch {
            barEntries = getDailyExpense()
            Log.d(TAG, "BAR Entries: ${barEntries}")
            withContext(Dispatchers.IO) {
                displayBarGraph()
            }
        }




        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    /**
     * Method used to display Bar graph
     */
    private fun displayBarGraph() {
        Log.d(TAG, "DISPLAYING of Chart Start")
        barChart = binding.piChart
        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)
        barChart.setMaxVisibleValueCount(50)
        barChart.setPinchZoom(false)
        barChart.setDrawGridBackground(false)
        barChart.setFitBars(true);

        //Hide Legend
        var legend = barChart.legend
        legend.isEnabled = false

        //Disable Description
        var desc = barChart.description
        desc.text = ""
        barChart.description = desc

        //Hide Background grid
        var grid2 = barChart.xAxis
        grid2.setDrawGridLines(false)

        //Disable left and Right axis
        var yAxis1 = barChart.axisRight
        yAxis1.isEnabled = false
        var yAxis2 = barChart.axisLeft
        yAxis2.isEnabled = false

//        barEntries = ArrayList<BarEntry>()
//        barEntries!!.add(BarEntry(1f, 140f))
//        barEntries!!.add(BarEntry(2f, 45f))
//        barEntries!!.add(BarEntry(3f, 30f))
//        barEntries!!.add(BarEntry(4f, 38f))
//        barEntries!!.add(BarEntry(5f, 20f))
//        barEntries!!.add(BarEntry(6f, 78f))
//        barEntries!!.add(BarEntry(7f, 58f))


        var barDataSet = BarDataSet(barEntries, "DailyExpense")
        barDataSet.valueTextSize = 8f


        //Setting color to Bars
        barDataSet.setColors(BAR_COLOR.toList())
        //barDataSet.setColor(Color.parseColor("#ABACAE"))

        var data = BarData(barDataSet)
        data.barWidth = 0.2f

        barChart.data = data
        barChart.invalidate()

        //Set the xAxis label
        var days = getLastSevenDays()
        var xAxis = barChart.xAxis
        xAxis.setValueFormatter(MyXAxisValueFormatter(days))
        xAxis.position = XAxis.XAxisPosition.BOTTOM
    }

    class MyXAxisValueFormatter(values: List<String>) : IAxisValueFormatter {
        var TAG1 = "MyXAxisValueFormatter"
        var mValues = values


        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
            return mValues[(value - 1).toInt()]
        }
    }

    /**
     * Method used to initialize the Model for Database
     */
    private fun initializeModel() {
        //Create a DAO Instance
        val application = requireNotNull(this.activity).application

        //Initialize IncomeDAO
        val incomeDao: IncomeDAO = ExpenseTrackerDB.getInstance(application).incomeDao
        val incomeRepository: IncomeRepository = IncomeRepository(incomeDao)
        val incomeViewModelFactory: IncomeViewModelFactory =
            IncomeViewModelFactory(incomeRepository)
        incomeViewModel =
            ViewModelProviders.of(this, incomeViewModelFactory).get(IncomeViewModel::class.java)

        //Initialize ExpenseDAO
        val expenseDao: ExpenseDAO = ExpenseTrackerDB.getInstance(application).expenseDao
        val expenseRepository: ExpenseRepository = ExpenseRepository(expenseDao)
        val expenseViewModelFactory: ExpenseViewModelFactory =
            ExpenseViewModelFactory(expenseRepository)
        expenseViewModel =
            ViewModelProviders.of(this, expenseViewModelFactory).get(ExpenseViewModel::class.java)
    }

    /**
     * Method used to read the income for the user for current month
     */
    private suspend fun readIncomeForCurrentMonth(): IncomeEntity {
        var sdf = SimpleDateFormat("yyyy/MM")
        return incomeViewModel.checkForIncomeForCurrentMonth(userEntity.userId, sdf.format(Date()))
    }

    /**
     * Method used to read the expense for the user for current month
     */
    private suspend fun readExpenseForCurrentMonth(): Double {
        var sdf = SimpleDateFormat("yyyy/MM")
        return expenseViewModel.getExpenseForMonth(userEntity.userId, sdf.format(Date()))
        //return incomeViewModel.get(userEntity.userId, sdf.format(Date()))
    }

    /**
     * Method used to get the last 7 days from current day
     */
    private fun getLastSevenDays(): List<String> {
        var lastSevenDays = mutableListOf<String>()

        var sdf = SimpleDateFormat("MM/dd")
        var cal = Calendar.getInstance()
        //Get Starting date
        cal.add(Calendar.DAY_OF_YEAR, -7)

        //Loop adding one day in each iteration
        for (i in 0 until 7) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
            lastSevenDays.add(sdf.format(cal.time))
        }

        return lastSevenDays
    }

    /**
     * Method used to get expense based on date
     */
    suspend private fun getDailyExpense(): ArrayList<BarEntry> {
        var barEntries = ArrayList<BarEntry>()

        var sdf = SimpleDateFormat("MM/dd")
        var cal = Calendar.getInstance()
        //Get Starting date
        cal.add(Calendar.DAY_OF_YEAR, -7)
        //Loop adding one day in each iteration
        for (i in 0 until 7) {
            var indx = i.toFloat() + 1
            cal.add(Calendar.DAY_OF_YEAR, 1)
            barEntries.add(
                BarEntry(
                    indx,
                    getExpense(userEntity.userId, sdf.format(cal.time)).toFloat()
                )
            )
        }
        return barEntries
    }

    /**
     * Method used to get the yesterday and todays expense
     */
    suspend private fun getYesterdayTodaysExpesne() {
        //Get yesterday and todays expense
        var sdf = SimpleDateFormat("MM/dd")
        var cal = Calendar.getInstance()
        //Get Starting date
        cal.add(Calendar.DAY_OF_YEAR, -1)
        //Loop adding one day in each iteration
        for (i in 0 until 2) {
            var exp = getExpense(userEntity.userId, sdf.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, 1)
            if(i == 0) {
                withContext(Dispatchers.Main) {
                    Log.d(TAG, "YESTERDAY EXPENSE: ${exp}")
                    setYesterdayExpense(exp)
                }
            } else {
                withContext(Dispatchers.Main) {
                    Log.d(TAG, "TODAYS EXPENSE: ${exp}")
                    setTodayExpense(exp)
                }
            }
        }
    }

    /**
     * Method used to set the yesterday expense
     */
    private fun setYesterdayExpense(exp: Double) {
        binding.yesterdayExpense.setText(exp.toString())
    }

    /**
     * Method used to set the todays expense
     */
    private fun setTodayExpense(exp: Double) {
        binding.todayExpense.setText(exp.toString())
    }

    /**
     * Method used to get daily expense
     */
    private suspend fun getExpense(pUserID: Int, pExpDate: String): Double {
        var dailyExpesne = expenseViewModel.getDailyExpense(pUserID, pExpDate)
        if (dailyExpesne > 0) {
            return dailyExpesne
        } else {
            return 0.0
        }
    }


}
