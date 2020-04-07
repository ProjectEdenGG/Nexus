package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

@Aliases("trust")
public class AllowCommand extends CustomCommand {

	public AllowCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void allow(Player playerArg) {
		String playerArgName = "<player>";
		if (playerArg != null)
			playerArgName = playerArg.getName();

		line();

		String playerWorld = player().getWorld().getName();
		if (playerWorld.equalsIgnoreCase("creative"))
			creative(playerArgName);
		else if (playerWorld.contains("skyblock"))
			skyblock(playerArgName);
		else
			protection(playerArgName);

		line();
	}

	void creative(String playerName) {
		send("&3 Allowing to Plots:");
		send(json("  &c/plot trust " + playerName + " &3- Full access").suggest("/plot trust " + playerName));
		send(json("  &c/plot add " + playerName + " &3- Only allowed to build while you are online").suggest("/plot add " + playerName));
	}

	void skyblock(String playerName) {
		send("&3 Allowing to Islands:");
		send(json("  &c/is coop " + playerName + " &3- Allow a player").suggest("/is coop " + playerName));
		send(json("  &c/is invite " + playerName + " &3- Invite them to your team (players can only be on one team at a time)").suggest("/is invite " + playerName));
	}

	void protection(String playerName) {
		send(json()
				.next("&3  Which protection type?  ||  &3")
				.next("&6&lLWC")
				.suggest("/cmodify " + playerName + " OR /cmodifyall " + playerName)
				.hover("&6&lLock With Commands\n" +
						"&eProtects chests, doors, furnaces, etc. Automatically applies when you place it")
				.group()
				.next("  &3||  &3")
				.next("&e&lHomes")
				.command("/homes edit")
				.hover("&eUse the GUI to edit your homes.")
				.group()
				.next("  &3||"));
	}
}
