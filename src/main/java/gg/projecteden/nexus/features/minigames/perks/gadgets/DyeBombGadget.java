package gg.projecteden.nexus.features.minigames.perks.gadgets;

import gg.projecteden.nexus.features.events.DyeBombCommand;
import gg.projecteden.nexus.features.minigames.models.perks.common.GadgetPerk;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static gg.projecteden.nexus.utils.StringUtils.colorize;

public class DyeBombGadget implements GadgetPerk {
	@Override
	public @NotNull String getName() {
		return "Dye Bomb";
	}

	@Override
	public @NotNull String getDescription() {
		return "Show your friends your love by throwing colorful bombs at them";
	}

	@Override
	public int getPrice() {
		return 30;
	}

	@Override
	public ItemStack getItem() {
		ItemStack item = DyeBombCommand.getDyeBomb();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(colorize("&3Dye Bomb"));
		item.setItemMeta(meta);
		item.setLore(new ArrayList<>());
		return item;
	}

	@Override
	public boolean cancelEvent() {
		return false;
	}

	@Override
	public int getCooldown() {
		return TickTime.SECOND.x(2);
	}

	@Override
	public void useGadget(Player player) {}
}
