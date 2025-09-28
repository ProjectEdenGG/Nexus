package gg.projecteden.nexus.features.events.y2025.halloween25;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.boost.Boostable;
import gg.projecteden.nexus.models.boost.Booster;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static gg.projecteden.api.common.utils.Nullables.isNotNullOrEmpty;
import static gg.projecteden.api.common.utils.RandomUtils.chanceOf;
import static gg.projecteden.api.common.utils.RandomUtils.randomElement;
import static gg.projecteden.api.common.utils.RandomUtils.randomInt;
import static gg.projecteden.nexus.features.listeners.KillerMoney.DEATH_CAUSE_BLACKLIST;
import static gg.projecteden.nexus.features.listeners.KillerMoney.UNNATURAL_SPAWN_REASONS;
import static org.bukkit.HeightMap.MOTION_BLOCKING_NO_LEAVES;
import static org.bukkit.HeightMap.OCEAN_FLOOR;

@NoArgsConstructor
public class Halloween25 extends Feature implements Listener {
	private static final NamespacedKey PUMPKIN_HEAD_KEY = new NamespacedKey(Nexus.getInstance(), "halloween25_pumpkin_head");

	private static final List<ItemModelType> CANDY = List.of(
		ItemModelType.CANDY_CANDY_CORN,
		ItemModelType.CANDY_CARAMEL_APPLE,
		ItemModelType.CANDY_GHOST_CHOCOLATE,
		ItemModelType.CANDY_GHOST_MARSHMALLOW,
		ItemModelType.CANDY_GUMMIES_BRAINS_LIME,
		ItemModelType.CANDY_GUMMIES_BRAINS_STRAWBERRY,
		ItemModelType.CANDY_GUMMIES_WORMS_FIZZY,
		ItemModelType.CANDY_GUMMIES_WORMS_FRUITY,
		ItemModelType.CANDY_GUMMIES_WORMS_SOUR,
		ItemModelType.CANDY_LOLLIPOPS_1,
		ItemModelType.CANDY_LOLLIPOPS_EYEBALL_CHERRY,
		ItemModelType.CANDY_LOLLIPOPS_EYEBALL_LIME,
		ItemModelType.CANDY_LOLLIPOPS_EYEBALL_VANILLA,
		ItemModelType.CANDY_WRAPPED_1,
		ItemModelType.CANDY_WRAPPED_5,
		ItemModelType.CANDY_WRAPPED_12,
		ItemModelType.CANDY_WRAPPED_16,
		ItemModelType.CANDY_WRAPPED_17
	);

	private static final List<ItemModelType> PUMPKIN_COSTUMES = Arrays.stream(ItemModelType.values())
		.filter(itemModelType -> isPumpkinCostume(itemModelType.getModel()))
		.toList();

	private static final List<EntityType> PUMPKIN_HEAD_TYPES = List.of(
		EntityType.ZOMBIE,
		EntityType.DROWNED,
		EntityType.HUSK,
		EntityType.SKELETON,
		EntityType.BOGGED,
		EntityType.STRAY,
		EntityType.ZOMBIE_VILLAGER,
		EntityType.WITHER_SKELETON,
		EntityType.ZOMBIFIED_PIGLIN
	);

	private static boolean isBeforeEvent() {
		if (Nexus.getEnv() != Env.PROD)
			return false;

		var oct1 = LocalDate.of(2025, 10, 1);
		return LocalDateTime.now().isBefore(oct1.atStartOfDay());
	}

	public static ItemModelType randomCandyModel() {
		return randomElement(CANDY);
	}

	public static ItemBuilder randomCandy() {
		return randomCandyModel().getNamedItemBuilder().lore("Halloween 2025");
	}

	public static ItemModelType randomPumpkinCostume() {
		return randomElement(PUMPKIN_COSTUMES);
	}

	public static boolean isCandy(ItemStack item) {
		if (item == null)
			return false;

		String model = new ItemBuilder(item).model();
		if (model == null)
			return false;

		return isCandy(model);
	}

	private static boolean isCandy(String model) {
		return isNotNullOrEmpty(model) && model.contains("food/candy/");
	}

	private static boolean isPumpkinCostume(String model) {
		return isNotNullOrEmpty(model) && model.contains("costumes/exclusive/hat/pumpkins/");
	}

	@EventHandler
	public void on(EntityAddToWorldEvent event) {
		if (isBeforeEvent())
			return;

		if (!(event.getEntity() instanceof LivingEntity entity))
			return;

		if (!PUMPKIN_HEAD_TYPES.contains(entity.getType()))
			return;

		if (!WorldGroup.of(entity).isSurvivalMode())
			return;

		if (CitizensUtils.isNPC(entity))
			return;

		var pdc = entity.getPersistentDataContainer();
		if (pdc.has(PUMPKIN_HEAD_KEY))
			return;

		if (shouldIgnore(entity)) {
			pdc.set(PUMPKIN_HEAD_KEY, PersistentDataType.BOOLEAN, false);
			return;
		}

		boolean value = chanceOf(15);
		pdc.set(PUMPKIN_HEAD_KEY, PersistentDataType.BOOLEAN, value);
		if (!value)
			return;

		entity.getEquipment().setItem(EquipmentSlot.HEAD, randomPumpkinCostume().getItem());
	}

	private boolean shouldIgnore(Entity entity) {
		Location location = entity.getLocation();

		if (UNNATURAL_SPAWN_REASONS.contains(entity.getEntitySpawnReason()))
			return true;

		if (entity.getLocation().getBlock().getType() == Material.CAVE_AIR)
			return false;

		if (entity.getType() == EntityType.WITHER_SKELETON)
			return entity.getLocation().getBlock().getType() != Material.WITHER_ROSE;

		if (entity.getType() == EntityType.ZOMBIFIED_PIGLIN) {
			Material on = entity.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
			return on != Material.MAGMA_BLOCK && on != Material.OBSIDIAN && on != Material.CRYING_OBSIDIAN;
		}

		if (WorldGroup.of(entity) == WorldGroup.SURVIVAL)
			if (location.getY() <= 63)
				return false;

		var heightMap = entity.getType() == EntityType.DROWNED ? OCEAN_FLOOR : MOTION_BLOCKING_NO_LEAVES;
		var highest = location.getWorld().getHighestBlockAt(location, heightMap);

		if (highest.getY() > location.getY())
			return true;

		return false;
	}

	@EventHandler
	public void on(EntityDeathEvent event) {
		if (isBeforeEvent())
			return;

		if (event.getEntity().getLastDamageCause() != null)
			if (DEATH_CAUSE_BLACKLIST.contains(event.getEntity().getLastDamageCause().getCause()))
				return;

		event.getDrops().removeIf(item -> isPumpkinCostume(Model.of(item)));

		var entity = event.getEntity();
		var player = entity.getKiller();
		var pdc = entity.getPersistentDataContainer();

		var pumpkinHead = pdc.get(PUMPKIN_HEAD_KEY, PersistentDataType.BOOLEAN);
		if (pumpkinHead == null || !pumpkinHead)
			return;

		var candyBoost = (int) Booster.getTotalBoost(player, Boostable.HALLOWEEN_CANDY);
		for (int i = 0; i < randomInt(1, 2 + candyBoost); i++)
			event.getDrops().add(randomCandy().build());

		var crateKeyBoost = Booster.getTotalBoost(player, Boostable.HALLOWEEN_CRATE_KEY);
		if (chanceOf(.4 * crateKeyBoost))
			event.getDrops().add(CrateType.HALLOWEEN.getKey());
	}

}

