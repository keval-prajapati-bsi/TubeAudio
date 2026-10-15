package com.vibesync.app.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class AppUser(val uid: String, val displayName: String, val email: String?)

sealed class AuthResult {
    data class Success(val user: AppUser) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

/** Plain email/password accounts via Firebase Auth -- Google Sign-In was dropped for now (needs
 *  an OAuth consent screen + auto-provisioned web client that wasn't coming through cleanly), but
 *  everything here is keyed by [FirebaseAuth]'s uid, so adding another sign-in provider later
 *  doesn't require touching the party/queue data model at all. Every signed-up user also gets a
 *  `users/{uid}` Firestore doc (displayName/email/createdAt) -- this is the "users table" the
 *  party feature's member lists and host/requester attribution read from. */
class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    /** Emits the signed-in user (or null) immediately and on every sign-in/out change --
     *  Firebase Auth persists the session locally, so this reflects a cached signed-in user
     *  before any network round-trip completes on app relaunch. */
    val currentUserFlow: Flow<AppUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { fbAuth ->
            trySend(fbAuth.currentUser?.toAppUser())
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    val currentUid: String? get() = auth.currentUser?.uid

    suspend fun signUp(email: String, password: String, displayName: String): AuthResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val fbUser = result.user ?: return AuthResult.Error("Something went wrong creating your account.")
            // Both of these are best-effort: the account and sign-in session already exist by this
            // point, so a hiccup writing the display name or the users/{uid} doc (most likely
            // because the Firestore security rules haven't been published to the console yet)
            // shouldn't fail the whole sign-up and leave the user stuck -- they'd just be signed in
            // with a blank display name until the next successful write.
            try {
                fbUser.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(displayName).build()).await()
            } catch (_: Exception) { /* non-fatal, see above */ }
            try {
                firestore.collection("users").document(fbUser.uid)
                    .set(mapOf("displayName" to displayName, "email" to email.trim(), "createdAt" to FieldValue.serverTimestamp()))
                    .await()
            } catch (_: Exception) { /* non-fatal, see above */ }
            AuthResult.Success(AppUser(fbUser.uid, displayName, fbUser.email))
        } catch (e: Exception) {
            AuthResult.Error(e.friendlyMessage())
        }
    }

    suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val fbUser = result.user ?: return AuthResult.Error("Something went wrong signing you in.")
            AuthResult.Success(fbUser.toAppUser())
        } catch (e: Exception) {
            AuthResult.Error(e.friendlyMessage())
        }
    }

    /** [idToken] comes from Google's own sign-in Intent (see AuthScreen's launcher) -- exchanged
     *  here for a real Firebase session. Also (best-effort) upserts the `users/{uid}` doc, since a
     *  first-time Google sign-in never goes through [signUp]. */
    suspend fun signInWithGoogle(idToken: String): AuthResult {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val fbUser = result.user ?: return AuthResult.Error("Something went wrong signing you in with Google.")
            try {
                firestore.collection("users").document(fbUser.uid)
                    .set(
                        mapOf(
                            "displayName" to (fbUser.displayName ?: fbUser.email?.substringBefore('@') ?: "You"),
                            "email" to fbUser.email
                        ),
                        SetOptions.merge()
                    )
                    .await()
            } catch (_: Exception) { /* non-fatal, see signUp()'s comment */ }
            AuthResult.Success(fbUser.toAppUser())
        } catch (e: Exception) {
            AuthResult.Error(e.friendlyMessage())
        }
    }

    fun signOut() = auth.signOut()

    private fun com.google.firebase.auth.FirebaseUser.toAppUser() =
        AppUser(uid = uid, displayName = displayName ?: email?.substringBefore('@') ?: "You", email = email)

    /** Firebase's raw exception messages are technical ("The email address is badly formatted.")
     *  -- these are already user-presentable as-is, but network failures throw a generic
     *  [java.io.IOException] with no message at all, so give that case an actual sentence. */
    private fun Exception.friendlyMessage(): String =
        message?.takeIf { it.isNotBlank() } ?: "Couldn't reach the server -- check your connection and try again."
}
