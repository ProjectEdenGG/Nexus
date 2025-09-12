package gg.projecteden.nexus.features.commands;

import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.doublejump.DoubleJumpUser;
import gg.projecteden.nexus.models.doublejump.DoubleJumpUserService;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardFlagUtils;
import gg.projecteden.nexus.utils.WorldGuardFlagUtils.CustomFlags;
import gg.projecteden.parchment.HasLocation;
import kotlin.Pair;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInputEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// https://github.com/TreyRuffy/TreysDoubleJump
@NoArgsConstructor
public class DoubleJumpCommand extends CustomCommand implements Listener {
	private static final DoubleJumpUserService service = new DoubleJumpUserService();

	public DoubleJumpCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[state] [player]")
	@Description("Toggle double jump in applicable areas")
	void toggle(Boolean state, @Arg("self") DoubleJumpUser user) {
		if (state == null)
			state = !user.isEnabled();

		user.setEnabled(state);
		service.save(user);

		final String stateText = state ? "&aEnabled" : "&cDisabled";
		send(user, PREFIX + stateText);
		if (!isSelf(user))
			send(PREFIX + stateText + " &3for &e" + user.getNickname());
	}

	public static boolean isInDoubleJumpRegion(HasLocation location) {
		return WorldGuardFlagUtils.query(location.getLocation(), CustomFlags.DOUBLE_JUMP) == State.ALLOW;
	}

	private static boolean isDoubleJumpRegion(ProtectedRegion region) {
		return region.getFlag(CustomFlags.DOUBLE_JUMP.get()) == State.ALLOW;
	}

	private static Integer getDoubleJumpCooldown(HasLocation location) {
		return WorldGuardFlagUtils.queryValue(location.getLocation(), CustomFlags.DOUBLE_JUMP_COOLDOWN);
	}

	private static final Map<UUID, DoubleJumpData> DATA = new HashMap<>();

	@Data
	public static class DoubleJumpData {
		private final UUID uuid;
		private final List<Pair<Integer, Boolean>> previousStates = new ArrayList<>();
		private int taskId = -1;

		public boolean isDoubleJumping() {
			return taskId != -1;
		}
	}

	@EventHandler
	public void on(PlayerInputEvent event) {
		var player = event.getPlayer();
		var data = DATA.computeIfAbsent(player.getUniqueId(), $ -> new DoubleJumpData(player.getUniqueId()));

		if (data.isDoubleJumping())
			return;

		if (player.getGameMode() == GameMode.CREATIVE || player.getAllowFlight())
			return;

		final Location location = player.getLocation();
		if (!isInDoubleJumpRegion(location))
			return;

		final DoubleJumpUser user = service.get(player);
		if (!user.isEnabled())
			return;

		data.getPreviousStates().removeIf(state -> state.getFirst() < Bukkit.getCurrentTick() - 6);
		data.getPreviousStates().add(new Pair<>(Bukkit.getCurrentTick(), event.getInput().isJump()));

		if (data.getPreviousStates().size() < 3)
			return;

		if (!(
			data.getPreviousStates().get(0).getSecond() &&
			!data.getPreviousStates().get(1).getSecond() &&
			data.getPreviousStates().get(2).getSecond()
		)) {
			return;
		}

		data.getPreviousStates().clear();

		Integer cooldown = getDoubleJumpCooldown(location);
		if (cooldown != null && cooldown > 0)
			if (CooldownService.isOnCooldown(player, "doublejump", cooldown))
				return;

		final var velocity = DoubleJumpVelocity.of(player);
		player.setVelocity(location.getDirection().multiply(velocity.getForward()).setY(velocity.getUp()));

		new SoundBuilder(Sound.ENTITY_GHAST_SHOOT)
			.receiver(player)
			.volume(MuteMenuItem.DOUBLE_JUMP)
			.play();

		data.setTaskId(Tasks.repeat(10, 2, () -> {
			if (!player.isOnGround())
				return;

			Tasks.cancel(data.getTaskId());
			data.setTaskId(-1);
		}));
	}

	@Data
	private static class DoubleJumpVelocity {
		private final double forward;
		private final double up;

		public static DoubleJumpVelocity of(Player player) {
			double forward = 1.7;
			double up = .6;

			if (player.isSprinting()) {
				forward += .2;
				up += .2;
			}

			up += (Math.abs(player.getLocation().getPitch()) / 100) * 1.3;

			return new DoubleJumpVelocity(forward, up);
		}
	}

}
