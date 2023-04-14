package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.WikiConfig;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.entity.Player;

import static gg.projecteden.nexus.utils.StringUtils.pretty;
import static gg.projecteden.nexus.utils.StringUtils.stripTrailingZeros;
import static gg.projecteden.nexus.utils.StringUtils.trimFirst;

@Aliases({"exp", "xp"})
@Permission("experience.use")
@WikiConfig(rank = "Guest", feature = "Creative")
public class ExperienceCommand extends CustomCommand {

	public ExperienceCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@NoLiterals
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
		String totalExp = stripTrailingZeros(pretty(Double.parseDouble(player.getLevel() + trimFirst(String.valueOf(player.getExp())))));
		return totalExp + plural(" level", Double.parseDouble(totalExp));
	}

}
