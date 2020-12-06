package me.pugabyte.nexus.features.radio;

import com.xxmicloxx.NoteBlockAPI.model.FadeType;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.SoundCategory;
import com.xxmicloxx.NoteBlockAPI.songplayer.Fade;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.radio.RadioConfig;
import me.pugabyte.nexus.models.radio.RadioConfig.Radio;
import me.pugabyte.nexus.models.radio.RadioConfigService;
import me.pugabyte.nexus.models.radio.RadioUser;
import me.pugabyte.nexus.models.radio.RadioUserService;
import me.pugabyte.nexus.utils.ActionBarUtils;
import org.bukkit.entity.Player;

import java.util.Set;

public class Utils {

	public static void actionBar(Player player, String songTitle) {
		actionBar(player, songTitle, false);
	}

	public static void actionBar(Player player, String songTitle, boolean nowPlaying) {
		String message = "&2&lCurrently Playing: &a";
		if (nowPlaying) message = "&2&lNow Playing: &a";
		message += " " + songTitle;
		ActionBarUtils.sendActionBar(player, message);
	}

	public static boolean isListening(Player player, SongPlayer songPlayer) {
		return songPlayer.getPlayerUUIDs().contains(player.getUniqueId());
	}

	public static SongPlayer getListenedRadio(Player player, boolean includeRadius) {
		RadioUserService userService = new RadioUserService();
		RadioUser user = userService.get(player);
		SongPlayer songPlayer = user.getRadio().getSongPlayer();

		//
		if (songPlayer instanceof RadioSongPlayer) {
			RadioSongPlayer radioSongPlayer = (RadioSongPlayer) songPlayer;
			if (isListening(player, radioSongPlayer))
				return songPlayer;

		} else if (songPlayer instanceof PositionSongPlayer) {
			PositionSongPlayer positionSongPlayer = (PositionSongPlayer) songPlayer;
			if (isListening(player, positionSongPlayer)) {
				if (includeRadius) {
					if (isInRangeOfRadiusRadio(player, positionSongPlayer))
						return positionSongPlayer;
				} else {
					if (isListening(player, positionSongPlayer))
						return positionSongPlayer;
				}
			}
		}

		return null;
	}

	public static boolean isInRangeOfRadiusRadio(Player player, PositionSongPlayer radio) {
		if (radio == null) return false;
		if (radio.getTargetLocation() == null) return false;
		if (radio.getTargetLocation().getWorld() == null) return false;
		return radio.getTargetLocation().getWorld().equals(player.getWorld()) && radio.isInRange(player);
	}

	public static Set<Radio> getRadios() {
		RadioConfigService configService = new RadioConfigService();
		RadioConfig config = configService.get(Nexus.getUUID0());
		return config.getRadios();
	}

	public static boolean isInRangeOfAnyRadiusRadio(Player player) {
		for (Radio radio : getRadios()) {
			SongPlayer songPlayer = radio.getSongPlayer();
			if (songPlayer instanceof PositionSongPlayer) {
				PositionSongPlayer radiusRadio = (PositionSongPlayer) songPlayer;
				if (radiusRadio.getTargetLocation().getWorld().equals(player.getWorld()) && radiusRadio.isInRange(player))
					return true;
			}
		}

		return false;
	}

	public static void setRadioDefaults(SongPlayer radio) {
		if (radio instanceof RadioSongPlayer)
			((RadioSongPlayer) radio).setStereo(true);

		Fade fadeIn = radio.getFadeIn();
		fadeIn.setType(FadeType.LINEAR);
		fadeIn.setFadeDuration(60);

		Fade fadeOut = radio.getFadeOut();
		fadeOut.setType(FadeType.LINEAR);
		fadeOut.setFadeDuration(60);

		radio.setRandom(true);
		radio.setRepeatMode(RepeatMode.ALL);

		radio.setCategory(SoundCategory.RECORDS);
		radio.setPlaying(true);
	}

	public static void addPlayer(Player player, SongPlayer radio) {
		if (!Utils.isListening(player, radio)) {
			radio.addPlayer(player);
			if (radio instanceof PositionSongPlayer) {
				PositionSongPlayer songPlayer = (PositionSongPlayer) radio;
				if (Utils.isInRangeOfRadiusRadio(player, songPlayer))
					actionBar(player, radio.getSong().getTitle());
			}
		}
	}

	public static void removePlayer(Player player, SongPlayer radio) {
		if (Utils.isListening(player, radio)) {
			radio.removePlayer(player);
			actionBar(player, "&c&lYou have left the server radio");
		} else
			player.sendMessage(RadioFeature.getPREFIX() + " &cYou are not listening to a radio!");
	}

	public static void removeRadio(Radio radio) {
		RadioFeature.removeSongPlayer(radio.getSongPlayer());

		RadioConfigService configService = new RadioConfigService();
		RadioConfig config = configService.get(Nexus.getUUID0());
		config.getRadios().remove(radio);
	}
}
