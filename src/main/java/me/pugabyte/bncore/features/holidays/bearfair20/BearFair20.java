package me.pugabyte.bncore.features.holidays.bearfair20;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class BearFair20 implements Listener {
	Map<Player, Integer> musicTaskMap = new HashMap<>();
	String mainRg = "bearfair2020";
	String halloweenRg = mainRg + "_halloween";
	Location halloweenMusicLoc = new Location(Bukkit.getWorld("safepvp"), -921, 128, -1920);

	public BearFair20() {
		BNCore.registerListener(this);
		soundTasks();
	}

	private void soundTasks() {
		// Halloween sounds

	}

	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase(halloweenRg)) return;
		startMusicTask(event.getPlayer());
	}

	@EventHandler
	public void onRegionExit(RegionLeftEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase(halloweenRg)) return;
		stopMusicTask(event.getPlayer());
	}

	private void startMusicTask(Player player) {
		int taskId = Tasks.repeat(0, Time.SECOND.x(350), () -> {
			player.stopSound(Sound.MUSIC_DISC_13);
			player.playSound(halloweenMusicLoc, Sound.MUSIC_DISC_13, SoundCategory.RECORDS, 3F, 0.1F);
		});

		musicTaskMap.put(player, taskId);
	}

	private void stopMusicTask(Player player) {
		int taskId = musicTaskMap.remove(player);
		Tasks.cancel(taskId);
	}
}
