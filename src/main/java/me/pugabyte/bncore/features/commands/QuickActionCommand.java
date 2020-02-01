package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

public class QuickActionCommand extends CustomCommand {

	public QuickActionCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void quickAction(Player player) {
		line();
		String playerName = player.getName();
		send("&8&l[&eQuickAction&8&l] &6&l" + playerName);
		line();
		send(json()
				.next(" &3|&3|  ")
				.next(" &eMessage")
				.suggest("/msg " + playerName + " ")
				.group()
				.next("&3  ||  ")
				.next("&ePoof Request")
				.suggest("/poof " + playerName)
				.group()
				.next("&3  ||  ")
				.next("&ePoof Here Request")
				.suggest("/poofhere " + playerName)
				.group()
				.next("  &3||"));

		send(json()
				.next(" &3||  ")
				.next("&eAllow")
				.command("/allow " + playerName)
				.group()
				.next("  &3||  ")
				.next("&eSPVP Challenge")
				.suggest("/spvp " + playerName)
				.group()
				.next("  &3||  ")
				.next("&eShop")
				.suggest("/shop " + playerName)
				.group()
				.next("  &3||  ")
				.next("&ePay")
				.suggest("/pay " + playerName + " 10")
				.group()
				.next("  &3||"));
		line();
	}
}
