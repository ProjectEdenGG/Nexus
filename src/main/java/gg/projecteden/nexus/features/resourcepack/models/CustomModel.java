package gg.projecteden.nexus.features.resourcepack.models;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.models.files.CustomModelFolder;
import gg.projecteden.nexus.features.resourcepack.models.files.CustomModelGroup;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static gg.projecteden.nexus.utils.ItemUtils.isNullOrAir;
import static gg.projecteden.utils.StringUtils.camelCase;
import static gg.projecteden.utils.StringUtils.isNullOrEmpty;

@Data
@AllArgsConstructor
public class CustomModel implements Comparable<CustomModel> {
	private CustomModelFolder folder;
	private CustomModelGroup.Override override;
	private Material material;
	private int data;
	private CustomModelMeta meta;
	private String fileName;

	public static final String NBT_KEY = "CustomModelData";
	public static final String ICON = "icon";

	public CustomModel(@NonNull CustomModelFolder folder, @NonNull CustomModelGroup.Override override, @NonNull Material material) {
		this.folder = folder;
		this.override = override;
		this.material = material;
		this.data = override.getPredicate().getCustomModelData();
		this.meta = override.getMeta();
		this.fileName = override.getFileName();
	}

	public static CustomModel of(Material material, int data) {
		return ResourcePack.getModels().values().stream()
				.filter(model -> model.getMaterial() == material && model.getData() == data)
				.findFirst()
				.orElse(null);
	}

	public static ItemStack itemOf(Material material, int data) {
		CustomModel model = of(material, data);
		return model == null ? null : model.getItem();
	}

	public static boolean exists(ItemStack item) {
		return new NBTItem(item).hasKey(NBT_KEY);
	}

	public static CustomModel of(ItemStack item) {
		if (isNullOrAir(item))
			return null;

		return of(item.getType(), getModelId(item));
	}

	public static CustomModel of(String path) {
		if (isNullOrEmpty(path))
			return null;

		return ResourcePack.getModels().get(path);
	}

	@NotNull
	public String getId() {
		return folder.getDisplayPath() + "/" + fileName;
	}

	public static Integer getModelId(ItemStack item) {
		if (isNullOrAir(item))
			return null;

		return new NBTItem(item).getInteger(NBT_KEY);
	}

	public boolean equals(ItemStack itemStack) {
		if (isNullOrAir(itemStack))
			return false;
		if (itemStack.getType() != material)
			return false;

		final NBTItem nbtItem = new NBTItem(itemStack);
		return nbtItem.hasKey(NBT_KEY) && nbtItem.getInteger(NBT_KEY) == data;
	}

	public ItemStack getItem() {
		return new ItemBuilder(material)
				.customModelData(data)
				.name(meta.getName())
				.lore(meta.getLore())
				.build();
	}

	public ItemStack getDisplayItem() {
		return new ItemBuilder(getItem())
				.name(isNullOrEmpty(meta.getName()) ? camelCase(fileName) : meta.getName())
				.build();
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
	}

}
