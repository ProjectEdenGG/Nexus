package me.pugabyte.nexus.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

import static me.pugabyte.nexus.utils.StringUtils.pretty;
import static me.pugabyte.nexus.utils.StringUtils.stripTrailingZeros;
import static me.pugabyte.nexus.utils.StringUtils.trimFirst;

@Aliases("exp")
@Permission("group.staff")
public class ExperienceCommand extends CustomCommand {

	public ExperienceCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("get <player>")
	void get(Player player) {
		send(PREFIX + player.getName() + " has &e" + getFormattedExp(player));
	}

	private void tellTotalExp(Player player) {
		send(PREFIX + player.getName() + " now has &e" + getFormattedExp(player));
	}

	@Path("set <player> <level>")
	void set(Player player, double amount) {
		int levels = (int) amount;
		float exp = (float) (amount - levels);
		validateAndRun(player, levels, exp);
	}

	@Path("give <player> <amount>")
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
