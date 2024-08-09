package gg.projecteden.nexus.features.quests;

import gg.projecteden.nexus.features.menus.MenuUtils.NPCShopMenu.NPCShopMenuBuilder;
import gg.projecteden.nexus.features.quests.interactable.InteractableNPC;
import org.bukkit.entity.Player;

public interface QuestShopMenu {

	InteractableNPC getNPC();

	NPCShopMenuBuilder getShopBuilder();

	default NPCShopMenuBuilder getShopMenu() {
		return getShopBuilder().npcId(getNPC().getNpcId()).shopGroup(null);
	}

	default void open(Player player) {
		getShopMenu().open(player);
	}

}
