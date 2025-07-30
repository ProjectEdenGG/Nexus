package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Pugmas25Interactions implements Listener {

	private static final ItemStack snowball = new ItemStack(Material.SNOWBALL, 1);

	public Pugmas25Interactions() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		if (!Pugmas25.get().isAtEvent(event))
			return;

		Player player = event.getPlayer();
		ItemStack tool = player.getInventory().getItemInMainHand();
		if (Nullables.isNotNullOrAir(tool) && tool.getType() != Material.SNOWBALL)
			return;

		Block block = event.getClickedBlock();
		if (Nullables.isNullOrAir(block) || event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (block.getType() != Material.SNOW && block.getType() != Material.SNOW_BLOCK)
			return;

		event.setCancelled(true);
		player.swingMainHand();

		if (!PlayerUtils.hasRoomFor(player, snowball))
			return;

		PlayerUtils.giveItem(player, new ItemStack(Material.SNOWBALL));
		new SoundBuilder(Sound.BLOCK_SNOW_HIT).volume(0.25).location(event.getInteractionPoint()).play();
	}

}
