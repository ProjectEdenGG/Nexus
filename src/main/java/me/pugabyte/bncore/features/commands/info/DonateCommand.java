package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Aliases({"donatereturn", "buy", "store"})
public class DonateCommand extends CustomCommand {
	String PLUS = "&3[+] &e";

	public DonateCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void donate() {
		line();
		send("&3Enjoying the server? &3Share the love by &edonating&3! We are always extremely grateful for donations, " +
				"and they come with some cool &erewards&3! Visit &ehttps://store.bnn.gg &3to view the packages you can " +
				"get.");
		line();
		send(json(PLUS + "Terms and Conditions").hover(PLUS + "Click here before you donate for anything.").command("/donate tac"));
	}

	@Path("tac")
	void tac() {
		line();
		send("&3Before you donate on the server, here are some things you must know before you do so.");
		send(PLUS + "There are no refunds.");
		send(PLUS + "If you are under the age of eighteen, be sure to have a parent or guardians permission.");
		send(PLUS + "None of the money that is donated goes to a Staff member personally. The money is for improving " +
				"the server only.");
		send(PLUS + "Just because you donate does not mean you can not be banned.");
	}

}
