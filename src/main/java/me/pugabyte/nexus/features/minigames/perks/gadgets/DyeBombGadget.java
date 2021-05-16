package me.pugabyte.nexus.features.minigames.perks.gadgets;

import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.features.events.DyeBombCommand;
import me.pugabyte.nexus.features.minigames.models.perks.common.GadgetPerk;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

public class DyeBombGadget extends GadgetPerk {
	@Override
	public String getName() {
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
		ItemStack item = DyeBombCommand.getDyeBomb().clone();
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
		return Time.SECOND.x(2);
	}
}
