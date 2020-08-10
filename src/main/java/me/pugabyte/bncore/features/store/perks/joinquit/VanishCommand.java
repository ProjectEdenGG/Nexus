package me.pugabyte.bncore.features.store.perks.joinquit;

import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Fallback;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Fallback("premiumvanish")
@Redirect(from = {"/fj", "/fakejoin"}, to = "/bncore:vanish fj")
@Redirect(from = {"/fq", "/fakequit"}, to = "/bncore:vanish fq")
@Redirect(from = {"/ni", "/nointeract"}, to = "/bncore:vanish ni")
@Redirect(from = {"/np", "/nopickup"}, to = "/bncore:vanish np")
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
			BNCore.getPerms().playerRemove(player(), "pv.interact");
			BNCore.getPerms().playerRemove(player(), "pv.useblocks");
			BNCore.getPerms().playerRemove(player(), "pv.damage");
			BNCore.getPerms().playerRemove(player(), "pv.breakblocks");
			BNCore.getPerms().playerRemove(player(), "pv.placeblocks");
			BNCore.getPerms().playerRemove(player(), "pv.dropitems");
			send(PREFIX + "Interaction disabled");
		} else {
			BNCore.getPerms().playerAdd(player(), "pv.interact");
			BNCore.getPerms().playerAdd(player(), "pv.useblocks");
			BNCore.getPerms().playerAdd(player(), "pv.damage");
			BNCore.getPerms().playerAdd(player(), "pv.breakblocks");
			BNCore.getPerms().playerAdd(player(), "pv.placeblocks");
			BNCore.getPerms().playerAdd(player(), "pv.dropitems");
			send(PREFIX + "Interaction enabled");
		}
	}

	@Path("(np|nopickup)")
	@Permission("pv.use")
	void togglePickup() {
		runCommand("premiumvanish:vanish tipu");
	}
}
