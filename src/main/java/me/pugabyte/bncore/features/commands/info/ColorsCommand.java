package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Aliases({"colours", "color"})
public class ColorsCommand extends CustomCommand {

	public ColorsCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void colors() {
		line();
		send("&3You must be &eTrusted &3to use colors on signs, and &b&oModerator &3to use formats on signs.");
		send("&eMinecraft colors:");
		line();
		send(" &0 &&00  &1 &&11  &2 &&22  &3 &&33  &4 &&44  &5 &&55  &6 &&66  &7 &&77  ");
		send(" &8 &&88  &9 &&99  &a &&aa  &b &&bb  &c &&cc  &d &&dd  &e &&ee  &f &&ff  ");
		line();
		send("&eMinecraft formats:");
		line();
		send("&f &&fk &kMagic&f &&fl &lBold&r  &&fm &mStrike&r  &&fn &nUline&r  &&fo &oItalic&r  &&fr &rReset");
		line();
		send("&eFormat &3codes may &enot &3be used in &eprefixes&3; only &ecolors&3.");
		line();
	}
}
