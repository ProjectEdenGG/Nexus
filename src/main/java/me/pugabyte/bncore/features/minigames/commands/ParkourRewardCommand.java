package me.pugabyte.bncore.features.minigames.commands;

import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown;
import me.pugabyte.bncore.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Time;

@Permission("group.admin")
@Cooldown(@Part(Time.WEEK))
public class ParkourRewardCommand extends CustomCommand {

	public ParkourRewardCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void reward() {
		BNCore.getEcon().depositPlayer(player(), 40);
		send(PREFIX + "You have received $40");
	}

}
