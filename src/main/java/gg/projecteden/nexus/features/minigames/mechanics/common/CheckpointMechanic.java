package gg.projecteden.nexus.features.minigames.mechanics.common;

import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.PlayerManager;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.arenas.CheckpointArena;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchTimerTickEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MinigamerQuitEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.CheckpointMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.singleplayer.SingleplayerMechanic;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.utils.TimeUtils.Timespan;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public abstract class CheckpointMechanic extends SingleplayerMechanic {

	public CheckpointMatchData getMatchData(Minigamer minigamer) {
		return minigamer.getMatch().getMatchData();
	}

	@Override
	public void onDeath(@NotNull MinigamerDeathEvent event) {
		super.onDeath(event);

		getMatchData(event.getMinigamer()).toCheckpoint(event.getMinigamer());
	}

	@Override
	public void onQuit(@NotNull MinigamerQuitEvent event) {
		super.onQuit(event);

		getMatchData(event.getMinigamer()).clearData(event.getMinigamer());
	}

	@EventHandler
	public void onEnterWinRegion(PlayerEnteredRegionEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		Arena arena = minigamer.getMatch().getArena();
		if (arena.ownsRegion(event.getRegion().getId(), "win")) {
			Minigames.broadcast("&e" + minigamer.getColoredName() + " &3completed &e" + arena.getDisplayName() + " &3in &e" + Timespan.ofSeconds(minigamer.getScore()).format());
			minigamer.quit();
		}
	}

	@EventHandler
	public void onEnterCheckpointRegion(PlayerEnteredRegionEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		CheckpointArena arena = minigamer.getMatch().getArena();

		if (arena.ownsRegion(event.getRegion().getId(), "checkpoint")) {
			int checkpointId = Arena.getRegionNumber(event.getRegion());
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
		if (!ActionGroup.RIGHT_CLICK.applies(event)) return;

		getMatchData(minigamer).toCheckpoint(minigamer);
	}

}
