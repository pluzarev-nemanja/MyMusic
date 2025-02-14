package com.example.mymusic.presentation.album

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mymusic.R
import com.example.mymusic.domain.model.Album
import com.example.mymusic.domain.model.Playlist
import com.example.mymusic.domain.model.Song
import com.example.mymusic.presentation.navigation.Screen
import com.example.mymusic.presentation.playlist.SongsList
import com.example.mymusic.presentation.playlist.TopBarPlaylist
import com.example.mymusic.ui.theme.lightBlueToWhite
import com.example.mymusic.ui.theme.whiteToDarkGrey
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun AlbumDetailScreen(
    navController: NavController,
    album: Album?,
    allPlaylists: List<Playlist>,
    insertSongIntoPlaylist: (Song, String, String) -> Unit,
    onItemClick: (Song,List<Song>) -> Unit,
    currentPlayingAudio: Song?,
    shuffle: (Album) -> Unit,
    onStart: (Song, List<Song>) -> Unit,
    shareSong: (Song) -> Unit,
    changeSongImage: (Song, String) -> Unit,
    changeAlbumImage: (Album, String) -> Unit,

) {

    Scaffold(
        topBar = {
            TopBarAlbum(
                navController = navController,
                album = album!!,
                changeAlbumImage = changeAlbumImage
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AlbumInfo(
                album = album!!,
                shuffle = { shuffle.invoke(album) },
                onStart = {
                    if (album.songs.isNotEmpty())
                        onStart.invoke(album.songs[0], album.songs)
                }
            )
            SongsList(
                currentPlayingAudio = currentPlayingAudio,
                audioList = album.songs,
                allPlaylists = allPlaylists,
                insertSongIntoPlaylist = insertSongIntoPlaylist,
                onItemClick = onItemClick,
                shareSong = shareSong,
                changeSongImage
            )
        }
    }
}

@Composable
fun TopBarAlbum(
    navController: NavController,
    changeAlbumImage: (Album, String) -> Unit,
    album: Album
) {
    var showMenu by remember {
        mutableStateOf(false)
    }

    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedImageUri = uri
            //here update that in database user image
            changeAlbumImage.invoke(album, uri.toString())
        }
    )

    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.primary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {

            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back button")
            }

            IconButton(onClick = {
                //add more options to playlist
                showMenu = true
            }) {
                MaterialTheme(shapes = MaterialTheme.shapes.copy(medium = RoundedCornerShape(16.dp))) {
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = {
                            showMenu = false
                        },
                    ) {
                        DropdownMenuItem(onClick = {
                            singlePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                            showMenu = false
                        }) {
                            Text(text = "Change image")
                        }
                        DropdownMenuItem(onClick = {
                            navController.navigate(Screen.SettingsScreen.route)
                            showMenu = false
                        }) {
                            Text(text = "Settings")
                        }
                    }
                }
                Icon(imageVector = Icons.Filled.MoreHoriz, contentDescription = "More options")
            }
        }
    }
}

@Composable
fun AlbumInfo(
    album: Album,
    shuffle: () -> Unit,
    onStart: () -> Unit,
) {

    val songsText by remember {
        if (album.songCount > 1)
            mutableStateOf("songs")
        else mutableStateOf("song")
    }

    val image by remember {
        mutableStateOf(album.albumImage)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlideImage(
            imageModel = {
                if (image != "") Uri.parse(image)
                else R.drawable.album
            },
            imageOptions = ImageOptions(
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(Color.DarkGray)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = album.albumName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = album.artist)
            Text(text = " · ${album.songCount} $songsText")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(
                onClick = {
                    onStart.invoke()
                },
                shape = RoundedCornerShape(35.dp),
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.lightBlueToWhite)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play album",
                    tint = MaterialTheme.colors.whiteToDarkGrey
                )
                Text(
                    text = " Play",
                    color = MaterialTheme.colors.whiteToDarkGrey
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Button(
                onClick = {
                    shuffle.invoke()
                },
                shape = CircleShape,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Shuffle,
                    contentDescription = "shuffle",
                )
                Text(text = " Shuffle")

            }
        }
    }

}