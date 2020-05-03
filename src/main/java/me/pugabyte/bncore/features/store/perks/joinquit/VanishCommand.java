package me.pugabyte.bncore.features.store.perks.joinquit;

import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Fallback;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;

@Fallback("premiumvanish")
@Redirect(from = {"/fj", "/fakejoin"}, to = "/bncore:vanish fj")
@Redirect(from = {"/fq", "/fakequit"}, to = "/bncore:vanish fq")
@Redirect(from = {"/ni", "/nointeract"}, to = "/bncore:vanish ni")
@Redirect(from = {"/np", "/nopickup"}, to = "/bncore:vanish np")
public class VanishCommand extends CustomCommand {

	public VanishCommand(@NonNull CommandEvent event) {
		super(event);
		Utils.callEvent(new VanishEvent(player()));
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
	@Permission("vanish.vanish")
	void toggleInteract() {
		if (player().hasPermission("pv.interact")) {
			BNCore.getPerms().playerRemove(player(), "pv.interact");
			BNCore.getPerms().playerRemove(player(), "pv.useblocks");
			BNCore.getPerms().playerRemove(player(), "pv.damage");
			BNCore.getPerms().playerRemove(player(), "pv.breakblocks");
			BNCore.getPerms().playerRemove(player(), "pv.placeblocks");
			BNCore.getPerms().playerRemove(player(), "pv.dropitems");
			send("interaction disabled");
		} else {
			BNCore.getPerms().playerAdd(player(), "pv.interact");
			BNCore.getPerms().playerAdd(player(), "pv.useblocks");
			BNCore.getPerms().playerAdd(player(), "pv.damage");
			BNCore.getPerms().playerAdd(player(), "pv.breakblocks");
			BNCore.getPerms().playerAdd(player(), "pv.placeblocks");
			BNCore.getPerms().playerAdd(player(), "pv.dropitems");
			send("interaction enabled");
		}
	}

	@Path("(np|nopickup)")
	@Permission("vanish.vanish")
	void togglePickup() {
		if (player().hasPermission("pv.toggleitems")) {
			BNCore.getPerms().playerRemove(player(), "pv.toggleitems");
			send("pickup enabled");
		} else {
			BNCore.getPerms().playerAdd(player(), "pv.toggleitems");
			send("pickup disabled");
		}
	}

}
