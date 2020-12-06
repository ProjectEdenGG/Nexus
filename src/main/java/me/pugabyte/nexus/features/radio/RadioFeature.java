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
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;

import static me.pugabyte.nexus.features.radio.Utils.setRadioDefaults;

public class RadioFeature extends Feature {

	@Getter
	private static final String PREFIX = StringUtils.getPrefix("Radio");
	private static final File songsDirectory = Nexus.getFile("radio/songs");

	@Getter
	private static File[] songs;

	RadioConfigService configService = new RadioConfigService();

	@Override
	public void startup() {
		songs = null;
		new Listeners();

		songs = songsDirectory.listFiles();
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
}