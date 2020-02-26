package me.pugabyte.bncore.features.minigames.commands;

import lombok.NonNull;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.mechanics.Battleship;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;

import java.util.Arrays;

@Aliases("bs")
@Permission("group.staff")
public class BattleshipCommand extends CustomCommand {
	private Minigamer minigamer;

	public BattleshipCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayer())
			minigamer = PlayerManager.get(player());
	}

	@Path("kit")
	void kit() {
		Arrays.asList(Battleship.Ship.values()).forEach(ship -> Utils.giveItem(player(), ship.getItem()));
	}

}
