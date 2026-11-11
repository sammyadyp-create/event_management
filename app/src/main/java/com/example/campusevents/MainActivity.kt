package com.example.campusevents
import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

private val Navy = Color(0xFF172554)
private val Purple = Color(0xFF6D5CE8)
private val PageBackground = Color(0xFFF7F8FC)

data class CampusEvent(
    val title: String,
    val category: String,
    val date: String,
    val venue: String,
    val description: String,
    val fee: Int = 0,
    val posterColor: Color = Purple,
    val posterImageRes: Int? = null
)

data class StudentRegistration(
    val studentName: String,
    val email: String,
    val phone: String,
    val studentId: String,
    val department: String,
    val eventTitle: String,
    val paymentMethod: String
)

val sampleEvents = listOf(
    CampusEvent(
        title = "Annual Cultural Fest",
        category = "Cultural",
        date = "15 November 2026",
        venue = "College Auditorium",
        description = "A day of music, dance and performances by students.",
        fee = 0,
        posterColor = Color(0xFF6D5CE8),
        posterImageRes = R.drawable.cultural_fest_poster
    ),
    CampusEvent(
        title = "Code Sprint",
        category = "Technical",
        date = "20 November 2026",
        venue = "Computer Lab",
        description = "A friendly coding challenge for student developers.",
        fee = 0,
        posterColor = Color(0xFF172554),
        posterImageRes = R.drawable.code_sprint_poster
    ),
    CampusEvent(
        title = "Photography Walk",
        category = "Creative",
        date = "25 November 2026",
        venue = "College Campus",
        description = "Explore photography and capture campus life.",
        fee = 0,
        posterColor = Color(0xFF0D9488)
    )
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseAuth.getInstance().signOut()

        setContent {
            var currentScreen by remember { mutableStateOf("login") } // "login", "user_app", "admin_app"

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = PageBackground) {
                    when (currentScreen) {
                        "user_app" -> CampusEventsApp(onLogout = { currentScreen = "login" })
                        "admin_app" -> AdminDashboardApp(onLogout = { currentScreen = "login" })
                        else -> LoginScreen(
                            onUserLogin = { currentScreen = "user_app" },
                            onAdminLogin = { currentScreen = "admin_app" }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    onUserLogin: () -> Unit,
    onAdminLogin: () -> Unit
) {
    var isAdminMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.campus_logo),
            contentDescription = "Campus Events Logo",
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(120.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            if (isAdminMode) "Admin Portal Login" else "Sign in to explore and register for events",
            color = Color.Gray,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Role Switcher Tabs
        Row(
            modifier = Modifier.fillMaxWidth().background(Color(0xFFE2E8F0), RoundedCornerShape(12.dp)).padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { isAdminMode = false; email = ""; password = ""; errorMessage = "" },
                modifier = Modifier.weight(1f).padding(end = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (!isAdminMode) Navy else Color.Transparent)
            ) {
                Text("User Login", color = if (!isAdminMode) Color.White else Navy)
            }
            Button(
                onClick = { isAdminMode = true; email = "admin@campus.com"; password = "admin123"; errorMessage = "" },
                modifier = Modifier.weight(1f).padding(start = 4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isAdminMode) Navy else Color.Transparent)
            ) {
                Text("Admin Login", color = if (isAdminMode) Color.White else Navy)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email address") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        if (errorMessage.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, color = Color(0xFFDC3545), fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Please enter email and password"
                    return@Button
                }
                if (isAdminMode) {
                    if (email.trim() == "admin@campus.com" && password == "admin123") {
                        onAdminLogin()
                    } else {
                        errorMessage = "Invalid admin credentials. Use admin@campus.com / admin123"
                    }
                } else {
                    isLoading = true
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email.trim(), password)
                        .addOnSuccessListener {
                            isLoading = false
                            onUserLogin()
                        }
                        .addOnFailureListener {
                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.trim(), password)
                                .addOnSuccessListener {
                                    isLoading = false
                                    onUserLogin()
                                }
                                .addOnFailureListener { err ->
                                    isLoading = false
                                    errorMessage = err.message ?: "Authentication failed"
                                }
                        }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(if (isAdminMode) "Login as Admin" else "Login")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusEventsApp(onLogout: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedEvent by remember { mutableStateOf<CampusEvent?>(null) }
    var pendingRegistration by remember { mutableStateOf<StudentRegistration?>(null) }
    val registrations = remember { mutableStateListOf<StudentRegistration>() }
    var currentPage by remember { mutableStateOf("main") }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = PageBackground) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Image(
                                painter = painterResource(id = R.drawable.campus_logo),
                                contentDescription = "Campus Events Logo",
                                modifier = Modifier
                                    .height(36.dp)
                                    .padding(vertical = 2.dp)
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFC084FC))
                    )
                },
                bottomBar = {
                    if (currentPage == "main" && selectedEvent == null) {
                        NavigationBar(containerColor = Color.White) {
                            NavigationBarItem(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home") }
                            )
                            NavigationBarItem(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
                                label = { Text("Explore") }
                            )
                            NavigationBarItem(
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 },
                                icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "My Events") },
                                label = { Text("My Events") }
                            )
                            NavigationBarItem(
                                selected = selectedTab == 3,
                                onClick = { selectedTab = 3 },
                                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                                label = { Text("Profile") }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    when (currentPage) {
                        "registration" -> RegistrationScreen(
                            event = selectedEvent!!,
                            onBack = { currentPage = "details" },
                            onContinue = {
                                pendingRegistration = it
                                currentPage = "payment"
                            }
                        )
                        "payment" -> PaymentScreen(
                            event = selectedEvent!!,
                            onBack = { currentPage = "registration" },
                            onPaymentComplete = { method ->
                                val registration = pendingRegistration
                                if (registration != null) {
                                    registrations.add(registration.copy(paymentMethod = method))
                                }
                                currentPage = "confirmation"
                            }
                        )
                        "confirmation" -> ConfirmationScreen(
                            onDone = {
                                currentPage = "main"
                                selectedTab = 2
                                selectedEvent = null
                                pendingRegistration = null
                            }
                        )
                        else -> {
                            if (selectedEvent != null) {
                                EventDetailsScreen(
                                    event = selectedEvent!!,
                                    onBack = { selectedEvent = null },
                                    onRegister = { currentPage = "registration" }
                                )
                            } else {
                                when (selectedTab) {
                                    0 -> HomeScreen(onEventClick = { selectedEvent = it })
                                    1 -> ExploreScreen(onEventClick = { selectedEvent = it })
                                    2 -> MyEventsScreen(registrations)
                                    else -> ProfileScreen(onLogout = onLogout)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(onEventClick: (CampusEvent) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Hello, Student 👋", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Navy)
            Text("Find something exciting happening on campus.", color = Color.DarkGray)
        }
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Navy),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("CAMPUS HIGHLIGHT", color = Color(0xFFC7D2FE), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Make memories.\nJoin the next event.", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Discover events, register and participate.", color = Color.White)
                }
            }
        }
        item { SectionHeading("Upcoming Events") }
        items(sampleEvents) { event -> EventCard(event = event, onClick = { onEventClick(event) }) }
    }
}

@Composable
private fun ExploreScreen(onEventClick: (CampusEvent) -> Unit) {
    var searchText by remember { mutableStateOf("") }
    val filteredEvents = sampleEvents.filter {
        it.title.contains(searchText, ignoreCase = true) ||
                it.category.contains(searchText, ignoreCase = true)
    }
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Explore Events", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Navy)
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Search events or categories") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filteredEvents) { event -> EventCard(event = event, onClick = { onEventClick(event) }) }
        }
    }
}

@Composable
private fun MyEventsScreen(registrations: List<StudentRegistration>) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("My Events", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Navy)
        Spacer(modifier = Modifier.height(12.dp))
        if (registrations.isEmpty()) {
            Text("You haven't registered for any events yet.", color = Color.Gray)
            Text("Explore events and register to see them here.", modifier = Modifier.padding(top = 8.dp))
        } else {
            registrations.forEach { registration ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(registration.eventTitle, fontWeight = FontWeight.Bold, color = Navy)
                        Text("Registered by ${registration.studentName}", color = Color.DarkGray)
                        Text("Payment: ${registration.paymentMethod}", color = Purple)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileScreen(onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(88.dp).background(Purple, RoundedCornerShape(44.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White, modifier = Modifier.size(48.dp))
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text("Student Name", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Navy)
        Text("student@example.com", color = Color.Gray)
        Spacer(modifier = Modifier.height(24.dp))
        ProfileInfoRow("Role", "Student")
        ProfileInfoRow("College", "Your College Name")
        ProfileInfoRow("Account", "Demo profile")
        Spacer(modifier = Modifier.height(28.dp))
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                onLogout()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC3545)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout", color = Color.White)
        }
    }
}

@Composable
private fun RegistrationScreen(
    event: CampusEvent,
    onBack: () -> Unit,
    onContinue: (StudentRegistration) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("‹ Back", color = Purple, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onBack() })
        Spacer(modifier = Modifier.height(12.dp))
        Text("Event Registration", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Navy)
        Text(event.title, color = Purple, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(14.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                OutlinedTextField(name, { name = it }, label = { Text("Full name *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
            item {
                OutlinedTextField(email, { email = it }, label = { Text("Email address *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
            item {
                OutlinedTextField(phone, { phone = it }, label = { Text("Mobile number *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
            item {
                OutlinedTextField(studentId, { studentId = it }, label = { Text("Student ID / PRN *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
            item {
                OutlinedTextField(department, { department = it }, label = { Text("Department / Year *") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
            item {
                if (error.isNotBlank()) {
                    Text(error, color = Color(0xFFDC3545))
                }
                Text("Fields marked * are required.", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        error = when {
                            name.isBlank() || email.isBlank() || phone.isBlank() ||
                                    studentId.isBlank() || department.isBlank() -> "Please complete all required fields."
                            !email.contains("@") -> "Please enter a valid email address."
                            phone.filter { it.isDigit() }.length < 10 -> "Please enter a valid mobile number."
                            else -> ""
                        }

                        if (error.isBlank()) {
                            val registrationData = hashMapOf(
                                "studentName" to name.trim(),
                                "email" to email.trim(),
                                "phone" to phone.trim(),
                                "studentId" to studentId.trim(),
                                "department" to department.trim(),
                                "eventName" to event.title
                            )

                            FirebaseFirestore.getInstance()
                                .collection("registrations")
                                .add(registrationData)
                                .addOnSuccessListener {
                                    onContinue(
                                        StudentRegistration(
                                            studentName = name.trim(),
                                            email = email.trim(),
                                            phone = phone.trim(),
                                            studentId = studentId.trim(),
                                            department = department.trim(),
                                            eventTitle = event.title,
                                            paymentMethod = ""
                                        )
                                    )
                                }
                                .addOnFailureListener { exception ->
                                    error = "Failed to save registration: ${exception.message}"
                                }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continue to Payment")
                }
            }
        }
    }
}

@Composable
private fun PaymentScreen(
    event: CampusEvent,
    onBack: () -> Unit,
    onPaymentComplete: (String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("UPI") }
    var showDemoNotice by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("‹ Back", color = Purple, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onBack() })
        Spacer(modifier = Modifier.height(16.dp))
        Text("Payment", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Navy)
        Text(event.title, color = Color.Gray)
        Spacer(modifier = Modifier.height(18.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text("Registration fee", color = Color.Gray)
                Text(if (event.fee == 0) "FREE" else "₹${event.fee}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Navy)
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text("Choose payment method", fontWeight = FontWeight.Bold, color = Navy)
        listOf("UPI", "Debit / Credit Card", "Pay at venue").forEach { method ->
            Row(
                modifier = Modifier.fillMaxWidth().clickable { selectedMethod = method }.padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = selectedMethod == method, onClick = { selectedMethod = method })
                Text(method, modifier = Modifier.padding(start = 8.dp))
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Button(
            onClick = {
                if (event.fee == 0 || selectedMethod == "Pay at venue") {
                    onPaymentComplete(if (event.fee == 0) "Free registration" else selectedMethod)
                } else {
                    showDemoNotice = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (event.fee == 0) "Confirm Registration" else "Proceed to Payment")
        }
        Text(
            "Demo payment screen: no money will be charged.",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 12.dp)
        )
    }

    if (showDemoNotice) {
        AlertDialog(
            onDismissRequest = { showDemoNotice = false },
            title = { Text("Payment gateway not connected") },
            text = { Text("The payment option is shown as a UI prototype only.") },
            confirmButton = {
                TextButton(onClick = { showDemoNotice = false }) { Text("OK") }
            }
        )
    }
}

@Composable
private fun ConfirmationScreen(onDone: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("✓", fontSize = 54.sp, color = Color(0xFF16865B), fontWeight = FontWeight.Bold)
        Text("Registration Confirmed!", fontSize = 24.sp, color = Navy, fontWeight = FontWeight.Bold)
        Text("Your event registration has been saved.", color = Color.Gray, modifier = Modifier.padding(top = 10.dp))
        Spacer(modifier = Modifier.height(22.dp))
        Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) { Text("Go to My Events") }
    }
}

@Composable
private fun EventDetailsScreen(
    event: CampusEvent,
    onBack: () -> Unit,
    onRegister: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("‹ Back", color = Purple, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onBack() })
        }
        item {
            // Event Poster / Banner Image
            if (event.posterImageRes != null) {
                Image(
                    painter = painterResource(id = event.posterImageRes),
                    contentDescription = event.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(event.posterColor, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color.White, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(event.category.uppercase(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item {
            // Name of event below poster
            Text(event.title, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Navy)
        }
        item {
            ProfileInfoRow("Date", event.date)
            ProfileInfoRow("Venue", event.venue)
            ProfileInfoRow("Registration fee", if (event.fee == 0) "Free" else "₹${event.fee}")
        }
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text("About this event", fontWeight = FontWeight.Bold, color = Navy, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(event.description, color = Color.DarkGray)
        }
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = onRegister, modifier = Modifier.fillMaxWidth()) {
                Text("Register Now")
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = Color.Gray)
            Text(value, color = Navy, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun EventCard(event: CampusEvent, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Event Poster / Banner Image above the name
            if (event.posterImageRes != null) {
                Image(
                    painter = painterResource(id = event.posterImageRes),
                    contentDescription = event.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(event.posterColor),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(event.category.uppercase(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            // Name of event and details below poster
            Column(modifier = Modifier.padding(16.dp)) {
                Text(event.title, color = Navy, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text("📅 ${event.date}", color = Color.DarkGray, fontSize = 13.sp)
                Text("📍 ${event.venue}", color = Color.DarkGray, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("View details →", color = Purple, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun SectionHeading(title: String) {
    Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Navy)
}