package gg.projecteden.nexus.features.commands.ranks;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;

@HideFromWiki
public class ArchitectCommand extends CustomCommand {

	public ArchitectCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void architect() {
		line(5);
		send(Rank.ARCHITECT.getChatColor() + "Architects &3oversee the building tasks for the server and recruit players to help with projects.");
		line();
		send("&3[+] &eHow to achieve&3: Promoted from " + Rank.BUILDER.getChatColor() + "Builder &3by Senior Staff.");
		send(json("&3[+] &eClick here &3for a list of architects").command("/architect list"));
		line();
		RanksCommand.ranksReturn(player());
	}

	@Async
	@Path("list")
	void list() {
		Rank.ARCHITECT.getNerds().thenAccept(nerds -> {
			line();
			send("&3All current " + Rank.ARCHITECT.getChatColor() + "Architects &3and the date they were promoted:");
			nerds.forEach(nerd -> send(nerd.getColoredName() + " &7-&e " + TimeUtils.shortDateFormat(nerd.getPromotionDate())));
			line();
			RanksCommand.ranksReturn(player());
		});
	}
}
