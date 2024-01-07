package com.example.comp3717_project.database

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class FirebaseRepository @Inject constructor() {

    private val database: FirebaseFirestore = Firebase.firestore
    private val usersCollection = database.collection("users")

    /**
     * Checks whether the username and password matches any in the users collection. Whether the
     * user has been found or not is passed into the callback function.
     *
     * @param name: The user name to be checked in the database.
     * @param password: The user password to be checked in the database.
     * @param callback: The function to be called once the user has or hasn't been found.
     */
    fun findUser(name: String, password: String, callback: (Boolean) -> Unit) {
        var userExists = false
        usersCollection.document(name).get()
            .addOnSuccessListener { user ->
                if (user != null) {
                    Log.d(DEBUG, "DocumentSnapshot data: ${user.data}")
                    if (user.get(FirestoreViewModel.PASSWORD_FIELD).toString() == password) {
                        userExists = true
                    }
                } else {
                    Log.d(DEBUG, "No such document")
                }
                callback(userExists)
            }
            .addOnFailureListener { exception ->
                Log.d(DEBUG, "get failed with ", exception)
            }
        // this gets called first

    }

    /**
     * Adds a new user to the users collection.
     *
     * @param name: The user's name.
     * @param password: The user's password.
     * @param callback: The function to be called after the user has been added to the collection.
     */
    fun addUser(name: String, password: String, callback: (Boolean) -> Unit) {
        val user = hashMapOf(
            FirestoreViewModel.NAME_FIELD to name,
            FirestoreViewModel.PASSWORD_FIELD to password,
            FirestoreViewModel.HIGH_SCORE_FIELD to FirestoreViewModel.DEFAULT_SCORE
        )
        usersCollection.document(name).set(user)
            .addOnSuccessListener {
                Log.i(INFO, "Successfully added user $name")
                callback(true)
            }
            .addOnFailureListener { exception ->
                Log.w(WARNING, "Error in setting the document", exception)
                callback(false)
            }
    }



    /**
     * Retrieves a set amount of users ordered by their highest score and stores them into an
     * `ArrayList<User>`.
     *
     * @param maxRank: The amount of users to be retrieved from the database.
     * @param callback: The function to be called after the highest scoring users have been
     * retrieved.
     */
    fun getTopUsers(maxRank: Long, callback: (ArrayList<User>) -> Unit) {
        val topUsers = ArrayList<User>()
        usersCollection
            .orderBy(FirestoreViewModel.HIGH_SCORE_FIELD, Query.Direction.DESCENDING)
            .limit(maxRank)
            .get()
            .addOnSuccessListener { users ->
                for (user in users) {
                    if (user != null) {
                        Log.d(DEBUG, "DocumentSnapshot data: ${user.data}")
                        val name: String = user.data[FirestoreViewModel.NAME_FIELD].toString()
                        val password: String = user.data[FirestoreViewModel.PASSWORD_FIELD].toString()
                        val highScore: Int = user.data[FirestoreViewModel.HIGH_SCORE_FIELD].toString().toInt()
                        val topUser = User(name, password, highScore)
                        topUsers.add(topUser)
                    } else {
                        Log.d(DEBUG, "No such document")
                    }
                }
                callback(topUsers)
            }
    }

    /**
     * Retrieves a specific user's info from the users collection.
     *
     * @param name: The name of the document to retrieve data from.
     * @param callback: The function to call after the user's info has been retrieved.
     */
    fun getUserInfo(name: String, callback: (DocumentSnapshot) -> Unit) {
        usersCollection.document(name).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(DEBUG, "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d(DEBUG, "No such document")
                }
                callback(document)
            }.addOnFailureListener { exception ->
                Log.d(DEBUG, "get failed with ", exception)
            }
    }

    fun updateUserHighScore(name: String, newHighScore: Int) {
        val data = hashMapOf(FirestoreViewModel.HIGH_SCORE_FIELD to newHighScore)
        usersCollection.document(name).set(data, SetOptions.merge())
            .addOnSuccessListener {
                Log.i(INFO, "Successfully added user $name")
            }
            .addOnFailureListener { exception ->
                Log.w(WARNING, "Error in setting the document", exception)
            }
    }

}