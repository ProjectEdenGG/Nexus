package gg.projecteden.nexus.features.store.perks.inventory.autoinventory.features;

import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.nexus.features.itemtags.ItemTagsUtils;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.features.store.perks.inventory.autoinventory.AutoInventoryFeature;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUser;
import gg.projecteden.nexus.models.autoinventory.AutoInventoryUser.AutoTrashBehavior;
import gg.projecteden.nexus.models.dumpster.Dumpster;
import gg.projecteden.nexus.models.dumpster.DumpsterService;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
public class AutoTrash implements Listener {

	@EventHandler
	public void onPickup(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		AutoInventoryUser user = AutoInventoryUser.of(player);
		if (!user.hasFeatureEnabled(AutoInventoryFeature.AUTOTRASH))
			return;

		if (!Arrays.asList(WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK).contains(WorldGroup.of(player)))
			return;

		ItemStack item = event.getItem().getItemStack();
		ItemMeta meta = item.getItemMeta();
		List<String> lore = item.clone().getLore();
		if (meta.hasLore() && !Nullables.isNullOrEmpty(lore)) {
			ItemTagsUtils.clearTags(lore);
			if (lore.size() > 0)
				return;
		}

		if (meta.hasDisplayName() || meta.hasEnchants() || CustomModel.exists(item))
			return;

		if (!user.getAutoTrashInclude().contains(item.getType()))
			return;

		event.setCancelled(true);
		if (user.getAutoTrashBehavior() == AutoTrashBehavior.TRASH) {
			DumpsterService dumpsterService = new DumpsterService();
			Dumpster dumpster = dumpsterService.get0();

			dumpster.add(item);
			dumpsterService.save(dumpster);

			event.getItem().remove();
		}
	}

}
