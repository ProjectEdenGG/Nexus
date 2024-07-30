package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.commands.staff.CheatsCommand;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.MathUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Permission("essentials.speed")
@Description("Change your fly/walk speed")
@Redirect(from = "/flyspeed", to = "/speed fly")
@Redirect(from = "/walkspeed", to = "/speed walk")
@WikiConfig(rank = "Guest", feature = "Creative")
public class SpeedCommand extends CustomCommand {
	private static final float MAX_SPEED = 3f;

	public SpeedCommand(CommandEvent event) {
		super(event);
	}

	@Path("<speed> [player]")
	@Description("Set your movement speed")
	void speed(float speed, @Arg(value = "self", permission = Group.SENIOR_STAFF) Player player) {
		if (player.isFlying())
			fly(speed, player);
		else
			walk(speed, player);
	}

	@Path("fly <speed> [player]")
	@Description("Set your fly speed")
	void fly(float speed, @Arg(value = "self", permission = Group.SENIOR_STAFF) Player player) {
		speed = validateSpeed(player, speed);
		SpeedType.FLY.set(player, speed);
		tell(speed, player, "Fly");
	}

	@Path("walk <speed> [player]")
	@Description("Set your walk speed")
	void walk(float speed, @Arg(value = "self", permission = Group.SENIOR_STAFF) Player player) {
		speed = validateSpeed(player, speed);
		SpeedType.WALK.set(player, speed);
		tell(speed, player, "Walk");
	}

	@Path("both <speed> [player]")
	@Description("Set both your fly and walk speed")
	void both(float speed, @Arg(value = "self", permission = Group.SENIOR_STAFF) Player player) {
		speed = validateSpeed(player, speed);
		setSpeed(player, speed);
		tell(speed, player, "Fly and walk");
	}

	@Path("fly reset [player]")
	@Description("Reset your fly speed")
	void fly(@Arg(value = "self", permission = Group.SENIOR_STAFF) Player player) {
		SpeedType.FLY.reset(player);
		tellReset(player, "Fly");
	}

	@Path("walk reset [player]")
	@Description("Reset your walk speed")
	void walk(@Arg(value = "self", permission = Group.SENIOR_STAFF) Player player) {
		SpeedType.WALK.reset(player);
		tellReset(player, "Walk");
	}

	@Path("(r|reset) [player]")
	@Description("Reset your fly and walk speed")
	void reset(@Arg(value = "self", permission = Group.SENIOR_STAFF) Player player) {
		resetSpeed(player);
		tellReset(player, "Fly and walk");
	}

	private void tell(float speed, Player player, String type) {
		send(player, PREFIX + type + " speed set to &e" + speed);
		if (!isSelf(player))
			send(PREFIX + type + " speed set to &e" + speed + " &3for &e" + player.getName());
	}

	private void tellReset(Player player, String type) {
		send(player, PREFIX + type + " speed reset");
		if (!isSelf(player))
			send(PREFIX + type + " speed reset for &e" + player.getName());
	}

	public float validateSpeed(Player player, float speed) {
		if (isSelf(player))
			if (!CheatsCommand.canEnableCheats(player))
				error("You cannot enable cheats in this world");

		speed = MathUtils.clamp(speed, 0.0001f, 10f);

		if (isPlayer() && !isStaff() && speed > MAX_SPEED) {
			send(player(), "&cMax speed is " + MAX_SPEED);
			speed = MAX_SPEED;
		}

		return speed;
	}

	// Static helpers

	public static void setSpeed(Player player, float speed) {
		SpeedType.FLY.set(player, speed);
		SpeedType.WALK.set(player, speed);
	}

	public static void resetSpeed(Player player) {
		SpeedType.WALK.reset(player);
		SpeedType.FLY.reset(player);
	}

	@AllArgsConstructor
	public enum SpeedType {
		WALK(.2f, Player::getWalkSpeed, Player::setWalkSpeed),
		FLY(.1f, Player::getFlySpeed, Player::setFlySpeed),
		;

		@Getter
		private final float defaultSpeed;
		private final Function<Player, Float> getter;
		private final BiConsumer<Player, Float> setter;

		public float get(Player player) {
			return getter.apply(player);
		}

		public void set(Player player, float speed) {
			if (!new SpeedChangeEvent(player, this, getter.apply(player), speed).callEvent())
				return;

			setter.accept(player, getRealMoveSpeed(speed));
			IOUtils.fileAppend("cheats", Nickname.of(player) + " set their speed to " + StringUtils.getDf().format(speed) + " at " + StringUtils.getShortLocationString(player.getLocation()));
		}

		public void reset(Player player) {
			set(player, 1);
		}

		private float getRealMoveSpeed(final float speed) {
			if (speed < 1f) {
				return defaultSpeed * speed;
			} else {
				float ratio = ((speed - 1) / 9) * (1 - defaultSpeed);
				return ratio + defaultSpeed;
			}
		}
	}

	@Getter
	@Setter
	public static class SpeedChangeEvent extends PlayerEvent implements Cancellable {
		@Getter
		private static final HandlerList handlerList = new HandlerList();
		private final SpeedType speedType;
		private final float oldSpeed, newSpeed;
		private boolean cancelled;

		public SpeedChangeEvent(@NotNull Player player, SpeedType speedType, float oldSpeed, float newSpeed) {
			super(player);
			this.player = player;
			this.speedType = speedType;
			this.oldSpeed = oldSpeed;
			this.newSpeed = newSpeed;
		}

		@Override
		public @NotNull HandlerList getHandlers() {
			return handlerList;
		}

	}

}
