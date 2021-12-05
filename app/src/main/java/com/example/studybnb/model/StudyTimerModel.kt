package com.example.studybnb.model

import java.util.Date

data class StudyTimerModel(
    var study_time : Long?=null,
    var start_time : Long? = null,
    var finish_time : Long? = null,
    var date: String? = null,
    var subject: String? = null
)
