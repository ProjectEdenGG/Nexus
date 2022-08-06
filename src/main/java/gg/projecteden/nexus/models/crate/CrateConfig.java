package gg.projecteden.nexus.models.crate;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.models.boost.BoostConfig;
import gg.projecteden.nexus.models.boost.Boostable;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
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
	private int nextId = 1;

	public void save() {
		CrateConfigService.instance.save(this);
	}

	private int nextId() {
		return nextId++;
	}

	@PostLoad
	void fixLootIds() {
		AtomicInteger fixed = new AtomicInteger();
		loot.forEach(loot -> {
			if (loot.getId() == 0) {
				loot.setId(nextId());
				fixed.getAndIncrement();
			}
		});
		if (fixed.get() > 0)
			save();
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CrateLoot {

		public static @Nullable CrateLoot byId(int id) {
			return CrateConfigService.get().getLoot().stream().filter(loot -> loot.getId() == id).findFirst().orElse(null);
		}

		private int id;
		private String title = "";
		private List<ItemStack> items = new ArrayList<>();
		private double weight = 20;
		private boolean active = false;
		private CrateType type = null;
		private ItemStack displayItem;
		private List<String> commandsNoSlash = new ArrayList<>();
		private boolean shouldAnnounce;
		private String announcement;

		public CrateLoot(CrateType type) {
			this.type = type;
			this.id = CrateConfigService.get().nextId();
		}

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
