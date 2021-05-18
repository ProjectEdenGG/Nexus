package me.pugabyte.nexus.models.autosort;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.lexikiq.HasUniqueId;
import me.pugabyte.nexus.features.store.perks.autosort.AutoSort;
import me.pugabyte.nexus.features.store.perks.autosort.AutoSortFeature;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.tip.Tip;
import me.pugabyte.nexus.models.tip.Tip.TipType;
import me.pugabyte.nexus.models.tip.TipService;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Entity("auto_sort")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class AutoSortUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Set<AutoSortFeature> disabledFeatures = new HashSet<>();

	private Set<Material> autoDepositExclude = new HashSet<>() {{ addAll(MaterialTag.ITEMS_ARROWS.getValues()); }};
	private Set<Material> autoRefillExclude = new HashSet<>();
	private Set<Material> autoCraftExclude = new HashSet<>();

	private AutoTrashBehavior autoTrashBehavior = AutoTrashBehavior.TRASH;
	private Set<Material> autoTrashMaterials = new HashSet<>();

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
		if (tip.show(tipType))
			sendMessage(AutoSort.PREFIX + switch (tipType) {
				case AUTOSORT_SORT_INVENTORY -> "Your inventory has been automatically sorted. Use &c/autosort inventory &3to disable";
				case AUTOSORT_SORT_CHESTS -> "Your chests have been automatically sorted. Use &c/autosort chests &3to disable";
				case AUTOSORT_REFILL -> "Broken tools and depleted stacks will be automatically refilled from your inventory";
				case AUTOSORT_DEPOSIT_ALL -> "Instantly deposit all matching items from your inventory into all nearby containers with &c/sort";
				case AUTOSORT_DEPOSIT_QUICK -> "Quickly deposit all matching items from your inventory into a specific container by hitting it while crouching";
				default -> null;
			});
		tipService.save(tip);
	}

	public boolean hasFeatureEnabled(AutoSortFeature feature) {
		if (!isOnline())
			return false;

		Player player = getOnlinePlayer();
		if (!player.hasPermission(AutoSort.PERMISSION))
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

}
