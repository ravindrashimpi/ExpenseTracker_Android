package com.deere.exptracker.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    @JvmStatic
    fun toSimpleString(date: Date) : String {
        val format = SimpleDateFormat("EEEE, dd MMM yyyy")
        return format.format(date)
    }
}