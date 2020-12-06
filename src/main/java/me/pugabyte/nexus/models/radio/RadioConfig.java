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
import org.bukkit.Location;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
		private boolean enabled = true;
		private Location location;
		private int radius;
		@Embedded
		private Set<String> songs = new HashSet<>();
		private transient SongPlayer songPlayer;

		public Playlist getPlaylist() {
			Set<String> radioSongs = getSongs();
			Nexus.log("<" + getId() + "> " + radioSongs.size() + " Loaded Songs: " + radioSongs.toString());

			Set<File> loadedSongs = new HashSet<>();
			for (String songFile : radioSongs) {
				for (File song : RadioFeature.getSongs()) {
					if (song.getName().equals(songFile)) {
						loadedSongs.add(song);
						break;
					}
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

			return new Playlist(songList.toArray(new Song[0]));
		}
	}

}
