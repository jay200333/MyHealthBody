package com.example.myhealthybody.util

import android.content.Context
import android.util.Log
import com.example.myhealthybody.mainView.MyApplication
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

object AuthUtil {
    fun withdrawal(user: FirebaseUser, context: Context, onComplete: (Boolean, String) -> Unit) {
        val userId = user.uid
        deleteFirestoreUserData(userId, context) {
            deleteRealtimeDatabaseUserData(userId, context) {
                user.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onComplete(true, "탈퇴가 완료되었습니다.")
                    } else {
                        val message = task.exception?.message ?: "계정 탈퇴 실패"
                        onComplete(false, message)
                    }
                }
            }
        }
    }

    private fun deleteFirestoreUserData(userId: String, context: Context, onComplete: () -> Unit) {
        val userImagesRef = MyApplication.db.collection("userImages")
        userImagesRef.whereEqualTo("userId", userId).get()
            .addOnSuccessListener { documents ->
                // 모든 문서에 대해 삭제 처리
                for (document in documents) {
                    userImagesRef.document(document.id).delete()
                }
                onComplete()
            }
            .addOnFailureListener {
                // 오류 처리
                Log.d("AccountDelete", "Firestore 데이터 삭제 실패: ${it.message}")
            }
    }

    private fun deleteRealtimeDatabaseUserData(
        userId: String,
        context: Context,
        onComplete: () -> Unit
    ) {
        var usersRef = FirebaseDatabase.getInstance().reference.child("users")
        usersRef.child(userId).removeValue().addOnCompleteListener {
            onComplete()
        }
    }
}