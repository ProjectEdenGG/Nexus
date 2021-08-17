package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.LuckPermsUtils.GroupChange;
import gg.projecteden.nexus.utils.SoundUtils.Jingle;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Permission("group.seniorstaff")
public class PromoteCommand extends CustomCommand {

	public PromoteCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void promote(Nerd nerd) {
		Rank rank = nerd.getRank();
		Rank next = rank.getPromotion();
		if (rank == next)
			error("User is already max rank");

		GroupChange.remove().player(nerd).groups(Rank.values()).run();
		GroupChange.add().player(nerd).group(rank).run();
		send(PREFIX + "Promoted " + nerd.getName() + " to " + next.getColoredName());

		if (nerd.isOnline()) {
			nerd.sendMessage(new JsonBuilder("\n", NamedTextColor.DARK_AQUA)
				.next("Congratulations! ", NamedTextColor.YELLOW, TextDecoration.BOLD)
				.next("You've been promoted to ").next(next).next("!"));

			Jingle.RANKUP.play(nerd.getOnlinePlayer());
		}
	}

}
