package gg.projecteden.nexus.features.itemtags;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Material;

@Data
@NoArgsConstructor
public class RarityArgs {
	Rarity rarity;
	int raritySum = 0;

	Condition condition;
	int conditionSum = 0;

	boolean isArmor;
	Material material;
	int materialSum = 0;

	int vanillaEnchantsSum = 0;
	boolean aboveVanillaEnchants;

	boolean conflictingEnchants;
	boolean incompatibleEnchants;

	int customEnchantsSum = 0;

	public boolean isCraftable() {
		return customEnchantsSum <= 0 && !aboveVanillaEnchants && !conflictingEnchants && !incompatibleEnchants;
	}

	public int getTotalSum() {
		return materialSum + vanillaEnchantsSum + customEnchantsSum + conditionSum;
	}
}
