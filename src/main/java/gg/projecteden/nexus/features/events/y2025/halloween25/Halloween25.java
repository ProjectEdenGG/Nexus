package gg.projecteden.nexus.features.events.y2025.halloween25;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks.BackpackTier;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.boost.Boostable;
import gg.projecteden.nexus.models.boost.Booster;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.models.halloween25.Halloween25UserService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.NoArgsConstructor;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.time.YearMonth;
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
	private static final String PREFIX = StringUtils.getPrefix("Halloween");
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

	public static boolean isEventActive() {
		return isEventActive(null);
	}

	public static boolean isEventActive(Player player) {
		if (player != null)
			if (Rank.of(player).isAdmin())
				return true;

		if (Nexus.getEnv() != Env.PROD)
			return false;

		return YearMonth.now().equals(YearMonth.of(2025, 10));
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
		if (!isEventActive())
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

		boolean value = chanceOf(30);
		pdc.set(PUMPKIN_HEAD_KEY, PersistentDataType.BOOLEAN, value);
		if (!value)
			return;

		entity.getEquipment().setItem(EquipmentSlot.HEAD, randomPumpkinCostume().getItem());
	}

	private boolean shouldIgnore(Entity entity) {
		Location location = entity.getLocation();

		if (!new WorldGuardUtils(location).getRegionsAt(location).isEmpty())
			return true;

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
		var entity = event.getEntity();
		var player = entity.getKiller();

		if (!isEventActive(player))
			return;

		event.getDrops().removeIf(item -> isPumpkinCostume(Model.of(item)));

		if (event.getEntity().getLastDamageCause() != null)
			if (DEATH_CAUSE_BLACKLIST.contains(event.getEntity().getLastDamageCause().getCause()))
				return;

		var pdc = entity.getPersistentDataContainer();
		var pumpkinHead = pdc.get(PUMPKIN_HEAD_KEY, PersistentDataType.BOOLEAN);

		if (player == null)
			return;

		if (pumpkinHead == null || !pumpkinHead)
			return;

		var candyBoost = (int) Booster.getTotalBoost(player, Boostable.HALLOWEEN_CANDY);
		for (int i = 0; i < randomInt(1, 2 + candyBoost); i++)
			event.getDrops().add(randomCandy().build());

		var crateKeyBoost = Booster.getTotalBoost(player, Boostable.HALLOWEEN_CRATE_KEY);
		if (chanceOf(.4 * crateKeyBoost)) {
			event.getDrops().add(CrateType.HALLOWEEN.getKey());
			PlayerUtils.send(player, PREFIX + "A Halloween Crate Key was dropped!");
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void on(NPCRightClickEvent event) {
		if (!isEventActive(event.getClicker()))
			return;

		if (event.getNPC().getId() != 4971)
			return;

		event.setCancelled(true);

		new Halloween25UserService().edit(event.getClicker(), user -> {
			user.sendMessage("");
			user.sendMessage(Dev.BLAST.getNerd().getColoredName() + " &5&l> &6Happy Halloween! &fA bunch of mobs stole all the candy and crate keys we had gathered for you.");
			user.sendMessage(Dev.BLAST.getNerd().getColoredName() + " &5&l> &fIf you find any mobs with pumpkins on their heads, make sure to take back your treats!");

			if (user.hasAcquiredCandyBasket()) {
				user.sendMessage(new JsonBuilder(Dev.BLAST.getNerd().getColoredName() + " &5&l> &fIf you want another candy basket, ").group().next("&eclick here to buy one!").command("/halloween25 buy basket"));
			} else {
				user.sendMessage(Dev.BLAST.getNerd().getColoredName() + " &5&l> &fHere's a candy basket to take with you!");
				PlayerUtils.giveItem(event.getClicker(), BackpackTier.HALLOWEEN.create());
				user.incrementCandyBaskets();
			}
		});
	}

}

