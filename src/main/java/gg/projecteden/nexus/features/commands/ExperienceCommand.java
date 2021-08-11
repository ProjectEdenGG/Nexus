package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.entity.Player;

import static gg.projecteden.nexus.utils.StringUtils.pretty;
import static gg.projecteden.nexus.utils.StringUtils.stripTrailingZeros;
import static gg.projecteden.nexus.utils.StringUtils.trimFirst;

@Aliases("exp")
@Permission("experience.use")
public class ExperienceCommand extends CustomCommand {

	public ExperienceCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<level>")
	void level(@Arg(max = 10, minMaxBypass = "group.seniorstaff") double amount) {
		set(player(), amount);
	}

	@Path("get <player>")
	@Permission(value = "group.seniorstaff", absolute = true)
	void get(Player player) {
		send(PREFIX + player.getName() + " has &e" + getFormattedExp(player));
	}

	private void tellTotalExp(Player player) {
		send(PREFIX + player.getName() + " now has &e" + getFormattedExp(player));
	}

	@Path("set <player> <level>")
	@Permission(value = "group.seniorstaff", absolute = true)
	void set(Player player, double amount) {
		int levels = (int) amount;
		float exp = (float) (amount - levels);
		validateAndRun(player, levels, exp);
	}

	@Path("give <player> <amount>")
	@Permission(value = "group.seniorstaff", absolute = true)
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
	@Permission(value = "group.seniorstaff", absolute = true)
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
