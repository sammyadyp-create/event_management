package com.example.campusevents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AdminDashboardApp(onLogout: () -> Unit) {
    val registrations = remember { mutableStateListOf<StudentRegistration>() }
    val events = remember {
        mutableStateListOf<CampusEvent>().apply {
            addAll(sampleEvents)
        }
    }
    var selectedAdminTab by remember { mutableIntStateOf(0) } // 0: Ongoing Events, 1: Student Registrations
    var showCreateDialog by remember { mutableStateOf(false) }
    var eventName by remember { mutableStateOf("") }
    var eventCategory by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }
    var eventVenue by remember { mutableStateOf("") }
    var eventDescription by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance().collection("registrations")
            .get()
            .addOnSuccessListener { result ->
                registrations.clear()
                for (document in result) {
                    val name = document.getString("studentName") ?: ""
                    val email = document.getString("email") ?: ""
                    val phone = document.getString("phone") ?: ""
                    val studentId = document.getString("studentId") ?: ""
                    val department = document.getString("department") ?: ""
                    val eventName = document.getString("eventName") ?: ""
                    registrations.add(
                        StudentRegistration(
                            studentName = name,
                            email = email,
                            phone = phone,
                            studentId = studentId,
                            department = department,
                            eventTitle = eventName,
                            paymentMethod = "Registered"
                        )
                    )
                }
            }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Admin Dashboard",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF172554)
                )
                Text(
                    text = "Manage ongoing events & student registrations",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            TextButton(onClick = onLogout) {
                Text("Logout", color = Color(0xFFDC3545), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Stats Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF172554))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("CAMPUS STATS", color = Color(0xFFC7D2FE), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${events.size} Active Events", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("${registrations.size} Registrations", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs for Admin: Ongoing Events vs Registered Students
        Row(
            modifier = Modifier.fillMaxWidth().background(Color(0xFFE2E8F0), RoundedCornerShape(10.dp)).padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { selectedAdminTab = 0 },
                modifier = Modifier.weight(1f).padding(end = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (selectedAdminTab == 0) Color(0xFF172554) else Color.Transparent)
            ) {
                Text("Ongoing Events", color = if (selectedAdminTab == 0) Color.White else Color(0xFF172554), fontSize = 13.sp)
            }
            Button(
                onClick = { selectedAdminTab = 1 },
                modifier = Modifier.weight(1f).padding(start = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (selectedAdminTab == 1) Color(0xFF172554) else Color.Transparent)
            ) {
                Text("Student Registrations", color = if (selectedAdminTab == 1) Color.White else Color(0xFF172554), fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedAdminTab == 0) {
            // Ongoing Events Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Ongoing & Upcoming Events", fontWeight = FontWeight.Bold, color = Color(0xFF172554), fontSize = 16.sp)
                Button(onClick = { showCreateDialog = true }) {
                    Text("＋ Add Event")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(events) { event ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(event.category.uppercase(), color = Color(0xFF6D5CE8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(event.title, fontWeight = FontWeight.Bold, color = Color(0xFF172554), fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("📅 ${event.date} • 📍 ${event.venue}", color = Color.Gray, fontSize = 13.sp)
                            Text(event.description, color = Color.DarkGray, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { events.remove(event) }) {
                                    Text("Delete Event", color = Color(0xFFDC3545), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Student Registrations Section
            Text("Registered Students List", fontWeight = FontWeight.Bold, color = Color(0xFF172554), fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (registrations.isEmpty()) {
                    item {
                        Text("No student registrations yet.", color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
                    }
                } else {
                    items(registrations) { reg ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(reg.eventTitle, fontWeight = FontWeight.Bold, color = Color(0xFF6D5CE8), fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("👤 Name: ${reg.studentName}", fontWeight = FontWeight.Bold, color = Color(0xFF172554))
                                Text("📧 Email: ${reg.email}", color = Color.DarkGray, fontSize = 13.sp)
                                Text("📞 Phone: ${reg.phone}", color = Color.DarkGray, fontSize = 13.sp)
                                Text("🆔 ID: ${reg.studentId} | Dept: ${reg.department}", color = Color.DarkGray, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create Event") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = eventName,
                        onValueChange = { eventName = it },
                        label = { Text("Event name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = eventCategory,
                        onValueChange = { eventCategory = it },
                        label = { Text("Category") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = eventDate,
                        onValueChange = { eventDate = it },
                        label = { Text("Date") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = eventVenue,
                        onValueChange = { eventVenue = it },
                        label = { Text("Venue") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = eventDescription,
                        onValueChange = { eventDescription = it },
                        label = { Text("Description") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (eventName.isNotBlank() && eventDate.isNotBlank()) {
                            events.add(
                                0,
                                CampusEvent(
                                    title = eventName.trim(),
                                    category = eventCategory.ifBlank { "General" }.trim(),
                                    date = eventDate.trim(),
                                    venue = eventVenue.ifBlank { "To be announced" }.trim(),
                                    description = eventDescription.trim()
                                )
                            )
                            eventName = ""
                            eventCategory = ""
                            eventDate = ""
                            eventVenue = ""
                            eventDescription = ""
                            showCreateDialog = false
                        }
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
