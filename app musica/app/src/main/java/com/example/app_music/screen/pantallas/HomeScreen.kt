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
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.example.app_music.data.Song
import com.example.app_music.ui.theme.*
import com.example.app_music.screen.SongListItem

@Composable
fun HomeScreen(songs: List<Song>, onSongClick: (Song) -> Unit, onMoreClick: (Song) -> Unit) {
    if (songs.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.MusicNote, contentDescription = null, tint = TextMuted, modifier = Modifier.size(64.dp))
                Spacer(Modifier.height(16.dp))
                Text("No se encontraron canciones", color = TextMuted)
            }
        }
    } else {
        LazyColumn(
            modifier            = Modifier.fillMaxSize().background(DeepBlack),
            contentPadding      = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFF1A0A2E), DeepBlack)
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            "Buenas tardes",
                            color    = TextMuted,
                            fontSize = 14.sp,
                            letterSpacing = 2.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "¿Qué quieres\nescuchar hoy?",
                            color      = TextPrimary,
                            fontSize   = 28.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 34.sp
                        )
                    }
                }
            }

            item {
                SectionTitle("Tu música")
            }

            items(songs) { song ->
                SongListItem(
                    song    = song,
                    index   = songs.indexOf(song) + 1,
                    onClick = { onSongClick(song) },
                    onMoreClick = { onMoreClick(song) }
                )
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text       = text,
        color      = TextPrimary,
        fontSize   = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier   = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 14.dp)
    )
}

@Composable
fun RecentSongCard(song: Song, onClick: () -> Unit) {
    Column(
        modifier       = Modifier.width(120.dp).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.linearGradient(listOf(song.color1, song.color2))),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.MusicNote,
                contentDescription = null,
                tint   = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            song.title,
            color      = TextPrimary,
            fontSize   = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines   = 1,
            overflow   = TextOverflow.Ellipsis,
            textAlign  = TextAlign.Center,
            modifier   = Modifier.fillMaxWidth()
        )
        Text(
            song.artist,
            color    = TextMuted,
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun GenreChip(genre: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(CardDark)
            .border(1.dp, Separator, RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(genre, color = TextPrimary, fontSize = 13.sp)
    }
}
