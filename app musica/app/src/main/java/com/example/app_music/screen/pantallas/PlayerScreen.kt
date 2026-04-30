package com.example.app_music.screen.pantallas

import androidx.compose.animation.core.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.example.app_music.data.Song
import com.example.app_music.ui.theme.*

@Composable
fun PlayerScreen(
    song: Song,
    isPlaying: Boolean,
    progress: Float,
    isLiked: Boolean,
    isShuffle: Boolean,
    isRepeat: Boolean,
    onBack: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onProgressChange: (Float) -> Unit,
    onLike: () -> Unit,
    onShuffle: () -> Unit,
    onRepeat: () -> Unit,
    onMoreClick: (Song) -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = if (isPlaying) 1.04f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "artScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        song.color1.copy(alpha = 0.8f),
                        song.color2.copy(alpha = 0.5f),
                        DeepBlack,
                        DeepBlack,
                    )
                )
            )
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        // Top bar
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Volver", tint = TextPrimary, modifier = Modifier.size(32.dp))
            }
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("REPRODUCIENDO AHORA", color = TextMuted, fontSize = 10.sp, letterSpacing = 2.sp)
                Text(song.album, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            IconButton(onClick = { onMoreClick(song) }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Más opciones", tint = TextPrimary)
            }
        }

        Spacer(Modifier.height(40.dp))

        // Album Art
        Box(
            modifier = Modifier
                .size(280.dp)
                .scale(scale)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(listOf(song.color1, song.color2)))
                .shadow(32.dp, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Vinyl pattern
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                for (r in listOf(0.85f, 0.7f, 0.55f, 0.4f, 0.25f)) {
                    drawCircle(
                        color  = Color.Black.copy(alpha = 0.15f),
                        radius = (size.minDimension / 2f) * r,
                        center = Offset(cx, cy),
                        style  = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                    )
                }
            }
            Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White.copy(alpha = 0.4f), modifier = Modifier.size(80.dp))
        }

        Spacer(Modifier.height(36.dp))

        // Song info + like
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(song.title, color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(song.artist, color = TextMuted, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            IconButton(onClick = onLike) {
                Icon(
                    if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isLiked) "Quitar de favoritos" else "Agregar a favoritos",
                    tint   = if (isLiked) AccentPink else TextMuted,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        // Progress Bar
        Column {
            Slider(
                value          = progress,
                onValueChange  = onProgressChange,
                colors         = SliderDefaults.colors(
                    thumbColor        = Color.White,
                    activeTrackColor  = Color.White,
                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                val totalSecs = song.durationMs / 1000
                val elapsed = (totalSecs * progress).toInt()
                Text(
                    "%d:%02d".format(elapsed / 60, elapsed % 60),
                    color = TextMuted, fontSize = 12.sp
                )
                Text(song.duration, color = TextMuted, fontSize = 12.sp)
            }
        }

        Spacer(Modifier.height(24.dp))

        // Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            // Shuffle
            IconButton(onClick = onShuffle) {
                Icon(
                    Icons.Default.Shuffle,
                    contentDescription = "Mezclar",
                    tint     = if (isShuffle) AccentPink else TextMuted,
                    modifier = Modifier.size(22.dp)
                )
            }
            // Previous
            IconButton(onClick = onPrev) {
                Icon(Icons.Default.SkipPrevious, contentDescription = "Anterior", tint = TextPrimary, modifier = Modifier.size(36.dp))
            }
            // Play/Pause
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(onClick = onPlayPause),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pausar" else "Reproducir",
                    tint     = DeepBlack,
                    modifier = Modifier.size(34.dp)
                )
            }
            // Next
            IconButton(onClick = onNext) {
                Icon(Icons.Default.SkipNext, contentDescription = "Siguiente", tint = TextPrimary, modifier = Modifier.size(36.dp))
            }
            // Repeat
            IconButton(onClick = onRepeat) {
                Icon(
                    Icons.Default.Repeat,
                    contentDescription = "Repetir",
                    tint     = if (isRepeat) AccentPink else TextMuted,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        // Bottom actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = {}) {
                Icon(Icons.Default.DevicesOther, contentDescription = "Dispositivos", tint = TextMuted, modifier = Modifier.size(22.dp))
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.QueueMusic, contentDescription = "Cola", tint = TextMuted, modifier = Modifier.size(22.dp))
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.Share, contentDescription = "Compartir", tint = TextMuted, modifier = Modifier.size(22.dp))
            }
            IconButton(onClick = {}) {
                Icon(Icons.Default.PlaylistAdd, contentDescription = "Agregar a playlist", tint = TextMuted, modifier = Modifier.size(22.dp))
            }
        }
    }
}
