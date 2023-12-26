package com.example.oderfoodapp.data.local

import android.content.Context
import android.database.sqlite.SQLiteQueryBuilder
import com.example.oderfoodapp.model.Order
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper

class Database(context: Context?) : SQLiteAssetHelper(context, DB_NAME, null, DB_VER) {
    val carts: List<Order>
        get() {
            val db = readableDatabase
            val qb = SQLiteQueryBuilder()
            val sqlselect =
                arrayOf("ID", "ProductName", "ProductId", "Quantity", "Price", "Discount", "Image")
            val sqlTable = "OrderDetail"
            qb.tables = sqlTable
            val c = qb.query(db, sqlselect, null, null, null, null, null)
            val result: MutableList<Order> = ArrayList()
            if (c.moveToFirst()) {
                do {
                    result.add(
                        Order(
                            c.getInt(c.getColumnIndex("ID")),
                            c.getString(c.getColumnIndex("ProductId")),
                            c.getString(c.getColumnIndex("ProductName")),
                            c.getString(c.getColumnIndex("Quantity")),
                            c.getString(c.getColumnIndex("Price")),
                            c.getString(c.getColumnIndex("Discount")),
                            c.getString(c.getColumnIndex("Image"))
                        )
                    )
                } while (c.moveToNext())
            }
            return result
        }

    fun addToCart(order: Order) {
        val db = readableDatabase
        val query = String.format(
            "INSERT INTO OrderDetail(ProductId,ProductName,Quantity,Price,Discount,Image) VALUES('%s','%s', '%s','%s','%s','%s');",
            order.productId,
            order.productName,
            order.quantity,
            order.price,
            order.discount,
            order.image
        )
        db.execSQL(query)
    }

    fun cleanCart() {
        val db = readableDatabase
        val query = String.format("DELETE FROM OrderDetail")
        db.execSQL(query)
    }

    fun addFavorites(foodId: String?) {
        val db = readableDatabase
        val query = String.format("INSERT INTO Favorites VALUES('%s');", foodId)
        db.execSQL(query)
    }

    fun removeFavorites(foodId: String?) {
        val db = readableDatabase
        val query = String.format("DELETE FROM Favorites WHERE FoodId ='%s';", foodId)
        db.execSQL(query)
    }

    fun isFavorites(foodId: String?): Boolean {
        val db = readableDatabase
        val query = String.format("SELECT * FROM FAVORITES WHERE FoodId ='%s';", foodId)
        val cursor = db.rawQuery(query, null)
        if (cursor.count <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        return true
    }

    val countCart: Int
        get() {
            var count = 0
            val db = readableDatabase
            val query = String.format("SELECT COUNT(*) FROM OrderDetail")
            val cursor = db.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                do {
                    count = cursor.getInt(0)
                } while (cursor.moveToNext())
            }
            return count
        }

    fun updateCart(order: Order) {
        val db = readableDatabase
        val query = String.format(
            "UPDATE OrderDetail SET Quantity = %s WHERE ID = %d",
            order.quantity,
            order.iD
        )
        db.execSQL(query)
    }

    companion object {
        private const val DB_NAME = "EatItDB.db"
        private const val DB_VER = 1
    }
}