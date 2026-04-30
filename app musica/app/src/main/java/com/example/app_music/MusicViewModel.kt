package com.example.app_music

import android.app.Application
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.app_music.data.Playlist
import com.example.app_music.data.Song
import com.example.app_music.ui.theme.AccentPink
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs = _songs.asStateFlow()

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists = _playlists.asStateFlow()

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress = _progress.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _pendingIntent = MutableSharedFlow<android.content.IntentSender?>()
    val pendingIntent = _pendingIntent.asSharedFlow()

    private val exoPlayer = ExoPlayer.Builder(application).build()

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val currentMediaItem = mediaItem ?: return
                val song = _songs.value.find { it.id.toString() == currentMediaItem.mediaId }
                _currentSong.value = song
            }

            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    playNext()
                }
            }
        })

        viewModelScope.launch {
            while (true) {
                if (_isPlaying.value) {
                    val currentPos = exoPlayer.currentPosition
                    val duration = exoPlayer.duration
                    if (duration > 0) {
                        _progress.value = currentPos.toFloat() / duration.toFloat()
                    }
                }
                delay(500)
            }
        }
    }

    fun loadSongs() {
        viewModelScope.launch {
            _isLoading.value = true
            val fetchedSongs = fetchSongsFromDevice()
            _songs.value = fetchedSongs
            _isLoading.value = false
            if (fetchedSongs.isNotEmpty() && _currentSong.value == null) {
                _currentSong.value = fetchedSongs[0]
            }
        }
    }

    private suspend fun fetchSongsFromDevice(): List<Song> = withContext(Dispatchers.IO) {
        val songsList = mutableListOf<Song>()
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        try {
            val contentResolver = getApplication<Application>().contentResolver
            contentResolver.query(
                collection,
                projection,
                selection,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn) ?: "Desconocido"
                    val artist = cursor.getString(artistColumn) ?: "Artista Desconocido"
                    val album = cursor.getString(albumColumn) ?: "Álbum Desconocido"
                    val durationMs = cursor.getLong(durationColumn)
                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    songsList.add(
                        Song(
                            id = id,
                            title = title,
                            artist = artist,
                            album = album,
                            duration = formatDuration(durationMs),
                            durationMs = durationMs,
                            contentUri = contentUri
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        songsList
    }

    private fun formatDuration(durationMs: Long): String {
        val totalSeconds = durationMs / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%d:%02d".format(minutes, seconds)
    }

    // Playlist Management
    fun createPlaylist(name: String) {
        val newPlaylist = Playlist(
            id = UUID.randomUUID().toString(),
            name = name
        )
        _playlists.value = _playlists.value + newPlaylist
    }

    fun addSongToPlaylist(playlistId: String, songId: Long) {
        _playlists.value = _playlists.value.map { playlist ->
            if (playlist.id == playlistId && !playlist.songIds.contains(songId)) {
                playlist.copy(songIds = playlist.songIds + songId)
            } else playlist
        }
    }

    fun removeSongFromPlaylist(playlistId: String, songId: Long) {
        _playlists.value = _playlists.value.map { playlist ->
            if (playlist.id == playlistId) {
                playlist.copy(songIds = playlist.songIds - songId)
            } else playlist
        }
    }

    fun deletePlaylist(playlistId: String) {
        _playlists.value = _playlists.value.filter { it.id != playlistId }
    }

    // Playback
    fun playSong(song: Song) {
        _currentSong.value = song
        val mediaItem = MediaItem.Builder()
            .setUri(song.contentUri)
            .setMediaId(song.id.toString())
            .build()
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun deleteSong(song: Song) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val intentSender = MediaStore.createDeleteRequest(
                        getApplication<Application>().contentResolver,
                        listOf(song.contentUri)
                    ).intentSender
                    _pendingIntent.emit(intentSender)
                } else {
                    getApplication<Application>().contentResolver.delete(song.contentUri, null, null)
                    withContext(Dispatchers.Main) {
                        loadSongs()
                        if (_currentSong.value?.id == song.id) {
                            _currentSong.value = null
                            exoPlayer.stop()
                        }
                    }
                }
            } catch (e: SecurityException) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val recoverableSecurityException = e as? android.app.RecoverableSecurityException
                    _pendingIntent.emit(recoverableSecurityException?.userAction?.actionIntent?.intentSender)
                } else {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun renameSong(song: Song, newTitle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val values = android.content.ContentValues().apply {
                    put(MediaStore.Audio.Media.TITLE, newTitle)
                }
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val intentSender = MediaStore.createWriteRequest(
                        getApplication<Application>().contentResolver,
                        listOf(song.contentUri)
                    ).intentSender
                    _pendingIntent.emit(intentSender)
                    // After user grants permission, they would need to click rename again 
                    // or we could save the pending operation. For simplicity, we trigger the request.
                } else {
                    getApplication<Application>().contentResolver.update(song.contentUri, values, null, null)
                    withContext(Dispatchers.Main) {
                        loadSongs()
                    }
                }
            } catch (e: SecurityException) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val recoverableSecurityException = e as? android.app.RecoverableSecurityException
                    _pendingIntent.emit(recoverableSecurityException?.userAction?.actionIntent?.intentSender)
                } else {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            if (exoPlayer.playbackState == Player.STATE_IDLE || exoPlayer.playbackState == Player.STATE_ENDED) {
                _currentSong.value?.let { playSong(it) }
            } else {
                exoPlayer.play()
            }
        }
    }

    fun playNext() {
        val currentIndex = _songs.value.indexOf(_currentSong.value)
        if (currentIndex != -1 && currentIndex < _songs.value.size - 1) {
            playSong(_songs.value[currentIndex + 1])
        } else if (_songs.value.isNotEmpty()) {
            playSong(_songs.value[0])
        }
    }

    fun playPrevious() {
        val currentIndex = _songs.value.indexOf(_currentSong.value)
        if (currentIndex > 0) {
            playSong(_songs.value[currentIndex - 1])
        } else if (_songs.value.isNotEmpty()) {
            playSong(_songs.value.last())
        }
    }

    fun seekTo(progress: Float) {
        val duration = exoPlayer.duration
        if (duration > 0) {
            exoPlayer.seekTo((duration * progress).toLong())
        }
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }
}
