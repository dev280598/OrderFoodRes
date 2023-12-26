package com.example.oderfoodapp.model

class Request {
    var phone: String? = null
    var name: String? = null
    var address: String? = null
    var total: String? = null
    var status: String? = null
    var foods: List<Order>? = null

    constructor() {}
    constructor(
        phone: String?,
        name: String?,
        address: String?,
        total: String?,
        foods: List<Order>?
    ) {
        this.phone = phone
        this.name = name
        this.address = address
        this.total = total
        this.foods = foods
        status = "0"
    }
}