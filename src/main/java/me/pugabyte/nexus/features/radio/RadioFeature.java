package me.pugabyte.nexus.features.radio;

import com.destroystokyo.paper.ParticleBuilder;
import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import eden.utils.TimeUtils.Time;
import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.models.radio.RadioConfig;
import me.pugabyte.nexus.models.radio.RadioConfig.Radio;
import me.pugabyte.nexus.models.radio.RadioConfigService;
import me.pugabyte.nexus.models.radio.RadioSong;
import me.pugabyte.nexus.models.radio.RadioType;
import me.pugabyte.nexus.models.radio.RadioUser;
import me.pugabyte.nexus.models.radio.RadioUserService;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static me.pugabyte.nexus.features.radio.RadioUtils.addPlayer;
import static me.pugabyte.nexus.features.radio.RadioUtils.getRadios;
import static me.pugabyte.nexus.features.radio.RadioUtils.isInRangeOfRadiusRadio;
import static me.pugabyte.nexus.features.radio.RadioUtils.isListening;
import static me.pugabyte.nexus.features.radio.RadioUtils.removePlayer;
import static me.pugabyte.nexus.features.radio.RadioUtils.setRadioDefaults;
import static me.pugabyte.nexus.utils.Utils.isNullOrEmpty;

// TODO: fix bugs when switching between local and server radios
// TODO: display what song of the playlist is currently player
// TODO: re-add players to radios they were previously listening to over reload/restart

public class RadioFeature extends Feature {

	public static final String PREFIX = StringUtils.getPrefix("Radio");
	private static final File songsDirectory = Nexus.getFile("songs");

	@Getter
	private static List<RadioSong> allSongs;

	@Override
	public void onStart() {
		allSongs = new ArrayList<>();
		new Listeners();

		for (File file : songsDirectory.listFiles())
			allSongs.add(new RadioSong(file.getName(), file));

		setupRadios();

		// Radio Particles Task
		Tasks.repeat(0, Time.SECOND.x(2), () -> {
			RadioConfigService configService = new RadioConfigService();
			RadioConfig config = configService.get(Nexus.getUUID0());
			for (Radio radio : config.getRadios()) {
				if (!radio.getType().equals(RadioType.RADIUS)) continue;
				if (!radio.isEnabled()) continue;
				if (!radio.isParticles()) continue;
				if (radio.getLocation() == null) continue;
				new ParticleBuilder(Particle.NOTE)
						.count(7)
						.offset(0.25, 0.25, 0.25)
						.location(radio.getLocation().add(0, 1, 0))
						.spawn();
			}
		});

		// Radius Radio User Task
		Tasks.repeat(0, Time.SECOND.x(2), () -> {
			RadioUserService service = new RadioUserService();
			for (Radio radio : getRadios()) {
				if (!(radio.getSongPlayer() instanceof PositionSongPlayer))
					continue;

				for (Player player : Bukkit.getOnlinePlayers()) {
					RadioUser user = service.get(player);

					if (user.isMute()) continue;
					if (user.getLeftRadiusRadios().contains(radio.getId())) continue;

					boolean isInRange = isInRangeOfRadiusRadio(player, radio);
					boolean isListening = isListening(player, radio);

					if (isInRange && !isListening) {
						if (user.getServerRadio() != null)
							removePlayer(player, user.getServerRadio());
						addPlayer(player, radio);
					} else if (!isInRange && isListening) {
						removePlayer(player, radio);
						if (user.getLastServerRadio() != null)
							addPlayer(player, user.getLastServerRadio());
					}
				}
			}
		});
	}

	@Override
	public void onStop() {
		RadioConfigService configService = new RadioConfigService();
		RadioConfig radioConfig = configService.get(Nexus.getUUID0());
		for (Radio radio : radioConfig.getRadios()) {
			if (radio.getSongPlayer() != null)
				removeSongPlayer(radio.getSongPlayer());
		}
		configService.save(radioConfig);
	}

	private void setupRadios() {
		RadioConfigService configService = new RadioConfigService();
		RadioConfig radioConfig = configService.get(Nexus.getUUID0());
		for (Radio radio : radioConfig.getRadios()) {
			if (!radio.isEnabled())
				continue;

			Playlist playlist = radio.getPlaylist();
			if (playlist == null || playlist.getSongList().size() <= 0) {
				Nexus.severe(radio.getId() + " radio playlist is empty!");
				continue;
			}

			createSongPlayer(radio, playlist);
		}
		configService.save(radioConfig);
	}

	public static void createSongPlayer(Radio radio, Playlist playlist) {
		playlist = RadioUtils.shufflePlaylist(playlist);

		if (radio.getType().equals(RadioType.RADIUS)) {
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

	public static Optional<RadioSong> getRadioSongByName(String name) {
		return allSongs.stream()
				.filter(radioSong -> radioSong.getName().equalsIgnoreCase(name))
				.findFirst();
	}

	public static void removeSongPlayer(SongPlayer songPlayer) {
		songPlayer.setAutoDestroy(true);
		songPlayer.setPlaying(false);
		songPlayer.destroy();
	}

	public static void verify(Radio radio) {
		if (radio == null)
			throw new InvalidInputException("Radio is null");

		String radioId = StringUtils.camelCase(radio.getType()) + " Radio " + radio.getId();
		if (radio.getType().equals(RadioType.RADIUS)) {
			if (radio.getLocation() == null)
				throw new InvalidInputException(radioId + ": Location is null");
			if (radio.getRadius() <= 0)
				throw new InvalidInputException(radioId + ": Radius is <= 0");
		}

		if (isNullOrEmpty(radio.getSongs()))
			throw new InvalidInputException(radioId + ": Songs is null or empty");
	}
}