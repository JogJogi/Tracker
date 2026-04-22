package com.example.fittracker.data.repository

import com.example.fittracker.data.db.dao.ActivityDao
import com.example.fittracker.data.db.dao.TrackPointDao
import com.example.fittracker.data.db.entity.Activity
import com.example.fittracker.data.db.entity.TrackPoint
import kotlinx.coroutines.flow.Flow

class ActivityRepository(
    private val activityDao: ActivityDao,
    private val trackPointDao: TrackPointDao
) {

    fun getAllActivitiesFlow(): Flow<List<Activity>> = activityDao.getAllFlow()

    fun getRecentActivitiesFlow(limit: Int = 10): Flow<List<Activity>> =
        activityDao.getRecentFlow(limit)

    suspend fun getActivityById(id: Long): Activity? = activityDao.getById(id)

    fun getTrackPointsFlow(activityId: Long): Flow<List<TrackPoint>> =
        trackPointDao.getForActivityFlow(activityId)

    suspend fun getTrackPoints(activityId: Long): List<TrackPoint> =
        trackPointDao.getForActivity(activityId)

    suspend fun saveActivity(activity: Activity): Long = activityDao.insert(activity)

    suspend fun updateActivity(activity: Activity) = activityDao.update(activity)

    suspend fun deleteActivity(activity: Activity) {
        trackPointDao.deleteForActivity(activity.id)
        activityDao.delete(activity)
    }

    suspend fun saveTrackPoint(trackPoint: TrackPoint): Long =
        trackPointDao.insert(trackPoint)

    suspend fun saveTrackPoints(trackPoints: List<TrackPoint>) =
        trackPointDao.insertAll(trackPoints)

    fun getTotalDistanceFlow(): Flow<Float?> = activityDao.getTotalDistanceFlow()

    fun getActivitiesInRangeFlow(fromMs: Long, toMs: Long): Flow<List<Activity>> =
        activityDao.getInRangeFlow(fromMs, toMs)
}
