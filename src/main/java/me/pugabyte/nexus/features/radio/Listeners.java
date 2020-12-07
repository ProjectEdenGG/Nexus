package me.pugabyte.nexus.features.radio;

import com.xxmicloxx.NoteBlockAPI.event.PlayerRangeStateChangeEvent;
import com.xxmicloxx.NoteBlockAPI.event.SongNextEvent;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.radio.RadioConfig.Radio;
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
		Radio radio = RadioUtils.getListenedRadio(player);
		if (radio != null)
			RadioUtils.removePlayer(player, radio);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		for (Radio radio : RadioUtils.getRadios()) {
			if (radio.getSongPlayer() instanceof PositionSongPlayer) {
				RadioUtils.addPlayer(player, radio);
			}
		}
	}

	@EventHandler
	public void onSongNext(SongNextEvent event) {
		SongPlayer songPlayer = event.getSongPlayer();
		String song = songPlayer.getSong().getTitle();
		Set<UUID> UUIDList = songPlayer.getPlayerUUIDs();

		for (UUID uuid : UUIDList) {
			Player player = Bukkit.getPlayer(uuid);
			if (player == null || !player.isOnline()) continue;

			if (songPlayer instanceof PositionSongPlayer) {
				Radio radio = RadioUtils.getRadio(songPlayer);
				if (radio != null) {
					if (RadioUtils.isInRangeOfRadiusRadio(player, radio))
						RadioUtils.actionBar(player, song, true);
				}
			} else
				RadioUtils.actionBar(player, song, true);
		}
	}

	// When entering & exiting radius radios
	@EventHandler
	public void onRangeChange(PlayerRangeStateChangeEvent event) {
		Player player = event.getPlayer();
		SongPlayer radiusSongPlayer = event.getSongPlayer();
		boolean isInRange = event.isInRange();

		RadioUserService service = new RadioUserService();
		RadioUser user = service.get(player);

		Radio radiusRadio = RadioUtils.getRadio(radiusSongPlayer);
		Radio listenedRadio = RadioUtils.getListenedRadio(player, true);

		if (listenedRadio == null) return;

		SongPlayer listenedSongPlayer = listenedRadio.getSongPlayer();
		if (listenedSongPlayer instanceof RadioSongPlayer && isInRange) {
			// Make the player leave the their listened server radio, and join the radius radio
			RadioUtils.removePlayer(player, listenedRadio);
			RadioUtils.addPlayer(player, radiusRadio);

		} else if (isInRange) {
			// Add the player to the radius radio, just in case.
			RadioUtils.addPlayer(player, radiusRadio);

		} else if (user.getLastRadioId() != null && !user.getLastRadioId().isEmpty()) {
			// If player had a server radio on before entering a radius radio, turn it back on
			RadioUtils.addPlayer(player, listenedRadio);

		}
	}

}
