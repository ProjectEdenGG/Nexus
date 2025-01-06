package gg.projecteden.nexus.models.crate;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.models.boost.Boostable;
import gg.projecteden.nexus.models.boost.Booster;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import joptsimple.internal.Strings;
import lombok.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
	private List<CrateGroup> groups = new ArrayList<>();
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
	public static class CrateLoot implements CrateDisplay {

		public static CrateLoot byId(int id) {
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

		@Override
		public ItemStack getDisplayItem() {
			if (!Nullables.isNullOrAir(displayItem)) return displayItem;
			if (items.isEmpty()) return null;
			return items.get(0).clone();
		}

		public ItemStack getDisplayItemWithNull() {
			return displayItem;
		}

		@Override
		public String getDisplayName() {
			if (!Nullables.isNullOrEmpty(title))
				return "&e" + title;
			if (getDisplayItem() == null)
				return "";
			return "&e" + getDisplayItem().getAmount() + "&3 x &e" + StringUtils.camelCase(getDisplayItem().getType().name());
		}

		@Override
		public double getWeightForPlayer(Player player) {
			double multiplier = 1;

			if (items.size() == 1 && items.get(0).isSimilar(CrateType.MYSTERY.getKey()))
				multiplier = Booster.getTotalBoost(player, Boostable.MYSTERY_CRATE_KEY);

			return weight * multiplier;
		}
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@RequiredArgsConstructor
	public static class CrateGroup implements CrateDisplay {
		private String title = "";
		private ItemStack displayItem;
		@NonNull
		private CrateType type;
		private List<Integer> lootIds = new ArrayList<>();

		@Override
		public String getDisplayName() {
			if (!Strings.isNullOrEmpty(title))
				return "&e" + title;
			if (lootIds.isEmpty())
				return "";
			return CrateLoot.byId(lootIds.get(0)).getDisplayName();
		}

		@Override
		public ItemStack getDisplayItem() {
			if (!Nullables.isNullOrAir(displayItem))
				return displayItem;
			if (lootIds.isEmpty())
				return null;
			return CrateLoot.byId(lootIds.get(0)).getDisplayItem();
		}

		@Override
		public double getWeight() {
			return lootIds.stream().map(CrateLoot::byId).mapToDouble(CrateLoot::getWeight).sum() / lootIds.size();
		}

		@Override
		public boolean isActive() {
			return lootIds.stream().map(CrateLoot::byId).anyMatch(CrateLoot::isActive);
		}
	}

}
