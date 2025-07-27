package gg.projecteden.nexus.features.resourcepack.models;

import com.google.gson.annotations.SerializedName;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.features.legacy.listeners.LegacyItems;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.models.files.ItemModelFolder;
import gg.projecteden.nexus.models.custommodels.CustomModelConfig;
import gg.projecteden.nexus.models.custommodels.CustomModelConfigService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils.Dev;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@AllArgsConstructor
public class ItemModelInstance implements Comparable<ItemModelInstance> {
	private ItemModelFolder folder;
	private String itemModel;
	private CustomItemModelMeta meta;
	private String fileName;
	@SerializedName("old_custom_model_data")
	private int oldCustomModelData;
	@SerializedName("old_base_material")
	private Material oldMaterial;
	private Material material;

	public static final String NBT_KEY = "CustomModelData";
	public static final String ICON = "icon";

	@Getter
	private static final String modelsSubdirectory = "/assets/minecraft/models";
	@Getter
	private static final String itemsSubdirectory = "/assets/minecraft/items";

	private static final Map<Material, Map<Integer, String>> cmdToModelsConversionCache = new HashMap<>() {{
		put(Material.LAPIS_LAZULI, LegacyItems.PLUSHIES);
	}};

	public static ItemModelInstance of(Material material, String data) {
		if (ResourcePack.getModels() == null)
			return null;

		return ResourcePack.getModels().values().stream()
				.filter(model -> model.getMaterial() == material && Objects.equals(model.getItemModel(), data))
				.findFirst()
				.orElse(null);
	}

	public static ItemModelInstance convertFromCustomModelData(Material material, int data, Location location) {
		if (ResourcePack.isReloading() || ResourcePack.getModels() == null)
			return null;

		var cachedModels = cmdToModelsConversionCache.get(material);
		if (cachedModels != null) {
			var cachedData = cachedModels.get(data);
			if (cachedData != null) {
				return ResourcePack.getModels().get(cachedData);
			}
		}

		ItemModelInstance match = ResourcePack.getModels().values().stream()
			.filter(model -> material.equals(model.getOldMaterial()) && model.getOldCustomModelData() == data)
			.findFirst()
			.orElse(null);

		if (match == null) {
			warn(material, data, location);
			return null;
		}

		cmdToModelsConversionCache.computeIfAbsent(material, k -> new HashMap<>()).put(data, match.getItemModel());

		return match;
	}

	private static Map<Material, List<Integer>> ignoredWarns = new HashMap<>() {{
		put(Material.ANVIL, List.of(2, 5, 6, 8, 9));
		put(Material.BROWN_MUSHROOM, List.of(6));
		put(Material.CLOCK, List.of(2));
		put(Material.COCOA_BEANS, List.of(1));
		put(Material.COOKIE, List.of(10001));
		put(Material.CRAFTING_TABLE, List.of(1));
		put(Material.DIAMOND_AXE, List.of(1010));
		put(Material.DIAMOND_SHOVEL, List.of(1010));
		put(Material.DIAMOND_SWORD, List.of(1010));
		put(Material.ICE, List.of(905));
		put(Material.IRON_AXE, List.of(1000, 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009, 1010));
		put(Material.IRON_BOOTS, List.of(1, 2));
		put(Material.IRON_CHESTPLATE, List.of(1, 2));
		put(Material.IRON_HELMET, List.of(1, 2));
		put(Material.IRON_HOE, List.of(1000, 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009, 1010));
		put(Material.IRON_LEGGINGS, List.of(1, 2));
		put(Material.IRON_PICKAXE, List.of(1000, 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009, 1010));
		put(Material.IRON_SHOVEL, List.of(1000, 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009, 1010));
		put(Material.IRON_SWORD, List.of(1000, 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009, 1010));
		put(Material.LEATHER_HORSE_ARMOR, List.of(1000, 1001, 1002, 11000, 13000, 20000, 20001, 20003, 20012, 20018, 20022, 20029, 20031, 20033, 20037, 20058, 50009));
		put(Material.MINECART, List.of(207));
		put(Material.MUSHROOM_STEW, List.of(10001));
		put(Material.NETHERITE_BOOTS, List.of(1));
		put(Material.NETHERITE_CHESTPLATE, List.of(1));
		put(Material.NETHERITE_HELMET, List.of(1));
		put(Material.NETHERITE_LEGGINGS, List.of(1));
		put(Material.NETHERITE_PICKAXE, List.of(1010));
		put(Material.PAPER, List.of(200, 4132, 4133, 4134, 4135, 4136, 6042, 6219, 6400, 6409, 6415, 6499, 19900, 20060, 22000));
		put(Material.PLAYER_HEAD, List.of(12));
		put(Material.RED_DYE, List.of(1));
		put(Material.STICK, List.of(16));
		put(Material.STONE_AXE, List.of(1009));
		put(Material.STONE_BUTTON, List.of(214));
	}};

	private static void warn(Material material, int data, Location location) {
		if (ignoredWarns.containsKey(material) && ignoredWarns.get(material).contains(data))
			return;

		if (material == Material.ARROW && data >= 1000 && data <= 4999)
			return;

		String message = "Could not find new model for custom model data " + data + " on material " + material + " at " + StringUtils.xyzw(location);
		Nexus.warn(message);

		if (location != null && location.getWorld().getName().contains("pugmas"))
			Dev.WAKKA.send("&e[Saturn] &c" + message);
		else if (Nexus.getEnv() != Env.PROD)
			Chat.Broadcast.ingame()
				.channel(Chat.StaticChannel.GLOBAL)
				.message("&e[Saturn] &c" + message)
				.send();
	}

	public static ItemModelInstance convert(ItemStack item, Location location) {
		if (Model.hasModel(item))
			return of(item);

		return convertLegacy(item.getType(), new ItemBuilder(item).customModelData(), location);
	}

	private static final CustomModelConfig LEGACY_CONFIG = new CustomModelConfigService().get0();

	public static ItemModelInstance convertLegacy(Material material, int data, Location location) {
		if (material == Material.LEATHER_BOOTS)  // keeps converting custom armor to stockings
			return null;

		final var oldModels = LEGACY_CONFIG.getOldModels();
		final var newModels = LEGACY_CONFIG.getNewModels();
		if (!oldModels.containsKey(material))
			return convertFromCustomModelData(material, data, location);

		final String model = oldModels.get(material).get(data);
		if (Nullables.isNullOrEmpty(model))
			return convertFromCustomModelData(material, data, location);

		for (var map1 : newModels.entrySet())
			for (var map2 : map1.getValue().entrySet())
				if (model.equals(map2.getValue()))
					return convertFromCustomModelData(map1.getKey(), map2.getKey(), location);

		return null;
	}

	public static ItemStack itemOf(Material material, String data) {
		ItemModelInstance model = of(material, data);
		return model == null ? null : model.getItem();
	}

	public static ItemModelInstance of(ItemStack item) {
		if (Nullables.isNullOrAir(item))
			return null;

		return of(item.getType(), Model.of(item));
	}

	public static ItemModelInstance of(String path) {
		if (Nullables.isNullOrEmpty(path))
			return null;

		return ResourcePack.getModels().get(path);
	}

	public static ItemModelInstance of(ItemModelType itemModelType) {
		return of(itemModelType.getMaterial(), itemModelType.getModel());
	}

	@NotNull
	public String getId() {
		return itemModel;
	}

	public boolean equals(ItemStack itemStack) {
		if (Nullables.isNullOrAir(itemStack))
			return false;
		if (itemStack.getType() != material)
			return false;

		return Objects.equals(Model.of(itemStack), itemModel);
	}

	public ItemStack getItem() {
		final ItemBuilder builder = new ItemBuilder(material)
			.model(itemModel)
			.name(meta.getName())
			.lore(meta.getLore());

		if (meta.hasDefaultColor())
			builder.dyeColor(meta.getDefaultColor());

		return builder.build();
	}

	public ItemStack getDisplayItem() {
		return new ItemBuilder(getItem())
				.name(Nullables.isNullOrEmpty(meta.getName()) ? StringUtils.camelCase(fileName) : meta.getName())
				.build();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ItemModelInstance that = (ItemModelInstance) o;
		return Objects.equals(itemModel, that.itemModel) && material == that.material;
	}

	@Override
	public int compareTo(@NotNull ItemModelInstance other) {
		if (!material.equals(other.getMaterial()))
			return material.compareTo(other.getMaterial());

		return CharSequence.compare(itemModel, other.getItemModel());
	}

	@Data
	public static class CustomItemModelMeta {
		private String name;
		private List<String> lore;
		private String defaultColor;

		public boolean hasDefaultColor() {
			return !Nullables.isNullOrEmpty(defaultColor);
		}
	}

}
