package com.example.justchatting.data.auth

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.justchatting.Cache
import com.example.justchatting.data.DTO.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.Completable
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject

class AuthFirebaseSource : KoinComponent {
    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    fun loginWithEmail(email: String, password: String): Completable =
        Completable.create { emitter ->
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    emitter.onComplete()
                } else {
                    emitter.onError(it.exception!!)
                }
            }
        }

    fun signUp(email: String, password: String): Completable = Completable.create { emitter ->
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                emitter.onComplete()
            }
            .addOnFailureListener {
                emitter.onError(it)
            }
    }

    fun uploadProfileImage(uri: Uri?): Single<String> {
        return Single.create { emitter ->
            if (uri == null) {
                emitter.onSuccess("")
            } else {
                val uid = FirebaseAuth.getInstance().uid
                val ref = FirebaseStorage.getInstance().getReference("/profileImages/$uid")

                ref.delete().addOnCompleteListener {
                    ref.putFile(uri)
                        .addOnSuccessListener {
                            ref.downloadUrl.addOnSuccessListener {
                                emitter.onSuccess(it.toString())
                            }
                        }.addOnCanceledListener {
                            emitter.onError(Exception("upload is canceled"))
                        }.addOnFailureListener {
                            emitter.onError(it)
                        }
                }.addOnCanceledListener {
                    ref.putFile(uri)
                        .addOnSuccessListener {
                            ref.downloadUrl.addOnSuccessListener {
                                emitter.onSuccess(it.toString())
                            }
                        }.addOnCanceledListener {
                            emitter.onError(Exception("upload is canceled"))
                        }.addOnFailureListener {
                            emitter.onError(it)
                        }
                }.addOnFailureListener {
                    Log.d("업로드failure", it.toString())
                    ref.putFile(uri)
                        .addOnSuccessListener {
                            ref.downloadUrl.addOnSuccessListener {
                                emitter.onSuccess(it.toString())
                            }
                        }.addOnCanceledListener {
                            emitter.onError(Exception("upload is canceled"))
                        }.addOnFailureListener {
                            emitter.onError(it)
                        }
                }

            }
        }
    }

    fun saveUser(
        name: String,
        phoneNumber: String,
        firebaseImageResourcePath: String,
        email: String
    ): Single<Boolean> =
        Single.create { emitter ->
            val uid = FirebaseAuth.getInstance().uid!!

            val re = Regex("[^A-Za-z0-9 ]")
            val email = re.replace(email, "")

            val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
            val user = UserModel(
                uid = uid,
                username = name,
                phoneNumber = phoneNumber,
                profileImageUrl = firebaseImageResourcePath,
                email = email
            )

            ref.setValue(user)
                .addOnSuccessListener {
                    val phoneRef =
                        FirebaseDatabase.getInstance().getReference("/phone/$phoneNumber")

                    phoneRef.setValue(uid).addOnSuccessListener {
                        val emailRef = FirebaseDatabase.getInstance().getReference("/email/$email")
                        emailRef.setValue(uid).addOnSuccessListener {

                            emitter.onSuccess(true)
                        }
                    }
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }

    fun updateToken(): Completable = Completable.create { emitter ->
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            val myUid = FirebaseAuth.getInstance().uid
            val token = it.result

            FirebaseDatabase.getInstance().getReference("/users/${FirebaseAuth.getInstance().uid}")
                .child("token").setValue(token)

            FirebaseDatabase.getInstance().getReference("/user_groups/$myUid")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        emitter.onError(Exception(error.message))
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (!snapshot.exists()) emitter.onComplete()

                        snapshot.children.forEachIndexed { index: Int, chatRoom: DataSnapshot ->
                            FirebaseDatabase.getInstance()
                                .getReference("/members/${chatRoom.key}/$myUid").child("token")
                                .setValue(token)
                            if (index == snapshot.children.count() - 1)
                                emitter.onComplete()
                        }
                    }

                })
        }

    }

    fun saveProfileImageToCache(context: Context): Completable = Completable.create { emitter ->
        val myUid = FirebaseAuth.getInstance().uid
        FirebaseDatabase.getInstance().getReference("/users/$myUid")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(UserModel::class.java) ?: return

                    if (user.profileImageUrl != "") {
                        val ONE_MEGABYTE: Long = 1024 * 1024
                        FirebaseStorage.getInstance().getReferenceFromUrl(user.profileImageUrl!!)
                            .getBytes(ONE_MEGABYTE).addOnSuccessListener {

                            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                            val cache: Cache by inject()
                            cache.saveBitmap(context, bitmap)
                            emitter.onComplete()
                        }.addOnFailureListener {
                            emitter.onError(it)
                        }
                    } else {
                        emitter.onComplete()
                    }
                }
            })
    }

    fun editProfileImageUrl(url: String) {
        val uid = FirebaseAuth.getInstance().uid
        val ref =
            FirebaseDatabase.getInstance().getReference("/users/$uid/profileImageUrl").setValue(url)
    }
}