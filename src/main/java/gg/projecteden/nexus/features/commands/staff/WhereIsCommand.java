package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.whereis.WhereIs;
import gg.projecteden.nexus.models.whereis.WhereIsService;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.GlowUtils;
import gg.projecteden.nexus.utils.GlowUtils.GlowColor;
import gg.projecteden.nexus.utils.PlayerMovementUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collections;

@Permission(Group.STAFF)
public class WhereIsCommand extends CustomCommand {
	private final WhereIsService service = new WhereIsService();
	private final WhereIs whereIs;

	public WhereIsCommand(CommandEvent event) {
		super(event);
		whereIs = service.get(player());
	}

	@Path("<player>")
	@Description("Look at and glow ")
	void whereIs(Player playerArg) {
		if (Minigamer.of(player()).isPlaying())
			error("Cannot use in minigames");

		Location playerArgLoc = playerArg.getLocation().clone();

		if (!world().equals(playerArg.getWorld()))
			error(playerArg.getName() + " is in " + StringUtils.camelCase(playerArg.getWorld().getName()));

		if (distanceTo(playerArg).gt(Chat.getLocalRadius()))
			error(StringUtils.camelCase(playerArg.getName() + " not found"));

		PlayerMovementUtils.lookAt(player(), playerArgLoc);

		GlowUtils.GlowTask.builder()
				.duration(10 * 20)
				.entity(playerArg)
				.color(GlowColor.RED)
				.viewers(Collections.singletonList(player()))
				.start();
	}

	@Path("glow threshold <threshold>")
	@Description("Set the radius at which players start glowing")
	void glowThreshold(int threshold) {
		whereIs.setThreshold(threshold);
		service.save(whereIs);
		process(player());
		send(PREFIX + "Glow threshold set to " + threshold);
	}

	@Path("glow <on|off>")
	@Description("Toggle glowing nearby players")
	void glowToggle(Boolean enable) {
		if (enable == null)
			enable = !whereIs.isEnabled();

		whereIs.setEnabled(enable);
		service.save(whereIs);

		if (!enable)
			OnlinePlayers.getAll().forEach(_player -> unglow(_player, player()));
		else
			process(player());

		send(PREFIX + (enable ? "&aEnabled" : "&cDisabled"));
	}

	static {
		Tasks.repeatAsync(TickTime.SECOND, TickTime.SECOND.x(3), () -> OnlinePlayers.getAll().forEach(WhereIsCommand::process));
	}

	private static void process(Player viewer) {
		if (!Rank.of(viewer).isStaff()) {
			unglow(viewer);
			return;
		}
		Dev.GRIFFIN.debug("WhereIs 1 " + Nickname.of(viewer));

		if (!WorldGroup.STAFF.contains(viewer.getWorld()) && !WorldGroup.EVENTS.contains(viewer.getWorld())) {
			unglow(viewer);
			return;
		}
		Dev.GRIFFIN.debug("WhereIs 2 " + Nickname.of(viewer));

		WhereIsService service = new WhereIsService();
		WhereIs whereIs = service.get(viewer);
		if (!whereIs.isEnabled()) {
			unglow(viewer);
			return;
		}
		Dev.GRIFFIN.debug("WhereIs 3 " + Nickname.of(viewer));

		Integer threshold = whereIs.getThreshold();
		if (threshold == null)
			threshold = 30;

		for (Player glower : OnlinePlayers.getAll()) {
			if (!viewer.getWorld().equals(glower.getWorld()))
				continue;

			Distance distance = Distance.distance(viewer, glower);
			if (distance.gte(threshold) && distance.lte(200))
				glow(glower, viewer);
			else
				unglow(glower, viewer);
		}
	}

	private static void glow(Player glower, Player viewer) {
		GlowUtils.glow(glower).color(Rank.of(glower).getGlowColor()).receivers(viewer).run();
	}

	private static void unglow(Player viewer) {
		OnlinePlayers.getAll().forEach(glower -> unglow(glower, viewer));
	}

	private static void unglow(Player glower, Player viewer) {
		GlowUtils.unglow(glower).receivers(viewer).run();
	}

}

