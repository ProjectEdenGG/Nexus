package gg.projecteden.nexus.models.autosort;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.store.perks.autosort.AutoSort;
import gg.projecteden.nexus.features.store.perks.autosort.AutoSortFeature;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.tip.Tip;
import gg.projecteden.nexus.models.tip.Tip.TipType;
import gg.projecteden.nexus.models.tip.TipService;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.lexikiq.HasUniqueId;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Dropper;
import org.bukkit.block.Hopper;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;
import static gg.projecteden.utils.StringUtils.isNullOrEmpty;

@Data
@Entity(value = "auto_sort", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class AutoSortUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Set<AutoSortFeature> disabledFeatures = new HashSet<>();

	private Set<AutoSortInventoryType> disabledInventoryTypes = new HashSet<>();

	private Set<Material> autoDepositExclude = new HashSet<>() {{
		addAll(MaterialTag.ITEMS_ARROWS.getValues());
		addAll(MaterialTag.SHULKER_BOXES.getValues());
		addAll(MaterialTag.TOOLS_NETHERITE.getValues());
	}};

	private Set<Material> autoRefillExclude = new HashSet<>();

	private Set<Material> autoCraftExclude = new HashSet<>() {{
		add(Material.GLOWSTONE);
	}};

	private Set<Material> autoTrashInclude = new HashSet<>();
	private AutoTrashBehavior autoTrashBehavior = AutoTrashBehavior.TRASH;

	private transient boolean sortingInventory;

	public static AutoSortUser of(String name) {
		return of(PlayerUtils.getPlayer(name));
	}

	public static AutoSortUser of(HasUniqueId player) {
		return of(player.getUniqueId());
	}

	public static AutoSortUser of(UUID uuid) {
		return new AutoSortUserService().get(uuid);
	}

	public void tip(TipType tipType) {
		TipService tipService = new TipService();
		Tip tip = tipService.get(this);

		if (tip.show(tipType)) {
			String message = switch (tipType) {
				case AUTOSORT_SORT_INVENTORY -> "Your inventory has been automatically sorted. Use &c/autosort inventory &3to disable";
				case AUTOSORT_SORT_CHESTS -> "Your chests have been automatically sorted. Use &c/autosort chests &3to disable";
				case AUTOSORT_REFILL -> "Broken tools and depleted stacks will be automatically refilled from your inventory";
				case AUTOSORT_DEPOSIT_ALL -> "Instantly deposit all matching items from your inventory into all nearby containers with &c/sort";
				case AUTOSORT_DEPOSIT_QUICK -> "Quickly deposit all matching items from your inventory into a specific container by hitting it while crouching";
				default -> null;
			};

			if (!isNullOrEmpty(message))
				sendMessage(AutoSort.PREFIX + message);
		}

		tipService.save(tip);
	}

	public boolean hasFeatureEnabled(AutoSortFeature feature) {
		if (!isOnline())
			return false;

		Player player = getOnlinePlayer();
		if (!feature.hasPermission(player))
			return false;

		if (AutoSort.isWorldDisabled(player.getWorld()))
			return false;

		if (player.getGameMode() != GameMode.SURVIVAL)
			return false;

		return !disabledFeatures.contains(feature);
	}

	public Inventory getInventory() {
		return getOnlinePlayer().getInventory();
	}

	public enum AutoTrashBehavior {
		NO_PICKUP,
		TRASH
	}

	@Getter
	@AllArgsConstructor
	@RequiredArgsConstructor
	public enum AutoSortInventoryType {
		CHEST(Material.CHEST),
		DOUBLE_CHEST(Material.CHEST),
		TRAPPED_CHEST(Material.TRAPPED_CHEST),
		TRAPPED_DOUBLE_CHEST(Material.TRAPPED_CHEST),
		MINECART_CHEST(Material.CHEST_MINECART),
		BARREL(Material.BARREL),
		SHULKER_BOX(Material.SHULKER_BOX),
		HOPPER(Material.HOPPER),
		DROPPER(Material.DROPPER),
		DISPENSER(Material.DISPENSER),
		BACKPACK(Material.SHULKER_BOX, 1),
		VAULT(Material.IRON_BARS),
		ENDER_CHEST(Material.ENDER_CHEST),
		;

		@NonNull
		private final Material material;
		private int customModelData;

		@SneakyThrows
		public static AutoSortInventoryType of(@Nullable Inventory inventory, String title) {
			if (inventory == null)
				return null;

			title = stripColor(title);
			InventoryHolder holder = inventory.getHolder();
			if (holder instanceof Chest chest)
				if (chest.getType() == Material.TRAPPED_CHEST)
					return TRAPPED_CHEST;
				else
					return CHEST;
			if (holder instanceof DoubleChest doubleChest)
				if (doubleChest.getLocation().getBlock().getType() == Material.TRAPPED_CHEST)
					return TRAPPED_DOUBLE_CHEST;
				else
					return DOUBLE_CHEST;

			if (holder instanceof StorageMinecart)
				return MINECART_CHEST;

			if (holder instanceof Barrel)
				return BARREL;

			if (holder instanceof ShulkerBox)
				return SHULKER_BOX;

			if (holder instanceof Hopper)
				return HOPPER;
			if (holder instanceof Dropper)
				return DROPPER;
			if (holder instanceof Dispenser)
				return DISPENSER;

			if (Class.forName("com.drtshock.playervaults.vaultmanagement.VaultHolder").isInstance(holder))
				return VAULT;

			if (holder == null)
				if (title.equalsIgnoreCase("Backpack"))
					return BACKPACK;
				else if (title.contains("Ender Chest"))
					return ENDER_CHEST;

			return null;
		}
	}

}
