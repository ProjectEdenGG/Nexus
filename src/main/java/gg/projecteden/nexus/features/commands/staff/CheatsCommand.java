package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.commands.FlyCommand;
import gg.projecteden.nexus.features.commands.GamemodeCommand;
import gg.projecteden.nexus.features.commands.SpeedCommand;
import gg.projecteden.nexus.features.commands.SpeedCommand.SpeedType;
import gg.projecteden.nexus.features.listeners.events.SubWorldGroupChangedEvent;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.godmode.GodmodeService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.SubWorldGroup;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.NoArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

@NoArgsConstructor
@Permission(Group.STAFF)
@Redirect(from = "/nocheats", to = "/cheats off")
@Redirect(from = "/allcheats", to = "/cheats on")
public class CheatsCommand extends CustomCommand implements Listener {
	private static final String PREFIX = StringUtils.getPrefix("Cheats");

	public static final List<SubWorldGroup> DISABLE_CHEATS_ALL_STAFF = List.of(
		SubWorldGroup.STAFF_SURVIVAL
	);

	public static final List<SubWorldGroup> DISABLE_CHEATS_NON_SENIOR_STAFF = List.of(
		SubWorldGroup.SKYBLOCK,
		SubWorldGroup.ONEBLOCK
	);

	public CheatsCommand(CommandEvent event) {
		super(event);
	}

	@Path("<on|off>")
	@Description("Toggles vanish, god, flight, WorldGuard edit, and gamemode")
	void toggle(boolean enabled) {
		if (enabled) {
			on(player());
			send(PREFIX + "&aEnabled");
		} else {
			off(player(), true);
			send(PREFIX + "&cDisabled");
		}
	}

	public static void off(Player player, boolean unvanish) {
		if (unvanish)
			Vanish.unvanish(player);

		new GodmodeService().edit(player, godmode -> godmode.setEnabled(false));
		WorldGuardEditCommand.off(player);
		SpeedCommand.resetSpeed(player);

		if (WorldGroup.of(player) != WorldGroup.CREATIVE) {
			GamemodeCommand.setGameMode(player, GameMode.SURVIVAL);
			FlyCommand.off(player);
		}
	}

	public static void on(Player player) {
		if (!canEnableCheats(player))
			throw new InvalidInputException("You cannot enable cheats in this world");

		if (Rank.of(player).gte(Rank.ARCHITECT))
			new GodmodeService().edit(player, godmode -> godmode.setEnabled(true));

		if (player.hasPermission("essentials.gamemode.creative"))
			GamemodeCommand.setGameMode(player, GameMode.CREATIVE);

		if (player.hasPermission("essentials.fly"))
			FlyCommand.on(player);

		if (player.hasPermission("pv.use"))
			Vanish.vanish(player);
	}

	public static boolean canEnableCheats(Player player) {
		final SubWorldGroup subWorldGroup = SubWorldGroup.of(player);

		if (!Rank.of(player).isSeniorStaff())
			if (DISABLE_CHEATS_NON_SENIOR_STAFF.contains(subWorldGroup))
				return false;

		if (DISABLE_CHEATS_ALL_STAFF.contains(subWorldGroup))
			return false;

		return true;
	}

	@EventHandler
	public void on(SubWorldGroupChangedEvent event) {
		final Player player = event.getPlayer();

		if (canEnableCheats(player))
			return;

		Tasks.wait(20, () -> CheatsCommand.off(player, false));
	}

	private static void handleBuildEvent(Player player, Cancellable event) {
		if (!Rank.of(player).between(Rank.BUILDER, Rank.MODERATOR))
			return;

		if (!WorldGroup.of(player).isSurvivalMode())
			return;

		boolean fly = player.getAllowFlight();
		boolean god = new GodmodeService().get(player).isEnabled();
		boolean speed = SpeedType.WALK.get(player) != SpeedType.WALK.getDefaultSpeed();

		if (!fly && !god && !speed)
			return;

		event.setCancelled(true);
		if (CooldownService.isNotOnCooldown(player, "cheats_no_interact", TickTime.SECOND.x(2)))
			player.sendMessage(CheatsCommand.PREFIX + "You cannot build while Fly, God, or Speed is enabled");
	}

	@EventHandler
	public void on(BlockPlaceEvent event) {
		handleBuildEvent(event.getPlayer(), event);
	}

	@EventHandler
	public void on(BlockBreakEvent event) {
		handleBuildEvent(event.getPlayer(), event);
	}

//	@EventHandler
	public void on(EntityDamageByEntityEvent event) {
		Dev.GRIFFIN.send("1");
		if (!(event.getEntity() instanceof LivingEntity livingEntity))
			return;

		Dev.GRIFFIN.send("2");
		if (!(event.getDamager() instanceof Player player))
			return;

		Dev.GRIFFIN.send("3");
		if (!Rank.of(player).isStaff())
			return;

		// TODO final damage is always 0.9399999976158142 ?????
		Dev.GRIFFIN.send("4 " + livingEntity.getHealth() + " - " + event.getFinalDamage() + " = " + (livingEntity.getHealth() - event.getFinalDamage()));
		if (livingEntity.getHealth() - event.getFinalDamage() > 0)
			return;

		Dev.GRIFFIN.send("5");
		if (!player.isFlying() && !new GodmodeService().get(player).isEnabled())
			return;

		Dev.GRIFFIN.send("6");
		IOUtils.fileAppend("cheats", Nickname.of(player) + " killed a " + camelCase(event.getEntity().getType()) + " at " + StringUtils.xyzw(player.getLocation()));
	}

}
