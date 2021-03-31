package me.pugabyte.nexus.features.minigames.perks.loadouts;

import me.pugabyte.nexus.features.minigames.models.perks.PerkCategory;
import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class DragonSkull extends LoadoutPerk {
	private static final Map<EnumItemSlot, ItemStack> loadout = new HashMap<>();

	static {
		loadout.put(EnumItemSlot.HEAD, new ItemStack(Material.DRAGON_HEAD));
	}

	@Override
	public String getName() {
		return "Dragon Skull";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.DRAGON_HEAD);
	}

	@Override
	public String[] getDescription() {
		return new String[]{"Scare your enemies with",
		                    "the frightening skull",
				            "of a mighty dragon!"};
	}

	@Override
	public PerkCategory getPerkCategory() {
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
