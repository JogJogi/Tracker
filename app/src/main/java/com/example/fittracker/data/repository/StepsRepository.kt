package com.example.fittracker.data.repository

import com.example.fittracker.data.db.dao.StepsDao
import com.example.fittracker.data.db.entity.StepsDaily
import com.example.fittracker.data.db.entity.StepsHourly
import kotlinx.coroutines.flow.Flow

class StepsRepository(private val stepsDao: StepsDao) {

    fun getTodayStepsFlow(todayIso: String): Flow<StepsDaily?> =
        stepsDao.getDailyForDateFlow(todayIso)

    fun getHourlyStepsFlow(dateIso: String): Flow<List<StepsHourly>> =
        stepsDao.getHourlyForDateFlow(dateIso)

    fun getWeeklyStepsFlow(fromDate: String, toDate: String): Flow<List<StepsDaily>> =
        stepsDao.getDailyInRangeFlow(fromDate, toDate)

    fun getWeeklyStepSumFlow(fromDate: String, toDate: String): Flow<Int?> =
        stepsDao.getSumInRange(fromDate, toDate)

    suspend fun upsertDailySteps(stepsDaily: StepsDaily) = stepsDao.upsertDaily(stepsDaily)

    suspend fun upsertHourlySteps(stepsHourly: StepsHourly) = stepsDao.upsertHourly(stepsHourly)

    suspend fun getDailySteps(dateIso: String): StepsDaily? = stepsDao.getDailyForDate(dateIso)
}
