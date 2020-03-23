package me.pugabyte.bncore.features.store.perks.fireworks;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.preconfigured.NoPermissionException;
import org.bukkit.Material;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class FireworkBowCommand extends CustomCommand {

	public FireworkBowCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		if (!(player().hasPermission("fireworkbow.single") || player().hasPermission("fireworkbow.infinite")))
			throw new NoPermissionException();

		if (player().getInventory().getItemInMainHand().getType() != Material.BOW)
			error("You must be holding a bow");

		runCommandAsOp("ce enchant firework");
		if (player().hasPermission("fireworkbow.single")) {
			send("&eYou have created your one firework bow! If you lose this bow, you won't be able to get another unless you purchase the command again.");
			PermissionsEx.getUser(player()).removePermission("fireworkbow.single");
		}

	}
}
