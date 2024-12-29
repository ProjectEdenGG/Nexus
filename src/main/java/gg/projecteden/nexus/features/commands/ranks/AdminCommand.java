package gg.projecteden.nexus.features.commands.ranks;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;

@HideFromWiki
public class AdminCommand extends CustomCommand {

	public AdminCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void admin() {
		line(5);
		send(Rank.ADMIN.getChatColor() + "Administrator &3is the highest possible rank to achieve on the server. They are in charge of the &eentire &3server and staff, " +
				"and making sure everything is running as it should.");
		line();
		send("&3[+] &eSenior Staff rank");
		send("&3[+] &eHow to achieve&3: &3Promoted from " + Rank.OPERATOR.getChatColor() + "Operator &3by existing Admins");
		send(json("&3[+] &eClick here &3for a list of admins").command("/admin list"));
		line();
		RanksCommand.ranksReturn(player());
	}

	@Async
	@Path("list")
	void list() {
		Rank.ADMIN.getNerds().thenAccept(nerds -> {
			line();
			send("&3All current " + Rank.ADMIN.getChatColor() + "Admins &3and the date they were promoted:");
			nerds.forEach(nerd -> send(nerd.getColoredName() + " &7-&e " + TimeUtils.shortDateFormat(nerd.getPromotionDate())));
			line();
			RanksCommand.ranksReturn(player());
		});
	}
}
