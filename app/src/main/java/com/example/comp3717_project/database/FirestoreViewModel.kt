package com.example.comp3717_project.database

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

const val INFO = "info"
const val DEBUG = "debug"
const val WARNING = "warning"

class FirestoreViewModel: ViewModel() {


    private var currentUser: User? = null

    companion object {
        const val NAME_FIELD = "name"
        const val PASSWORD_FIELD = "password"
        const val HIGH_SCORE_FIELD = "highScore"
        const val DEFAULT_SCORE = 0
    }

    /**
     * Sets the current user to null.
     */
    fun logOut() {
        currentUser = null
    }

    fun setCurrentUser(user: User) {
        currentUser = user
    }

    fun getCurrentUser(): User? {
        return currentUser
    }
}