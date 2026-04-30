package com.example.app_music.data

import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.example.app_music.ui.theme.*

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: String,
    val durationMs: Long,
    val contentUri: Uri,
    val color1: Color = AccentPink,
    val color2: Color = AccentBlue,
    val genre: String = ""
)

data class Playlist(
    val id: String,
    val name: String,
    val songIds: List<Long> = emptyList(),
    val color: Color = AccentPink
)
