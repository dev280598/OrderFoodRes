package com.example.oderfoodapp.model

class Order {
    var iD = 0
    var productId: String? = null
    var productName: String? = null
    var quantity: String? = null
    var price: String? = null
    var discount: String? = null
    var image: String? = null

    constructor() {}
    constructor(
        productId: String?,
        productName: String?,
        quantity: String?,
        price: String?,
        discount: String?,
        image: String?
    ) {
        this.productId = productId
        this.productName = productName
        this.quantity = quantity
        this.price = price
        this.discount = discount
        this.image = image
    }

    constructor(
        ID: Int,
        productId: String?,
        productName: String?,
        quantity: String?,
        price: String?,
        discount: String?,
        image: String?
    ) {
        iD = ID
        this.productId = productId
        this.productName = productName
        this.quantity = quantity
        this.price = price
        this.discount = discount
        this.image = image
    }
}