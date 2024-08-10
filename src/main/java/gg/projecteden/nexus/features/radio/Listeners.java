package gg.projecteden.nexus.features.radio;

import com.xxmicloxx.NoteBlockAPI.event.SongNextEvent;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.models.radio.RadioConfig.Radio;
import gg.projecteden.nexus.models.radio.RadioUser;
import gg.projecteden.nexus.models.radio.RadioUserService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static gg.projecteden.nexus.features.radio.RadioUtils.addPlayer;
import static gg.projecteden.nexus.features.radio.RadioUtils.removePlayer;

public class Listeners implements Listener {

	public Listeners() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Radio radio = RadioUtils.getListenedRadio(player);
		if (radio != null)
			removePlayer(player, radio);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		RadioUserService userService = new RadioUserService();
		RadioUser user = userService.get(event.getPlayer());
		Radio radio = user.getLastServerRadio();
		if (radio != null)
			addPlayer(user.getOnlinePlayer(), radio);

		user.getLeftRadiusRadios().clear();
	}

	@EventHandler
	public void onSongNext(SongNextEvent event) {
		SongPlayer songPlayer = event.getSongPlayer();
		Song song = songPlayer.getSong();
		Set<UUID> UUIDList = songPlayer.getPlayerUUIDs();

		for (UUID uuid : UUIDList) {
			Player player = Bukkit.getPlayer(uuid);
			if (player == null || !player.isOnline())
				continue;

			if (songPlayer instanceof PositionSongPlayer) {
				List<Radio> radios = RadioUtils.getRadiosOf(songPlayer);
				for (Radio radio : radios) {
					if (radio == null || !radio.isUpdatePlaying() || !RadioUtils.isInRangeOfRadiusRadio(player, radio))
						continue;

					RadioUtils.actionBar(player, song, true);
				}
			} else {
				RadioUtils.actionBar(player, song, true);
			}
		}
	}

}
