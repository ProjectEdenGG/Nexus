package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Aliases({"exp", "xp"})
@Permission("experience.use")
@WikiConfig(rank = "Guest", feature = "Creative")
public class ExperienceCommand extends CustomCommand {

	public ExperienceCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<level>")
	@Description("Set your experience level")
	void level(@Arg(max = 10, minMaxBypass = Group.SENIOR_STAFF) double amount) {
		set(player(), amount);
	}

	@Path("get <player>")
	@Permission(Group.SENIOR_STAFF)
	@Description("View a player's experience level")
	void get(Player player) {
		send(PREFIX + player.getName() + " has &e" + getFormattedExp(player));
	}

	private void tellTotalExp(Player player) {
		send(PREFIX + player.getName() + " now has &e" + getFormattedExp(player));
	}

	@Path("set <player> <level>")
	@Permission(Group.SENIOR_STAFF)
	@Description("Set a player's experience level")
	void set(Player player, double amount) {
		int levels = (int) amount;
		float exp = (float) (amount - levels);
		validateAndRun(player, levels, exp);
	}

	@Path("give <player> <amount>")
	@Permission(Group.SENIOR_STAFF)
	@Description("Give experience levels to a player")
	void give(Player player, double amount) {
		int levels = (int) amount;
		float exp = (float) (amount - levels);
		if (player.getExp() + exp >= 1) {
			++levels;
			--exp;
		}

		validateAndRun(player, player.getLevel() + levels, player.getExp() + exp);
	}

	@Path("take <player> <amount>")
	@Description("Take experience levels from a player")
	@Permission(Group.SENIOR_STAFF)
	void take(Player player, double amount) {
		int levels = (int) amount;
		float exp = (float) (amount - levels);
		if (player.getExp() - exp < 0) {
			++levels;
			--exp;
		}

		validateAndRun(player, player.getLevel() - levels, player.getExp() - exp);
	}

	private void validateAndRun(Player player, int levels, float exp) {
		if (levels < 0)
			error("Level cannot be negative");
		if (levels > Short.MAX_VALUE)
			error("Level cannot be greater than " + Short.MAX_VALUE);
		if (exp < 0)
			error("Exp cannot be negative");
		if (exp >= 1)
			error("Exp cannot be greater or equal to than 1");

		player.setLevel(levels);
		player.setExp(exp);
		tellTotalExp(player);
	}

	private String getFormattedExp(Player player) {
		String pretty = StringUtils.pretty(Double.parseDouble(player.getLevel() + StringUtils.trimFirst(String.valueOf(player.getExp()))));
		String totalExp = StringUtils.stripTrailingZeros(pretty.replaceAll(",", ""));
		return totalExp + plural(" level", Double.parseDouble(totalExp));
	}

}
