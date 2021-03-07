package me.pugabyte.nexus.features.minigames.mechanics;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchQuitEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchStartEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class HideAndSeek extends Infection {
	@Override
	public String getName() {
		return "Hide and Seek";
	}

	@Override
	public String getDescription() {
		return "Hide from the hunters!";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.GRASS_BLOCK);
	}

	@Override
	public void onJoin(MatchJoinEvent event) {
		super.onJoin(event);
		if (!event.getMatch().isStarted()) {
			 // TODO: select block
		}
	}

	@Override
	public void onStart(MatchStartEvent event) {
		super.onStart(event);
		event.getMatch().getMinigamers().forEach(minigamer -> {
			MiscDisguise disguise = new MiscDisguise(DisguiseType.FALLING_BLOCK, Material.GRASS_BLOCK);
			disguise.setEntity(minigamer.getPlayer());
			disguise.startDisguise();

			int taskId = event.getMatch().getTasks().repeat(0, 1, () -> {
				event.getMatch().getMinigamers().forEach(minigamer1 -> {
					if (minigamer1 != minigamer) {
						minigamer1.getPlayer().sendBlockChange(minigamer.getPlayer().getLocation(), Material.GRASS_BLOCK.createBlockData());
					}
				});
			});
			event.getMatch().getTasks().register(taskId);
		});
	}

	@Override
	public void onQuit(MatchQuitEvent event) {
		super.onQuit(event);
		DisguiseAPI.undisguiseToAll(event.getMinigamer().getPlayer());
	}

	@Override
	public void onEnd(MatchEndEvent event) {
		super.onEnd(event);
		event.getMatch().getMinigamers().forEach(minigamer -> DisguiseAPI.undisguiseToAll(minigamer.getPlayer()));
	}
}
