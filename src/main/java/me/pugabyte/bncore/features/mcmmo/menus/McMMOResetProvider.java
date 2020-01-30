package me.pugabyte.bncore.features.mcmmo.menus;

import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.datatypes.skills.SkillType;
import com.gmail.nossr50.util.player.UserManager;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.Getter;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.utils.ItemStackBuilder;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class McMMOResetProvider extends MenuUtils implements InventoryProvider {

	@Getter
	public enum SKILL {
		SWORDS(1, 3, Material.DIAMOND_SWORD, "A frozen sword that slows your enemies and may trap them in ice"),
		MINING(1, 5, Material.DIAMOND_PICKAXE, "Any helmet of your choice that gives you night vision for deep mining expeditions"),
		EXCAVATION(2, 2, Material.DIAMOND_SPADE, "A shovel that hastens the user after every block broken"),
		AXES(2, 4, Material.DIAMOND_AXE, "The power of Thor is imbued in this axe, giving you a chance of smiting your target with lightning"),
		HERBALISM(2, 6, Material.DIAMOND_HOE, "The boots of the druid grant the users speed and regeneration when they stand on dirt or grass"),
		FISHING(3, 1, Material.FISHING_ROD, "A godly rod that increases your catch"),
		ACROBATICS(3, 3, Material.DIAMOND_BOOTS, "Jump higher and run faster with these boots"),
		REPAIR(3, 5, Material.ANVIL, "Want an item that auto repairs itself? Mending without xp? Thats what you get here, a chance to add that enchantment to one item of your choice!"),
		ARCHERY(3, 7, Material.BOW, "Now you can be the wither- enemies take wither damage for a short period from every successful hit"),
		TAMING(4, 0, Material.BONE, "One horse, your favorite color, max stats. Simple, No?"),
		WOODCUTTING(4, 2, Material.WOOD, "For every log or plank broken this shiny new axe will give you a short burst of haste"),
		UNARMED(4, 5, Material.ROTTEN_FLESH, "Punching your enemies to death can be dangerous work, this bandage will help you heal"),
		ALCHEMY(4, 8, Material.SPLASH_POTION, "You want to throw potions mate? Why not launch them from this hopper? Watch your potions soar like they were shot from a bow to hit friend and foe alike from afar!");

		int row, column;
		Material material;
		String rewardDescription;

		SKILL(int row, int column, Material material, String rewardsDescription) {
			this.row = row;
			this.column = column;
			this.material = material;
			this.rewardDescription = rewardsDescription;
		}
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		McMMOPlayer mcmmoPlayer = UserManager.getPlayer(player);
		for (SKILL skill : SKILL.values()) {
			ItemStack item = new ItemStackBuilder(skill.getMaterial())
					.name("&c" + Utils.camelCase(skill.name()))
					.lore("&6Level: " + mcmmoPlayer.getSkillLevel(SkillType.valueOf(skill.name())) +
							"|| ||&e&lReward:" +
							"||&r$10,000" +
							"||" + skill.getRewardDescription())
					.build();
			if (mcmmoPlayer.getSkillLevel(SkillType.valueOf(skill.name())) >= 100) addGlowing(item);
			contents.set(skill.getRow(), skill.getColumn(),
					ClickableItem.empty(item));
		}
		// all --> replace "<power_level>" with the players power level
	}

	@Override
	public void update(Player player, InventoryContents contents) {

	}
}
