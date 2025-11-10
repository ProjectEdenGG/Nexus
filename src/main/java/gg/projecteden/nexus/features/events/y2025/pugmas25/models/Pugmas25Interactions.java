package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25Command;
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
	public void onClickQuestBoard(PlayerInteractEvent event) {
		if (!Pugmas25.get().isAtEvent(event))
			return;

		Block block = event.getClickedBlock();
		if (Nullables.isNullOrAir(block))
			return;

		if (block.getType() != Material.BARRIER)
			return;

		if (Pugmas25.get().worldguard().getRegionsLikeAt("pugmas25_questboard_[0-9]+", block.getLocation()).isEmpty())
			return;

		PlayerUtils.runCommand(event.getPlayer(), "pugmas25 quest progress");
	}

	@EventHandler
	public void onClickSnow(PlayerInteractEvent event) {
		if (!Pugmas25.get().isAtEvent(event))
			return;

		Player player = event.getPlayer();
		ItemStack tool = event.getItem();
		if (Nullables.isNotNullOrAir(tool) && tool.getType() != snowball.getType())
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

		PlayerUtils.giveItem(player, snowball);
		new SoundBuilder(Sound.BLOCK_SNOW_HIT).volume(0.25).location(event.getInteractionPoint()).play();
	}

}
