package com.example.app_music.screen

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.example.app_music.MusicViewModel
import com.example.app_music.data.*
import com.example.app_music.ui.theme.*
import com.example.app_music.screen.pantallas.*

enum class BottomTab { HOME, SEARCH, LIBRARY }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicApp(viewModel: MusicViewModel) {
    var currentTab by remember { mutableStateOf(BottomTab.HOME) }
    val songs by viewModel.songs.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showPlayer by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Launcher for Scoped Storage permissions
    val intentSenderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            viewModel.loadSongs()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.pendingIntent.collect { intentSender ->
            intentSender?.let {
                intentSenderLauncher.launch(
                    IntentSenderRequest.Builder(it).build()
                )
            }
        }
    }

    // Song Options State
    var selectedSongForOptions by remember { mutableStateOf<Song?>(null) }
    var showSongOptions by remember { mutableStateOf(false) }
    var showAddToPlaylist by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var editTitle by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState()

    // Edit Dialog
    if (showEditDialog && selectedSongForOptions != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar nombre", color = TextPrimary) },
            text = {
                TextField(
                    value = editTitle,
                    onValueChange = { editTitle = it },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CardDark,
                        unfocusedContainerColor = CardDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (editTitle.isNotBlank()) {
                        viewModel.renameSong(selectedSongForOptions!!, editTitle)
                        showEditDialog = false
                    }
                }) {
                    Text("Guardar", color = AccentPink)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar", color = TextMuted)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // Delete Confirm Dialog
    if (showDeleteConfirm && selectedSongForOptions != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Eliminar audio", color = TextPrimary) },
            text = { Text("¿Estás seguro de que quieres eliminar \"${selectedSongForOptions!!.title}\" del dispositivo?", color = TextPrimary) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteSong(selectedSongForOptions!!)
                    showDeleteConfirm = false
                }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar", color = TextMuted)
                }
            },
            containerColor = SurfaceDark
        )
    }

    if (showSongOptions && selectedSongForOptions != null) {
        ModalBottomSheet(
            onDismissRequest = { showSongOptions = false },
            sheetState = sheetState,
            containerColor = SurfaceDark
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
                Text(
                    selectedSongForOptions!!.title,
                    modifier = Modifier.padding(16.dp),
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                ListItem(
                    headlineContent = { Text("Añadir a lista de reproducción", color = TextPrimary) },
                    leadingContent = { Icon(Icons.Default.PlaylistAdd, contentDescription = null, tint = AccentPink) },
                    modifier = Modifier.clickable {
                        showSongOptions = false
                        showAddToPlaylist = true
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
                ListItem(
                    headlineContent = { Text("Editar", color = TextPrimary) },
                    leadingContent = { Icon(Icons.Default.Edit, contentDescription = null, tint = AccentPink) },
                    modifier = Modifier.clickable {
                        editTitle = selectedSongForOptions!!.title
                        showSongOptions = false
                        showEditDialog = true
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
                ListItem(
                    headlineContent = { Text("Compartir", color = TextPrimary) },
                    leadingContent = { Icon(Icons.Default.Share, contentDescription = null, tint = AccentPink) },
                    modifier = Modifier.clickable {
                        showSongOptions = false
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "audio/*"
                            putExtra(Intent.EXTRA_STREAM, selectedSongForOptions!!.contentUri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Compartir audio"))
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
                ListItem(
                    headlineContent = { Text("Eliminar del dispositivo", color = Color.Red) },
                    leadingContent = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) },
                    modifier = Modifier.clickable {
                        showSongOptions = false
                        showDeleteConfirm = true
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
        }
    }

    if (showAddToPlaylist && selectedSongForOptions != null) {
        ModalBottomSheet(
            onDismissRequest = { showAddToPlaylist = false },
            containerColor = SurfaceDark
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
                Text(
                    "Seleccionar lista",
                    modifier = Modifier.padding(16.dp),
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                if (playlists.isEmpty()) {
                    Text(
                        "No tienes listas creadas",
                        modifier = Modifier.padding(16.dp),
                        color = TextMuted
                    )
                }
                playlists.forEach { playlist ->
                    ListItem(
                        headlineContent = { Text(playlist.name, color = TextPrimary) },
                        leadingContent = { Icon(Icons.Default.QueueMusic, contentDescription = null, tint = AccentPink) },
                        modifier = Modifier.clickable {
                            viewModel.addSongToPlaylist(playlist.id, selectedSongForOptions!!.id)
                            showAddToPlaylist = false
                            selectedSongForOptions = null
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(DeepBlack)) {
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentPink)
            }
        } else {
            if (showPlayer && currentSong != null) {
                PlayerScreen(
                    song = currentSong!!,
                    isPlaying = isPlaying,
                    progress = progress,
                    isLiked = false, // State can be added later
                    isShuffle = false,
                    isRepeat = false,
                    onBack = { showPlayer = false },
                    onPlayPause = { viewModel.togglePlayPause() },
                    onNext = { viewModel.playNext() },
                    onPrev = { viewModel.playPrevious() },
                    onProgressChange = { viewModel.seekTo(it) },
                    onLike = { },
                    onShuffle = { },
                    onRepeat = { },
                    onMoreClick = { song ->
                        selectedSongForOptions = song
                        showSongOptions = true
                    }
                )
            } else {
                Scaffold(
                    containerColor = DeepBlack,
                    bottomBar = {
                        BottomNavBar(
                            currentTab = currentTab,
                            onTabChange = { currentTab = it }
                        )
                    }
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        when (currentTab) {
                            BottomTab.HOME -> HomeScreen(
                                songs = songs,
                                onSongClick = { song ->
                                    viewModel.playSong(song)
                                    showPlayer = true
                                },
                                onMoreClick = { song ->
                                    selectedSongForOptions = song
                                    showSongOptions = true
                                }
                            )
                            BottomTab.SEARCH -> SearchScreen(
                                songs = songs,
                                onSongClick = { song ->
                                    viewModel.playSong(song)
                                    showPlayer = true
                                },
                                onMoreClick = { song ->
                                    selectedSongForOptions = song
                                    showSongOptions = true
                                }
                            )
                            BottomTab.LIBRARY -> LibraryScreen(
                                playlists = playlists,
                                songs = songs,
                                onSongClick = { song ->
                                    viewModel.playSong(song)
                                    showPlayer = true
                                },
                                onCreatePlaylist = { name -> viewModel.createPlaylist(name) },
                                onDeletePlaylist = { id -> viewModel.deletePlaylist(id) },
                                onAddSongToPlaylist = { pid, sid -> viewModel.addSongToPlaylist(pid, sid) },
                                onRemoveSongFromPlaylist = { pid, sid -> viewModel.removeSongFromPlaylist(pid, sid) },
                                onMoreClick = { song ->
                                    selectedSongForOptions = song
                                    showSongOptions = true
                                }
                            )
                        }
                    }
                }

                // Mini Player
                if (currentSong != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        MiniPlayer(
                            song = currentSong!!,
                            isPlaying = isPlaying,
                            onPlayPause = { viewModel.togglePlayPause() },
                            onNext = { viewModel.playNext() },
                            onClick = { showPlayer = true }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(currentTab: BottomTab, onTabChange: (BottomTab) -> Unit) {
    NavigationBar(
        containerColor = SurfaceDark,
        tonalElevation = 0.dp,
        modifier = Modifier.height(72.dp)
    ) {
        listOf(
            Triple(BottomTab.HOME, Icons.Default.Home, "Inicio"),
            Triple(BottomTab.SEARCH, Icons.Default.Search, "Buscar"),
            Triple(BottomTab.LIBRARY, Icons.Default.LibraryMusic, "Biblioteca"),
        ).forEach { (tab, icon, label) ->
            NavigationBarItem(
                selected = currentTab == tab,
                onClick = { onTabChange(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (currentTab == tab) AccentPink else TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        label,
                        fontSize = 10.sp,
                        color = if (currentTab == tab) AccentPink else TextMuted
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = CardDark
                )
            )
        }
    }
}

@Composable
fun MiniPlayer(song: Song, isPlaying: Boolean, onPlayPause: () -> Unit, onNext: () -> Unit, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    listOf(song.color1.copy(alpha = 0.9f), CardDark)
                )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Brush.linearGradient(listOf(song.color1, song.color2))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(song.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(song.artist, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            IconButton(onClick = onPlayPause) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            IconButton(onClick = onNext) {
                Icon(Icons.Default.SkipNext, contentDescription = "Siguiente", tint = Color.White, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
fun SongListItem(song: Song, index: Int? = null, onClick: () -> Unit, onMoreClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (index != null) {
            Text(
                "$index",
                color = TextMuted,
                fontSize = 13.sp,
                modifier = Modifier.width(24.dp),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.width(12.dp))
        }
        // Artwork
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Brush.linearGradient(listOf(song.color1, song.color2))),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(song.title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(song.artist, color = TextMuted, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.width(8.dp))
        Text(song.duration, color = TextMuted, fontSize = 12.sp)
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = onMoreClick) {
            Icon(Icons.Default.MoreVert, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
        }
    }
}
