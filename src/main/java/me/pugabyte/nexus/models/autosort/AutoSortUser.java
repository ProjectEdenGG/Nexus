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
import me.pugabyte.nexus.features.autosort.AutoSort;
import me.pugabyte.nexus.features.autosort.AutoSortFeature;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.autotrash.AutoTrash;
import me.pugabyte.nexus.models.tip.Tip;
import me.pugabyte.nexus.models.tip.Tip.TipType;
import me.pugabyte.nexus.models.tip.TipService;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.Material;
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

	private AutoTrash.Behavior autoTrashBehavior = AutoTrash.Behavior.TRASH;
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
		tip.show(tipType);
		tipService.save(tip);
	}

	public boolean isFeatureEnabled(AutoSortFeature feature) {
		if (!isOnline())
			return false;

		if (!getOnlinePlayer().hasPermission(AutoSort.getPermission()))
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
