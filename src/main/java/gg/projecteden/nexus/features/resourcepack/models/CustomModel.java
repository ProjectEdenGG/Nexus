package gg.projecteden.nexus.features.resourcepack.models;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.models.files.CustomModelFolder;
import gg.projecteden.nexus.features.resourcepack.models.files.ResourcePackOverriddenMaterial;
import gg.projecteden.nexus.features.resourcepack.models.files.ResourcePackOverriddenMaterial.ModelOverride;
import gg.projecteden.nexus.models.custommodels.CustomModelConfig;
import gg.projecteden.nexus.models.custommodels.CustomModelConfigService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Data
@AllArgsConstructor
public class CustomModel implements Comparable<CustomModel> {
	private CustomModelFolder folder;
	private ModelOverride override;
	private Material material;
	private int data;
	private CustomModelMeta meta;
	private String fileName;

	public static final String NBT_KEY = "CustomModelData";
	public static final String ICON = "icon";

	@Getter
	private static final String modelsSubdirectory = "/assets/minecraft/models/";
	@Getter
	private static final String vanillaSubdirectory = modelsSubdirectory + "item";
	@Getter
	private static final String customSubdirectory = modelsSubdirectory + "projecteden";

	public CustomModel(@NonNull CustomModelFolder folder, @NonNull ResourcePackOverriddenMaterial.ModelOverride override, @NonNull Material material) {
		this.folder = folder;
		this.override = override;
		this.material = material;
		this.data = override.getPredicate().getModelId();
		this.meta = override.getMeta();
		this.fileName = override.getFileName();
	}

	public static CustomModel of(Material material, int data) {
		return ResourcePack.getModels().values().stream()
				.filter(model -> model.getMaterial() == material && model.getData() == data)
				.findFirst()
				.orElse(null);
	}

	public static CustomModel convert(ItemStack item) {
		return convert(item.getType(), ModelId.of(item));
	}

	public static CustomModel convert(Material material, int data) {
		if (material == Material.LEATHER_BOOTS) // keeps converting custom armor to stockings
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
					return CustomModel.of(map1.getKey(), map2.getKey());

		return null;
	}

	public static ItemStack itemOf(Material material, int data) {
		CustomModel model = of(material, data);
		return model == null ? null : model.getItem();
	}

	public static boolean exists(ItemStack item) {
		return new NBTItem(item).hasKey(NBT_KEY);
	}

	public static CustomModel of(ItemStack item) {
		if (Nullables.isNullOrAir(item))
			return null;

		return of(item.getType(), ModelId.of(item));
	}

	public static CustomModel of(String path) {
		if (Nullables.isNullOrEmpty(path))
			return null;

		return ResourcePack.getModels().get(path);
	}

	public static CustomModel of(CustomMaterial material) {
		return of(material.getMaterial(), material.getModelId());
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

		final NBTItem nbtItem = new NBTItem(itemStack);
		return nbtItem.hasKey(NBT_KEY) && nbtItem.getInteger(NBT_KEY) == data;
	}

	public ItemStack getItem() {
		final ItemBuilder builder = new ItemBuilder(material)
			.modelId(data)
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
		return data == that.data && material == that.material;
	}

	@Override
	public int compareTo(@NotNull CustomModel other) {
		if (!material.equals(other.getMaterial()))
			return material.compareTo(other.getMaterial());

		return Integer.compare(data, other.getData());
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
