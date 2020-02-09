package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.annotations.Regenerating;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Regenerating("floor")
public class TNTRun extends TeamlessMechanic {

	@Override
	public String getName() {
		return "TNTRun";
	}

	@Override
	public String getDescription() {
		return "???";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.TNT);
	}

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);
		new TNTRunTask(event.getMatch());
	}

	@EventHandler
	public void onExplode(EntityExplodeEvent event) {
		if (!event.getLocation().getWorld().equals(Minigames.getGameworld()))
			return;

		Arena arena = ArenaManager.getFromLocation(event.getLocation());
		if (arena == null || arena.getMechanic().equals(this))
			return;

		event.blockList().clear();
	}

	public static class TNTRunTask {
		private Match match;
		private int taskId;
		public List<Minigamer> minigamers;

		TNTRunTask(Match match) {
			this.match = match;
			minigamers = match.getMinigamers();
			start();
		}

		void start() {
			taskId = match.getTasks().repeat(3 * 20, 1, () -> {
				if (match.isEnded())
					stop(taskId);

				minigamers.forEach(minigamer -> {
					if (!minigamer.getPlayer().isOnGround())
						return;

					Block standingOn = Utils.getBlockStandingOn(minigamer.getPlayer());
					if (standingOn == null)
						return;

					if (!(standingOn.getType().equals(Material.SAND) || standingOn.getType().equals(Material.GRAVEL)))
						return;

					Block tnt = standingOn.getRelative(0, -1, 0);
					if (!tnt.getType().equals(Material.TNT))
						return;

					Tasks.wait(4, () -> {
						standingOn.setType(Material.AIR);
						tnt.setType(Material.AIR);
					});
				});
			});
		}

		void stop(int taskId) {
			Tasks.cancel(taskId);
		}
	}
}
