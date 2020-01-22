package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

public class AllowCommand extends CustomCommand {

	public AllowCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void allow(@Arg Player playerArg) {
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
			main(playerArgName);

		line();
	}

	private void creative(String playerName) {
		send("&3 Allowing to Plots:");
		json("  &c/plot trust " + playerName + " &3- Full access ||sgt:/plot trust " + playerName);
		json("  &c/plot add " + playerName + " &3- Only allowed to build while you are online||sgt:/plot add " + playerName);
	}

	private void skyblock(String playerName) {
		send("&3 Allowing to Islands:");
		json("  &c/is coop " + playerName + " &3- Allow a player||sgt:/is coop " + playerName);
		json("  &c/is invite " + playerName + " &3- Invite them to your team (players can only be on one team at a time)||sgt:/is invite " + playerName);
	}

	private void main(String playerName) {
		json("&3  Which protection type?  &3|&3|  " +
				"||&b&lP-Stones||sgt:/ps allow " + playerName + " OR /ps allowall " + playerName + "||ttp:&b&lProtection Stones\n" +
				"&eCoal, Lapis, Diamond, and Emerald ores \n" +
				"&eProtects all blocks & animals inside \n" +
				"&ethe field" +
				"||  &3|&3|  " +
				"||&6&lLWC||sgt:/cmodify " + playerName + " OR /cmodifyall " + playerName + "||ttp:&6&lLock With Commands\n" +
				"&eProtects chests, doors, \n" +
				"&efurnaces, etc \n" +
				"&eAutomatically applies \n" +
				"&ewhen you place it" +
				"||  &3|&3|  " +
				"||&e&lHomes||cmd:/homes edit||ttp:&eUse the GUI to edit your homes." +
				"||  &3|&3|  ");
	}
}
