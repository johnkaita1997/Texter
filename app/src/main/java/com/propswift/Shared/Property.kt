package com.propswift.Shared

data class Property(
    val area: Double,
    val area_unit: String,
    val created_at: String,
    val deleted_at: Any,
    val files: List<Any>,
    val id: String,
    val location: String,
    val name: String,
    val rent_amount: String,
    val updated_at: String
)