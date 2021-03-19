package me.pugabyte.nexus.features.minigames.menus.perks;

import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.features.minigames.models.perks.Perk;
import me.pugabyte.nexus.features.minigames.models.perks.PerkOwnerService;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

public abstract class CommonPerksMenu extends MenuUtils {
	protected static final PerkOwnerService service = new PerkOwnerService();

	protected static List<String> getLore(Perk perk) {
		List<String> lore = new ArrayList<>();
		lore.add("&e"+perk.getCategory().toString());
		lore.addAll(Arrays.stream(perk.getDescription()).map(str -> "&3" + str).collect(Collectors.toList()));
		return lore;
	}

	protected static ItemStack getItem(Perk perk, List<String> lore) {
		return new ItemBuilder(perk.getMenuItem()).name("&b"+perk.getName()).lore(lore).build();
	}

	protected static void send(Player player, String message) {
		player.sendMessage(Minigames.PREFIX + colorize(message));
	}
}
