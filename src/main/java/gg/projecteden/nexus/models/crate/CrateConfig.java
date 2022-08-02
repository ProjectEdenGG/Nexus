package gg.projecteden.nexus.models.crate;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.models.boost.BoostConfig;
import gg.projecteden.nexus.models.boost.Boostable;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.*;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@Data
@Entity(value = "crate_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class CrateConfig implements DatabaseObject {

	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled = false;
	private Map<CrateType, List<UUID>> crateEntities = new ConcurrentHashMap<>();
	private List<CrateLoot> loot = new ArrayList<>();

	public void save() {
		CrateConfigService.instance.save(this);
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CrateLoot {

		public String title = "";
		public List<ItemStack> items = new ArrayList<>();
		public double weight = 20;
		public boolean active = true;
		public CrateType type = null;
		public ItemStack displayItem;
		public List<String> commandsNoSlash = new ArrayList<>();
		private boolean shouldAnnounce;

		public List<ItemStack> getItems() {
			return items.stream().map(ItemStack::clone).collect(Collectors.toList());
		}

		public ItemStack getDisplayItem() {
			if (!isNullOrAir(displayItem)) return displayItem;
			if (items.isEmpty()) return null;
			return items.get(0).clone();
		}

		public ItemStack getDisplayItemWithNull() {
			return displayItem;
		}

		public String getDisplayName() {
			if (!Nullables.isNullOrEmpty(title))
				return "&e" + title;
			if (getDisplayItem() == null)
				return "";
			return "&e" + getDisplayItem().getAmount() + "&3 x &e" + StringUtils.camelCase(getDisplayItem().getType().name());
		}

		public double getWeight() {
			double multiplier = 1;

			if (items.size() == 1 && items.get(0).isSimilar(CrateType.MYSTERY.getKey()))
				multiplier = BoostConfig.multiplierOf(Boostable.MYSTERY_CRATE_KEY);

			return weight * multiplier;
		}
	}

}
