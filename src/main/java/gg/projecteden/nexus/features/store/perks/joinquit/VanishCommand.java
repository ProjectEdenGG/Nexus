package gg.projecteden.nexus.features.store.perks.joinquit;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Fallback;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.LuckPermsUtils.PermissionChange;
import gg.projecteden.nexus.utils.LuckPermsUtils.PermissionChange.PermissionChangeBuilder;
import lombok.NonNull;

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

	private static final List<String> INTERACT_PERMISSIONS = List.of(
		"pv.interact",
		"pv.useblocks",
		"pv.damage",
		"pv.breakblocks",
		"pv.placeblocks",
		"pv.dropitems"
	);

	@Path("(ni|nointeract)")
	@Permission("pv.use")
	void toggleInteract() {
		final boolean disabling = player().hasPermission(INTERACT_PERMISSIONS.get(0));
		final PermissionChangeBuilder change = disabling ? PermissionChange.unset() : PermissionChange.set();

		change.uuid(uuid()).permissions(INTERACT_PERMISSIONS).runAsync().thenRun(() ->
			send(PREFIX + "Interaction " + (disabling ? "&cdisabled" : "&aenabled")));
	}

	@Path("(np|nopickup)")
	@Permission("pv.use")
	void togglePickup() {
		runCommand("premiumvanish:vanish tipu");
	}

}
