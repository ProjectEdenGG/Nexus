package me.pugabyte.bncore.features.oldminigames.quake;

import au.com.mineauz.minigames.Minigames;
import me.pugabyte.bncore.BNCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class Railgun extends Gun {

	@Override
	public void shoot() {
		List<Block> los = player.getPlayer().getLineOfSight(passthroughMaterials, (int) this.getRange());
		double block_distance = los.get(los.size() - 1).getLocation().distance(player.getLocation());

		Location start = player.getPlayer().getEyeLocation();
		Vector increase = start.getDirection();

		for (int counter = 0; counter < block_distance - 1; counter++) {
			Location point = start.add(increase);
			for (Player _player : Bukkit.getOnlinePlayers()) {
				_player.spawnParticle(Particle.CRIT, point, 1, 0, 0, 0, 0.1);
			}
		}

		player.getPlayer().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 0.8F);

		Location observerPos = player.getPlayer().getEyeLocation();
		Vector3D observerDir = new Vector3D(observerPos.getDirection());

		Vector3D observerStart = new Vector3D(observerPos);
		Vector3D observerEnd = observerStart.add(observerDir.multiply(this.getRange()));

		int i = 0;
		for (Player target : player.getPlayer().getWorld().getPlayers()) {
			Vector3D targetPos = new Vector3D(target.getLocation());
			double hitboxVal = this.getHitbox() / 2;
			Vector3D minimum = targetPos.add(-hitboxVal, 0, -hitboxVal);
			Vector3D maximum = targetPos.add(hitboxVal, 1.80, hitboxVal);

			boolean hasIntersection = hasIntersection(observerStart, observerEnd, minimum, maximum);
			boolean notTargetingSelf = target != player.getPlayer();
			if (notTargetingSelf && hasIntersection) {
				boolean inGunRange = block_distance > target.getPlayer().getLocation().distance(player.getLocation());
				boolean playerNotNull = this.player.getMinigame().getPlayers().contains(Minigames.getPlugin().getPlayerManager().getMinigamePlayer(Bukkit.getPlayer(target.getUniqueId())));
				if (inGunRange && playerNotNull) {
					final int finalI = i;
					Bukkit.getScheduler().runTaskLater(BNCore.getInstance(), () -> {
						player.getPlayer().playSound(player.getLocation(), Sound.ENTITY_SHULKER_BULLET_HIT, 1, 1 + (finalI * 0.25F));
					}, 2 + (i * 2));
					EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(player.getPlayer(), target.getPlayer(), EntityDamageEvent.DamageCause.ENTITY_ATTACK, this.getDamage());
					Bukkit.getServer().getPluginManager().callEvent(event);
					if (this.shouldDamageWithConsole()) {
						BNCore.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(BNCore.getInstance(), () ->
										target.damage(this.getDamage()),
								2L);
					} else {
						BNCore.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(BNCore.getInstance(), () ->
										target.damage(this.getDamage(), player.getPlayer()),
								2L);
					}
				}
			}
		}
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	@Override
	public Weapon clone() {
		Railgun railgun = new Railgun();
		railgun.setPassthroughMaterials(this.getPassthroughMaterials());
		railgun.setName(this.getName());
		railgun.setRange(this.getRange());
		railgun.setMaterial(this.getMaterial());
		railgun.setCooldown(this.getCooldown());
		railgun.setDamage(this.getDamage());
		railgun.setLore(this.getLore());
		railgun.setHitbox(this.getHitbox());

		return railgun;
	}
}