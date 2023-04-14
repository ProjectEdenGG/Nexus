package gg.projecteden.nexus.features.commands.ranks;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.models.nerd.Rank;

import static gg.projecteden.api.common.utils.TimeUtils.shortDateFormat;

@HideFromWiki
public class ArchitectCommand extends CustomCommand {

	public ArchitectCommand(CommandEvent event) {
		super(event);
	}

	@Override
	@NoLiterals
	@Description("Learn about the Architect rank")
	public void help() {
		line(5);
		send(Rank.ARCHITECT.getChatColor() + "Architects &3oversee the building tasks for the server and recruit players to help with projects.");
		line();
		send("&3[+] &eHow to achieve&3: Promoted from " + Rank.BUILDER.getChatColor() + "Builder &3by Senior Staff.");
		send(json("&3[+] &eClick here &3for a list of architects").command("/architect list"));
		line();
		RanksCommand.ranksReturn(player());
	}

	@Async
	@Description("List current Architects")
	void list() {
		Rank.ARCHITECT.getNerds().thenAccept(nerds -> {
			line();
			send("&3All current " + Rank.ARCHITECT.getChatColor() + "Architects &3and the date they were promoted:");
			nerds.forEach(nerd -> send(nerd.getColoredName() + " &7-&e " + shortDateFormat(nerd.getPromotionDate())));
			line();
			RanksCommand.ranksReturn(player());
		});
	}
}
