package gg.projecteden.nexus.features.crates.models;

import gg.projecteden.nexus.features.crates.Crates;
import gg.projecteden.nexus.models.boost.BoostConfig;
import gg.projecteden.nexus.models.boost.Boostable;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.SerializationUtils;
import gg.projecteden.nexus.utils.StringUtils;
import joptsimple.internal.Strings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SerializableAs("CrateLoot")
public class CrateLoot implements ConfigurationSerializable {

	public int id;
	public String title = "";
	public List<ItemStack> items = new ArrayList<>();
	public double weight = 20;
	public boolean active = true;
	public CrateType type = CrateType.ALL;
	public ItemStack displayItem;

	public CrateLoot(Map<String, Object> map) {
		this.title = (String) map.getOrDefault("title", title);
		this.items = Arrays.stream(SerializationUtils.YML.deserializeItems((Map<String, Object>) map.getOrDefault("items", items)))
				.filter(itemStack -> !ItemUtils.isNullOrAir(itemStack)).collect(Collectors.toList());
		this.weight = (double) map.getOrDefault("weight", weight);
		this.active = (boolean) map.getOrDefault("active", active);
		this.type = CrateType.valueOf((String) map.getOrDefault("type", type.name()));
		this.displayItem = (ItemStack) map.getOrDefault("displayItem", displayItem);
	}

	public CrateLoot(String title, List<ItemStack> itemStacks, double weight, CrateType type, ItemStack itemStack) {
		this.title = title;
		this.items = itemStacks;
		this.weight = weight;
		this.type = type;
		this.displayItem = itemStack;
	}

	public List<ItemStack> getItems() {
		return items.stream().map(ItemStack::clone).collect(Collectors.toList());
	}

	public ItemStack getDisplayItem() {
		if (!ItemUtils.isNullOrAir(displayItem)) return displayItem;
		if (items.size() == 0) return null;
		return items.get(0).clone();
	}

	public ItemStack getDisplayItemWithNull() {
		return displayItem;
	}

	public String getTitle() {
		if (!Strings.isNullOrEmpty(title))
			return "&e" + title;
		if (getDisplayItem() == null)
			return "";
		return "&e" + getDisplayItem().getAmount() + "&3 x &e" + StringUtils.camelCase(getDisplayItem().getType().name());
	}

	@Override
	public @NotNull Map<String, Object> serialize() {
		return new LinkedHashMap<>() {{
			put("title", title);
			put("items", SerializationUtils.YML.serializeItems(items.toArray(getItems().toArray(ItemStack[]::new))));
			put("weight", weight);
			put("active", active);
			put("type", type.name());
			put("displayItem", displayItem);
		}};
	}

	public void update() {
		Crates.config.set(id + "", this);
		Crates.save();
	}

	public void delete() {
		Crates.config.set(id + "", null);
		Crates.save();
		Crates.lootCache.remove(this);
	}

	public double getWeight() {
		double multiplier = items.size() == 1 && items.get(0).isSimilar(CrateType.MYSTERY.getKey()) ? BoostConfig.multiplierOf(Boostable.MYSTERY_CRATE_KEY) : 1;
		return weight * multiplier;
	}
}
