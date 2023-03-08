package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.LuckPermsUtils.GroupChange;
import gg.projecteden.nexus.utils.SoundUtils.Jingle;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

@Permission(Group.SENIOR_STAFF)
public class PromoteCommand extends CustomCommand {

	public PromoteCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	@Description("Promotes a player to the next rank up")
	void promote(Nerd nerd) {
		Rank rank = nerd.getRank();
		Rank next = rank.getPromotion();
		if (rank == next)
			error("User is already max rank");

		GroupChange.set().player(nerd).group(next).runAsync();

		String message = "&e" + nickname() + " &3promoted &e" + nerd.getNickname() + " &3to " + next.getColoredName();
		Broadcast.staff().prefix("Promote").message(message).send();

		if (nerd.isOnline()) {
			nerd.sendMessage(new JsonBuilder()
				.newline()
				.color(NamedTextColor.DARK_AQUA)
				.next("Congratulations! ", NamedTextColor.YELLOW, TextDecoration.BOLD)
				.next("You've been promoted to ").next(next).next("!"));

			Jingle.RANKUP.play(nerd.getOnlinePlayer());
		}
	}

}
