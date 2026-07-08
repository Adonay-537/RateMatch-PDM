package com.example.com.pdm0126.ratematch.data.repository

import com.example.com.pdm0126.ratematch.data.model.User
import com.example.com.pdm0126.ratematch.data.remote.KtorClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    val currentUser: User?
        get() = auth.currentUser?.let {
            User(uid = it.uid, email = it.email ?: "", displayName = it.displayName)
        }

    suspend fun refreshDynamicConfig() {
        try {
            val document = firestore.collection("config").document("api_keys").get().await()
            val remoteKey = document.getString("api_football_key") ?: document.getString("football_data_key")
            if (!remoteKey.isNullOrBlank()) {
                KtorClient.updateApiKey(remoteKey)
            }
        } catch (e: Exception) {
        }
    }

    suspend fun login(email: String, pass: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            val user = result.user?.let {
                User(uid = it.uid, email = it.email ?: "", displayName = it.displayName)
            }
            
            refreshDynamicConfig()
            
            if (user != null) Result.success(user) else Result.failure(Exception("Error al iniciar sesión"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, pass: String, name: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val user = result.user?.let {
                User(uid = it.uid, email = it.email ?: "", displayName = name)
            }
            
            refreshDynamicConfig()
            
            if (user != null) Result.success(user) else Result.failure(Exception("Error al registrar"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }
}
