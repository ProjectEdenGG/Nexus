package gg.projecteden.nexus.features.shops.update;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;

public class ShopDisabler implements Listener {

	public ShopDisabler() {
		Nexus.registerListener(this);
	}

	public boolean isShopSign(Block block) {
		if (block == null) return false;
		if (!MaterialTag.SIGNS.isTagged(block.getType())) return false;

		Sign sign = (Sign) block.getState();
		return Arrays.asList("[Trade]", "[Arrow Trade]", "[Potion Trade]", "[Ench Trade]").contains(stripColor(sign.getLine(0)));
	}

	public void tell(Player player) {
		PlayerUtils.send(player, StringUtils.getPrefix("Shops") + "Shop signs are no longer supported, use &c/shops&3. Ask an admin to convert signs for you");
	}

	@EventHandler
	public void onSignClick(PlayerInteractEvent event) {
		if (!ActionGroup.CLICK_BLOCK.applies(event)) return;
		if (!isShopSign(event.getClickedBlock())) return;

		event.setCancelled(true);
		tell(event.getPlayer());
	}

	@EventHandler
	public void onBreakSign(BlockBreakEvent event) {
		if (!isShopSign(event.getBlock())) return;

		event.setCancelled(true);
		tell(event.getPlayer());
	}

	@EventHandler
	public void onBreakSignSupport(BlockBreakEvent event) {
		Block up = event.getBlock().getRelative(BlockFace.UP);
		if (isShopSign(up) && MaterialTag.STANDING_SIGNS.isTagged(up.getType())) {
			event.setCancelled(true);
			tell(event.getPlayer());
			return;
		}

		for (BlockFace blockFace : Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)) {
			Block relative = event.getBlock().getRelative(blockFace);
			if (!MaterialTag.WALL_SIGNS.isTagged(relative.getType()))
				continue;

			WallSign data = (WallSign) relative.getBlockData();
			if (data.getFacing() == blockFace.getOppositeFace())
				continue;

			if (!isShopSign(relative))
				continue;

			event.setCancelled(true);
			tell(event.getPlayer());
			return;
		}
	}
}
