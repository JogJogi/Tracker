package com.example.fittracker.data.repository

import com.example.fittracker.data.db.dao.BodyMeasurementDao
import com.example.fittracker.data.db.entity.BodyMeasurement
import kotlinx.coroutines.flow.Flow

class BodyMeasurementRepository(private val dao: BodyMeasurementDao) {

    fun getAllFlow(): Flow<List<BodyMeasurement>> = dao.getAllFlow()

    fun getLatestFlow(): Flow<BodyMeasurement?> = dao.getLatestFlow()

    suspend fun save(measurement: BodyMeasurement): Long = dao.insert(measurement)

    suspend fun delete(measurement: BodyMeasurement) = dao.delete(measurement)
}
