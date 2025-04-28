package gg.projecteden.nexus.features.minigames.mechanics;

import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.annotations.MatchStatisticsClass;
import gg.projecteden.nexus.features.minigames.models.annotations.Regenerating;
import gg.projecteden.nexus.features.minigames.models.events.matches.MatchBeginEvent;
import gg.projecteden.nexus.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import gg.projecteden.nexus.features.minigames.models.statistics.TNTRunStatistics;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Regenerating("floor")
@MatchStatisticsClass(TNTRunStatistics.class)
public class TNTRun extends TeamlessMechanic {

	@Override
	public @NotNull String getName() {
		return "TNTRun";
	}

	@Override
	public @NotNull String getDescription() {
		return "Try to survive as blocks break beneath your feet";
	}

	@Override
	public @NotNull ItemStack getMenuItem() {
		return new ItemStack(Material.TNT);
	}

	@Override
	public void onBegin(@NotNull MatchBeginEvent event) {
		super.onBegin(event);
		event.getMatch().broadcast("&eGo!");
		new TNTRunTask(event.getMatch());
	}

	@EventHandler
	public void onExplode(EntityExplodeEvent event) {
		if (!event.getLocation().getWorld().equals(Minigames.getWorld()))
			return;

		Arena arena = ArenaManager.getFromLocation(event.getLocation());
		if (arena == null || !(arena.getMechanic() instanceof TNTRun))
			return;

		event.blockList().clear();
	}

	public static class TNTRunTask {
		private final Match match;
		private int taskId;

		TNTRunTask(Match match) {
			this.match = match;
			start();
		}

		void start() {
			taskId = match.getTasks().repeat(0, 1, () -> {
				if (match.isEnded())
					stop();

				match.getAliveMinigamers().forEach(minigamer -> {
					Block standingOn = BlockUtils.getBlockStandingOn(minigamer.getOnlinePlayer());
					if (standingOn == null)
						return;

					Block tnt = standingOn.getRelative(0, -1, 0);
					if (!tnt.getType().equals(Material.TNT))
						return;

					match.getTasks().wait(4, () -> {
						if (match.isEnded())
							return;
						standingOn.setType(Material.AIR);
						tnt.setType(Material.AIR);

						match.getMatchStatistics().award(TNTRunStatistics.BLOCKS_BROKEN, minigamer);
					});
				});

			});
		}

		void stop() {
			Tasks.cancel(taskId);
		}
	}
}
