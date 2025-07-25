package gg.projecteden.nexus.models.radio;

import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.radio.RadioFeature;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(value = "radio", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class RadioConfig implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	@Embedded
	private Set<Radio> radios = new HashSet<>();

	public Radio getById(String id) {
		return radios.stream().filter(radio -> radio.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
	}

	public void add(Radio radio) {
		if (getById(radio.getId()) != null)
			throw new InvalidInputException("Radio &e" + radio.getId() + " &calready exists");

		radios.add(radio);
	}

	public void addSong(Radio radio, RadioSong song) {
		Radio _radio = getById(radio.getId());
		Set<String> songs = _radio.getSongs();
		songs.add(song.getName());
		_radio.setSongs(songs);
	}

	public void removeSong(Radio radio, RadioSong song) {
		Radio _radio = getById(radio.getId());
		Set<String> songs = _radio.getSongs();
		songs.remove(song.getName());
		_radio.setSongs(songs);
	}

	public void clearSongs(Radio radio) {
		Radio _radio = getById(radio.getId());
		_radio.setSongs(new HashSet<>());
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@Converters({UUIDConverter.class, LocationConverter.class})
	public static class Radio {
		private String id;
		private RadioType type;
		@Default
		private boolean enabled = false;
		@Default
		private boolean particles = false;
		@Default
		private boolean updatePlaying = true;
		private List<Location> locations = new ArrayList<>();
		private int radius = 5;
		@Embedded
		@Default
		private Set<String> songs = new HashSet<>();

		private transient List<SongPlayer> songPlayers = new ArrayList<>();

		public void addSongPlayer(SongPlayer songPlayer) {
			if (songPlayers == null)
				songPlayers = new ArrayList<>();

			this.songPlayers.add(songPlayer);
		}

		public Location getRadiusLocation() {
			Location location = _getRadiusLocationReference();
			if (location == null)
				return null;

			return location.clone();
		}

		public Location _getRadiusLocationReference() {
			if (this.locations == null || this.locations.isEmpty())
				return null;

			return this.locations.getFirst();
		}

		public List<Location> getStationLocations() {
			List<Location> locations = _getStationLocationsReference();
			if (locations == null)
				return null;

			return new ArrayList<>(locations);
		}

		public List<Location> _getStationLocationsReference() {
			if (this.locations == null || this.locations.isEmpty())
				return null;

			return this.locations;
		}

		public void setRadiusLocation(Location location) {
			this.locations = Collections.singletonList(location);
		}

		public void addStationLocation(Location location) {
			this.locations.add(location);
		}

		public void removeStationLocation(int index) {
			this.locations.remove(index);
		}

		public void setEnabled(boolean bool) {
			this.enabled = bool;

			if (bool) {
				RadioFeature.createSongPlayer(this, getPlaylist());
			} else
				getSongPlayers().forEach(songPlayer -> songPlayer.setPlaying(false));
		}

		public Set<String> getSongs() {
			if (this.songs == null)
				return new HashSet<>();
			return this.songs;
		}

		public Playlist getPlaylist() {
			Set<String> unloadedSongs = new HashSet<>(songs);
			Set<File> loadedSongs = new HashSet<>() {{
				for (String songName : songs)
					for (RadioSong song : RadioFeature.getAllSongs())
						if (song.getName().equals(songName)) {
							add(song.getFile());
							unloadedSongs.remove(songName);
							break;
						}
			}};

			if (!unloadedSongs.isEmpty()) {
				Nexus.log("[Radio] [" + id + "] " + unloadedSongs.size() + " unloaded songs: " + String.join(", ", unloadedSongs));
				for (String unloadedSong : unloadedSongs) {
					songs.remove(unloadedSong);
				}
			}

			ArrayList<File> list = new ArrayList<>(loadedSongs);
			Collections.shuffle(list);
			Set<File> shuffled = new HashSet<>(list);

			List<Song> songList = new ArrayList<>();
			for (File songFile : shuffled) {
				Song song = NBSDecoder.parse(new File(songFile.getPath()));
				songList.add(song);
			}

			if (songList.isEmpty())
				return null;

			return new Playlist(songList.toArray(Song[]::new));
		}

		public void reload() {
			setEnabled(false);
			setEnabled(true);
		}
	}

	@Getter
	@AllArgsConstructor
	public enum RadioType {
		SERVER(false), // No location
		RADIUS(true), // Single location
		STATION(true), // Multi location
		;

		final boolean radiusBased;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class RadioSong {
		private String name;
		private File file;

	}
}
