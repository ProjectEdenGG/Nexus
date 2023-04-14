package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Redirects.Redirect;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.utils.GlowUtils.GlowColor;
import gg.projecteden.nexus.utils.GlowUtils.GlowTask;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Collections;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@Redirect(from = "/entityhealth", to = "/health target")
public class HealthCommand extends CustomCommand {

	public HealthCommand(CommandEvent event) {
		super(event);
	}

	@NoLiterals
	@Path("<player> [number]")
	@Description("View a player's health")
	void health(@Optional("self") Player player, @Arg(permission = Group.STAFF, min = 0.0, max = 20.0) Double health) {
		String healthFormat = getFormattedHealth(player);
		if (health == null)
			send(PREFIX + player.getName() + "'s health is &e" + healthFormat);
		else {
			player.setHealth(health);
			send(PREFIX + stripColor(player.getName()) + "'s health set to &e" + healthFormat);
		}
	}

	@Path("target [number]")
	@Description("View the health of the entity you are looking at")
	void target(@Arg(permission = Group.STAFF, min = 0.0, max = 20.0) Double health) {
		LivingEntity target = getTargetLivingEntityRequired();

		GlowTask.builder()
			.duration(10 * 20)
			.entity(target)
			.color(GlowColor.RED)
			.viewers(Collections.singletonList(player()))
			.start();

		String healthFormat = getFormattedHealth(target);
		if (health == null)
			send(PREFIX + stripColor(target.getName()) + "'s health is &e" + healthFormat);
		else {
			target.setHealth(health);
			send(PREFIX + stripColor(target.getName()) + "'s health set to &e" + healthFormat);
		}
	}

	private String getFormattedHealth(LivingEntity livingEntity) {
		return StringUtils.getDf().format(livingEntity.getHealth());
	}
}
