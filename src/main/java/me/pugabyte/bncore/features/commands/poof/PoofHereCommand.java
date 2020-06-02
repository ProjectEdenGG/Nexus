package me.pugabyte.bncore.features.commands.poof;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.poof.Poof;
import me.pugabyte.bncore.models.poof.PoofService;
import org.bukkit.entity.Player;

@Aliases("tpahere")
public class PoofHereCommand extends CustomCommand {
	PoofService service = new PoofService();

	public PoofHereCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void player(Player target) {
		if (target == player().getPlayer())
			error("You cannot poof to yourself");

		Poof request = new Poof(player(), target, Poof.PoofType.POOF_HERE);
		service.save(request);
		send(json("&ePoof-here &3request sent to " + target.getName() + ". ").next("&eClick to cancel").command("poof cancel"));
		send(target, "  &e" + player().getName() + " &3is asking you to poof &eto them&3.");
		send(target, json("&3  Click one  ||  &a&lAccept")
				.command("/poof accept")
				.hover("&eClick &3to accept")
				.group()
				.next("  &3||  &3")
				.group()
				.next("&c&lDeny")
				.command("/poof deny")
				.hover("&eClick &3to deny.")
				.group()
				.next("&3  ||"));

	}

	@Path("accept")
	void accept() {
		new PoofCommand().accept(player());
	}

	@Path("deny")
	void deny() {
		new PoofCommand().deny(player());
	}

	@Path("cancel")
	void cancel() {
		new PoofCommand().cancel(player());
	}

}
