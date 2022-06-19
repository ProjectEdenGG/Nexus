package gg.projecteden.nexus.features.recipes.functionals.birdhouses;

import gg.projecteden.nexus.features.recipes.functionals.birdhouses.Birdhouse.BirdhouseType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@NoArgsConstructor
public class BirdhouseListener implements Listener {

	@EventHandler
	public void onPlaceBirdhouse(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof ItemFrame itemFrame))
			return;

		Tasks.wait(1, () -> {
			if (!itemFrame.isValid())
				return;

			final ItemStack item = itemFrame.getItem();

			if (isNullOrAir(item))
				return;

			if (item.getType() != Material.PAPER)
				return;

			BirdhouseType type = BirdhouseType.of(item);
			if (type == null)
				return;

			int customModelData = type.baseModel();

			final BlockFace face = itemFrame.getAttachedFace();

			if (face == BlockFace.UP)
				customModelData += 2;
			else if (face != BlockFace.DOWN)
				customModelData += 1;

			itemFrame.setSilent(true);
			itemFrame.setItem(new ItemBuilder(item).resetName().customModelData(customModelData).build());
			itemFrame.setSilent(false);
		});
	}

	@EventHandler
	public void onItemSpawn(ItemSpawnEvent event) {
		final ItemStack item = event.getEntity().getItemStack();
		if (isNullOrAir(item))
			return;

		if (item.getType() != Material.PAPER)
			return;

		BirdhouseType type = BirdhouseType.of(item);
		if (type == null)
			return;

		event.getEntity().setItemStack(new ItemBuilder(type.getDisplayItem()).amount(item.getAmount()).build());
	}

}
