package gg.projecteden.nexus.features.minigames.mechanics.common;

import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.MatchStatistics;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchStatisticsClass;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchQuitEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchTimerTickEvent;
import gg.projecteden.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import gg.projecteden.nexus.features.minigames.models.matchdata.CheckpointMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.singleplayer.SingleplayerMechanic;
import gg.projecteden.nexus.features.minigames.models.statistics.CheckpointStatistics;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

@MatchStatisticsClass(CheckpointStatistics.class)
public abstract class CheckpointMechanic extends SingleplayerMechanic {

	public CheckpointMatchData getMatchData(Minigamer minigamer) {
		return minigamer.getMatch().getMatchData();
	}

	@Override
	public void onJoin(@NotNull MatchJoinEvent event) {
		super.onJoin(event);

		getMatchData(event.getMinigamer()).initialize(event.getMinigamer());
	}

	@Override
	public void onDeath(@NotNull MinigamerDeathEvent event) {
		// preserve held item slot
		int heldItemSlot = event.getMinigamer().getPlayer().getInventory().getHeldItemSlot();
		super.onDeath(event);
		event.getMinigamer().getPlayer().getInventory().setHeldItemSlot(heldItemSlot);

		getMatchData(event.getMinigamer()).toCheckpoint(event.getMinigamer(), false);
	}

	@Override
	public void onQuit(@NotNull MatchQuitEvent event) {
		super.onQuit(event);

		getMatchData(event.getMinigamer()).clearData(event.getMinigamer());
	}

	@EventHandler
	public void onEnterWinRegion(PlayerEnteredRegionEvent event) {
		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		Arena arena = minigamer.getMatch().getArena();
		if (arena.ownsRegion(event.getRegion().getId(), "win")) {
			Instant now = Instant.now();
			CheckpointMatchData matchData = getMatchData(minigamer);
			Minigames.broadcast(
				new JsonBuilder(NamedTextColor.DARK_AQUA)
					.next(minigamer.getColoredName())
					.rawNext(" completed ")
					.next(arena.getDisplayName(), NamedTextColor.YELLOW)
					.rawNext(" in ")
					.next(
						new JsonBuilder("&e" + matchData.formatTotalChatTime(minigamer, now))
							.hover(matchData.formatCheckpointTimesHoverText(minigamer, now))
					)
			);
			// TODO: broadcast WRs to whole server
			matchData.getMatch().getMatchStatistics().award(MatchStatistics.WINS, minigamer);
			matchData.onWin(minigamer, now);
			minigamer.quit();
		}
	}

	@EventHandler
	public void onEnterCheckpointRegion(PlayerEnteredRegionEvent event) {
		Minigamer minigamer = Minigamer.of(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		Arena arena = minigamer.getMatch().getArena();

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
	public void onInteractEvent(PlayerInteractEvent event) {
		// TODO: add hide/show players item
		Player player = event.getPlayer();
		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying(this)) return;
		if (!ActionGroup.RIGHT_CLICK.applies(event)) return;

		Material item = player.getInventory().getItemInMainHand().getType();
		switch (item) {
			case POISONOUS_POTATO:
				if (canTeleport(minigamer))
					getMatchData(minigamer).toCheckpoint(minigamer, true);
				break;
			case GUNPOWDER:
				if (canTeleport(minigamer))
					getMatchData(minigamer).reset(minigamer);
				break;
			default:
				return;
		}

		event.setCancelled(true);
	}

	private boolean canTeleport(Minigamer minigamer) {
		if (!minigamer.getOnlinePlayer().isOnGround()) {
			minigamer.sendActionBar(new JsonBuilder("You must be on the ground to use this item.", NamedTextColor.RED));
			return false;
		}
		return true;
	}

}
