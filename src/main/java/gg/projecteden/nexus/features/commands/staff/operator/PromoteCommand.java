package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
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

	@NoLiterals
	@Description("Promote a player to the next rank up")
	void promote(Nerd player) {
		Rank rank = player.getRank();
		Rank next = rank.getPromotion();
		if (rank == next)
			error("User is already max rank");

		GroupChange.set().player(player).group(next).runAsync();

		String message = "&e" + nickname() + " &3promoted &e" + player.getNickname() + " &3to " + next.getColoredName();
		Broadcast.staff().prefix("Promote").message(message).send();

		if (player.isOnline()) {
			player.sendMessage(new JsonBuilder()
				.newline()
				.color(NamedTextColor.DARK_AQUA)
				.next("Congratulations! ", NamedTextColor.YELLOW, TextDecoration.BOLD)
				.next("You've been promoted to ").next(next).next("!"));

			Jingle.RANKUP.play(player.getOnlinePlayer());
		}
	}

}
