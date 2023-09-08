package com.example.mymusic.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mymusic.domain.model.Song
import com.example.mymusic.presentation.album.AlbumScreen
import com.example.mymusic.presentation.home.HomeScreen
import com.example.mymusic.presentation.playlist.PlaylistDetailsScreen
import com.example.mymusic.presentation.playlist.PlaylistScreen
import com.example.mymusic.presentation.playlist.PlaylistViewModel
import com.example.mymusic.presentation.search.SearchScreen
import com.example.mymusic.presentation.search.SearchViewModel
import com.example.mymusic.presentation.songs.SongsScreen
import com.example.mymusic.presentation.songs.SongsViewModel

@Composable
fun BottomNavGraph(
    navController: NavHostController,
    songsViewModel: SongsViewModel = hiltViewModel(),
    songList: List<Song>,
    searchViewModel: SearchViewModel = hiltViewModel(),
    searchText: String,
    songs: List<Song>,
    currentPlayingAudio: Song?,
    onItemClick: (Song) -> Unit,
    playlistViewModel: PlaylistViewModel = hiltViewModel()
) {

    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Home.route
    ) {
        composable(BottomBarScreen.Home.route) {
            HomeScreen()
        }
        composable(BottomBarScreen.Songs.route) {
            SongsScreen(
                isAudioPlaying = songsViewModel.isAudioPlaying,
                audioList = songList,
                currentPlayingAudio = songsViewModel
                    .currentPlayingAudio.value,
                onItemClick = {
                    songsViewModel.playAudio(it)
                },
                sortOrderChange = {
                    songsViewModel.changeSortOrderSongs(it)
                },
                navController = navController,
                playlists = playlistViewModel.playlists,
                insertSongIntoPlaylist = { song, playlistName ->
                    playlistViewModel.insertSongIntoPlaylist(song, playlistName)
                }
            )
        }
        composable(BottomBarScreen.Playlists.route) {
            PlaylistScreen(
                playlistViewModel = playlistViewModel,
                sortOrderChange = {
                    playlistViewModel.changeSortOrder(it)
                },
                navController = navController,
                currentPlayingAudio = songsViewModel
                    .currentPlayingAudio.value,
                deletePlaylist = {
                    playlistViewModel.deletePlaylist(it)
                }
            )
        }
        composable(BottomBarScreen.Album.route) {
            AlbumScreen()
        }
        composable(Screen.SearchScreen.route) {
            SearchScreen(
                searchText,
                songs,
                searchViewModel,
                currentPlayingAudio,
                onItemClick,
                playlists = playlistViewModel.playlists,
                insertSongIntoPlaylist = { song, playlistName ->
                    playlistViewModel.insertSongIntoPlaylist(song, playlistName)
                }
            )
        }
        composable(Screen.PlaylistDetailsScreen.route) {

            //shuffle button to play random song,not to be like normal shuffle

            PlaylistDetailsScreen(
                currentPlayingAudio = songsViewModel.currentPlayingAudio.value,
                navController = navController,
                playlist = playlistViewModel.clickedPlaylist.value,
                allPlaylists = playlistViewModel.playlists,
                insertSongIntoPlaylist = { song, playlistName ->
                    playlistViewModel.insertSongIntoPlaylist(song, playlistName)
                },
                onItemClick = {
                    songsViewModel.playAudio(it)
                },
                shuffle = {
                    songsViewModel.shuffle()
                },
                onStart = {currentPlayingAudio , songs ->
                    songsViewModel.playPlaylist(currentPlayingAudio,songs)
                },
            )
        }
    }
}