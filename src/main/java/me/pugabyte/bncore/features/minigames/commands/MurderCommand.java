package me.pugabyte.bncore.features.minigames.commands;

import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.mechanics.Murder;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.arenas.MurderArena;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.Material;

@Permission("minigames.manage")
public class MurderCommand extends CustomCommand {

	MurderCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void redirectToSkript() {
		runCommand("skmurder " + String.join(" ", event.getArgs()));
	}

	@Path("kit")
	void kit() {
		player().getInventory().addItem(Murder.getKnife());
		player().getInventory().addItem(Murder.getGun());
		player().getInventory().addItem(Murder.getScrap());
		player().getInventory().addItem(Murder.getFakeScrap());
		player().getInventory().addItem(Murder.getCompass());
		player().getInventory().addItem(Murder.getBloodlust());
		player().getInventory().addItem(Murder.getTeleporter());
		player().getInventory().addItem(Murder.getAdrenaline());
		player().getInventory().addItem(Murder.getRetriever());
		send(PREFIX + "Giving murder kit");
	}

	@Path("(scraps|scrapoints) add")
	void addScraps() {
		MurderArena murderArena = getMurderArena();
		murderArena.getScrapPoints().add(player().getLocation().getBlock().getLocation());
		ArenaManager.write(murderArena);
		send(PREFIX + "Added scrappoint");
	}

	@Path("(scraps|scrapoints) show")
	void showScraps() {
		MurderArena murderArena = getMurderArena();
		murderArena.getScrapPoints().forEach(location -> player().sendBlockChange(location, Material.EMERALD_BLOCK, (byte) 0));
		send(PREFIX + "Scrap points shown with emerald blocks");
	}

	@Path("(scraps|scrapoints) hide")
	void hideScraps() {
		MurderArena murderArena = getMurderArena();
		murderArena.getScrapPoints().forEach(location -> player().sendBlockChange(location, location.getBlock().getType(), location.getBlock().getData()));
		send(PREFIX + "Scrap points hidden");
	}

	private MurderArena getMurderArena() {
		Arena arena = ArenaManager.getFromLocation(player().getLocation());
		if (arena == null)
			error("No arena found at your location");
		if (!(arena.getMechanic() instanceof Murder))
			error("Arena at your location is not murder");

		MurderArena murderArena = (MurderArena) arena;
		return murderArena;
	}

}