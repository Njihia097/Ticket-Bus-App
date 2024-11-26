package com.example.tickectbusbooking_app.ui.theme.screens

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tickectbusbooking_app.viewModel.BusScheduleViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import androidx.compose.material3.CircularProgressIndicator
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.clickable
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusScheduleScreen(
    busScheduleViewModel: BusScheduleViewModel = hiltViewModel(),
    onBusSelected: (busId: Int, routeId: Int) -> Unit
) {
    val busSchedules by busScheduleViewModel.busSchedules.collectAsState()
    val operationStatus by busScheduleViewModel.operationStatus.collectAsState()
    val routes by busScheduleViewModel.routes.collectAsState(initial = emptyList())

    val context = LocalContext.current
    var origin by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var travelDate by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    var originExpanded by remember { mutableStateOf(false) }
    var destinationExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Dropdown for Origin
        ExposedDropdownMenuBox(
            expanded = originExpanded,
            onExpandedChange = { originExpanded = !originExpanded }
        ) {
            TextField(
                value = origin,
                onValueChange = { origin = it },
                label = { Text(text = "Select Origin") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = originExpanded) },
                readOnly = true
            )
            ExposedDropdownMenu(
                expanded = originExpanded,
                onDismissRequest = { originExpanded = false }
            ) {
                routes.distinctBy { it.origin }.forEach { route ->
                    DropdownMenuItem(
                        text = { Text(text = route.origin) },
                        onClick = {
                            origin = route.origin
                            originExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown for Destination
        ExposedDropdownMenuBox(
            expanded = destinationExpanded,
            onExpandedChange = { destinationExpanded = !destinationExpanded }
        ) {
            TextField(
                value = destination,
                onValueChange = { destination = it },
                label = { Text(text = "Select Destination") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = destinationExpanded) },
                readOnly = true
            )
            ExposedDropdownMenu(
                expanded = destinationExpanded,
                onDismissRequest = { destinationExpanded = false }
            ) {
                routes.distinctBy { it.destination }.forEach { route ->
                    DropdownMenuItem(
                        text = { Text(text = route.destination) },
                        onClick = {
                            destination = route.destination
                            destinationExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Date Picker for Travel Date
        Button(onClick = {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Travel Date")
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(selection))
                travelDate = selectedDate
            }

            datePicker.show((context as AppCompatActivity).supportFragmentManager, "datePicker")
        }) {
            Text(text = if (travelDate.isEmpty()) "Select Travel Date" else travelDate)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Button
        Button(
            onClick = {
                coroutineScope.launch {
                    busScheduleViewModel.searchBuses(origin, destination, travelDate)
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(text = "Search")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Operation Status
        when (operationStatus) {
            is BusScheduleViewModel.OperationStatus.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is BusScheduleViewModel.OperationStatus.Error -> {
                Text(
                    text = (operationStatus as BusScheduleViewModel.OperationStatus.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> Unit
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display Bus Schedules
        LazyColumn {
            items(busSchedules) { bus ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onBusSelected(bus.id, bus.routeId) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Bus Name: ${bus.name}", style = MaterialTheme.typography.titleMedium)
                        Text(text = "Bus Number: ${bus.number}")
                        Text(text = "Total Seats: ${bus.totalSeats}")
                    }
                }
            }
        }
    }
}
