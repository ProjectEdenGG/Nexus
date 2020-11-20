package me.pugabyte.nexus.features.commands.info;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.curiosity.Curiosity;
import me.pugabyte.nexus.models.curiosity.Curiosity.CuriosityReward;
import me.pugabyte.nexus.models.curiosity.CuriosityService;
import org.bukkit.entity.Player;

@Permission("group.seniorstaff")
public class CuriosityCommand extends CustomCommand {
	private final CuriosityService service = new CuriosityService();

	public CuriosityCommand(CommandEvent event) {
		super(event);
	}

	@Path("<reward> <player>")
	void cookies(CuriosityReward reward, Player player) {
		Curiosity curiosity = service.get(player);
		if (curiosity.has(reward))
			return;

		curiosity.give(reward);
		service.save(curiosity);
	}


}
