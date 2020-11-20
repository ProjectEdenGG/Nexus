package me.pugabyte.nexus.features.store.perks.fireworks;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.Material;

public class FireworkBowCommand extends CustomCommand {

	public FireworkBowCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		if (!(player().hasPermission("fireworkbow.single") || player().hasPermission("fireworkbow.infinite")))
			permissionError();

		if (player().getInventory().getItemInMainHand().getType() != Material.BOW)
			error("You must be holding a bow");

		runCommandAsOp("ce enchant firework");
		if (player().hasPermission("fireworkbow.single")) {
			send("&eYou have created your one firework bow! If you lose this bow, you won't be able to get another unless you purchase the command again.");
			Nexus.getPerms().playerRemove(player(), "fireworkbow.single");
		}

	}
}
