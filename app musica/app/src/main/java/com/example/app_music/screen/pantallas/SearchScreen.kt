package com.example.app_music.screen.pantallas

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
fun SearchScreen(songs: List<Song>, onSongClick: (Song) -> Unit, onMoreClick: (Song) -> Unit) {
    var query   by remember { mutableStateOf("") }
    val results = remember(query) {
        if (query.isBlank()) emptyList()
        else songs.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.artist.contains(query, ignoreCase = true) ||
                    it.album.contains(query, ignoreCase = true) ||
                    it.genre.contains(query, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .padding(top = 24.dp)
    ) {
        // Title
        Text(
            "Buscar",
            color      = TextPrimary,
            fontSize   = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier   = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )

        // Search Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CardDark)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            BasicTextField(
                value       = query,
                onValueChange = { query = it },
                singleLine  = true,
                textStyle   = androidx.compose.ui.text.TextStyle(
                    color    = TextPrimary,
                    fontSize = 16.sp
                ),
                decorationBox = { inner ->
                    if (query.isEmpty()) {
                        Text("Canciones, artistas, álbumes…", color = TextMuted, fontSize = 16.sp)
                    }
                    inner()
                },
                modifier = Modifier.weight(1f)
            )
            if (query.isNotEmpty()) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Limpiar",
                    tint     = TextMuted,
                    modifier = Modifier.size(18.dp).clickable { query = "" }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        if (query.isEmpty()) {
            // Browse categories
            Text(
                "Explorar categorías",
                color      = TextPrimary,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
            val categories = listOf(
                "Pop"        to Color(0xFFFF3E7E),
                "Hip-Hop"    to Color(0xFF3E7EFF),
                "R&B"        to Color(0xFFFFB347),
                "Rock"       to Color(0xFF4CAF50),
                "Electrónica" to Color(0xFF9C27B0),
                "Indie"      to Color(0xFF00BCD4),
                "K-Pop"      to Color(0xFFE91E63),
                "Jazz"       to Color(0xFFFFC107),
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement   = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(categories) { (name, color) ->
                    CategoryCard(name, color)
                }
            }
        } else {
            // Results
            if (results.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.SearchOff, contentDescription = null, tint = TextMuted, modifier = Modifier.size(56.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("No se encontraron resultados", color = TextMuted, fontSize = 14.sp)
                        Text("para \"$query\"", color = TextMuted, fontSize = 14.sp)
                    }
                }
            } else {
                Text(
                    "${results.size} resultado${if (results.size != 1) "s" else ""}",
                    color    = TextMuted,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(results) { song ->
                        SongListItem(song = song, onClick = { onSongClick(song) }, onMoreClick = { onMoreClick(song) })
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(name: String, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Brush.linearGradient(listOf(color, color.copy(alpha = 0.4f))))
            .clickable { },
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            name,
            color      = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize   = 15.sp,
            modifier   = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = false,
    textStyle: androidx.compose.ui.text.TextStyle = androidx.compose.ui.text.TextStyle.Default,
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit = { it() }
) {
    androidx.compose.foundation.text.BasicTextField(
        value         = value,
        onValueChange = onValueChange,
        modifier      = modifier,
        singleLine    = singleLine,
        textStyle     = textStyle,
        decorationBox = decorationBox
    )
}
