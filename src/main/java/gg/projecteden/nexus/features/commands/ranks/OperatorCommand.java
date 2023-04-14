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
public class OperatorCommand extends CustomCommand {

	public OperatorCommand(CommandEvent event) {
		super(event);
	}

	@Override
	@NoLiterals
	@Description("Learn about the Operator rank")
	public void help() {
		line(5);
		send(Rank.OPERATOR.getChatColor() + "Operators &3are the second level of staff. Along with all the duties of a mod, they participate in " +
				"&eevent planning &3and take on &especial tasks &3to help improve Project Eden");
		line();
		send("&3[+] &eSenior Staff rank");
		send("&3[+] &eHow to achieve&3: &3Promoted from " + Rank.MODERATOR.getColoredName() + " &3by Senior Staff");
		send(json("&3[+] &eClick here &3for a list of operators").command("/operator list"));
		line();
		RanksCommand.ranksReturn(player());
	}

	@Async
	@Description("List current Operators")
	void list() {
		Rank.OPERATOR.getNerds().thenAccept(nerds -> {
			line();
			send("&3All current " + Rank.OPERATOR.getChatColor() + "Operators &3and the date they were promoted:");
			nerds.forEach(nerd -> send(nerd.getColoredName() + " &7-&e " + shortDateFormat(nerd.getPromotionDate())));
			line();
			RanksCommand.ranksReturn(player());
		});
	}
}
