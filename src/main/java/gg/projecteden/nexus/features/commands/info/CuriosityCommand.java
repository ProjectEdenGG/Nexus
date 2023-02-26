package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.curiosity.Curiosity;
import gg.projecteden.nexus.models.curiosity.Curiosity.CuriosityReward;
import gg.projecteden.nexus.models.curiosity.CuriosityService;
import org.bukkit.entity.Player;

@Permission(Group.SENIOR_STAFF)
@HideFromWiki
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
