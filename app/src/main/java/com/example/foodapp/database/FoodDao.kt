 package com.example.foodapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FoodDao {

    @Insert
    fun insertFood(foodEntity: FoodEntity)

    @Delete
    fun deleteFood(foodEntity: FoodEntity)

    @Query("SELECT * FROM menu")
    fun getAllFood(): List<FoodEntity>

    @Query("SELECT * FROM menu WHERE id = :resId")
    fun getFoodById(resId: String): FoodEntity
}