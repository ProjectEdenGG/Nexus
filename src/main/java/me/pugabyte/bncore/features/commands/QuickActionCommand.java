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
		json(" &3|&3|  " +
				"|| &eMessage ||sgt:/msg " + playerName + " || &3|&3| " +
				"|| &ePoof request ||sgt:/poof " + playerName + " || &3|&3| " +
				"|| &ePoof Here request ||sgt:/poofhere " + playerName + " || &3|&3| "
		);

		json(" &3|&3|  " +
				"|| &eAllow ||cmd:/allow " + playerName + " || &3|&3| " +
				"|| &eSPVP Challenge ||sgt:/spvp " + playerName + "|| &3|&3| " +
				"|| &eShop ||sgt:/shop " + playerName + "|| &3|&3| " +
				"|| &ePay ||sgt:/pay " + playerName + " 10|| &3|&3| "
		);
		line();
	}
}
