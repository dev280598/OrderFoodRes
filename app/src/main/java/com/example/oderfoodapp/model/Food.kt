package com.example.oderfoodapp.model

class Food {
    var name: String? = null
    var image: String? = null
    var description: String? = null
    var price: String? = null
    var discount: String? = null
    var menuId: String? = null

    constructor() {}
    constructor(
        name: String?,
        image: String?,
        description: String?,
        price: String?,
        discount: String?,
        menuId: String?
    ) {
        this.name = name
        this.image = image
        this.description = description
        this.price = price
        this.discount = discount
        this.menuId = menuId
    }
}