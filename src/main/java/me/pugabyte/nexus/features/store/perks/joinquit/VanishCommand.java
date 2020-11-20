package me.pugabyte.nexus.features.store.perks.joinquit;

import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Fallback;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

@Fallback("premiumvanish")
@Redirect(from = {"/fj", "/fakejoin"}, to = "/nexus:vanish fj")
@Redirect(from = {"/fq", "/fakequit"}, to = "/nexus:vanish fq")
@Redirect(from = {"/ni", "/nointeract"}, to = "/nexus:vanish ni")
@Redirect(from = {"/np", "/nopickup"}, to = "/nexus:vanish np")
public class VanishCommand extends CustomCommand {
	private static String pickupPermission = "pv.toggleitems";

	public VanishCommand(@NonNull CommandEvent event) {
		super(event);
		new VanishEvent(player()).callEvent();
	}

	@Path("(fj|fakejoin)")
	@Permission("vanish.fakeannounce")
	void fakeJoin() {
		runCommand("vanish off");
		JoinQuit.join(player());
	}

	@Path("(fq|fakequit)")
	@Permission("vanish.fakeannounce")
	void fakeQuit() {
		runCommand("vanish on");
		JoinQuit.quit(player());
	}

	@Path("(ni|nointeract)")
	@Permission("pv.use")
	void toggleInteract() {
		if (player().hasPermission("pv.interact")) {
			Nexus.getPerms().playerRemove(player(), "pv.interact");
			Nexus.getPerms().playerRemove(player(), "pv.useblocks");
			Nexus.getPerms().playerRemove(player(), "pv.damage");
			Nexus.getPerms().playerRemove(player(), "pv.breakblocks");
			Nexus.getPerms().playerRemove(player(), "pv.placeblocks");
			Nexus.getPerms().playerRemove(player(), "pv.dropitems");
			send(PREFIX + "Interaction disabled");
		} else {
			Nexus.getPerms().playerAdd(player(), "pv.interact");
			Nexus.getPerms().playerAdd(player(), "pv.useblocks");
			Nexus.getPerms().playerAdd(player(), "pv.damage");
			Nexus.getPerms().playerAdd(player(), "pv.breakblocks");
			Nexus.getPerms().playerAdd(player(), "pv.placeblocks");
			Nexus.getPerms().playerAdd(player(), "pv.dropitems");
			send(PREFIX + "Interaction enabled");
		}
	}

	@Path("(np|nopickup)")
	@Permission("pv.use")
	void togglePickup() {
		runCommand("premiumvanish:vanish tipu");
	}
}
