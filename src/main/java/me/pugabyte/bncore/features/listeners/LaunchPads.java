package me.pugabyte.bncore.features.listeners;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.WorldGroup;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class LaunchPads implements Listener {
	private boolean disable = true;
	private int taskID;
	private final static double DECELERATION_RATE = 0.98D;
	private final static double GRAVITY_CONSTANT = 0.08D;
	private final static double VANILLA_ANTICHEAT_THRESHOLD = 9.5D; // actual 10D

	public LaunchPads() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onRedstoneBlockActivate(PlayerInteractEvent event) {
		// Disabled until this works properly
		if (disable)
			return;

		Block block = event.getClickedBlock();
		if (block == null)
			return;

		if (!block.getType().equals(Material.GLOWING_REDSTONE_ORE))
			return;

		if (!event.getAction().equals(Action.PHYSICAL))
			return;

		if (event.getPlayer().isSneaking())
			return;

		Block below = block.getRelative(0, -1, 0).getLocation().getBlock();
		if (Utils.isNullOrAir(below.getType()))
			return;

		if (Utils.isSign(below.getType())) {
			Sign sign = (Sign) below.getState();
			String[] lines = sign.getLines();

			if (!lines[1].equalsIgnoreCase("[LaunchPad]"))
				return;

			if (lines[2].equalsIgnoreCase("") || lines[3].equalsIgnoreCase(""))
				return;

			double height = Double.parseDouble(lines[2]);
			double distance = Double.parseDouble(lines[3]);

			launchPlayer(event.getPlayer(), height, distance);
		} else if (WorldGroup.get(block.getLocation().getWorld()).equals(WorldGroup.MINIGAMES))
			launchPlayer(event.getPlayer());
	}

	public void launchPlayer(Player player) {
		launchPlayer(player, 5.0, 5.0);
	}

	public void launchPlayer(Player player, double power, double angle) {
		launchPlayer(player, power, angle, -1);
	}

	// <Power>
	// <Angle>
	// [Direction]
	public void launchPlayer(Player player, double power, double angle, double direction) {
		if (direction == -1) {

		} else {

		}

	}

	private void playerVelTask(Player player) {

	}

	private void cancelPlayerVelTask() {
		Tasks.cancel(taskID);
		BNCore.log("Canceled Task: " + taskID);
	}

	/*
			double y = height * 0.5;
			playerVelTask(player, y, distance);

		private void playerVelTask(Player player, double height, double distance){
			final double[] velY = {height};
			Location locCached = new Location(null,0,0,0);
			Vector direction = player.getLocation().getDirection().multiply(distance);

			taskID = Tasks.repeat(0, 1, () -> {
				if (velY[0] > VANILLA_ANTICHEAT_THRESHOLD) {
					player.getLocation(locCached).setY(locCached.getY() + velY[0]);
					player.teleport(locCached);
					player.setVelocity(new Vector(direction.getX(), VANILLA_ANTICHEAT_THRESHOLD, direction.getZ()));
				} else {
					player.setVelocity(new Vector(direction.getX(), velY[0], direction.getZ()));
					cancelPlayerVelTask();
				}

				velY[0] -= GRAVITY_CONSTANT;
				velY[0] *= DECELERATION_RATE;
			});
	}
	 */

	/*
		double y = height * 0.5;
		Vector upwards = player.getVelocity().setY(y);
		player.setVelocity(upwards);

		Vector direction = player.getLocation().getDirection().multiply(distance);
		direction.setY(upwards.getY());

		player.setVelocity(direction);
		player.getWorld().createExplosion(player.getLocation(), -1);
	}
	 */

	/* Pstones Player Launch

	final float height = field.getVelocity() > 0 ? field.getVelocity() : field.getSettings().getLaunchHeight();
	double speed = 8;

	Vector loc = player.getLocation().toVector();
	Vector target = new Vector(field.getX(), field.getY(), field.getZ());

	final Vector velocity = target.clone().subtract(new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
	velocity.multiply(speed / velocity.length());
	velocity.setY(height > 0 ? height : (((player.getLocation().getPitch() * -1) + 90) / 35));
	Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
		plugin.getPermissionsManager().allowFly(player);
		player.setVelocity(velocity);

		plugin.getCommunicationManager().showLaunch(player);
		startFallImmunity(player);
		player.getWorld().createExplosion(player.getLocation(), -1);
	}, 0L);

	*/

}
