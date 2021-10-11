package gg.projecteden.nexus.features.store.perks.autosort.features;

import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.features.store.perks.autosort.AutoSortFeature;
import gg.projecteden.nexus.models.autosort.AutoSortUser;
import gg.projecteden.nexus.models.autosort.AutoSortUser.AutoTrashBehavior;
import gg.projecteden.nexus.models.dumpster.Dumpster;
import gg.projecteden.nexus.models.dumpster.DumpsterService;
import gg.projecteden.nexus.utils.WorldGroup;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

@NoArgsConstructor
public class AutoTrash implements Listener {

	@EventHandler
	public void onPickup(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		AutoSortUser user = AutoSortUser.of(player);
		if (!user.hasFeatureEnabled(AutoSortFeature.AUTOTRASH))
			return;

		if (!Arrays.asList(WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK).contains(WorldGroup.of(player)))
			return;

		ItemStack item = event.getItem().getItemStack();
		ItemMeta meta = item.getItemMeta();
		if (meta.hasDisplayName() || meta.hasLore() || meta.hasEnchants() || CustomModel.exists(item))
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
