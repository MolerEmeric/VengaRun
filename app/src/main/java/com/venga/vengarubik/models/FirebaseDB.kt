package com.venga.vengarubik.models
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object FirebaseDB {
    private var database: FirebaseDatabase? = null
    private var reference: DatabaseReference? = null

    fun getDatabase(): FirebaseDatabase {
        if (database == null) {
            database = FirebaseDatabase.getInstance()
            database!!.setPersistenceEnabled(true) // Optional: enable offline persistence
        }
        return database!!
    }

    fun getReference(): DatabaseReference {
        if (reference == null) {
            reference = Firebase.database("https://vengarubikfinal-default-rtdb.europe-west1.firebasedatabase.app/").reference  //reference = getDatabase().reference
        }
        return reference!!
    }

    fun addUserWithScore(user: User) {
        val usersRef = getReference().child("users")
        usersRef.orderByChild("pseudo").equalTo(user.pseudo).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // The username exists in the database
                    snapshot.children.first().ref.child("scores").child(user.difficulty.toString()).push().setValue(user.score)
                } else {
                    // The username does not exist in the database
                    usersRef.child(user.pseudo).setValue(user.toMap())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Received error when try to upload user ${user.pseudo} : $error")
            }
        })
    }

    fun getTopScores(pseudo: String, difficulty: Int, callback: (ArrayList<Long>) -> Unit) {
        val scoresRef = getReference().child("users").child(pseudo).child("scores").child(difficulty.toString())
        scoresRef.orderByValue().addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val scoresList = ArrayList<Long>()
                for (scoreSnapshot in dataSnapshot.children) {
                    val score = scoreSnapshot.getValue(Long::class.java)
                    if (score != null) {
                        scoresList.add(score)
                    }
                }
                // Sort the values list in ascending order
                scoresList.sort()
                callback(scoresList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Error when getting user ${pseudo} top scores: $databaseError")
            }
        })
    }
}
