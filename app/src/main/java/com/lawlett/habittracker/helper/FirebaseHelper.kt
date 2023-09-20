package com.lawlett.habittracker.helper

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lawlett.habittracker.ext.TAG
import com.lawlett.habittracker.models.HabitModel

class FirebaseHelper {
    val db = Firebase.firestore
    var auth: FirebaseAuth = Firebase.auth

    fun insertOrUpdateHabitFB(model: HabitModel) {
        if (isSigned()) {
            db.collection(getUserName())
                .document(model.title.toString()).set(model)
                .addOnSuccessListener { documentReference ->
                    Log.e(TAG, "DocumentSnapshot added with ID: $documentReference")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error adding document", e)
                }
        }
    }


    fun isSigned(): Boolean {
        return auth.currentUser != null
    }

    fun logOut() {
        auth.signOut()
    }

    fun getUserName() = "${auth.currentUser?.displayName}:${auth.currentUser?.uid}"

    fun delete(model: HabitModel) {
        if (isSigned()) {
            db.collection(getUserName())
                .document(model.title.toString())
                .delete()
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }
    }

    private fun deleteCollection(collection: CollectionReference, batchSize: Int) {
        try {
            // Retrieve a small batch of documents to avoid out-of-memory errors/
            var deleted = 0
            collection
                .limit(batchSize.toLong())
                .get()
                .addOnCompleteListener {
                    for (document in it.result.documents) {
                        document.reference.delete()
                        ++deleted
                    }
                    if (deleted >= batchSize) {
                        // retrieve and delete another batch
                        deleteCollection(collection, batchSize)
                    }
                }
        } catch (e: Exception) {
            System.err.println("Error deleting collection : " + e.message)
        }
    }

    fun deleteAll() {
        if (isSigned()) {
            val reference = db.collection(getUserName())
            db.collection(getUserName()).get().addOnCompleteListener { result ->
                deleteCollection(reference, result.result.size())
            }
        }
    }
}