package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.curiosity.Curiosity;
import me.pugabyte.bncore.models.curiosity.Curiosity.CuriosityReward;
import me.pugabyte.bncore.models.curiosity.CuriosityService;
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
