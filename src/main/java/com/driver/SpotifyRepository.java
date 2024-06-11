package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;
	
    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
    	User user=new User(name, mobile);
    	users.add(user);
    	return user;
    }

    public Artist createArtist(String name) {
    	Artist artist = new Artist(name);
    	artists.add(artist);
    	return artist;
    }

    public Album createAlbum(String title, String artistName) {
    	//If the artist does not exist, first create an artist with given name
        //Create an album with given title and artist
    	Album album;
    	boolean isArtistPresent=false;
    	boolean updatedArtistAlbumMap=false;
    	Artist newArtist = null;
    	
    	for(Artist artist:artists) {
    		if(artist.getName().equals(artistName)) {
    			isArtistPresent=true;
    			newArtist=artist;
    			break;
    		}
    	}
    	if(!isArtistPresent) {
    		newArtist = new Artist(artistName);
    		artists.add(newArtist);
    	}
    	
    	album=new Album(title);
    	albums.add(album);
    	
    	//need to add in hashmap artistalbum
    	for(Artist artistKey : artistAlbumMap.keySet()) {
    		if(artistKey.getName().equals(artistName)) {
    			List<Album> albumList=artistAlbumMap.get(artistKey);
    			albumList.add(album);
    			artistAlbumMap.put(artistKey, albumList);
    			updatedArtistAlbumMap = true;
    			break;
    		}
    	}
    	if(!updatedArtistAlbumMap) {
    		List<Album> albumList=new ArrayList<>();
			albumList.add(album);
			artistAlbumMap.put(newArtist, albumList);
    	}
    	
    
    	return album;
    	
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
    	
    	 //If the album does not exist in database, throw "Album does not exist" exception
        //Create and add the song to respective album
    	boolean isAlbumPresent=false;
    	boolean updateAlbumSongMap=false;
    	Album newAlbum=null;
    	Song song=new Song(title, length);
    	for(Album album:albums) {
    		if(album.getTitle().equals(albumName)) {
    			isAlbumPresent=true;
    			newAlbum=album;
    			break;
    		}
    	}
    	if(!isAlbumPresent) {
    		throw new Exception("Album does not exist");
    	}
    	songs.add(song);
    	
    	for(Album albumKey : albumSongMap.keySet()) {
    		if(albumKey.getTitle().equals(albumName)) {
    			updateAlbumSongMap = true;
    			List<Song> songList = albumSongMap.get(albumKey);
    			songList.add(song);
    			albumSongMap.put(albumKey, songList);
    			break;
    		}
    	}
    	if(!updateAlbumSongMap) {
    		List<Song> songList = new ArrayList<Song>();
			songList.add(song);
			albumSongMap.put(newAlbum, songList);
    	}
    	return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
    	//Create a playlist with given title and add all songs having the given length in the database to that playlist
        //The creater of the playlist will be the given user and will also be the only listener at the time of playlist creation
        //If the user does not exist, throw "User does not exist" exception
    	
    	boolean isUserPresent=false;
    	User creator=null;
    	for(User user:users) {
    		if(user.getMobile().equals(mobile)) {
    			isUserPresent=true;
    			creator=user;
    			break;
    		}
    	}
    	if(!isUserPresent)
    		throw new Exception("User does not exist");
    	
    	Playlist playList = new Playlist(title);
    	//add  to playLists
    	playlists.add(playList);
    	
    	//update playlistSong map
    	List<Song> songListOfPlaylist = new ArrayList<>();
    	for(Song song: songs) {
    		if(song.getLength()==length) {
    			songListOfPlaylist.add(song);
    		}
    	}
    	playlistSongMap.put(playList, songListOfPlaylist);
    	
    	//map playslist and creator
    	creatorPlaylistMap.put(creator, playList);
    	
    	//update playListListener map
    	if(playlistListenerMap.containsKey(playList)) {
    		List<User> playlistUsers = playlistListenerMap.get(playList);
    		playlistUsers.add(creator);
    		playlistListenerMap.put(playList, playlistUsers);
    	}else {
    		List<User> playlistUsers = new ArrayList<>();
    		playlistUsers.add(creator);
    		playlistListenerMap.put(playList, playlistUsers);
    	}
    	
    	//update userPlaylist
    	if(userPlaylistMap.containsKey(creator)) {
    		List<Playlist> userPlayLists = userPlaylistMap.get(creator);
    		userPlayLists.add(playList);
    		userPlaylistMap.put(creator, userPlayLists);
    	}else {
    		List<Playlist> userPlayLists = new ArrayList<Playlist>();
    		userPlayLists.add(playList);
    		userPlaylistMap.put(creator, userPlayLists);
    	}
    	
    	return playList;
    	
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
    	//Create a playlist with given title and add all songs having the given titles in the database to that playlist
        //The creater of the playlist will be the given user and will also be the only listener at the time of playlist creation
        //If the user does not exist, throw "User does not exist" exception
    	
    	boolean isUserPresent=false;
    	User creator=null;
    	for(User user:users) {
    		if(user.getMobile().equals(mobile)) {
    			isUserPresent=true;
    			creator = user;
    			break;
    		}
    	}
    	if(!isUserPresent)
    		throw new Exception("User does not exist");
    	
    	Playlist playList = new Playlist(title);
    	playlists.add(playList);
    	List<Song> playListSongName = new ArrayList<>();
    	for(String songTitle : songTitles) {
    		for(Song song: songs) {
    			if(song.getTitle().equals(songTitle)) {
    				playListSongName.add(song);
    			}
    		}
    	}
    	playlistSongMap.put(playList, playListSongName);
    	creatorPlaylistMap.put(creator, playList);
    	
    	//update playListListener map
    	if(playlistListenerMap.containsKey(playList)) {
    		List<User> playlistUsers = playlistListenerMap.get(playList);
    		playlistUsers.add(creator);
    		playlistListenerMap.put(playList, playlistUsers);
    	}else {
    		List<User> playlistUsers = new ArrayList<>();
    		playlistUsers.add(creator);
    		playlistListenerMap.put(playList, playlistUsers);
    	}
    	
    	//update userPlaylist
    	if(userPlaylistMap.containsKey(creator)) {
    		List<Playlist> userPlayLists = userPlaylistMap.get(creator);
    		userPlayLists.add(playList);
    		userPlaylistMap.put(creator, userPlayLists);
    	}else {
    		List<Playlist> userPlayLists = new ArrayList<Playlist>();
    		userPlayLists.add(playList);
    		userPlaylistMap.put(creator, userPlayLists);
    	}
    	
    	return playList;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
    	
    	//Find the playlist with given title and add user as listener of that playlist and update user accordingly
        //If the user is creater or already a listener, do nothing
        //If the user does not exist, throw "User does not exist" exception
        //If the playlist does not exists, throw "Playlist does not exist" exception
        // Return the playlist after updating
    	boolean isUserPresent=false;
    	boolean isPlaylist = false;
    	User obtainedUser = null;
    	Playlist obtainedPlaylist = null;
    	//is user present
    	for(User user:users) {
    		if(user.getMobile().equals(mobile)) {
    			isUserPresent=true;
    			obtainedUser=user;
    			break;
    		}
    	}
    	if(!isUserPresent) {
    		throw new Exception("User does not exist");
    	}
    	
    	//is playlist present
    	for(Playlist playlist: playlists) {
    		if(playlist.getTitle().equals(playlistTitle)) {
    			isPlaylist = true;
    			obtainedPlaylist=playlist;
    			break;
    		}
    	}
    	if(!isPlaylist) {
    		throw new Exception("Playlist does not exist");
    	}
    	
    	//update playlistListenerMap
    	if(playlistListenerMap.containsKey(obtainedPlaylist)) {
    		List<User> playlistUsers = playlistListenerMap.get(obtainedPlaylist);
    		if(! playlistUsers.contains(obtainedUser)) {
    			playlistUsers.add(obtainedUser);
    			playlistListenerMap.put(obtainedPlaylist, playlistUsers);
    		}
    	}else {
    		List<User> playlistUsers = new ArrayList<User>();
    		playlistUsers.add(obtainedUser);
    		playlistListenerMap.put(obtainedPlaylist, playlistUsers);
    	}
    	
    	//update userPlayList map
    	if(userPlaylistMap.containsKey(obtainedUser)) {
    		List<Playlist> userPlaylists = userPlaylistMap.get(obtainedUser);
    		if(!userPlaylists.contains(obtainedPlaylist)) {
    			userPlaylists.add(obtainedPlaylist);
    			userPlaylistMap.put(obtainedUser, userPlaylists);
    		}
    	}else {
    		List<Playlist> userPlaylists = new ArrayList<Playlist>();
    		userPlaylists.add(obtainedPlaylist);
    		userPlaylistMap.put(obtainedUser, userPlaylists);
    	}
    	return obtainedPlaylist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
    	//The user likes the given song. The corresponding artist of the song gets auto-liked
        //A song can be liked by a user only once. If a user tried to like a song multiple times, do nothing
        //However, an artist can indirectly have multiple likes from a user, if the user has liked multiple songs of that artist.
        //If the user does not exist, throw "User does not exist" exception
        //If the song does not exist, throw "Song does not exist" exception
        //Return the song after updating
    	
    	
    	boolean isUserPresent=false;
    	User obtainedUser = null;
    	boolean isSongPresent =false;
    	Song obtainedSong = null;
		for(User user:users) {
    		if(user.getMobile().equals(mobile)) {
    			isUserPresent=true;
    			obtainedUser = user;
    			break;
    		}
    	}
    	if(!isUserPresent) {
    		throw new Exception("User does not exist");
    	}
    	
    	for(Song song : songs) {
    		if(song.getTitle().equals(songTitle)) {
    			isSongPresent=true;
    			obtainedSong=song;
    			break;
    		}
    	}
    	if(!isSongPresent)
    		throw new Exception("Song does not exist");
    	
    	//update songLikeMap
    	List<User> userLikeSongs;
    	if(songLikeMap.containsKey(obtainedSong)) {
    		userLikeSongs = songLikeMap.get(obtainedSong);
    		if(!userLikeSongs.contains(obtainedUser)) {
    			userLikeSongs.add(obtainedUser);
    			songLikeMap.put(obtainedSong, userLikeSongs);
    		}
    	}else {
    		userLikeSongs = new ArrayList<>();
    		userLikeSongs.add(obtainedUser);
    		songLikeMap.put(obtainedSong, userLikeSongs);
    	}
    	
    	obtainedSong.setLikes(userLikeSongs.size());
    	for(Song song:songs) {
			if(song.getTitle().equals(songTitle)) {
				song.setLikes(userLikeSongs.size());
				break;
			}
		}
    	
    	
    	Album requiredAlbum=null;
    	for(Album album:albumSongMap.keySet()) {
    		List<Song> songList = albumSongMap.get(album);
    		for(Song song:songList) {
    			if(song.getTitle().equals(songTitle)) {
    				song.setLikes(userLikeSongs.size());
    				requiredAlbum = album;
    				break;
    			}
    		}
    	}
    	
    	Artist reqArtist=null;
    	for(Artist artist: artistAlbumMap.keySet()) {
    		List<Album> albumList = artistAlbumMap.get(artist);
    		for(Album album:albumList) {
    			if(album.getTitle().equals(requiredAlbum.getTitle())) {
    				artist.setLikes(artist.getLikes()+1);
    				reqArtist=artist;
    				break;
    			}
    		}
    		if(reqArtist != null)
    			break;
    	}
    	for(Artist artist:artists) {
    		if(artist.getName().equals(reqArtist.getName())) {
    			artist.setLikes(reqArtist.getLikes());
    		}
    	}
    	
    	
    	return obtainedSong;
    	
    	
    }

    public String mostPopularArtist() {
    	String popularArtist=null;
    	int maxLike=Integer.MIN_VALUE;
    	for(Artist artist:artists) {
    		if(artist.getLikes()>maxLike) {
    			maxLike=artist.getLikes();
    			popularArtist=artist.getName();
    		}
    	}
    	return popularArtist;
    }

    public String mostPopularSong() {
    	String popularSong=null;
    	int maxLike=Integer.MIN_VALUE;
    	for(Song song:songs) {
    		if(song.getLikes()>maxLike) {
    			maxLike=song.getLikes();
    			popularSong=song.getTitle();
    		}
    	}
    	return popularSong;
    }
}
