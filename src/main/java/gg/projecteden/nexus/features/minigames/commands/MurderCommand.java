package gg.projecteden.nexus.features.minigames.commands;

import gg.projecteden.nexus.features.minigames.managers.ArenaManager;
import gg.projecteden.nexus.features.minigames.mechanics.Murder;
import gg.projecteden.nexus.features.minigames.models.Arena;
import gg.projecteden.nexus.features.minigames.models.arenas.MurderArena;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.Material;

@Permission("minigames.manage")
public class MurderCommand extends CustomCommand {

	MurderCommand(CommandEvent event) {
		super(event);
	}

	@Path("kit")
	void kit() {
		inventory().addItem(Murder.getKnife());
		inventory().addItem(Murder.getGun());
		inventory().addItem(Murder.getScrap());
		inventory().addItem(Murder.getFakeScrap());
		inventory().addItem(Murder.getCompass());
		inventory().addItem(Murder.getBloodlust());
		inventory().addItem(Murder.getTeleporter());
		inventory().addItem(Murder.getAdrenaline());
		inventory().addItem(Murder.getRetriever());
		send(PREFIX + "Giving murder kit");
	}

	@Path("(scraps|scrapoints) add")
	void addScraps() {
		MurderArena murderArena = getMurderArena();
		murderArena.getScrapPoints().add(location().getBlock().getLocation());
		ArenaManager.write(murderArena);
		send(PREFIX + "Added scrappoint");
	}

	@Path("(scraps|scrapoints) show")
	void showScraps() {
		MurderArena murderArena = getMurderArena();
		murderArena.getScrapPoints().forEach(location -> player().sendBlockChange(location, Material.EMERALD_BLOCK.createBlockData()));
		send(PREFIX + "Scrap points shown with emerald blocks");
	}

	@Path("(scraps|scrapoints) hide")
	void hideScraps() {
		MurderArena murderArena = getMurderArena();
		murderArena.getScrapPoints().forEach(location -> player().sendBlockChange(location, location.getBlock().getType().createBlockData()));
		send(PREFIX + "Scrap points hidden");
	}

	private MurderArena getMurderArena() {
		Arena arena = ArenaManager.getFromLocation(location());
		if (arena == null)
			error("No arena found at your location");
		if (!(arena.getMechanic() instanceof Murder))
			error("Arena at your location is not murder");

		MurderArena murderArena = (MurderArena) arena;
		return murderArena;
	}

}
