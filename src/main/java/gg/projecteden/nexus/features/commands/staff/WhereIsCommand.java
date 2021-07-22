package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.whereis.WhereIs;
import gg.projecteden.nexus.models.whereis.WhereIsService;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGroup;
import gg.projecteden.utils.TimeUtils.Time;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.inventivetalent.glow.GlowAPI;

import java.util.Collections;

@Permission("group.staff")
public class WhereIsCommand extends CustomCommand {
	private static boolean enabled = true;
	private final WhereIsService service = new WhereIsService();
	private final WhereIs whereIs;

	public WhereIsCommand(CommandEvent event) {
		super(event);
		whereIs = service.get(player());
	}

	@Path("glow enabled [false]")
	void enabled(Boolean enabled) {
		if (enabled == null)
			enabled = !WhereIsCommand.enabled;
		WhereIsCommand.enabled = enabled;
		send(PREFIX + "Glowing " + (enabled ? "&aenabled" : "&cdisabled"));
	}

	@Path("<player>")
	void whereIs(Player playerArg) {
		if (WorldGroup.of(player()).equals(WorldGroup.MINIGAMES))
			error("Cannot use in gameworld");

		Location playerArgLoc = playerArg.getLocation().clone();

		if (!world().equals(playerArg.getWorld()))
			error(playerArg.getName() + " is in " + StringUtils.camelCase(playerArg.getWorld().getName()));

		double distance = location().distance(playerArgLoc);
		if (distance > Chat.getLocalRadius())
			error(StringUtils.camelCase(playerArg.getName() + " not found"));

		LocationUtils.lookAt(player(), playerArgLoc);

		Tasks.GlowTask.builder()
				.duration(10 * 20)
				.entity(playerArg)
				.color(GlowAPI.Color.RED)
				.viewers(Collections.singletonList(player()))
				.start();
	}

	@Path("glow threshold <threshold>")
	void glowThreshold(int threshold) {
		whereIs.setThreshold(threshold);
		service.save(whereIs);
		process(player());
		send(PREFIX + "Glow threshold set to " + threshold);
	}

	@Path("glow <on|off>")
	void glowToggle(Boolean enable) {
		if (enable == null)
			enable = !whereIs.isEnabled();

		whereIs.setEnabled(enable);
		service.save(whereIs);

		if (!enable)
			PlayerUtils.getOnlinePlayers().forEach(_player -> unglow(_player, player()));
		else
			process(player());

		send(PREFIX + (enable ? "&aEnabled" : "&cDisabled"));
	}

	static {
		Tasks.repeatAsync(Time.SECOND, Time.SECOND.x(3), () -> PlayerUtils.getOnlinePlayers().forEach(WhereIsCommand::process));
	}

	private static void process(Player viewer) {
		if (!Rank.of(viewer).isStaff()) {
			unglow(viewer);
			return;
		}

		if (!WorldGroup.STAFF.contains(viewer.getWorld()) && !WorldGroup.EVENTS.contains(viewer.getWorld())) {
			unglow(viewer);
			return;
		}

		WhereIsService service = new WhereIsService();
		WhereIs whereIs = service.get(viewer);
		if (!whereIs.isEnabled()) {
			unglow(viewer);
			return;
		}

		Integer threshold = whereIs.getThreshold();
		if (threshold == null)
			threshold = 30;

		for (Player glower : PlayerUtils.getOnlinePlayers()) {
			if (!viewer.getWorld().equals(glower.getWorld()))
				continue;

			double distance = viewer.getLocation().distance(glower.getLocation());
			if (distance >= threshold && distance <= 200)
				glow(glower, viewer);
			else
				unglow(glower, viewer);
		}
	}

	private static void glow(Player glower, Player viewer) {
		if (!enabled) {
			unglow(glower, viewer);
			return;
		}

		GlowAPI.setGlowing(glower, Nerd.of(glower).getRank().getGlowColor(), viewer);
	}

	private static void unglow(Player viewer) {
		PlayerUtils.getOnlinePlayers().forEach(glower -> unglow(glower, viewer));
	}

	private static void unglow(Player glower, Player viewer) {
		GlowAPI.setGlowing(glower, false, viewer);
	}

}
