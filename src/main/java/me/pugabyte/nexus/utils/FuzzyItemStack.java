package me.pugabyte.nexus.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class FuzzyItemStack implements Cloneable {
	private Set<Material> materials;
	private int amount;

	public FuzzyItemStack(Material material, int amount) {
		this.materials = Set.of(material);
		this.amount = amount;
	}

	public FuzzyItemStack(Tag<Material> tag, int amount) {
		this.materials = tag.getValues();
		this.amount = amount;
	}

	public static Set<FuzzyItemStack> ofEach(Tag<Material> tag, int amount) {
		return new HashSet<>() {{
			for (Material material : tag.getValues())
				add(new FuzzyItemStack(material, amount));
		}};
	}

	public FuzzyItemStack clone() {
		return new FuzzyItemStack(materials, amount);
	}

}
