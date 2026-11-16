package gg.projecteden.nexus.features.mcmmo.mastery.skills.acrobatics;

import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ElytraTemplate {

	// Recipe registration at CustomRecipes#smithingRecipes
	public static final ItemStack ITEM = new ItemBuilder(Material.PAPER).maxStackSize(1)
		.model("survival/mcmmoreset/skills/acrobatics/elytra_template")
		.name("&eElytra Template")
		.build();

}
