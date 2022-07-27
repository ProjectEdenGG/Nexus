package gg.projecteden.nexus.features.listeners;

import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Beehive;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.List;

import static gg.projecteden.nexus.utils.ItemUtils.getTool;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.StringUtils.colorize;

public class Beehives implements Listener {

	public static ItemStack addLore(ItemStack item) {
		if (isNullOrAir(item))
			return item;

		if (!(item.getItemMeta() instanceof BlockStateMeta meta))
			return item;

		if (!(meta.getBlockState() instanceof Beehive beehive))
			return item;

		return new ItemBuilder(item).resetLore().lore(getLore(beehive)).build();
	}

	private static String getLore(Beehive beehive) {
		return "&7Bees: " + beehive.getEntityCount() + " / " + beehive.getMaxEntities();
	}

	@EventHandler
	public void onBeeCatch(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked() instanceof Bee bee))
			return;

		if (event.getHand() != EquipmentSlot.HAND)
			return;

		final Player player = event.getPlayer();
		ItemStack tool = getTool(player);
		if (isNullOrAir(tool))
			return;
		if (!MaterialTag.ALL_BEEHIVES.isTagged(tool.getType()))
			return;

		final BlockStateMeta meta = (BlockStateMeta) tool.getItemMeta();
		final Beehive beehive = (Beehive) meta.getBlockState();
		int max = beehive.getMaxEntities();
		int current = beehive.getEntityCount();

		if (current < max) {
			beehive.addEntity(bee);
			meta.setBlockState(beehive);

			meta.setLore(List.of(colorize(getLore(beehive))));

			if (tool.getAmount() == 1) {
				tool.setItemMeta(meta);
			} else {
				tool = ItemBuilder.oneOf(tool).build();
				player.getInventory().removeItem(tool);
				tool.setItemMeta(meta);
				PlayerUtils.giveItem(player, tool);
			}

			new SoundBuilder(Sound.BLOCK_BEEHIVE_ENTER)
				.location(player.getLocation())
				.category(SoundCategory.BLOCKS)
				.play();
		}
	}
}
