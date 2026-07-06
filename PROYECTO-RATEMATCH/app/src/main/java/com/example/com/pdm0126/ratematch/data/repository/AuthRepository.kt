package com.example.com.pdm0126.ratematch.data.repository

import com.example.com.pdm0126.ratematch.data.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth) {

    val currentUser: User?
        get() = auth.currentUser?.let {
            User(uid = it.uid, email = it.email ?: "", displayName = it.displayName)
        }

    suspend fun login(email: String, pass: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            val user = result.user?.let {
                User(uid = it.uid, email = it.email ?: "", displayName = it.displayName)
            }
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
            if (user != null) Result.success(user) else Result.failure(Exception("Error al registrar"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }
}