package gg.projecteden.nexus.utils;

import org.bukkit.World;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.TippedArrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public record ArrowSnapshot(World world, Class<? extends AbstractArrow> arrowClass, float speed, double damage, ProjectileSource shooter,
                            boolean critical, int pierceLevel, int fireTicks, boolean gravity, ItemStack item, ItemStack weapon) {
		public static ArrowSnapshot of(AbstractArrow arrow) {
			ItemStack weapon = arrow.getWeapon();

			return new ArrowSnapshot(
				arrow.getWorld(),
				arrow instanceof SpectralArrow ? SpectralArrow.class : Arrow.class,
				(float) arrow.getVelocity().length(),
				arrow.getDamage(),
				arrow.getShooter(),
				arrow.isCritical(),
				arrow.getPierceLevel(),
				arrow.getFireTicks(),
				arrow.hasGravity(),
				arrow.getItemStack().clone(),
				weapon == null ? null : weapon.clone()
			);
		}
	}
