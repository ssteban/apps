package com.example.studybotia.pantalla

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studybotia.model.IARequest
import com.example.studybotia.model.Message
import com.example.studybotia.network.RetrofitClient
import com.example.studybotia.ui.theme.StudyBotIATheme
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.material3.Material3RichText
import com.tuapp.studybotia.data.AppDatabase
import com.tuapp.studybotia.data.ChatRepository
import kotlinx.coroutines.launch
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi

class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StudyBotIATheme {
                ChatScreen(this)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(activity: ComponentActivity) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val db = remember {
        AppDatabase(context)
    }

    val repo = remember {
        ChatRepository(db.messageDao())
    }

    var input by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<Message>()) }
    var loading by remember { mutableStateOf(false) }

    val azulPrimario = Color(0xFF1E88E5)
    val fondoChat = Color(0xFFF8F9FA)

    LaunchedEffect(Unit) {
        val historial = repo.getMessages().map {
            Message(it.text, it.isUser)
        }
        messages = historial
    }

    fun limpiarChat() {
        scope.launch {
            repo.clearChat()
            messages = emptyList()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF00ACC1), azulPrimario)
                            )
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Text(
                        "StudyBot IA",
                        fontSize = 24.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.List, null, tint = azulPrimario) },
                    label = { Text("Chat Principal") },
                    selected = true,
                    onClick = {
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Delete, null, tint = Color.Gray) },
                    label = { Text("Nuevo Chat") },
                    selected = false,
                    onClick = {
                        limpiarChat()
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.ExitToApp, null, tint = Color.Red) },
                    label = { Text("Cerrar sesión", color = Color.Red) },
                    selected = false,
                    onClick = {
                        activity.startActivity(
                            Intent(activity, LoginActivity::class.java)
                        )
                        activity.finish()
                    },
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 20.dp
                    )
                )
            }
        }
    ) {

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "StudyBot IA",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            }
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                null,
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = azulPrimario
                    )
                )
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(fondoChat)
            ) {

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    if (messages.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {

                                Text(
                                    "¿Cómo puedo ayudarte?",
                                    color = Color.Gray
                                )

                                SuggestionItem("Explícame este tema") {
                                    input = "Explícame este tema"
                                }

                                SuggestionItem("Resume este texto") {
                                    input = "Resume este texto"
                                }
                            }
                        }
                    }

                    items(messages) { msg ->
                        ChatBubbleItem(msg, azulPrimario)
                    }

                    if (loading) {
                        item {
                            Text(
                                "Escribiendo...",
                                color = Color.Gray,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }

                Surface(
                    tonalElevation = 4.dp,
                    color = Color.White
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        OutlinedTextField(
                            value = input,
                            onValueChange = { input = it },
                            placeholder = {
                                Text("Escribe aquí...")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(25.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        FloatingActionButton(
                            onClick = {

                                if (input.isNotBlank() && !loading) {

                                    val userMessage = input
                                    val msgUser = Message(userMessage, true)

                                    messages = messages + msgUser
                                    input = ""
                                    loading = true

                                    scope.launch {

                                        repo.saveMessage(userMessage, true)

                                        try {

                                            val response =
                                                RetrofitClient.instance
                                                    .preguntarIA(
                                                        IARequest(userMessage)
                                                    )

                                            if (response.isSuccessful) {

                                                val respuestaIA =
                                                    response.body()?.response
                                                        ?: "Sin respuesta"

                                                val msgIA =
                                                    Message(
                                                        respuestaIA,
                                                        false
                                                    )

                                                messages =
                                                    messages + msgIA

                                                repo.saveMessage(
                                                    respuestaIA,
                                                    false
                                                )

                                            } else {

                                                messages =
                                                    messages + Message(
                                                        "Error del servidor",
                                                        false
                                                    )
                                            }

                                        } catch (e: Exception) {

                                            messages =
                                                messages + Message(
                                                    "Error de conexión",
                                                    false
                                                )
                                        }

                                        loading = false
                                    }
                                }
                            },
                            containerColor = azulPrimario,
                            shape = RoundedCornerShape(50.dp)
                        ) {
                            Icon(
                                Icons.Default.Send,
                                null,
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatBubbleItem(msg: Message, color: Color) {

    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            if (msg.isUser) Arrangement.End
            else Arrangement.Start
    ) {

        Card(
            modifier = Modifier.combinedClickable(
                onClick = {},
                onLongClick = {

                    clipboard.setText(
                        AnnotatedString(msg.text)
                    )

                    Toast.makeText(
                        context,
                        "Mensaje copiado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            ),

            shape = RoundedCornerShape(
                topStart = 15.dp,
                topEnd = 15.dp,
                bottomStart =
                    if (msg.isUser) 15.dp else 0.dp,
                bottomEnd =
                    if (msg.isUser) 0.dp else 15.dp
            ),

            colors = CardDefaults.cardColors(
                containerColor =
                    if (msg.isUser) color
                    else Color(0xFFE9E9EB)
            )
        ) {

            Material3RichText(
                modifier = Modifier.padding(12.dp)
            ) {

                ProvideTextStyle(
                    value = LocalTextStyle.current.copy(
                        color =
                            if (msg.isUser) Color.White
                            else Color.Black
                    )
                ) {
                    Markdown(msg.text)
                }
            }
        }
    }
}

@Composable
fun SuggestionItem(
    text: String,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },

        shape = RoundedCornerShape(12.dp),

        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),

        border = BorderStroke(
            1.dp,
            Color.LightGray
        )
    ) {

        Text(
            text = text,
            modifier = Modifier.padding(14.dp),
            color = Color.DarkGray
        )
    }
}