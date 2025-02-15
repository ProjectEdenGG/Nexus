package gg.projecteden.nexus.features.resourcepack.models;

import com.google.gson.annotations.SerializedName;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.features.legacy.listeners.LegacyItems;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.models.files.CustomModelFolder;
import gg.projecteden.nexus.models.custommodels.CustomModelConfig;
import gg.projecteden.nexus.models.custommodels.CustomModelConfigService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
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
public class CustomModel implements Comparable<CustomModel> {
	private CustomModelFolder folder;
	private String data;
	private CustomModelMeta meta;
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

	public static CustomModel of(Material material, String data) {
		if (ResourcePack.getModels() == null)
			return null;

		return ResourcePack.getModels().values().stream()
				.filter(model -> model.getMaterial() == material && Objects.equals(model.getData(), data))
				.findFirst()
				.orElse(null);
	}

	public static CustomModel convertFromCustomModelData(Material material, int data, Location location) {
		if (ResourcePack.isReloading() || ResourcePack.getModels() == null)
			return null;

		var cachedModels = cmdToModelsConversionCache.get(material);
		if (cachedModels != null) {
			var cachedData = cachedModels.get(data);
			if (cachedData != null) {
				return ResourcePack.getModels().get(cachedData);
			}
		}

		CustomModel match = ResourcePack.getModels().values().stream()
			.filter(model -> material.equals(model.getOldMaterial()) && model.getOldCustomModelData() == data)
			.findFirst()
			.orElse(null);

		if (match == null) {
			warn(material, data, location);
			return null;
		}

		cmdToModelsConversionCache.computeIfAbsent(material, k -> new HashMap<>()).put(data, match.getData());

		return match;
	}

	private static Map<Material, List<Integer>> ignoredWarns = new HashMap<>() {{
		put(Material.ANVIL, List.of(2, 5, 6, 8, 9));
		put(Material.ARROW, List.of(4001, 4002, 4003, 4004, 4005, 4006, 4007, 4008, 4009, 4010, 4011, 4012, 4013, 4014, 4015, 4016, 4017, 4018));
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
		put(Material.IRON_BOOTS, List.of(1));
		put(Material.IRON_CHESTPLATE, List.of(1));
		put(Material.IRON_HELMET, List.of(1));
		put(Material.IRON_HOE, List.of(1000, 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009, 1010));
		put(Material.IRON_LEGGINGS, List.of(1));
		put(Material.IRON_PICKAXE, List.of(1000, 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009, 1010));
		put(Material.IRON_SHOVEL, List.of(1000, 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009, 1010));
		put(Material.IRON_SWORD, List.of(1000, 1001, 1002, 1003, 1004, 1005, 1006, 1007, 1008, 1009, 1010));
		put(Material.LEATHER_HORSE_ARMOR, List.of(11000, 13000, 20000, 20001, 20003, 20012, 20018, 20022, 20029, 20031, 20033, 20037, 20058));
		put(Material.MINECART, List.of(207));
		put(Material.MUSHROOM_STEW, List.of(10001));
		put(Material.NETHERITE_BOOTS, List.of(1));
		put(Material.NETHERITE_CHESTPLATE, List.of(1));
		put(Material.NETHERITE_HELMET, List.of(1));
		put(Material.NETHERITE_LEGGINGS, List.of(1));
		put(Material.NETHERITE_PICKAXE, List.of(1010));
		put(Material.PAPER, List.of(4132, 4133, 4134, 4135, 4136, 6042, 6219, 6409, 6415, 6499, 22000));
		put(Material.PLAYER_HEAD, List.of(12));
		put(Material.RED_DYE, List.of(1));
		put(Material.STICK, List.of(16));
		put(Material.STONE_AXE, List.of(1009));
		put(Material.STONE_BUTTON, List.of(214));
	}};

	private static void warn(Material material, int data, Location location) {
		String message = "Could not find new model for custom model data " + data + " on material " + material + " at " + StringUtils.getShortLocationString(location);
		Nexus.warn(message);
		if (ignoredWarns.containsKey(material) && ignoredWarns.get(material).contains(data))
			return;

		if (location != null && location.getWorld().getName().contains("pugmas"))
			PlayerUtils.Dev.WAKKA.send("&e[Saturn] &c" + message);
		else if (Nexus.getEnv() != Env.PROD)
			Chat.Broadcast.ingame()
				.channel(Chat.StaticChannel.GLOBAL)
				.message("&e[Saturn] &c" + message)
				.send();
	}

	public static CustomModel convert(ItemStack item, Location location) {
		if (Model.hasModel(item))
			return of(item);

		return convertLegacy(item.getType(), new ItemBuilder(item).customModelData(), location);
	}

	public static CustomModel convertLegacy(Material material, int data, Location location) {
		if (material == Material.LEATHER_BOOTS)  // keeps converting custom armor to stockings
			return null;

		final CustomModelConfig config = new CustomModelConfigService().get0();
		final var oldModels = config.getOldModels();
		final var newModels = config.getNewModels();
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
		CustomModel model = of(material, data);
		return model == null ? null : model.getItem();
	}

	public static CustomModel of(ItemStack item) {
		if (Nullables.isNullOrAir(item))
			return null;

		return of(item.getType(), Model.of(item));
	}

	public static CustomModel of(String path) {
		if (Nullables.isNullOrEmpty(path))
			return null;

		return ResourcePack.getModels().get(path);
	}

	public static CustomModel of(CustomMaterial material) {
		return of(material.getMaterial(), material.getModel());
	}

	@NotNull
	public String getId() {
		return data;
	}

	public boolean equals(ItemStack itemStack) {
		if (Nullables.isNullOrAir(itemStack))
			return false;
		if (itemStack.getType() != material)
			return false;

		return Objects.equals(Model.of(itemStack), data);
	}

	public ItemStack getItem() {
		final ItemBuilder builder = new ItemBuilder(material)
			.model(data)
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
		CustomModel that = (CustomModel) o;
		return Objects.equals(data, that.data) && material == that.material;
	}

	@Override
	public int compareTo(@NotNull CustomModel other) {
		if (!material.equals(other.getMaterial()))
			return material.compareTo(other.getMaterial());

		return CharSequence.compare(data, other.getData());
	}

	@Data
	public static class CustomModelMeta {
		private String name;
		private List<String> lore;
		private String defaultColor;

		public boolean hasDefaultColor() {
			return !Nullables.isNullOrEmpty(defaultColor);
		}
	}

}
