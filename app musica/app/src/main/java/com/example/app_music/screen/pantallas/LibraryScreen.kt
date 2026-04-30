package com.example.app_music.screen.pantallas

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.*
import com.example.app_music.data.Playlist
import com.example.app_music.data.Song
import com.example.app_music.ui.theme.*
import com.example.app_music.screen.SongListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    playlists: List<Playlist>,
    songs: List<Song>,
    onSongClick: (Song) -> Unit,
    onCreatePlaylist: (String) -> Unit,
    onDeletePlaylist: (String) -> Unit,
    onRemoveSongFromPlaylist: (String, Long) -> Unit,
    onAddSongToPlaylist: (String, Long) -> Unit,
    onMoreClick: (Song) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Playlists", "Canciones", "Artistas")
    var showCreateDialog by remember { mutableStateOf(false) }
    var playlistName by remember { mutableStateOf("") }
    
    var selectedPlaylist by remember { mutableStateOf<Playlist?>(null) }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Nueva lista de reproducción", color = TextPrimary) },
            text = {
                TextField(
                    value = playlistName,
                    onValueChange = { playlistName = it },
                    placeholder = { Text("Nombre de la lista") },
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
                    if (playlistName.isNotBlank()) {
                        onCreatePlaylist(playlistName)
                        playlistName = ""
                        showCreateDialog = false
                    }
                }) {
                    Text("Crear", color = AccentPink)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancelar", color = TextMuted)
                }
            },
            containerColor = SurfaceDark
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .padding(top = 24.dp)
    ) {
        if (selectedPlaylist == null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Biblioteca",
                    color = TextPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { showCreateDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Nueva playlist", tint = TextPrimary)
                }
            }

            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabs.forEachIndexed { idx, tab ->
                    val selected = selectedTab == idx
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selected) AccentPink else CardDark)
                            .clickable { selectedTab = idx }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            tab,
                            color = if (selected) Color.White else TextMuted,
                            fontSize = 13.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            when (selectedTab) {
                0 -> PlaylistsTab(playlists, onPlaylistClick = { selectedPlaylist = it })
                1 -> SongsTab(songs, onSongClick, onMoreClick)
                2 -> ArtistsTab(songs)
            }
        } else {
            // View Playlist Songs
            PlaylistDetailView(
                playlist = selectedPlaylist!!,
                allSongs = songs,
                onBack = { selectedPlaylist = null },
                onSongClick = onSongClick,
                onMoreClick = onMoreClick,
                onRemoveSong = { songId -> 
                    onRemoveSongFromPlaylist(selectedPlaylist!!.id, songId)
                    // Update local state to reflect change
                    selectedPlaylist = selectedPlaylist!!.copy(
                        songIds = selectedPlaylist!!.songIds - songId
                    )
                },
                onDeletePlaylist = {
                    onDeletePlaylist(selectedPlaylist!!.id)
                    selectedPlaylist = null
                },
                onAddSong = { songId ->
                    onAddSongToPlaylist(selectedPlaylist!!.id, songId)
                    selectedPlaylist = selectedPlaylist!!.copy(
                        songIds = selectedPlaylist!!.songIds + songId
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailView(
    playlist: Playlist,
    allSongs: List<Song>,
    onBack: () -> Unit,
    onSongClick: (Song) -> Unit,
    onMoreClick: (Song) -> Unit,
    onRemoveSong: (Long) -> Unit,
    onDeletePlaylist: () -> Unit,
    onAddSong: (Long) -> Unit
) {
    val playlistSongs = allSongs.filter { playlist.songIds.contains(it.id) }
    var showAddSongs by remember { mutableStateOf(false) }

    if (showAddSongs) {
        ModalBottomSheet(
            onDismissRequest = { showAddSongs = false },
            containerColor = SurfaceDark
        ) {
            LazyColumn(modifier = Modifier.fillMaxHeight(0.8f).padding(bottom = 24.dp)) {
                item {
                    Text(
                        "Añadir canciones a ${playlist.name}",
                        modifier = Modifier.padding(16.dp),
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                val availableSongs = allSongs.filter { !playlist.songIds.contains(it.id) }
                items(availableSongs) { song ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAddSong(song.id) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(song.title, color = TextPrimary)
                            Text(song.artist, color = TextMuted, fontSize = 12.sp)
                        }
                        Icon(Icons.Default.Add, contentDescription = null, tint = AccentPink)
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextPrimary)
            }
            Text(
                playlist.name,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { showAddSongs = true }) {
                Icon(Icons.Default.PlaylistAdd, contentDescription = null, tint = AccentPink)
            }
            IconButton(onClick = onDeletePlaylist) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
            }
        }

        if (playlistSongs.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay canciones en esta lista", color = TextMuted)
            }
        } else {
            LazyColumn {
                items(playlistSongs) { song ->
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.weight(1f)) {
                            SongListItem(song = song, onClick = { onSongClick(song) }, onMoreClick = { onMoreClick(song) })
                        }
                        IconButton(onClick = { onRemoveSong(song.id) }) {
                            Icon(Icons.Default.RemoveCircleOutline, contentDescription = null, tint = Color.Red.copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlaylistsTab(playlists: List<Playlist>, onPlaylistClick: (Playlist) -> Unit) {
    if (playlists.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No tienes listas de reproducción", color = TextMuted)
        }
    } else {
        LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)) {
            items(playlists) { playlist ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPlaylistClick(playlist) }
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Brush.linearGradient(listOf(playlist.color, playlist.color.copy(alpha = 0.3f)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.QueueMusic, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(playlist.name, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        Text("${playlist.songIds.size} canciones", color = TextMuted, fontSize = 13.sp)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
                }
                HorizontalDivider(color = Separator, thickness = 0.5.dp)
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun SongsTab(songs: List<Song>, onSongClick: (Song) -> Unit, onMoreClick: (Song) -> Unit) {
    LazyColumn {
        items(songs) { song ->
            SongListItem(song = song, onClick = { onSongClick(song) }, onMoreClick = { onMoreClick(song) })
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
fun ArtistsTab(songs: List<Song>) {
    val artists = songs.map { it.artist }.distinct().sorted()
    LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)) {
        items(artists) { artist ->
            val artistSong = songs.first { it.artist == artist }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {}
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(artistSong.color1, artistSong.color2))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        artist.first().toString(),
                        color      = Color.White,
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(artist, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    val count = songs.count { it.artist == artist }
                    Text("$count canción${if (count != 1) "es" else ""}", color = TextMuted, fontSize = 13.sp)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
            }
            HorizontalDivider(color = Separator, thickness = 0.5.dp)
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}
