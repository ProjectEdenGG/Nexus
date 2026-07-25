package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.entity.WindCharge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class PropulsionEnchant extends CustomEnchant implements Listener {

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public List<Material> getSupportedMaterials() {
		return List.of(Material.MACE);
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		if (!event.getAction().isRightClick()) return;
		if (event.getPlayer().isSneaking()) return;

		int level = getLevel(event.getItem());
		if (level <= 0) return;

		if (!event.getPlayer().isOnGround()) return;

		WindCharge windCharge = event.getPlayer().getWorld().spawn(event.getPlayer().getLocation().clone().add(0, .2, 0), WindCharge.class);
		windCharge.setShooter(event.getPlayer());
		windCharge.explode();

		Tasks.wait(1, () -> {
			Vector vel = event.getPlayer().getVelocity();
			vel.setY(1.5 + (level * 0.5));
			event.getPlayer().setVelocity(vel);
		});
	}
}
