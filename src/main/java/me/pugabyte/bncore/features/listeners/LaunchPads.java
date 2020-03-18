package me.pugabyte.bncore.features.listeners;

import me.pugabyte.bncore.BNCore;
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
import org.bukkit.util.Vector;

public class LaunchPads implements Listener {
	boolean disable = false;

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

	// Works ok
	public void launchPlayer(Player player, double height, double distance) {
		double y = height * 0.5;
		Vector upwards = player.getVelocity().setY(y);
		player.setVelocity(upwards);

		Vector direction = player.getLocation().getDirection().multiply(distance);
		direction.setY(upwards.getY());
		player.setVelocity(direction);
//		player.getWorld().createExplosion(player.getLocation(), -1);
	}

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
