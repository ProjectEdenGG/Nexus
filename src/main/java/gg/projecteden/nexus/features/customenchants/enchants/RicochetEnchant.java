package gg.projecteden.nexus.features.customenchants.enchants;

import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.models.pvp.PVPService;
import gg.projecteden.nexus.utils.ArrowSnapshot;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

public class RicochetEnchant extends CustomEnchant implements Listener {

	private static final PVPService PVP_SERVICE = new PVPService();
	private static final NamespacedKey RICOCHET_KEY = NamespacedKey.minecraft("ricochet_bounces");
	private static final NamespacedKey RICOCHET_IGNORE_KEY = NamespacedKey.minecraft("ricochet_ignore");

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public List<Material> getSupportedMaterials() {
		return List.of(Material.CROSSBOW);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onShoot(EntityShootBowEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		int level = getLevel(event.getBow());
		if (level <= 0)
			return;

		if (!(event.getProjectile() instanceof AbstractArrow arrow))
			return;

		PersistentDataContainer pdc = arrow.getPersistentDataContainer();
		pdc.set(RICOCHET_KEY, PersistentDataType.INTEGER, level);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onArrowDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof AbstractArrow arrow)) return;
		if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;

		PersistentDataContainer pdc = arrow.getPersistentDataContainer();
		if (!pdc.has(RICOCHET_KEY)) return;
		int bounces = pdc.get(RICOCHET_KEY, PersistentDataType.INTEGER).intValue();
		if (bounces == 0) return;

		ArrowSnapshot snapshot = ArrowSnapshot.of(arrow);
		bounce(arrow, snapshot, livingEntity, --bounces);
	}

	private void bounce(AbstractArrow originalArrow, ArrowSnapshot snapshot, LivingEntity originalEntity, int bounces) {
		List<UUID> ignoreUUIDs = new ArrayList<>() {{ add(originalEntity.getUniqueId()); }};
		PersistentDataContainer originalPDC = originalArrow.getPersistentDataContainer();
		if (originalPDC.has(RICOCHET_IGNORE_KEY))
			ignoreUUIDs.addAll(originalPDC.get(RICOCHET_IGNORE_KEY, PersistentDataType.LIST.strings()).stream()
				.map(UUID::fromString).toList());

		Location location = originalEntity.getEyeLocation();
		Optional<LivingEntity> target = getNearestEntity(originalEntity, 50, snapshot.shooter(), ignoreUUIDs);
		if (target.isEmpty()) return;

		Vector direction = target.get().getEyeLocation().toVector().subtract(location.toVector());

		AbstractArrow arrow = snapshot.world().spawnArrow(
			location.add(direction.clone().multiply(.5)),
			direction,
			snapshot.speed(),
			0,
			snapshot.arrowClass()
		);

		if (bounces > 0) {
			PersistentDataContainer pdc = arrow.getPersistentDataContainer();
			pdc.set(RICOCHET_KEY, PersistentDataType.INTEGER, bounces);
			pdc.set(RICOCHET_IGNORE_KEY, PersistentDataType.LIST.strings(), ignoreUUIDs.stream().map(UUID::toString).toList());
		}

		arrow.setShooter(snapshot.shooter());
		arrow.setDamage(snapshot.damage());
		arrow.setCritical(snapshot.critical());
		arrow.setPierceLevel(snapshot.pierceLevel());
		arrow.setFireTicks(snapshot.fireTicks());
		arrow.setGravity(snapshot.gravity());
		arrow.setItemStack(snapshot.item().clone());
		arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);

		if (snapshot.weapon() != null)
			arrow.setWeapon(snapshot.weapon().clone());
	}

	public Optional<LivingEntity> getNearestEntity(LivingEntity originEntity, int radius, ProjectileSource source, List<UUID> ignoreUUIDs) {
		return originEntity.getEyeLocation().getNearbyEntities(radius, radius, radius).stream()
			.filter(e -> e instanceof LivingEntity)
			.filter(e -> !e.getUniqueId().equals(originEntity.getUniqueId()))
			.map(LivingEntity.class::cast)
			.filter(entity -> {
				if (ignoreUUIDs.contains(entity.getUniqueId())) return false;
				if (!originEntity.hasLineOfSight(entity)) return false;
				if (entity instanceof ArmorStand) return false;
				if (!(source instanceof Player player)) return true;
				if (player.getUniqueId().equals(entity.getUniqueId())) return false;
				if (!(entity instanceof Player target)) return true;
				if (!PVP_SERVICE.get(player).isEnabled()) return false;
				if (target.getGameMode() != GameMode.SURVIVAL) return false;
				return PVP_SERVICE.get(target).isEnabled();
			})
			.min(Comparator.comparing(entity -> Distance.distance(originEntity, entity).get()));
	}

}
