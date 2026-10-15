package com.vibesync.app.ui

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.vibesync.app.ui.theme.CardBorder
import com.vibesync.app.ui.theme.ErrorRed
import com.vibesync.app.ui.theme.NavyPrimary
import com.vibesync.app.ui.theme.SurfaceWhite
import com.vibesync.app.ui.theme.TextSecondary

private enum class AuthMode { SIGN_IN, SIGN_UP }

/** Shown after the splash screen whenever there's no signed-in Firebase user -- gates the whole
 *  app behind a real account, which the party feature needs (host/requester identity, per-party
 *  membership). Styled to match the rest of the app (same OutlinedTextField/Button look as
 *  [SearchScreen]'s paste-link card) rather than looking like a bolted-on auth SDK screen. */
@Composable
fun AuthScreen(viewModel: MainViewModel) {
    var mode by remember { mutableStateOf(AuthMode.SIGN_IN) }
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val isLoading by viewModel.isAuthLoading.collectAsStateWithLifecycle()
    val error by viewModel.authError.collectAsStateWithLifecycle()

    val canSubmit = email.isNotBlank() && password.length >= 6 && (mode == AuthMode.SIGN_IN || displayName.isNotBlank())

    // The `default_web_client_id` string resource only exists once google-services.json actually
    // has an OAuth web client in it (i.e. Google sign-in has been enabled + an SHA-1 registered in
    // the Firebase console) -- looked up by name at runtime rather than via a generated R
    // reference so this screen still compiles and just hides the button if that's ever missing
    // again, instead of failing the whole build.
    val context = LocalContext.current
    val webClientIdRes = remember { context.resources.getIdentifier("default_web_client_id", "string", context.packageName) }
    val googleSignInAvailable = webClientIdRes != 0
    val googleSignInClient = remember(googleSignInAvailable) {
        if (!googleSignInAvailable) return@remember null
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(webClientIdRes))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, options)
    }
    val googleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        // This callback runs on the main thread with no surrounding try/catch of its own -- an
        // uncaught exception here crashes the app immediately, so this catches broadly
        // (Exception, not just ApiException) rather than assuming Play Services can only ever
        // fail in that one documented way.
        if (activityResult.resultCode != Activity.RESULT_OK) return@rememberLauncherForActivityResult
        try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(activityResult.data).getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) viewModel.signInWithGoogle(idToken)
        } catch (e: Exception) {
            android.util.Log.e("AuthScreen", "Google sign-in result handling failed", e)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = com.vibesync.app.ui.theme.Dimens.OuterMargin),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(56.dp))
        VibeSyncLogoMark(size = 68.dp)
        Spacer(modifier = Modifier.height(14.dp))
        Row {
            Text(text = "VIBE", fontWeight = FontWeight.Bold, fontSize = 22.sp, letterSpacing = 2.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(text = "SYNC", fontWeight = FontWeight.Bold, fontSize = 22.sp, letterSpacing = 2.sp, color = NavyPrimary)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (mode == AuthMode.SIGN_IN) "Welcome back" else "Create your account to start or join a party",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        AuthModeSelector(
            selected = mode,
            onSelect = { mode = it; viewModel.dismissAuthError() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (mode == AuthMode.SIGN_UP) {
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Display name") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(com.vibesync.app.ui.theme.Dimens.CornerSearchBar),
                colors = authFieldColors()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(com.vibesync.app.ui.theme.Dimens.CornerSearchBar),
            colors = authFieldColors()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = TextSecondary
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(com.vibesync.app.ui.theme.Dimens.CornerSearchBar),
            colors = authFieldColors()
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = error ?: "", style = MaterialTheme.typography.bodySmall, color = ErrorRed, textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (mode == AuthMode.SIGN_IN) viewModel.signIn(email, password) else viewModel.signUp(email, password, displayName)
            },
            enabled = canSubmit && !isLoading,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(2.dp), color = com.vibesync.app.ui.theme.OnAccent, strokeWidth = 2.dp)
            } else {
                Text(if (mode == AuthMode.SIGN_IN) "Sign in" else "Create account")
            }
        }

        if (googleSignInAvailable && googleSignInClient != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text("or", style = MaterialTheme.typography.bodySmall, color = TextSecondary, modifier = Modifier.padding(horizontal = 12.dp))
                HorizontalDivider(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = { googleLauncher.launch(googleSignInClient.signInIntent) },
                enabled = !isLoading,
                border = BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Continue with Google", color = MaterialTheme.colorScheme.onSurface)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = {
            mode = if (mode == AuthMode.SIGN_IN) AuthMode.SIGN_UP else AuthMode.SIGN_IN
            viewModel.dismissAuthError()
        }) {
            Text(
                text = if (mode == AuthMode.SIGN_IN) "New here? Create an account" else "Already have an account? Sign in",
                color = NavyPrimary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun authFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    focusedIndicatorColor = NavyPrimary
)

@Composable
private fun AuthModeSelector(selected: AuthMode, onSelect: (AuthMode) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(3.dp)
    ) {
        AuthMode.values().forEach { m ->
            val isSelected = m == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isSelected) NavyPrimary else Color.Transparent)
                    .clickable { onSelect(m) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (m == AuthMode.SIGN_IN) "Sign In" else "Sign Up",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) com.vibesync.app.ui.theme.OnAccent else TextSecondary
                )
            }
        }
    }
}
