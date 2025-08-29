package com.example.groceryoptimizer.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.groceryoptimizer.model.Item

/**
 * Persistent SQLite DB. Data will remain even after closing the app.
 */
object InMemoryDb {

    private lateinit var db: SQLiteDatabase
    private lateinit var helper: ItemDbHelper

    fun init(context: Context) {
        if (!this::db.isInitialized) {
            helper = ItemDbHelper(context)
            db = helper.writableDatabase
        }
    }

    fun insertItem(name: String, quantity: Int, price: Double, barcode: String?): Long {
        val values = ContentValues().apply {
            put("name", name)
            put("quantity", quantity)
            put("price", price)
            put("barcode", barcode)
        }
        return db.insert("items", null, values)
    }

    fun getAll(): List<Item> {
        val list = mutableListOf<Item>()
        val c: Cursor = db.rawQuery("SELECT id, name, quantity, price, barcode FROM items", null)
        c.use {
            while (it.moveToNext()) {
                list.add(
                    Item(
                        id = it.getLong(0),
                        name = it.getString(1),
                        quantity = it.getInt(2),
                        price = it.getDouble(3),
                        barcode = it.getString(4)
                    )
                )
            }
        }
        return list
    }

    fun deleteById(id: Long) {
        db.delete("items", "id = ?", arrayOf(id.toString()))
    }

    fun clearAll() {
        db.delete("items", null, null)
    }

    /**
     * Internal SQLiteOpenHelper for DB creation and default data
     */
    private class ItemDbHelper(context: Context) :
        SQLiteOpenHelper(context, "items.db", null, 1) {

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    quantity INTEGER NOT NULL,
                    price REAL NOT NULL,
                    barcode TEXT
                )
                """.trimIndent()
            )

            // Insert default items
            insertDefaults(db)
        }

        private fun insertDefaults(db: SQLiteDatabase) {
            val defaults = listOf(
                Triple("Milk", 2, 50.0),
                Triple("Bread", 1, 30.0),
                Triple("Rice", 5, 60.0),
                Triple("Oil", 1, 120.0),
                Triple("Fruits", 2, 80.0)
            )

            defaults.forEach { (name, qty, price) ->
                val cv = ContentValues().apply {
                    put("name", name)
                    put("quantity", qty)
                    put("price", price)
                }
                db.insert("items", null, cv)
            }
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
    }
}
