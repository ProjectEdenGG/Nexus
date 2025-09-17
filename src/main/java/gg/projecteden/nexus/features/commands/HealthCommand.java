package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.features.commands.staff.CheatsCommand;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nickname.Nickname;
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

	@Path("<player> [number]")
	@Description("View a player's health")
	void health(@Arg("self") Player player, @Arg(permission = Group.STAFF, min = 0.0, max = 20.0) Double health) {
		String healthFormat = getFormattedHealth(player);
		if (health == null)
			send(PREFIX + Nickname.of(player) + "'s health is &e" + healthFormat);
		else {
			if (isSelf(player))
				if (!CheatsCommand.canEnableCheats(player))
					error("You cannot use cheats in this world");

			player.setHealth(health);
			send(PREFIX + Nickname.of(player) + "'s health set to &e" + healthFormat);
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
