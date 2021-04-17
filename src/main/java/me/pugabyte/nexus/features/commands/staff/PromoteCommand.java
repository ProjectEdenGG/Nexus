package me.pugabyte.nexus.features.commands.staff;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.SoundUtils.Jingle;
import net.kyori.adventure.text.Component;
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

		for (Rank _rank : Rank.values())
			runCommandAsConsole("lp user " + nerd.getName() + " parent remove " + _rank.name());
		runCommandAsConsole("lp user " + nerd.getName() + " parent add " + next.name());
		send(PREFIX + "Promoted " + nerd.getName() + " to " + next.getColoredName());

		if (nerd.getOfflinePlayer().isOnline()) {
			nerd.getPlayer().sendMessage(Component.text("\n", NamedTextColor.DARK_AQUA)
				.append(Component.text("Congratulations!", NamedTextColor.YELLOW, TextDecoration.BOLD))
				.append(Component.text("You've been promoted to ")).append(next.getComponent())
				.append(Component.text("!")));

			Jingle.RANKUP.play(nerd.getPlayer());
		}
	}

}
