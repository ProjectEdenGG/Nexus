package me.pugabyte.bncore.features.minigames.commands;

import me.pugabyte.bncore.features.minigames.mechanics.Murder;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

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
}