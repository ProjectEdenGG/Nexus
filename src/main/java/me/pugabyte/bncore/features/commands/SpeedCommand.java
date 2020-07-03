package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Fallback;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

@Fallback("essentials")
@Permission("essentials.speed")
@Redirect(from = "/flyspeed", to = "/speed fly")
@Redirect(from = "/walkspeed", to = "/speed walk")
public class SpeedCommand extends CustomCommand {
	private static final float MAX_SPEED = 3f;

	public SpeedCommand(CommandEvent event) {
		super(event);
	}

	@Path("<speed> [player]")
	void speed(float speed, @Arg(value = "self", permission = "group.staff") Player player) {
		if (player.isFlying())
			fly(speed, player);
		else
			walk(speed, player);
	}

	@Path("fly <speed> [player]")
	void fly(float speed, @Arg(value = "self", permission = "group.staff") Player player) {
		setSpeed(player, validateSpeed(speed), true);
		tell(speed, player, "Fly");
	}

	@Path("walk <speed> [player]")
	void walk(float speed, @Arg(value = "self", permission = "group.staff") Player player) {
		setSpeed(player, validateSpeed(speed), false);
		tell(speed, player, "Walk");
	}

	@Path("both <speed> [player]")
	void both(float speed, @Arg(value = "self", permission = "group.staff") Player player) {
		speed = validateSpeed(speed);
		setSpeed(player, speed, true);
		setSpeed(player, speed, false);
		tell(speed, player, "Fly and walk");
	}

	@Path("fly reset [player]")
	void fly(@Arg(value = "self", permission = "group.staff") Player player) {
		resetSpeed(player, true);
		tellReset(player, "Fly");
	}

	@Path("walk reset [player]")
	void walk(@Arg(value = "self", permission = "group.staff") Player player) {
		resetSpeed(player, false);
		tellReset(player, "Walk");
	}

	@Path("reset [player]")
	void reset(@Arg(value = "self", permission = "group.staff") Player player) {
		resetSpeed(player);
		tellReset(player, "Fly and walk");
	}

	private void tell(float speed, Player player, String type) {
		send(player, PREFIX + type + " speed set to &e" + speed);
		if (!isSelf(player))
			send(PREFIX + type + " speed set to &e" + speed + " &3for &e" + player.getName());
	}

	private void tellReset(@Arg(value = "self", permission = "group.staff") Player player, String type) {
		send(player, PREFIX + type + " speed reset");
		if (!isSelf(player))
			send(PREFIX + type + " speed reset for &e" + player.getName());
	}

	public float validateSpeed(float speed) {
		if (speed > 10f)
			speed = 10f;
		else if (speed < 0.0001f)
			speed = 0.0001f;

		if (!player().hasPermission("group.staff") && speed > MAX_SPEED) {
			send(player(), "&cMax speed is " + MAX_SPEED);
			speed = MAX_SPEED;
		}

		return speed;
	}

	// Static helpers

	public static void resetSpeed(Player player) {
		resetSpeed(player, true);
		resetSpeed(player, false);
	}

	public static void resetSpeed(Player player, boolean isFly) {
		setSpeed(player, 1, isFly);
	}

	public static void setSpeed(Player player, float speed, boolean isFly) {
		if (isFly)
			player.setFlySpeed(getRealMoveSpeed(speed, isFly));
		else
			player.setWalkSpeed(getRealMoveSpeed(speed, isFly));
	}

	private static float getRealMoveSpeed(final float userSpeed, final boolean isFly) {
		final float defaultSpeed = getDefaultSpeed(isFly);

		if (userSpeed < 1f) {
			return defaultSpeed * userSpeed;
		} else {
			float ratio = ((userSpeed - 1) / 9) * (1 - defaultSpeed);
			return ratio + defaultSpeed;
		}
	}

	private static float getDefaultSpeed(boolean isFly) {
		return isFly ? 0.1f : 0.2f;
	}

}
