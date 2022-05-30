package com.game.app.utils.date

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

fun getWeekOfDay(date: LocalDate): String{
    return when(date.dayOfWeek){
        DayOfWeek.MONDAY -> {
            "пн"
        }
        DayOfWeek.TUESDAY -> {
            "вт"
        }
        DayOfWeek.WEDNESDAY -> {
            "ср"
        }
        DayOfWeek.THURSDAY -> {
            "чт"
        }
        DayOfWeek.FRIDAY -> {
            "пт"
        }
        DayOfWeek.SATURDAY -> {
            "сб"
        }
        DayOfWeek.SUNDAY -> {
            "вс"
        }
        else -> {
            "пн"
        }
    }
}

fun getMonthOfYear(date: LocalDate): String{
    return when(date.month){
        Month.JANUARY -> {
            "Январь"
        }
        Month.FEBRUARY -> {
            "Февраль"
        }
        Month.MARCH -> {
            "Март"
        }
        Month.APRIL -> {
            "Апрель"
        }
        Month.MAY -> {
            "Май"
        }
        Month.JUNE -> {
            "Июнь"
        }
        Month.JULY -> {
            "Июль"
        }
        Month.AUGUST -> {
            "Август"
        }
        Month.SEPTEMBER -> {
            "Сентябрь"
        }
        Month.OCTOBER -> {
            "Октябрь"
        }
        Month.NOVEMBER -> {
            "Ноябрь"
        }
        Month.DECEMBER -> {
            "Декабрь"
        }
        else -> {
            "Январь"
        }
    }
}