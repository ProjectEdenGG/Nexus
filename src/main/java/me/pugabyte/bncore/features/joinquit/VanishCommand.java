package me.pugabyte.bncore.features.joinquit;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Fallback;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Fallback("vanishnopacket")
@Redirect(from = {"/fj", "/fakejoin"}, to = "/vanish fj")
@Redirect(from = {"/fq", "/fakequit"}, to = "/vanish fq")
public class VanishCommand extends CustomCommand {

	public VanishCommand(@NonNull CommandEvent event) {
		super(event);
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

}
