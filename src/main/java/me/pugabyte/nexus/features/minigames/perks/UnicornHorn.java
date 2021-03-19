package me.pugabyte.nexus.features.minigames.perks;

import me.pugabyte.nexus.features.minigames.models.perks.PerkCategory;
import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class UnicornHorn extends LoadoutPerk {
	private static final Map<EnumItemSlot, ItemStack> loadout = new HashMap<>();

	static {
		loadout.put(EnumItemSlot.HEAD, new ItemStack(Material.END_ROD));
	}

	@Override
	public String getName() {
		return "Unicorn Horn";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.END_ROD);
	}

	@Override
	public String[] getDescription() {
		return new String[]{"Become a pretty unicorn",
		                    "with this glowing horn",
				            "on top of your head!"};
	}

	@Override
	public PerkCategory getCategory() {
		return PerkCategory.HAT;
	}

	@Override
	public int getPrice() {
		return 1;
	}

	@Override
	public Map<EnumItemSlot, ItemStack> getLoadout() {
		return loadout;
	}
}
