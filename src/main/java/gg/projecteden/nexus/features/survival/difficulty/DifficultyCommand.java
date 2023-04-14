package gg.projecteden.nexus.features.survival.difficulty;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.difficulty.DifficultyUser;
import gg.projecteden.nexus.models.difficulty.DifficultyUserService;
import org.bukkit.entity.Player;

@HideFromWiki // TODO
@Permission(Group.ADMIN)
public class DifficultyCommand extends CustomCommand {
	private static final DifficultyUserService service = new DifficultyUserService();
	private DifficultyUser user;

	public DifficultyCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@NoLiterals
	@Description("display your difficulty")
	void get() {
		send(PREFIX + "Your difficulty is " + user.getDifficulty().getColoredName());
	}

	@Description("set your difficulty")
	@NoLiterals
	@Path("<difficulty> [player]")
	void set(Difficulty difficulty, @Optional("self") @Permission(Group.ADMIN) Player player) {
		boolean isSelf = isSelf(player);
		if (!isSelf)
			user = service.get(player);

		String difficultyStr = difficulty.getColoredName();

		if (user.getDifficulty() == difficulty) {
			if (isSelf)
				error("Your difficulty is already set to " + difficultyStr);
			else
				error(user.getNickname() + "'s difficulty is already set to " + difficultyStr);
		}

		user.setDifficulty(difficulty);
		service.save(user);

		if (isSelf)
			send(PREFIX + "Set your difficulty to " + difficultyStr);
		else
			send(PREFIX + "Set &e" + user.getNickname() + "&3's difficulty to " + difficultyStr);
	}

	@Path("of <player>")
	@Permission(Group.ADMIN)
	void get(Player player) {
		if (isSelf(player)) {
			get();
			return;
		}

		user = service.get(player);

		send(PREFIX + user.getNickname() + "'s difficulty is " + user.getDifficulty().getColoredName());
	}
}
