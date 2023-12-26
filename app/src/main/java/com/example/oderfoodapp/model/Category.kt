package com.example.oderfoodapp.model

class Category {
    var name: String? = null
    var image: String? = null

    constructor() {}
    constructor(name: String?, image: String?) {
        this.name = name
        this.image = image
    }
}