package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Permission(Group.ADMIN)
public class UnlimitedElytraCommand extends CustomCommand {
	private static final Set<UUID> FLYING = new HashSet<>();
	private static final double SPEED_MULTIPLIER = 0.2;

	public UnlimitedElytraCommand(CommandEvent event) {
		super(event);
	}

	static {
		Tasks.repeat(0, TickTime.TICK.x(2), () -> {
			if (FLYING.isEmpty())
				return;

			for (UUID uuid : FLYING) {
				Player player = Bukkit.getPlayer(uuid);
				if (player == null || !player.isGliding())
					continue;

				Vector unitVector = new Vector(0, player.getLocation().getDirection().getY(), 0);
				player.setVelocity(player.getVelocity().add(unitVector.multiply(SPEED_MULTIPLIER)));
			}
		});
	}

	@Path("[enable]")
	@Description("Toggle unlimited elytra")
	public void elytra(Boolean enable) {
		UUID uuid = uuid();

		if (enable == null)
			enable = !FLYING.contains(uuid);

		if (enable)
			FLYING.add(uuid);
		else
			FLYING.remove(uuid);

		send(PREFIX + (enable ? "&aEnabled" : "&cDisabled"));
	}

}
