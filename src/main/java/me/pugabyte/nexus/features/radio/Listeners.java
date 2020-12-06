package me.pugabyte.nexus.features.radio;

import com.xxmicloxx.NoteBlockAPI.event.PlayerRangeStateChangeEvent;
import com.xxmicloxx.NoteBlockAPI.event.SongNextEvent;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.radio.RadioUser;
import me.pugabyte.nexus.models.radio.RadioUserService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;
import java.util.UUID;

public class Listeners implements Listener {

	public Listeners() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();


		SongPlayer radio = Utils.getListenedRadio(player, false);
		if (radio != null)
			Utils.removePlayer(player, radio);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		for (PositionSongPlayer radio : RadioFeature.getRadiusRadios())
			Utils.addPlayer(player, radio);
	}

	@EventHandler
	public void onSongNext(SongNextEvent event) {
		SongPlayer songPlayer = event.getSongPlayer();
		PositionSongPlayer radiusRadio;
		String song = songPlayer.getSong().getTitle();
		Set<UUID> UUIDList = songPlayer.getPlayerUUIDs();

		for (UUID uuid : UUIDList) {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null) {
				if (songPlayer instanceof PositionSongPlayer) {
					radiusRadio = (PositionSongPlayer) songPlayer;
					if (Utils.isInRangeOfRadiusRadio(player, radiusRadio))
						Utils.actionBar(player, song, true);
				} else
					Utils.actionBar(player, song, true);
			}
		}
	}

	@EventHandler
	public void onRangeChange(PlayerRangeStateChangeEvent event) {
		Player player = event.getPlayer();
		SongPlayer radiusRadio = event.getSongPlayer();
		boolean isInRange = event.isInRange();

		RadioUserService service = new RadioUserService();
		RadioUser user = service.get(player);

		SongPlayer listenedRadio = Utils.getListenedRadio(player, true);
		RadioSongPlayer serverRadio = RadioFeature.getServerRadio();

		if (listenedRadio == serverRadio && isInRange) {
			// Make the player leave the server radio if they are listening, and join the radius radio
			Utils.removePlayer(player, serverRadio);
			Utils.addPlayer(player, radiusRadio);

		} else if (isInRange) {
			// Add the player to the radius radio, just in case.
			Utils.addPlayer(player, radiusRadio);

		} else if (user.isListening()) {
			// If player had radio on before entering a radius radio, turn it back on
			Utils.addPlayer(player, serverRadio);
		}
	}

}
