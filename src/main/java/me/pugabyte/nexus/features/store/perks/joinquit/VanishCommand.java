package me.pugabyte.nexus.features.store.perks.joinquit;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Fallback;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

import java.util.Arrays;
import java.util.List;

@Fallback("premiumvanish")
@Redirect(from = {"/fj", "/fakejoin"}, to = "/nexus:vanish fj")
@Redirect(from = {"/fq", "/fakequit"}, to = "/nexus:vanish fq")
@Redirect(from = {"/ni", "/nointeract"}, to = "/nexus:vanish ni")
@Redirect(from = {"/np", "/nopickup"}, to = "/nexus:vanish np")
public class VanishCommand extends CustomCommand {

	public VanishCommand(@NonNull CommandEvent event) {
		super(event);
		new VanishEvent(player()).callEvent();
	}

	@Path("(fj|fakejoin)")
	@Permission("vanish.fakeannounce")
	void fakeJoin() {
		JoinQuit.join(player());
		runCommand("vanish off");
	}

	@Path("(fq|fakequit)")
	@Permission("vanish.fakeannounce")
	void fakeQuit() {
		JoinQuit.quit(player());
		runCommand("vanish on");
	}

	private static final List<String> interact_permissions = Arrays.asList("pv.interact", "pv.useblocks",
			"pv.damage", "pv.breakblocks", "pv.placeblocks", "pv.dropitems");

	@Path("(ni|nointeract)")
	@Permission("pv.use")
	void toggleInteract() {
		if (player().hasPermission("pv.interact")) {
			for (String perm : interact_permissions)
				runCommandAsConsole("lp user " + name() + " permission unset " + perm);

			send(PREFIX + "Interaction disabled");
		} else {
			for (String perm : interact_permissions)
				runCommandAsConsole("lp user " + name() + " permission set " + perm);

			send(PREFIX + "Interaction enabled");
		}
	}

	@Path("(np|nopickup)")
	@Permission("pv.use")
	void togglePickup() {
		runCommand("premiumvanish:vanish tipu");
	}

}
