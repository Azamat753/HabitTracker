package com.lawlett.habittracker.helper

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lawlett.habittracker.TAG
import com.lawlett.habittracker.models.HabitModel

class FirebaseHelper {
    val db = Firebase.firestore
    var auth: FirebaseAuth = Firebase.auth

    fun insertOrUpdateHabitFB(model: HabitModel) {
        db.collection("${getUserName()}:${auth.currentUser?.uid}")
            .document(model.title.toString()).set(model)
            .addOnSuccessListener { documentReference ->
                Log.e(TAG, "DocumentSnapshot added with ID: $documentReference")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding document", e)
            }
    }

    fun getUsersData(userName: String) {
        val docRef = db.collection(userName).document("SF")
//
//        docRef.collection("habits : ${getUserName()}")
//            .document(model.title.toString()).get(userName)
//            .addOnSuccessListener { documentReference ->
//                Log.e(TAG, "DocumentSnapshot added with ID: $documentReference")
//            }
//            .addOnFailureListener { e ->
//                Log.e(TAG, "Error adding document", e)
//            }
    }

    fun getUserName() = auth.currentUser?.displayName


    fun delete(model: HabitModel) {
        db.collection("habits : ${auth.currentUser?.displayName}").document(model.title.toString())
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }

}