package me.pugabyte.bncore.features.mcmmo.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.utils.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class McMMOResetProvider extends MenuUtils implements InventoryProvider {

	String powerLevel = "&6Power Level:  <power_level>/1300";
	ItemStack all = new ItemStackBuilder(Material.BEACON).name("&cAll Skills").lore(powerLevel + "||&f||&e&lReward:||" +
			"&f- $150,000||&f- All normal rewards||" + "&f- When your health gets low, this breastplate will give you the " +
			"strength of an angry barbarian!").build();
	ItemStack reset = new ItemStackBuilder(Material.BARRIER).name("&c&lReset all with &4no reward").build();

	String skillLevel = "&6Level:  <skill_level>";
	String moneyReward = "&e&lReward:||&f$10,000";
	ItemStack swords = new ItemStackBuilder(Material.DIAMOND_SWORD).name("&cSwords").lore(skillLevel + "||" + moneyReward +
			"||A frozen sword that slows your enemies and may trap them in ice").build();
	ItemStack mining = new ItemStackBuilder(Material.DIAMOND_PICKAXE).name("&cMining").lore(skillLevel + "||" + moneyReward +
			"||Any helmet of your choice that gives you night vision for deep mining expeditions").build();
	ItemStack excavation = new ItemStackBuilder(Material.DIAMOND_SPADE).name("&cExcavation").lore(skillLevel + "||" + moneyReward +
			"||A shovel that hastens the user after every block broken").build();
	ItemStack axes = new ItemStackBuilder(Material.DIAMOND_AXE).name("&cAxes").lore(skillLevel + "||" + moneyReward +
			"||The power of Thor is imbued in this axe, giving you a chance of smiting your target with lightning").build();
	ItemStack herbalism = new ItemStackBuilder(Material.DIAMOND_HOE).name("&cHerbalism").lore(skillLevel + "||" + moneyReward +
			"||The boots of the druid grant the users speed and regeneration when they stand on dirt or grass").build();
	ItemStack fishing = new ItemStackBuilder(Material.FISHING_ROD).name("&cFishing").lore(skillLevel + "||" + moneyReward +
			"||A godly rod that increases your catch").build();
	ItemStack acrobatics = new ItemStackBuilder(Material.DIAMOND_BOOTS).name("&cAcrobatics").lore(skillLevel + "||" + moneyReward +
			"||Jump higher and run faster with these boots").build();
	ItemStack repair = new ItemStackBuilder(Material.ANVIL).name("&cRepair").lore(skillLevel + "||" + moneyReward +
			"||Want an item that auto repairs itself? Mending without xp? Thats what you get here, a chance to add that enchantment " +
			"to one item of your choice!").build();
	ItemStack archery = new ItemStackBuilder(Material.BOW).name("&cArchery").lore(skillLevel + "||" + moneyReward +
			"||Now you can be the wither- enemies take wither damage for a short period from every successful hit").build();
	ItemStack taming = new ItemStackBuilder(Material.BONE).name("&cTaming").lore(skillLevel + "||" + moneyReward +
			"||One horse, your favorite color, max stats. Simple, No?").build();
	ItemStack woodcutting = new ItemStackBuilder(Material.WOOD).name("&cWoodcutting").lore(skillLevel + "||" + moneyReward +
			"||For every log or plank broken this shiny new axe will give you a short burst of haste").build();
	ItemStack unarmed = new ItemStackBuilder(Material.ROTTEN_FLESH).name("&cUnarmed").lore(skillLevel + "||" + moneyReward +
			"||Punching your enemies to death can be dangerous work, this bandage will help you heal").build();
	ItemStack alchemy = new ItemStackBuilder(Material.SPLASH_POTION).name("&cAlchemy").lore(skillLevel + "||" + moneyReward +
			"||You want to throw potions mate? Why not launch them from this hopper? Watch your potions soar like they were " +
			"shot from a bow to hit friend and foe alike from afar!").build();

	@Override
	public void init(Player player, InventoryContents contents) {
		// all --> replace "<power_level>" with the players power level
		// skills --> replace "<skill_level>" with the players level for that skill
		// set glowing to items that the player can reset

		contents.set(0, 4, ClickableItem.empty(all));
		contents.set(1, 3, ClickableItem.empty(swords));
		contents.set(1, 5, ClickableItem.empty(mining));
		contents.set(2, 2, ClickableItem.empty(excavation));
		contents.set(2, 4, ClickableItem.empty(axes));
		contents.set(2, 6, ClickableItem.empty(herbalism));
		contents.set(3, 1, ClickableItem.empty(fishing));
		contents.set(3, 3, ClickableItem.empty(acrobatics));
		contents.set(3, 5, ClickableItem.empty(repair));
		contents.set(3, 7, ClickableItem.empty(archery));
		contents.set(4, 0, ClickableItem.empty(taming));
		contents.set(4, 2, ClickableItem.empty(woodcutting));
		contents.set(4, 5, ClickableItem.empty(unarmed));
		contents.set(4, 8, ClickableItem.empty(alchemy));
		contents.set(5, 4, ClickableItem.empty(reset));
	}

	@Override
	public void update(Player player, InventoryContents contents) {

	}
}
