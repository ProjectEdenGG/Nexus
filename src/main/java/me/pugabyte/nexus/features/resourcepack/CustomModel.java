package me.pugabyte.nexus.features.resourcepack;

import de.tr7zw.nbtapi.NBTItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static eden.utils.StringUtils.camelCase;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CustomModel implements Comparable<CustomModel> {
	@NonNull
	private Material material;
	@NonNull
	private int data;
	private String fileName;
	private String name;
	private List<String> lore;

	public static boolean exists(ItemStack item) {
		return new NBTItem(item).hasKey("CustomModelData");
	}

	public ItemStack getItem() {
		return new ItemBuilder(material).customModelData(data).name(name).lore(lore).build();
	}

	public ItemStack getDisplayItem() {
		return new ItemBuilder(material).customModelData(data).name((name == null ? camelCase(fileName) : name)).lore(lore).build();
	}

	@Override
	public int compareTo(@NotNull CustomModel other) {
		if (!material.equals(other.getMaterial()))
			return material.compareTo(other.getMaterial());

		return Integer.compare(data, other.getData());
	}

}
