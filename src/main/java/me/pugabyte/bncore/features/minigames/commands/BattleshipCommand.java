package me.pugabyte.bncore.features.minigames.commands;

import lombok.NonNull;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.mechanics.Battleship;
import me.pugabyte.bncore.features.minigames.mechanics.Battleship.ShipType;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.matchdata.BattleshipMatchData;
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
	BattleshipMatchData matchData;

	public BattleshipCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayer()) {
			minigamer = PlayerManager.get(player());
			if (minigamer.isPlaying(Battleship.class))
				matchData = minigamer.getMatch().getMatchData();
		}
	}

	@Path("kit")
	void kit() {
		Arrays.asList(ShipType.values()).forEach(shipType -> Utils.giveItem(player(), shipType.getItem()));
	}

	@Path("getChatGrid")
	void getChatGrid() {
		matchData.getGrid(minigamer.getTeam()).getChatGrid().forEach(this::send);
	}

}
