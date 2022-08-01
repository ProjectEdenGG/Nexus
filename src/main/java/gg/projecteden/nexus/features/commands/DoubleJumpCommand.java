package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.doublejump.DoubleJumpUser;
import gg.projecteden.nexus.models.doublejump.DoubleJumpUserService;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.concurrent.atomic.AtomicInteger;

// https://github.com/TreyRuffy/TreysDoubleJump
@NoArgsConstructor
public class DoubleJumpCommand extends CustomCommand implements Listener {
	private static final DoubleJumpUserService service = new DoubleJumpUserService();
	private static final double VELOCITY = 0.5;

	public DoubleJumpCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[state] [player]")
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

	@EventHandler
	public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
		final Location location = player.getLocation();

		if (new WorldGuardUtils(player).getRegionsLikeAt(".*doublejump.*", location).size() == 0)
			return;

		final DoubleJumpUser user = service.get(player);
		if (!user.canDoubleJump())
			return;

		event.setCancelled(true);
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
				player.setAllowFlight(true);
			} else {
				player.setAllowFlight(false);
				player.setFlying(false);
			}
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
