package gg.projecteden.nexus.features.commands;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.doublejump.DoubleJumpUser;
import gg.projecteden.nexus.models.doublejump.DoubleJumpUserService;
import gg.projecteden.nexus.models.mode.ModeUser.FlightMode;
import gg.projecteden.nexus.models.mode.ModeUserService;
import gg.projecteden.nexus.utils.GameModeWrapper;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import gg.projecteden.parchment.HasLocation;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.concurrent.atomic.AtomicInteger;

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

	private static final String DOUBLEJUMP_REGEX = ".*doublejump.*";

	public static boolean isInDoubleJumpRegion(HasLocation location) {
		return !new WorldGuardUtils(location.getLocation()).getRegionsLikeAt(DOUBLEJUMP_REGEX, location.getLocation()).isEmpty();
	}

	private static boolean isDoubleJumpRegion(ProtectedRegion event) {
		return event.getId().matches(DOUBLEJUMP_REGEX);
	}

	@EventHandler
	public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
		final Location location = player.getLocation();

		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		if (!isInDoubleJumpRegion(location))
			return;

		final DoubleJumpUser user = service.get(player);
		if (!user.isEnabled()) {
			if (player.getGameMode() != GameMode.CREATIVE)
				event.setCancelled(true);

			return;
		}

		player.setAllowFlight(false);
		player.setFlying(false);

		final var velocity = DoubleJumpVelocity.of(player);
		player.setVelocity(location.getDirection().multiply(velocity.getForward()).setY(velocity.getUp()));

		new SoundBuilder(Sound.ENTITY_GHAST_SHOOT)
			.receiver(player)
			.volume(MuteMenuItem.DOUBLE_JUMP)
			.play();

		AtomicInteger repeat = new AtomicInteger(-1);
		repeat.set(Tasks.repeat(10, 2, () -> {
			if (player.isOnGround()) {
				Tasks.cancel(repeat.get());
				if (isInDoubleJumpRegion(player))
					player.setAllowFlight(true);
			} else {
				player.setAllowFlight(false);
				player.setFlying(false);
			}
		}));
	}

	@EventHandler
	public void on(PlayerEnteredRegionEvent event) {
		if (!isDoubleJumpRegion(event.getRegion()))
			return;

		final Player player = event.getPlayer();
		if (!GameModeWrapper.of(player.getGameMode()).isSurvival())
			return;

		player.setAllowFlight(true);
		player.setFlying(false);
	}

	@EventHandler
	public void on(PlayerLeftRegionEvent event) {
		if (!isDoubleJumpRegion(event.getRegion()))
			return;

		final Player player = event.getPlayer();

		if (Minigamer.of(player).isPlaying())
			return;

		final FlightMode user = new ModeUserService().get(player).getFlightMode(WorldGroup.of(player));
		player.setAllowFlight(user.isAllowFlight());
		if (!player.isFlying() && user.isFlying()) {
			player.setFlying(true);
		}
	}

	@EventHandler
	public void on(PlayerGameModeChangeEvent event) {
		final Player player = event.getPlayer();

		if (!isInDoubleJumpRegion(player.getLocation()))
			return;

		if (Minigamer.of(player).isPlaying())
			return;

		Tasks.wait(1, () -> {
			if (GameModeWrapper.of(player.getGameMode()).isSurvival()) {
				player.setAllowFlight(true);
			}
		});
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
