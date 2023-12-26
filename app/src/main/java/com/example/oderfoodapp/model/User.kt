package com.example.oderfoodapp.model

class User {
    var name: String? = null
    var password: String? = null
    var phone: String? = null
    var isStaff: String? = null
    var homeAddress: String? = null
    var secureCode: String? = null

    constructor() {}
    constructor(name: String?, password: String?, secureCode: String?) {
        this.name = name
        this.password = password
        isStaff = "false"
        this.secureCode = secureCode
    }
}