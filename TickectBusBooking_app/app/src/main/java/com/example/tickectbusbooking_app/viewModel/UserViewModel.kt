package com.example.tickectbusbooking_app.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tickectbusbooking_app.model.User
import com.example.tickectbusbooking_app.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel()

{
    // States for user authentication
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // Store the authenticated user's ID
    private var authenticatedUserId: Int? = null

    // Getter for authenticated user's ID
    fun getAuthenticatedUserId(): Int? {
        return authenticatedUserId
    }

    // Setter for authenticated user's details
    private fun setAuthenticatedUser(user: User) {
        authenticatedUserId = user.userId
    }


    // Email validation pattern
    private val emailPattern = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    )

    // Helper function for password hashing
    private fun hashPassword(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val saltedPassword = password + salt
        return digest.digest(saltedPassword.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    // Registration logic with validation
    fun registerUser(
        email: String,
        password: String,
        confirmPassword: String,
        fullName: String,
        phoneNumber: String
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            // Check for empty fields
            if (email.isBlank() || password.isBlank() || confirmPassword.isBlank() ||
                fullName.isBlank() || phoneNumber.isBlank()) {
                _authState.value = AuthState.Error("All fields are required.")
                return@launch
            }

            // Validate email format
            if (!emailPattern.matcher(email).matches()) {
                _authState.value = AuthState.Error("Invalid email format.")
                return@launch
            }

            // Validate phone number format (e.g., 10 digits)
            if (!phoneNumber.matches(Regex("^[0-9]{10}$"))) {
                _authState.value = AuthState.Error("Invalid phone number format. Must be 10 digits.")
                return@launch
            }

            // Check password match
            if (password != confirmPassword) {
                _authState.value = AuthState.Error("Passwords do not match.")
                return@launch
            }

            // Check if email already exists
            val existingUser = userRepository.getUserByEmail(email)
            if (existingUser != null) {
                _authState.value = AuthState.Error("Email is already registered.")
                return@launch
            }

            // Proceed with registration
            try {
                // Generate a unique salt (can also use UUID or SecureRandom for better entropy)
                val salt = email.substringBefore("@") // Example: unique salt based on email
                val hashedPassword = hashPassword(password, salt)

                val user = User(
                    email = email,
                    password = hashedPassword,
                    name = fullName,
                    phoneNumber = phoneNumber
                )
                val userId = userRepository.registerUser(user)
                if (userId > 0) {
                    val registeredUser = userRepository.getUserByEmail(email)
                    registeredUser?.let { setAuthenticatedUser(it) }
                    _authState.value = AuthState.Success("Registration successful!")
                } else {
                    _authState.value = AuthState.Error("Registration failed. Please Try again.")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("An error occurred during registration: ${e.message}")
            }
        }
    }

    // Login logic with email validation
    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            // Check for empty fields
            if (email.isBlank() || password.isBlank()) {
                _authState.value = AuthState.Error("Email and password are required.")
                return@launch
            }

            // Validate email format
            if (!emailPattern.matcher(email).matches()) {
                _authState.value = AuthState.Error("Invalid email format.")
                return@launch
            }

            // Proceed with login
            try {
                val user = userRepository.getUserByEmail(email)
                if (user != null) {
                    val hashedPassword = hashPassword(password, email.substringBefore("@"))
                    if (user.password == hashedPassword) {
                        // Set authenticated user after login
                        setAuthenticatedUser(user)
                        _authState.value = AuthState.Success("Login successful!")
                    } else {
                        _authState.value = AuthState.Error("Invalid email or password.")
                    }
                } else {
                    _authState.value = AuthState.Error("Invalid email or password.")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("An error occurred during login: ${e.message}")
            }
        }
    }

    // Reset the state to idle
    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }
}