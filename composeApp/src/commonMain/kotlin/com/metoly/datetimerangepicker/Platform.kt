package com.metoly.datetimerangepicker

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform