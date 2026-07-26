package gg.projecteden.nexus.features.customenchants.enchants;

import com.destroystokyo.paper.ParticleBuilder;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.customenchants.models.CustomEnchant;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.util.TriState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class GroundPoundEnchant extends CustomEnchant implements Listener {

	private static final NamespacedKey GROUND_POUND_KEY = NamespacedKey.minecraft("ground_pound");

	public GroundPoundEnchant() {
		Tasks.repeat(1, 1, this::check);
	}

	@Override
	public int getMaxLevel() {
		return 3;
	}

	@Override
	public List<Material> getSupportedMaterials() {
		return List.of(Material.MACE);
	}

	@EventHandler
	public void onShift(PlayerToggleSneakEvent event) {
		ItemStack tool = ItemUtils.getTool(event.getPlayer());
		int level = getLevel(tool);
		if (level == 0) return;

		Player player = event.getPlayer();

		PersistentDataContainer pdc = player.getPersistentDataContainer();
		if (pdc.has(GROUND_POUND_KEY)) return; // Already falling

		if (!isHighEnough(player)) return;
		pdc.set(GROUND_POUND_KEY, PersistentDataType.INTEGER, level);

		player.setVelocity(new Vector(0, -5, 0));
	}

	@EventHandler
	public void onFallDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		if (event.getCause() != DamageCause.FALL) return;

		PersistentDataContainer pdc = player.getPersistentDataContainer();
		if (!pdc.has(GROUND_POUND_KEY)) return;

		event.setCancelled(true);
	}

	private boolean isHighEnough(Player player) {
		if (player.isOnGround()) return false;
		for (int i = 0; i < 5; i++)
			if (player.getLocation().clone().subtract(0, i, 0).getBlock().getType() != Material.AIR)
				return false;
		return true;
	}

	private void check() {
		OnlinePlayers.getAll().forEach(player -> {
			if (player.getPersistentDataContainer().has(GROUND_POUND_KEY) &&
				(player.getLocation().clone().subtract(0, 0.1, 0).getBlock().isSolid()))
				trigger(player);
		});
	}

	private void trigger(Player player) {
		PersistentDataContainer pdc = player.getPersistentDataContainer();
		int level = pdc.get(GROUND_POUND_KEY, PersistentDataType.INTEGER).intValue();
		if (level == 0) return;

		pdc.remove(GROUND_POUND_KEY);
		new GroundPoundAnimation(player, level, player.getLocation()).start();
		player.getLocation().getWorld().playSound(player.getLocation(), Sound.ITEM_MACE_SMASH_GROUND_HEAVY, 2f, .6f);
	}

	@RequiredArgsConstructor
	private static class GroundPoundAnimation {

		private static final double BLOCKS_PER_SECOND = 8;
		private static final double BLOCKS_PER_LEVEL = 2;
		private static final double INITIAL_RANGE = 4;

		@NonNull
		private Player player;
		@NonNull
		private Integer level;
		@NonNull
		private Location center;

		private final Set<Vector3i> raised_blocks = new HashSet<>();

		public void start() {
			EntityUtils.getNearbyEntities(player.getLocation(), 1).keySet().stream().forEach(entity -> {
				if (!(entity instanceof LivingEntity livingEntity)) return;
				if (entity instanceof ArmorStand) return;
				double damage = 15 + (level * 10);

				if (!new EntityDamageByEntityEvent(player, livingEntity, DamageCause.ENTITY_ATTACK, DamageSource.builder(DamageType.PLAYER_ATTACK).withDirectEntity(livingEntity).withDamageLocation(player.getLocation()).withCausingEntity(player).build(), new HashMap<>() {{ put(DamageModifier.BASE, damage); }}, Map.of(DamageModifier.BASE, d -> d), false).callEvent())
					return;
				if (livingEntity.getNoDamageTicks() <= 0)
					livingEntity.damage(damage, player);
				livingEntity.setNoDamageTicks(5);

				Vector velocity = livingEntity.getLocation().toVector().subtract(center.toVector()).normalize().multiply(.5);
				velocity.setY(.6);
				livingEntity.setVelocity(velocity);
			});

			double maxRadius = INITIAL_RANGE + BLOCKS_PER_LEVEL * level;
			double radiusPerTick = BLOCKS_PER_SECOND / 20.0;

			AtomicReference<Double> radius = new AtomicReference<>(1d);
			AtomicInteger taskId = new AtomicInteger();

			taskId.set(Tasks.repeat(1, 1, () -> {
				double currentRadius = radius.get();

				if (currentRadius >= maxRadius) {
					Tasks.cancel(taskId.get());
					return;
				}

				int points = Math.max(8, (int) Math.ceil(currentRadius * Math.PI * 2));

				for (int i = 0; i < points; i++) {
					double angle = (Math.PI * 2 * i) / points;
					double x = Math.cos(angle) * currentRadius;
					double z = Math.sin(angle) * currentRadius;

					Location location = center.clone().add(x, 0.1, z);
					if (location.clone().subtract(0, .5, 0).getBlock().isSolid()) {
						if (location.getBlock().isSolid()) {
							location.add(0, 1, 0);
							if (location.getBlock().isSolid())
								continue;
						}
					}
					else {
						location.subtract(0, 1, 0);
						if (!location.clone().subtract(0, 0.5, 0).getBlock().isSolid())
							continue;
					}

					new ParticleBuilder(Particle.CRIT)
						.location(location)
						.offset(0, 0, 0)
						.extra(0)
						.allPlayers()
						.count(1)
						.spawn();

					EntityUtils.getNearbyEntities(location, .1).keySet().stream().forEach(entity -> {
						if (!(entity instanceof LivingEntity livingEntity)) return;
						if (entity instanceof ArmorStand) return;
						double damage = 15 + (level * 10);

						if (!new EntityDamageByEntityEvent(player, livingEntity, DamageCause.ENTITY_ATTACK, DamageSource.builder(DamageType.PLAYER_ATTACK).withDirectEntity(livingEntity).withDamageLocation(location).withCausingEntity(player).build(), new HashMap<>() {{ put(DamageModifier.BASE, damage); }}, Map.of(DamageModifier.BASE, d -> d), false).callEvent())
							return;
						if (livingEntity.getNoDamageTicks() <= 0)
							livingEntity.damage(damage, player);
						livingEntity.setNoDamageTicks(5);

						Vector velocity = livingEntity.getLocation().toVector().subtract(center.toVector()).normalize().multiply(.5);
						velocity.setY(.6);
						livingEntity.setVelocity(velocity);
					});

					Location blockPos = location.clone().subtract(0, 0.5, 0).getBlock().getLocation();
					Vector3i blockVec = new Vector3i((int) blockPos.x(), (int) blockPos.y(), (int) blockPos.z());
					if (raised_blocks.contains(blockVec)) continue;
					Block block = blockPos.getBlock();
					if (!block.getType().isSolid()) continue;

					raised_blocks.add(blockVec);
					new RaiseBlockAnimation(block).start();
				}

				radius.set(currentRadius + radiusPerTick);
			}));
		}
	}

	private static class RaiseBlockAnimation {
		private final Block block;
		private FallingBlock fallingBlock;

		public RaiseBlockAnimation(Block block) {
			this.block = block;
		}

		public void start() {
			block.getWorld().playSound(block.getLocation().toCenterLocation(), block.getSoundGroup().getBreakSound(), .2f, .8f);

			fallingBlock = block.getWorld().spawn(block.getLocation().toCenterLocation().clone().subtract(0, .3, 0), FallingBlock.class, fb -> {
				fb.setBlockData(block.getBlockData());
				fb.setVelocity(new Vector(0, .35, 0));
				fb.setDropItem(false);
				fb.setFireTicks(Integer.MAX_VALUE);
				fb.setVisualFire(TriState.FALSE);
				fb.setHurtEntities(false);
				fb.setCancelDrop(true);
				fb.setInvulnerable(true);
			});

			AtomicInteger taskId = new AtomicInteger();
			taskId.set(Tasks.repeat(1, 1, () -> {
				if (fallingBlock.getY() <= block.getY() || fallingBlock.isDead()) {
					fallingBlock.remove();
					Tasks.cancel(taskId.get());
				}
			}));

			// Fallback just in case...
			Tasks.wait(TickTime.SECOND.x(5), () -> {
				Tasks.cancel(taskId.get());
				if (fallingBlock != null)
					fallingBlock.remove();
			});
		}
	}

}
