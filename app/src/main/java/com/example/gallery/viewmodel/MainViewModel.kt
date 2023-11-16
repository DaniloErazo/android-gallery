package com.example.gallery.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class MainViewModel : ViewModel() {

    val userLD = MutableLiveData<User>()

    fun showUser(){

        viewModelScope.launch (Dispatchers.IO){
            try {
                //obtiene usuario
                val doc = Firebase.firestore
                    .collection("users")
                    .document("PqqDNP1ofdNMFznOq0B8NZGkHKz1")
                    .get()
                    .await()
                val user = doc.toObject(User::class.java)
                Log.e("<<<<", user?.photoID.toString())

                //storage con una corrutina (suspend)
                user?.let {
                    val url = Firebase.storage.reference
                        .child("profileImages")
                        .child(it.photoID)
                        .downloadUrl
                        .await()
                    Log.e("<<<<", url.toString())

                    val localUser = User(
                        user.id,
                        user.name,
                        user.photoID,
                        user.username,
                        url.toString()
                    )
                    //cambiar el contexto al principal para usar ese hilo
                    withContext(Dispatchers.Main){userLD.value= localUser}
                }

            }catch (ex:Exception){

            }
        }



    }

    fun uploadImage(uri: Uri){
        viewModelScope.launch (Dispatchers.IO) {
            //Cargar imagen
            //child crea carpetas

            try {
                val uuid = UUID.randomUUID().toString()
                Firebase.storage.reference
                    .child("profileImages")
                    .child(uuid)
                    .putFile(uri).await()

                //Se actualiza la foto asegurando que no se cachee

                Firebase.firestore.collection("users")
                    .document("PqqDNP1ofdNMFznOq0B8NZGkHKz1")
                    .update("photoID", uuid )
                    .await()
            }catch (ex:Exception){
                Log.e("<<<<<", ex.message.toString())
            }

        }
    }
}

data class User (
    var id: String = "",
    var name: String = "",
    var photoID: String = "",
    var username: String = "",
    var photoUrl: String? = null
)