package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
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
public class SpeedCommand extends CustomCommand {
	private static final float MAX_SPEED = 3f;

	public SpeedCommand(CommandEvent event) {
		super(event);
	}

	@Path("<speed> [player]")
	void speed(float speed, @Arg(value = "self", permission = Group.STAFF) Player player) {
		if (player.isFlying())
			fly(speed, player);
		else
			walk(speed, player);
	}

	@Path("fly <speed> [player]")
	void fly(float speed, @Arg(value = "self", permission = Group.STAFF) Player player) {
		speed = validateSpeed(speed);
		SpeedType.FLY.set(player, speed);
		tell(speed, player, "Fly");
	}

	@Path("walk <speed> [player]")
	void walk(float speed, @Arg(value = "self", permission = Group.STAFF) Player player) {
		speed = validateSpeed(speed);
		SpeedType.WALK.set(player, speed);
		tell(speed, player, "Walk");
	}

	@Path("both <speed> [player]")
	void both(float speed, @Arg(value = "self", permission = Group.STAFF) Player player) {
		speed = validateSpeed(speed);
		setSpeed(player, speed);
		tell(speed, player, "Fly and walk");
	}

	@Path("fly reset [player]")
	void fly(@Arg(value = "self", permission = Group.STAFF) Player player) {
		SpeedType.FLY.reset(player);
		tellReset(player, "Fly");
	}

	@Path("walk reset [player]")
	void walk(@Arg(value = "self", permission = Group.STAFF) Player player) {
		SpeedType.WALK.reset(player);
		tellReset(player, "Walk");
	}

	@Path("(r|reset) [player]")
	void reset(@Arg(value = "self", permission = Group.STAFF) Player player) {
		resetSpeed(player);
		tellReset(player, "Fly and walk");
	}

	private void tell(float speed, Player player, String type) {
		send(player, PREFIX + type + " speed set to &e" + speed);
		if (!isSelf(player))
			send(PREFIX + type + " speed set to &e" + speed + " &3for &e" + player.getName());
	}

	private void tellReset(@Arg(value = "self", permission = Group.STAFF) Player player, String type) {
		send(player, PREFIX + type + " speed reset");
		if (!isSelf(player))
			send(PREFIX + type + " speed reset for &e" + player.getName());
	}

	public float validateSpeed(float speed) {
		if (speed > 10f)
			speed = 10f;
		else if (speed < 0.0001f)
			speed = 0.0001f;

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

		private float defaultSpeed;
		private Function<Player, Float> getter;
		private BiConsumer<Player, Float> setter;

		public float get(Player player) {
			return getter.apply(player);
		}

		public void set(Player player, float speed) {
			if (new SpeedChangeEvent(player, this, getter.apply(player), speed).callEvent())
				setter.accept(player, getRealMoveSpeed(speed));
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
		private static final HandlerList handlers = new HandlerList();
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

		public static HandlerList getHandlerList() {
			return handlers;
		}

		@Override
		public HandlerList getHandlers() {
			return handlers;
		}

	}

}
