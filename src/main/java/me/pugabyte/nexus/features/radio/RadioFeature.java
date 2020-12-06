package me.pugabyte.nexus.features.radio;

import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.models.radio.RadioConfig;
import me.pugabyte.nexus.models.radio.RadioConfig.Radio;
import me.pugabyte.nexus.models.radio.RadioConfigService;
import me.pugabyte.nexus.models.radio.RadioSong;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static me.pugabyte.nexus.features.radio.Utils.setRadioDefaults;

public class RadioFeature extends Feature {

	@Getter
	private static final String PREFIX = StringUtils.getPrefix("Radio");
	private static final File songsDirectory = Nexus.getFile("radio/songs");

	@Getter
	private static List<RadioSong> allSongs;

	RadioConfigService configService = new RadioConfigService();

	@Override
	public void startup() {
		allSongs = null;
		new Listeners();

		for (File file : songsDirectory.listFiles())
			allSongs.add(new RadioSong(file.getName(), file));

		setupRadios();

		loadListeners();
	}

	@Override
	public void shutdown() {
		RadioConfig radioConfig = configService.get(Nexus.getUUID0());
		for (Radio radio : radioConfig.getRadios()) {
			SongPlayer songPlayer = radio.getSongPlayer();
			songPlayer.setAutoDestroy(true);
			songPlayer.setPlaying(false);
			songPlayer.destroy();
		}
	}

	private void setupRadios() {
		RadioConfig radioConfig = configService.get(Nexus.getUUID0());
		for (Radio radio : radioConfig.getRadios()) {
			if (!radio.isEnabled())
				continue;

			Playlist playlist = radio.getPlaylist();
			if (playlist == null || playlist.getSongList().size() <= 0) {
				Nexus.severe(radio.getId() + " radio playlist is empty!");
				continue;
			}

			if (radio.getRadius() > 0) {
				Location location = radio.getLocation();
				int radius = radio.getRadius();

				PositionSongPlayer positionSongPlayer = new PositionSongPlayer(playlist);
				positionSongPlayer.setTargetLocation(location);
				positionSongPlayer.setDistance(radius);
				setRadioDefaults(positionSongPlayer);

				radio.setSongPlayer(positionSongPlayer);
			} else {
				RadioSongPlayer radioSongPlayer = new RadioSongPlayer(playlist);
				setRadioDefaults(radioSongPlayer);

				radio.setSongPlayer(radioSongPlayer);
			}
		}
	}

	private void loadListeners() {
		RadioConfig radioConfig = configService.get(Nexus.getUUID0());

		for (Radio radio : radioConfig.getRadios()) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				radio.getSongPlayer().addPlayer(player);
			}
		}
	}

	public static Optional<RadioSong> getRadioSongByName(String name) {
		return allSongs.stream()
				.filter(radioSong -> radioSong.getName().equalsIgnoreCase(name))
				.findFirst();
	}
}