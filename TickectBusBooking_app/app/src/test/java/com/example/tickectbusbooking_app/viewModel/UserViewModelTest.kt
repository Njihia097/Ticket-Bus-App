package com.example.tickectbusbooking_app.viewModel

import com.example.tickectbusbooking_app.model.User
import com.example.tickectbusbooking_app.repository.UserRepository
import com.example.tickectbusbooking_app.viewModel.AuthState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {

    private lateinit var userRepository: UserRepository
    private lateinit var userViewModel: UserViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        userRepository = mockk()
        userViewModel = UserViewModel(userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset the main dispatcher
    }

    @Test
    fun `test successful registration`() = runTest {
        // Mock repository behavior
        val testUser = User(
            name = "Test User",
            email = "test@example.com",
            password = "password123",
            phoneNumber = "1234567890"
        )
        coEvery { userRepository.getUserByEmail("test@example.com") } returns null
        coEvery { userRepository.registerUser(testUser) } returns 1L

        // Perform registration
        userViewModel.registerUser(
            email = "test@example.com",
            password = "password123",
            confirmPassword = "password123",
            fullName = "Test User",
            phoneNumber = "1234567890"
        )

        // Execute pending coroutine tasks
        advanceUntilIdle()

        // Check state
        assertEquals(AuthState.Success("Registration successful!"), userViewModel.authState.value)
    }

    @Test
    fun `test email already registered`() = runTest {
        coEvery { userRepository.getUserByEmail("test@example.com") } returns User(
            name = "Existing User",
            email = "test@example.com",
            password = "password123",
            phoneNumber = "1234567890"
        )

        userViewModel.registerUser(
            email = "test@example.com",
            password = "password123",
            confirmPassword = "password123",
            fullName = "Test User",
            phoneNumber = "1234567890"
        )

        // Execute pending coroutine tasks
        advanceUntilIdle()

        assertEquals(AuthState.Error("Email is already registered."), userViewModel.authState.value)
    }

    @Test
    fun `test invalid phone number`() = runTest {
        userViewModel.registerUser(
            email = "test@example.com",
            password = "password123",
            confirmPassword = "password123",
            fullName = "Test User",
            phoneNumber = "123"
        )

        // Execute pending coroutine tasks
        advanceUntilIdle()

        assertEquals(AuthState.Error("Invalid phone number format. Must be 10 digits."), userViewModel.authState.value)
    }
}
