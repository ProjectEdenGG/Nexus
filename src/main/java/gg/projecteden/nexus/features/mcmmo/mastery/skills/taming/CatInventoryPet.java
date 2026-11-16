package gg.projecteden.nexus.features.mcmmo.mastery.skills.taming;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks.BackpackTier;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CatInventoryPet {

	public static final ItemStack ITEM = new ItemBuilder(ItemModelType.CAT_INVENTORY_PET)
		.maxStackSize(1)
		.name("&eCat Pet")
		.lore("&3Prevents phantoms from spawning")
		.build();

	static {
		Tasks.repeat(0, TickTime.MINUTE.x(1), () -> {
			for (Player player : OnlinePlayers.where(p -> WorldGroup.of(p) == WorldGroup.SURVIVAL).get()) {
				if (scanInventory(player.getInventory().getContents()))
					player.setTimeSinceLastRest(0);
			}
		});
	}

	private static boolean scanInventory(ItemStack[] inventory) {
		for (int i = 0; i < inventory.length; i++) {
			ItemStack item = inventory[i];
			if (ItemUtils.isModelMatch(item, ITEM))
				return true;
			if (Backpacks.isBackpack(item) && scanInventory(ItemUtils.getNBTContentsOfNonInventoryItem(item, BackpackTier.of(item).getRows() * 9).toArray(new ItemStack[0])))
				return true;
		}
		return false;
	}

}
