package gg.projecteden.nexus.features.difficulty;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.difficulty.DifficultyUser;
import gg.projecteden.nexus.models.difficulty.DifficultyUserService;
import org.bukkit.entity.Player;

public class DifficultyCommand extends CustomCommand {
	private static final DifficultyUserService service = new DifficultyUserService();
	private DifficultyUser user;

	public DifficultyCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Path
	@Description("display your difficulty")
	void get() {
		send(PREFIX + "Your difficulty is " + user.getDifficulty().getColoredName());
	}

	@Description("set your difficulty")
	@Path("<difficulty> [player]")
	void set(Difficulty difficulty, @Arg(value = "self", permission = Group.ADMIN) Player player) {
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

		send(PREFIX + user.getNickname() + "'s difficulty is " + user.getDifficulty().getColoredName());
	}
}
