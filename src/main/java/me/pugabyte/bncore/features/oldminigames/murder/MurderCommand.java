package me.pugabyte.bncore.features.oldminigames.murder;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.Bukkit;

@Permission("murder")
public class MurderCommand extends CustomCommand {

	MurderCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void redirectToSkript() {
		Bukkit.getServer().dispatchCommand(player(), "skmurder " + String.join(" ", event.getArgs()));
	}

	@Path("kit")
	void kit() {
		player().getInventory().addItem(MurderUtils.getKnife());
		player().getInventory().addItem(MurderUtils.getGun());
		player().getInventory().addItem(MurderUtils.getScrap());
		player().getInventory().addItem(MurderUtils.getFakeScrap());
		player().getInventory().addItem(MurderUtils.getCompass());
		player().getInventory().addItem(MurderUtils.getBloodlust());
		player().getInventory().addItem(MurderUtils.getTeleporter());
		player().getInventory().addItem(MurderUtils.getAdrenaline());
		player().getInventory().addItem(MurderUtils.getRetriever());
		send(PREFIX + "Giving murder kit");
	}
}