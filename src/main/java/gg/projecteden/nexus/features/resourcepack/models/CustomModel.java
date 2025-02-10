package gg.projecteden.nexus.features.resourcepack.models;

import com.google.gson.annotations.SerializedName;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.models.files.CustomModelFolder;
import gg.projecteden.nexus.models.custommodels.CustomModelConfig;
import gg.projecteden.nexus.models.custommodels.CustomModelConfigService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
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
	@SerializedName("old_material")
	private Material oldMaterial;
	private Material material; // this should eventually be removed... it doesn't actually matter what item the model is on

	public static final String NBT_KEY = "CustomModelData";
	public static final String ICON = "icon";

	@Getter
	private static final String modelsSubdirectory = "/assets/minecraft/models/";
	@Getter
	private static final String itemsSubdirectory = "/assets/minecraft/items/";

	public static CustomModel of(Material material, String data) {
		return ResourcePack.getModels().values().stream()
				.filter(model -> model.getMaterial() == material && Objects.equals(model.getData(), data))
				.findFirst()
				.orElse(null);
	}

	public static CustomModel ofCustomModelData(Material material, int data) {
		return null; // TODO 1.21.4
	}

	public static CustomModel convert(ItemStack item) {
		if (Model.hasModel(item))
			return convert(item.getType(), Model.of(item));
		return convertLegacy(item.getType(), new ItemBuilder(item).customModelData());
	}

	public static CustomModel convert(Material material, String model) {
		return null; // TODO 1.21.4
	}

	public static CustomModel convertLegacy(Material material, int data) {
		if (material == Material.LEATHER_BOOTS)  // keeps converting custom armor to stockings
			return null;

		final CustomModelConfig config = new CustomModelConfigService().get0();
		final var oldModels = config.getOldModels();
		final var newModels = config.getNewModels();
		if (!oldModels.containsKey(material))
			return null;

		final String model = oldModels.get(material).get(data);
		if (Nullables.isNullOrEmpty(model))
			return null;

		for (var map1 : newModels.entrySet())
			for (var map2 : map1.getValue().entrySet())
				if (model.equals(map2.getValue()))
					return convert(new ItemBuilder(map1.getKey()).customModelData(map2.getKey()).build());

		return null;
	}

	public static ItemStack itemOf(Material material, String data) {
		CustomModel model = of(material, data);
		return model == null ? null : model.getItem();
	}

	public static boolean exists(ItemStack item) {
		return item.getItemMeta().hasCustomModelData();
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
		return folder.getDisplayPath() + "/" + fileName;
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
