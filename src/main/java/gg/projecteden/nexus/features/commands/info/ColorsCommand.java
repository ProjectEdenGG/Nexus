package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import static gg.projecteden.nexus.utils.StringUtils.colorize;

@Aliases({"colours", "color"})
@Description("Shows all of the built-in Minecraft color codes and formats in chat.")
@NoArgsConstructor
public class ColorsCommand extends CustomCommand implements Listener {

	public ColorsCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void colors() {
		line();
		send("&eMinecraft colors:");
		line();
		send(" &0 &&00  &1 &&11  &2 &&22  &3 &&33  &4 &&44  &5 &&55  &6 &&66  &7 &&77  ");
		send(" &8 &&88  &9 &&99  &a &&aa  &b &&bb  &c &&cc  &d &&dd  &e &&ee  &f &&ff  ");
		line();
		send(" &#&f123456 &fHex");
		line();
		send("&eMinecraft formats:");
		line();
		send("&f &&fk &kMagic&f  &&fl &lBold&f  &&fm &mStrike&f  &&fn &nUline&f  &&fo &oItalic&f  &&fr &fReset");
		line();
		send("&eFormat &3codes may &enot &3be used in &eprefixes&3; only &ecolors&3.");
		line();
	}

	@EventHandler
	public void onSignEdit(SignChangeEvent event) {
		for (int i = 0; i < event.getLines().length; i++)
			event.setLine(i, colorize(event.getLine(i)));
	}
}
