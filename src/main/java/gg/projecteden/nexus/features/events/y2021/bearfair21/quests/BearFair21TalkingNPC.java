package gg.projecteden.nexus.features.events.y2021.bearfair21.quests;

import gg.projecteden.nexus.features.events.models.Talker.TalkingNPC;
import gg.projecteden.nexus.models.bearfair21.BearFair21User;
import gg.projecteden.nexus.models.bearfair21.BearFair21UserService;
import gg.projecteden.nexus.utils.ItemUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface BearFair21TalkingNPC extends TalkingNPC {
	default List<String> getScript(BearFair21User user) {
		return TalkingNPC.super.getScript(user.getPlayer());
	}

	@Override
	default List<String> getScript(Player player) {
		BearFair21UserService userService = new BearFair21UserService();
		return getScript(userService.get(player));
	}

	@Override
	default List<String> getScript() {
		return null;
	}

	default ItemStack getTool(Player player) {
		return ItemUtils.getTool(player);
	}
}
