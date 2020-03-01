package me.pugabyte.bncore.features.minigames.mechanics.common;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.arenas.CheckpointArena;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchQuitEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchTimerTickEvent;
import me.pugabyte.bncore.features.minigames.models.matchdata.CheckpointMatchData;
import me.pugabyte.bncore.features.minigames.models.mechanics.singleplayer.SingleplayerMechanic;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;

public abstract class CheckpointMechanic extends SingleplayerMechanic {

	public CheckpointMatchData getMatchData(Minigamer minigamer) {
		return minigamer.getMatch().getMatchData();
	}

	@Override
	public void kill(Minigamer minigamer, Minigamer attacker) {
		super.kill(minigamer, attacker);

		getMatchData(minigamer).toCheckpoint(minigamer);
	}

	@Override
	public void onQuit(MatchQuitEvent event) {
		super.onQuit(event);

		getMatchData(event.getMinigamer()).clearData(event.getMinigamer());
	}

	@EventHandler
	public void onEnterWinRegion(RegionEnteredEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		Arena arena = minigamer.getMatch().getArena();
		if (arena.ownsRegion(event.getRegion().getId(), "win")) {
			Minigames.broadcast("&e" + minigamer.getColoredName() + " &3completed &e" + arena.getDisplayName() + " &3in &e" + Utils.timespanFormat(minigamer.getScore()));
			minigamer.quit();
		}
	}

	@EventHandler
	public void onEnterCheckpointRegion(RegionEnteredEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		CheckpointArena arena = minigamer.getMatch().getArena();

		if (arena.ownsRegion(event.getRegion().getId(), "checkpoint")) {
			int checkpointId = arena.getRegionTypeId(event.getRegion());
			getMatchData(minigamer).setCheckpoint(minigamer, checkpointId);
		}
	}

	@EventHandler
	public void onTick(MatchTimerTickEvent event) {
		if (!event.getMatch().isMechanic(this)) return;

		event.getMatch().getMinigamers().forEach(Minigamer::scored);
	}

	@EventHandler
	public void onCheckpointReset(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isPlaying(this)) return;

		if (player.getInventory().getItemInMainHand() == null) return;
		if (player.getInventory().getItemInMainHand().getType() != Material.POISONOUS_POTATO) return;
		if (!Arrays.asList(Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR).contains(event.getAction())) return;

		getMatchData(minigamer).toCheckpoint(minigamer);
	}

}
