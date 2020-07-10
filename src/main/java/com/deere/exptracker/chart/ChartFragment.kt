package com.deere.exptracker.chart

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.deere.exptracker.databinding.FragmentChartBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class ChartFragment : Fragment() {

    lateinit var binding: FragmentChartBinding

    //Below variable is used for PieChart
    lateinit var piChart: PieChart

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = FragmentChartBinding.inflate(inflater, container, false)

        //Initialize PieChart
        piChart = binding.piChart
        //Build Pie Chart
        buildPieChart()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun buildPieChart() {
        piChart.setUsePercentValues(true)
        piChart.description.isEnabled = false
        piChart.setExtraOffsets(5f, 10f, 5f, 5f)
        piChart.dragDecelerationFrictionCoef = 0.95f
        piChart.isDrawHoleEnabled = true
        piChart.setHoleColor(Color.WHITE)
        piChart.transparentCircleRadius = 61f
        piChart.setEntryLabelTextSize(12f)
        //piChart.animateY(1000, Easing.EasingOption.EaseInOutCubic)

        //Set Description
        var desc: Description = Description()
        desc.text = "User Expense Tracker"
        desc.textSize = 12f
        desc.textColor = Color.rgb(232, 217, 174)
        piChart.description = desc

        val y_piChartData = mutableListOf<PieEntry>()
        y_piChartData.add(PieEntry(34f, "Hotel"))
        y_piChartData.add(PieEntry(23f, "Milk"))
        y_piChartData.add(PieEntry(37f, "General"))
        y_piChartData.add(PieEntry(50f, "Gas"))
        y_piChartData.add(PieEntry(44f, "Vegetables"))
        y_piChartData.add(PieEntry(10f, "Medical"))
        y_piChartData.add(PieEntry(44f, "Fruits"))
        y_piChartData.add(PieEntry(50f, "Gas"))
        y_piChartData.add(PieEntry(44f, "Vegetables"))

        val piDataSet: PieDataSet = PieDataSet(y_piChartData, "Categories")
        piDataSet.sliceSpace = 3f
        piDataSet.selectionShift = 5f
        val MY_COLORS = intArrayOf(
            Color.rgb(192, 0, 0),
            Color.rgb(255, 0, 0),
            Color.rgb(255, 192, 0),
            Color.rgb(127, 127, 127),
            Color.rgb(146, 208, 80),
            Color.rgb(225, 134, 0),
            Color.rgb(227, 145, 127),
            Color.rgb(186, 208, 80),
            Color.rgb(255, 192, 0),
            Color.rgb(127, 127, 127)

        )
        val colors = ArrayList<Int>()
        for(item in MY_COLORS)
            colors.add(item)
        piDataSet.setColors(colors)

        val pidata: PieData = PieData(piDataSet)
        pidata.setValueTextSize(10f)
        pidata.setValueTextColor(Color.YELLOW)

        var legend: Legend = piChart.legend
        legend.textSize = 10f
        legend.textColor = Color.rgb(232, 217, 174)
        legend.orientation = Legend.LegendOrientation.VERTICAL

        piChart.data = pidata
    }

}
