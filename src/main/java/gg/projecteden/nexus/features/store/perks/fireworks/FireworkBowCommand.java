package gg.projecteden.nexus.features.store.perks.fireworks;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.LuckPermsUtils;
import org.bukkit.Material;

public class FireworkBowCommand extends CustomCommand {

	public FireworkBowCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		if (!(player().hasPermission("fireworkbow.single") || player().hasPermission("fireworkbow.infinite")))
			permissionError();

		if (inventory().getItemInMainHand().getType() != Material.BOW)
			error("You must be holding a bow");

		runCommandAsOp("enchant firework");
		if (player().hasPermission("fireworkbow.single")) {
			send("&eYou have created your one firework bow! If you lose this bow, you won't be able to get another unless you purchase the command again.");
			LuckPermsUtils.PermissionChange.unset().uuid(uuid()).permissions("firework.single").runAsync();
		}

	}
}
