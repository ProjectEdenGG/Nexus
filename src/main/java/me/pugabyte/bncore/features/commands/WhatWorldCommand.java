package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.entity.Player;

public class WhatWorldCommand extends CustomCommand {

	public WhatWorldCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void world(@Arg("self") Player player) {
		send("&3" + (isSelf(player) ? "You are" : player.getName() + " is") + " in world &e" + player.getWorld().getName() + " &3in group &e" + WorldGroup.get(player));
	}
}
