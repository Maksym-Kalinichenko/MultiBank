package com.realtime.domain.model

data class Stock(val name: String, val price: Double = 50 + Math.random() * 100)