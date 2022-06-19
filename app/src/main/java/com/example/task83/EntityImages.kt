package com.example.task83


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class EntityImages {
    @PrimaryKey
    var id: Int = 0
    var imageCat: String? = null
}
