package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Aliases("colours")
public class ColorsCommand extends CustomCommand {

	public ColorsCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void colors() {
		empty();
		reply("&3You must be &eTrusted &3to use colors on signs, and &b&oModerator &3to use formats on signs.");
		reply("&eMinecraft colors:");
		empty();
		reply(" &0 &&00  &1 &&11  &2 &&22  &3 &&33  &4 &&44  &5 &&55  &6 &&66  &7 &&77  ");
		reply(" &8 &&88  &9 &&99  &a &&aa  &b &&bb  &c &&cc  &d &&dd  &e &&ee  &f &&ff  ");
		empty();
		reply("&eMinecraft formats:");
		empty();
		reply("&f &&fk &kMagic&f &&fl &lBold&r  &&fm &mStrike&r  &&fn &nUline&r  &&fo &oItalic&r  &&fr &rReset");
		empty();
		reply("&eFormat &3codes may &enot &3be used in &eprefixes&3; only &ecolors&3.");
		empty();
	}
}
