package me.pugabyte.nexus.features.minigames.commands;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown;
import me.pugabyte.nexus.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.banker.Banker;
import me.pugabyte.nexus.models.banker.BankerService;
import me.pugabyte.nexus.utils.Time;

@Permission("group.admin")
@Cooldown(@Part(Time.WEEK))
public class ParkourRewardCommand extends CustomCommand {

	public ParkourRewardCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void reward() {
		BankerService bankerService = new BankerService();
		Banker banker = bankerService.get(player());
		banker.deposit(40);
		bankerService.save(banker);
		send(PREFIX + "You have received $40");
	}

}
