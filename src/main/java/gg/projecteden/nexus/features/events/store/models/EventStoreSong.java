package gg.projecteden.nexus.features.events.store.models;

import com.ruinscraft.powder.PowderPlugin;
import com.ruinscraft.powder.model.Powder;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class EventStoreSong {
	private final Powder powder;
	private final Song song;

	public static List<EventStoreSong> SONGS = new ArrayList<>();

	private static Map<UUID, SongPreviewer> previewers = new HashMap<>();

	public static EventStoreSong of(String name) {
		return SONGS.stream()
			.filter(song -> song.getName().equalsIgnoreCase(name))
			.findFirst()
			.orElseThrow(() -> new InvalidInputException("Song &e" + name + " &cnot found"));
	}

	@NotNull
	private SongPreviewer previewer(Player player) {
		return previewers.computeIfAbsent(player.getUniqueId(), $ -> new SongPreviewer(player.getUniqueId()));
	}

	public void play(Player player) {
		stop(player);
		previewer(player).play();
	}

	public static boolean stop(Player player) {
		final UUID uuid = player.getUniqueId();
		if (!previewers.containsKey(uuid))
			return false;

		previewers.get(uuid).stop();
		previewers.remove(uuid);
		return true;
	}

	public String getName() {
		return powder.getName();
	}

	@Data
	@RequiredArgsConstructor
	public class SongPreviewer implements PlayerOwnedObject {
		private final UUID uuid;
		private RadioSongPlayer radio;

		void play() {
			radio = new RadioSongPlayer(song);
			radio.addPlayer(getOnlinePlayer());
			radio.setStereo(true);
			radio.setPlaying(true);
			Tasks.wait(radio.getSong().getLength(), this::stop);
		}

		void stop() {
			if (radio != null) {
				radio.destroy();
				radio = null;
			}
		}
	}

	static {
		Tasks.async(() -> {
			final YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/Powder/powders.yml"));
			final PowderPlugin plugin = (PowderPlugin) Bukkit.getServer().getPluginManager().getPlugin("Powder");

			if (plugin != null) {
				final List<Powder> songs = plugin.getPowderHandler().getPowdersFromCategory("Songs");

				for (Powder powder : songs) {
					final ConfigurationSection songConfig = config.getConfigurationSection(String.format("powders.%s.songs.song", powder.getPath()));
					final Song song = NBSDecoder.parse(new File("plugins/Powder/songs/" + songConfig.getString("fileName")));

					SONGS.add(new EventStoreSong(powder, song));
				}
			}
		});
	}

}
