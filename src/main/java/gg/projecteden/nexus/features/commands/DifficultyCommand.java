package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.difficulty.DifficultyService;
import gg.projecteden.nexus.models.difficulty.DifficultyUser;
import gg.projecteden.nexus.models.difficulty.DifficultyUser.Difficulty;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.entity.Player;

public class DifficultyCommand extends CustomCommand {
	private final DifficultyService service = new DifficultyService();
	private DifficultyUser user;

	public DifficultyCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Path
	void get() {
		send(PREFIX + "Your Difficulty is set to: &e" + StringUtils.camelCase(user.getDifficulty()));
	}

	@Path("reset [player]")
	void reset(@Arg(value = "self", permission = "group.admin") Player player) {
		set(Difficulty.values()[0], player);
	}

	@Path("<difficulty> [player]")
	void set(Difficulty difficulty, @Arg(value = "self", permission = "group.admin") Player player) {
		if (!isSelf(player))
			user = service.get(player);

		user.setDifficulty(difficulty);
		service.save(user);

		send(PREFIX + "Difficulty set to &e" + StringUtils.camelCase(difficulty));
	}

}
