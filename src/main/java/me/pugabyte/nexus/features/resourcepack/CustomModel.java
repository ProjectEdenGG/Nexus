package me.pugabyte.nexus.features.resourcepack;

import de.tr7zw.nbtapi.NBTItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static eden.utils.StringUtils.camelCase;
import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;

@Data
@AllArgsConstructor
public class CustomModel implements Comparable<CustomModel> {
	private CustomModelGroup.Override override;
	private Material material;
	private int data;
	private CustomModelMeta meta;
	private String fileName;

	public static final String NBT_KEY = "CustomModelData";

	public CustomModel(@NonNull CustomModelGroup.Override override, @NonNull Material material) {
		this.override = override;
		this.material = material;
		this.data = override.getPredicate().getCustomModelData();
		this.meta = override.getMeta();
		this.fileName = override.getFileName();
	}

	public static CustomModel of(Material material, int data) {
		return ResourcePack.getModels().stream()
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
				.name((meta.getName() == null ? camelCase(fileName) : null))
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
