package com.cleardocs.web.firebase

import kotlin.js.Json
import kotlin.js.json

external interface FirebaseConfigData {
    var apiKey: String?
    var authDomain: String?
    var projectId: String?
    var storageBucket: String?
    var messagingSenderId: String?
    var appId: String?
    var measurementId: String?
}

object FirebaseConfig {
    val config: FirebaseConfigData = json(
        "apiKey" to "AIzaSyDkYVYliutKS9snUQc3CzvvHpXuyqf3YAA",
        "authDomain" to "radar-cffdb.firebaseapp.com",
        "projectId" to "radar-cffdb",
        "storageBucket" to "radar-cffdb.appspot.com",
        "messagingSenderId" to "895561447215",
        "appId" to "1:895561447215:web:3244853d278422d1d1d3da",
        "measurementId" to "G-4L1T5SEYST"
    ) as FirebaseConfigData

    fun isConfigured(): Boolean {
        val values = listOf(
            config.apiKey,
            config.authDomain,
            config.projectId,
            config.storageBucket,
            config.messagingSenderId,
            config.appId,
            config.measurementId
        )
        return values.none { it == null || it.startsWith("YOUR_") }
    }
}
