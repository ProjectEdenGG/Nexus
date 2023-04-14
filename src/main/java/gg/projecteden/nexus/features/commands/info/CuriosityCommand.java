package gg.projecteden.nexus.features.commands.info;

import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
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

	@NoLiterals
	void cookies(CuriosityReward reward, Player player) {
		Curiosity curiosity = service.get(player);
		if (curiosity.has(reward))
			return;

		curiosity.give(reward);
		service.save(curiosity);
	}

}
