package gg.projecteden.nexus.features.radio;

import com.destroystokyo.paper.ParticleBuilder;
import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.radio.RadioConfig;
import gg.projecteden.nexus.models.radio.RadioConfig.Radio;
import gg.projecteden.nexus.models.radio.RadioConfig.RadioSong;
import gg.projecteden.nexus.models.radio.RadioConfig.RadioType;
import gg.projecteden.nexus.models.radio.RadioConfigService;
import gg.projecteden.nexus.models.radio.RadioUser;
import gg.projecteden.nexus.models.radio.RadioUserService;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static gg.projecteden.nexus.features.radio.RadioUtils.addPlayer;
import static gg.projecteden.nexus.features.radio.RadioUtils.getRadios;
import static gg.projecteden.nexus.features.radio.RadioUtils.isInRangeOfRadiusRadio;
import static gg.projecteden.nexus.features.radio.RadioUtils.isListening;
import static gg.projecteden.nexus.features.radio.RadioUtils.removePlayer;
import static gg.projecteden.nexus.features.radio.RadioUtils.setRadioDefaults;
import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;

// TODO: fix bugs when switching between local and server radios
// TODO: display what song of the playlist is currently playing

public class RadioFeature extends Feature {

	public static final String PREFIX = StringUtils.getPrefix("Radio");
	private static final File songsDirectory = IOUtils.getPluginFile("songs");
	private static final RadioUserService userService = new RadioUserService();

	@Getter
	private static List<RadioSong> allSongs;

	@Override
	public void onStart() {
		allSongs = new ArrayList<>();
		new Listeners();

		Tasks.async(() -> {
			File[] songs = songsDirectory.listFiles();
			if (songs != null) {
				for (File file : songs)
					allSongs.add(new RadioSong(file.getName(), file));
			}

			setupRadios();

			// Rejoin radios
			for (Player player : OnlinePlayers.getAll()) {
				RadioUser user = userService.get(player);
				if (user.getLastServerRadio() != null)
					RadioUtils.addPlayer(player, user.getLastServerRadio());
			}

			// Radio Particles Task
			Tasks.repeat(0, TickTime.TICK.x(5), () -> {
				for (Radio radio : getRadios()) {
					if (!radio.getType().equals(RadioType.RADIUS)) continue;
					if (!radio.isEnabled()) continue;
					if (!radio.isParticles()) continue;
					if (radio.getLocation() == null) continue;
					if (RandomUtils.chanceOf(20)) continue;

					new ParticleBuilder(Particle.NOTE)
						.count(RandomUtils.randomInt(1, 3))
						.offset(0.25, 0.25, 0.25)
						.location(radio.getLocation().add(0, RandomUtils.randomDouble(0.45, 0.75), 0))
						.spawn();
				}
			});

			// Radius Radio User Task
			RadioUserService service = new RadioUserService();
			Tasks.repeat(0, TickTime.SECOND.x(2), () -> {
				for (Radio radio : getRadios()) {
					if (!(radio.getSongPlayer() instanceof PositionSongPlayer))
						continue;

					for (Player player : OnlinePlayers.getAll()) {
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
		});
	}

	@Override
	public void onStop() {
		RadioConfigService configService = new RadioConfigService();
		RadioConfig radioConfig = configService.get0();
		RadioUserService userService = new RadioUserService();
		RadioUser user;
		for (Radio radio : radioConfig.getRadios()) {
			if (radio.getSongPlayer() != null) {
				SongPlayer songPlayer = radio.getSongPlayer();
				for (UUID uuid : songPlayer.getPlayerUUIDs()) {
					user = userService.get(uuid);
					user.setServerRadioId(radio.getId());
					userService.save(user);
				}

				removeSongPlayer(radio.getSongPlayer());
			}
		}
		configService.save(radioConfig);
	}

	private void setupRadios() {
		RadioConfigService configService = new RadioConfigService();
		RadioConfig radioConfig = configService.get0();
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
