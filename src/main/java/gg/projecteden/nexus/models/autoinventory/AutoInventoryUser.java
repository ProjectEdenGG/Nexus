package gg.projecteden.nexus.models.autoinventory;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.legacy.LegacyCommand.LegacyVaultMenu.LegacyVaultHolder;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks.BackpackMenu.BackpackHolder;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.AutoInventory;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.AutoInventoryFeature;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.features.AutoTool.AutoToolToolType;
import gg.projecteden.nexus.features.vaults.VaultCommand.VaultMenu.VaultHolder;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.tip.Tip;
import gg.projecteden.nexus.models.tip.Tip.TipType;
import gg.projecteden.nexus.models.tip.TipService;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Dropper;
import org.bukkit.block.Hopper;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.ChestBoat;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;

@Data
@Entity(value = "auto_sort", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class AutoInventoryUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	private String activeProfile;
	private Map<String, AutoInventoryProfile> profiles = new LinkedHashMap<>();

	@Data
	public static class AutoInventoryProfile implements Cloneable {
		private Set<AutoInventoryFeature> disabledFeatures = new HashSet<>();

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

		private Set<AutoToolToolType> autoToolExclude = new HashSet<>() {{
			add(AutoToolToolType.SWORDS);
		}};

		private Map<WorldGroup, Set<Material>> autoTrashInclude = new LinkedHashMap<>();
		private AutoTrashBehavior autoTrashBehavior = AutoTrashBehavior.TRASH;

		@Override
		public AutoInventoryProfile clone() {
			return Utils.getGson().fromJson(Utils.getGson().toJson(this), AutoInventoryProfile.class);
		}
	}

	private transient boolean sortingInventory;

	public static AutoInventoryUser of(String name) {
		return of(PlayerUtils.getPlayer(name));
	}

	public static AutoInventoryUser of(HasUniqueId player) {
		return of(player.getUniqueId());
	}

	public static AutoInventoryUser of(UUID uuid) {
		return new AutoInventoryUserService().get(uuid);
	}

	public AutoInventoryUser.AutoInventoryProfile getActiveProfile() {
		return profiles.get(getActiveProfileId());
	}

	public String getActiveProfileId() {
		if (profiles.isEmpty()) {
			profiles.put("default", new AutoInventoryProfile());
			activeProfile = "default";
		}

		if (activeProfile == null)
			activeProfile = profiles.keySet().iterator().next();

		if (!profiles.containsKey(activeProfile))
			profiles.put(activeProfile, new AutoInventoryProfile());

		return activeProfile;
	}

	public void tip(TipType tipType) {
		TipService tipService = new TipService();
		Tip tip = tipService.get(this);

		if (tip.show(tipType)) {
			String message = switch (tipType) {
				case AUTOSORT_SORT_INVENTORY -> "Your inventory has been automatically sorted. Use &c/autoinv features &3to disable";
				case AUTOSORT_SORT_CHESTS -> "Your chests have been automatically sorted. Use &c/autoinv features &3to disable";
				case AUTOSORT_REFILL -> "Broken tools and depleted stacks will be automatically refilled from your inventory";
				case AUTOSORT_DEPOSIT_ALL -> "Instantly deposit all matching items from your inventory into all nearby containers with &c/autoinv depositall";
				case AUTOSORT_DEPOSIT_QUICK -> "Quickly deposit all matching items from your inventory into a specific container by hitting it while crouching";
				default -> null;
			};

			if (!isNullOrEmpty(message))
				sendMessage(AutoInventory.PREFIX + message);
		}

		tipService.save(tip);
	}

	public boolean hasFeatureEnabled(AutoInventoryFeature feature) {
		if (!isOnline())
			return false;

		Player player = getOnlinePlayer();
		if (!feature.hasPermission(player))
			return false;

		if (AutoInventory.isWorldDisabled(player.getWorld()))
			return false;

		if (player.getGameMode() != GameMode.SURVIVAL)
			return false;

		return hasFeatureEnabledRaw(feature);
	}

	public boolean hasFeatureEnabledRaw(AutoInventoryFeature feature) {
		return !getActiveProfile().getDisabledFeatures().contains(feature);
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
		CHEST_BOAT(Material.OAK_CHEST_BOAT),
		BARREL(Material.BARREL),
		SHULKER_BOX(Material.SHULKER_BOX),
		HOPPER(Material.HOPPER),
		DROPPER(Material.DROPPER),
		DISPENSER(Material.DISPENSER),
		BACKPACK(ItemModelType.BACKPACK_3D_BASIC),
		VAULT(Material.IRON_BARS),
		ENDER_CHEST(Material.ENDER_CHEST),
		;

		@NonNull
		private final Material material;
		private String model;

		AutoSortInventoryType(@NonNull ItemModelType itemModelType) {
			this.material = itemModelType.getMaterial();
			this.model = itemModelType.getModel();
		}

		@SneakyThrows
		public static AutoSortInventoryType of(@Nullable Inventory inventory, String title) {
			if (inventory == null)
				return null;

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

			if (holder instanceof ChestBoat)
				return CHEST_BOAT;

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

			if (holder instanceof VaultHolder || holder instanceof LegacyVaultHolder)
				return VAULT;
			if (holder instanceof BackpackHolder)
				return BACKPACK;

			if (holder == null)
				if (StringUtils.stripColor(title).contains("Ender Chest"))
					return ENDER_CHEST;

			return null;
		}
	}

}
