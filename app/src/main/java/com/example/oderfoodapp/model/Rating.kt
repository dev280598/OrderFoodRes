package com.example.oderfoodapp.model

class Rating {
    var userPhone: String? = null
    var foodId: String? = null
    var rateValue: String? = null
    var comment: String? = null

    constructor() {}
    constructor(userPhone: String?, foodId: String?, rateValue: String?, comment: String?) {
        this.userPhone = userPhone
        this.foodId = foodId
        this.rateValue = rateValue
        this.comment = comment
    }
}