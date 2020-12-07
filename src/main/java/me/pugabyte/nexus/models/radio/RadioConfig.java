package me.pugabyte.nexus.models.radio;

import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.radio.RadioFeature;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@Entity("radio")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class RadioConfig extends PlayerOwnedObject {
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

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@Converters({UUIDConverter.class, LocationConverter.class})
	public static class Radio {
		private String id;
		private RadioType type;
		private boolean enabled = false;
		private Location location;
		private int radius;
		@Embedded
		private Set<String> songs = new HashSet<>();

		private transient SongPlayer songPlayer;

		public void setEnabled(boolean bool) {
			this.enabled = bool;

			if (bool) {
				RadioFeature.createSongPlayer(this, getPlaylist());
				if (type.equals(RadioType.RADIUS)) {
					for (Player player : Bukkit.getOnlinePlayers())
						getSongPlayer().addPlayer(player);
				}
			} else
				getSongPlayer().setPlaying(false);
		}

		public Set<String> getSongs() {
			if (this.songs == null)
				return new HashSet<>();
			return this.songs;
		}

		public Playlist getPlaylist() {
			Set<String> unloadedSongs = new HashSet<>(songs);
			Set<File> loadedSongs = new HashSet<File>() {{
				for (String songName : songs)
					for (RadioSong song : RadioFeature.getAllSongs())
						if (song.getName().equals(songName)) {
							add(song.getFile());
							unloadedSongs.remove(songName);
							break;
						}
			}};

			Nexus.log("[Radio] [" + id + "] " + loadedSongs.size() + " loaded songs: " + loadedSongs.stream().map(File::getName).collect(Collectors.joining(", ")));
			if (!unloadedSongs.isEmpty())
				Nexus.log("[Radio] [" + id + "] " + loadedSongs.size() + " unloaded songs: " + String.join(", ", unloadedSongs));

			ArrayList<File> list = new ArrayList<>(loadedSongs);
			Collections.shuffle(list);
			Set<File> shuffled = new HashSet<>(list);

			List<Song> songList = new ArrayList<>();
			for (File songFile : shuffled) {
				Song song = NBSDecoder.parse(new File(songFile.getPath()));
				songList.add(song);
			}

			return new Playlist(songList.toArray(new Song[0]));
		}


		public void reload() {
			setEnabled(false);
			setEnabled(true);
		}
	}

}
