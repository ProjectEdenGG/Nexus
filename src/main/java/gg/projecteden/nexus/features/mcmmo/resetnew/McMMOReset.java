package gg.projecteden.nexus.features.mcmmo.resetnew;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import gg.projecteden.nexus.features.mcmmo.resetnew.attributes.HeadshotHandler;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.mcmmo.McMMOPrestigeUser;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class McMMOReset extends Feature {

	@Override
	public void onStart() {
		// ACROBATICS
		register(McMMOResetReward.builder().skill(PrimarySkillType.ACROBATICS)
			.name("Rocket Boots")
			.description("Boots that allow you to ascend, hover, and descend in the air at the cost of fuel")
			.item(new ItemStack(Material.GOLDEN_BOOTS) /*TODO*/)
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.ACROBATICS)
			.name("Elytra Template")
			.description("Allows you to combine an elytra and chestplate into one item")
			.icon(new ItemStack(Material.ELYTRA))
			.item(null /*TODO*/)
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.ACROBATICS)
			.name("Bouncy Boots")
			.description("Boots that allow you to bounce on the ground instead of taking fall damage")
			.item(new ItemStack(Material.DIAMOND_BOOTS) /*TODO*/)
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.ACROBATICS)
			/*TODO*/
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.ACROBATICS)
			.enchant(Enchant.GEARS)
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.ACROBATICS)
			.enchant(Enchant.SPRINGS)
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.ACROBATICS)
			.enchant(Enchant.GRACEFUL_STEP)
			.build());

		// ALCHEMY
		register(PermissionReward.builder().skill(PrimarySkillType.ALCHEMY)
			.name("Potion Launcher & Stacking")
			.description("Stack potions in your inventory using /stackpotions, and launch them with this item at enemies")
			.permission("nexus.stackpotions")
			.item(new ItemStack(Material.HOPPER) /*TODO*/)
			.build());
		register(PermissionReward.builder().skill(PrimarySkillType.ALCHEMY)
			.name("Advanced Alchemy License")
			.description("Become a Master Alchemist and learn how to brew level 3 potions, super-extended potions, or extended level 2 potions!")
			.permission("nexus.advancedalchemy")
			.icon(new ItemBuilder(Material.POTION).potionEffect(PotionEffectType.ABSORPTION).build())
			.build());

		// ARCHERY
		register(McMMOResetReward.builder().skill(PrimarySkillType.ARCHERY)
			.name("Helios Bow")
			.description("Fires arrows imbued with solar energy, placing a torch at the impact point")
			.item(new ItemStack(Material.BOW) /*TODO*/)
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.ARCHERY)
			.name("Quiver")
			.description("Stores up to 9 stacks of arrows in a single slot. Bows will pull from this when firing")
			.item(new ItemStack(Material.ARROW) /*TODO*/)
			.build());
		register(AttributeReward.builder().skill(PrimarySkillType.ARCHERY)
			.name("Headshots")
			.description("Unlock the ability to headshot mobs with shots, killing them instantly")
			.icon(new ItemStack(Material.ZOMBIE_HEAD))
			//.checkHook(mcmmo -> mcmmo.canHeadshot()) TODO
			//.unlockHook(mcmmo -> mcmmo.setCanHeadshot(true)) TODO
			.handler(HeadshotHandler.class)
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.ARCHERY)
			/*TODO*/
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.ARCHERY)
			.enchant(Enchant.BARRAGE)
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.ARCHERY)
			.enchant(Enchant.OVERCHARGE)
			.build());

		// AXES
		register(AttributeReward.builder().skill(PrimarySkillType.AXES)
			.name("Faster Equip Time")
			.description("All axes will now charge to their fully equipped state in half the time")
			.icon(new ItemStack(Material.CLOCK))
			/*TODO*/
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.AXES)
			.name("Tomahawk Upgrade")
			.description("Upgrade an existing axe into a tomahawk, allowing you to throw it to damage mobs in front of you")
			.icon(new ItemStack(Material.DIAMOND_AXE)) // TODO
			.item(new ItemStack(Material.DIAMOND_AXE)) // TODO
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.AXES)
			/*TODO*/
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.AXES)
			.enchant(Enchant.BEHEADING)
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.AXES)
			.enchant(Enchant.THOR)
			.build());

		// CROSSBOWS
		register(AttributeReward.builder().skill(PrimarySkillType.CROSSBOWS)
			.name("Accuracy Increase")
			.description("All shots fired from crossbows are much more accurate")
			.icon(new ItemStack(Material.CROSSBOW))
			/*TODO*/
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.CROSSBOWS)
			.name("Harpoon")
			.description("A new ranged weapon that fires tridents at a much higher speed")
			.icon(new ItemStack(Material.TRIDENT))
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.CROSSBOWS)
			/*TODO*/
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.CROSSBOWS)
			.enchant(Enchant.DOUBLE_TAP)
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.CROSSBOWS)
			.enchant(Enchant.RICOCHET)
			.build());

		// EXCAVATION
		register(McMMOResetReward.builder().skill(PrimarySkillType.EXCAVATION)
			.name("Sieve")
			.description("Convert your extra dirt, gravel, and sand to better materials")
			.item(new ItemStack(Material.STRING))
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.EXCAVATION)
			.name("Excavation Zones")
			.description("Define excavation zones which stop falling blocks, give rare drops, and collect dug blocks automatically")
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.EXCAVATION)
			/*TODO*/
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.EXCAVATION)
			.enchant(Enchant.COLUMN_QUAKE)
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.EXCAVATION)
			.enchant(Enchant.MAGNET)
			.build());

		// FISHING
		register(McMMOResetReward.builder().skill(PrimarySkillType.FISHING)
			.name("Speed Boat")
			.description("This boat moves at double the speed of a normal boat")
			.item(new ItemStack(Material.OAK_BOAT)) // TODO
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.FISHING)
			.name("Fishing Orb")
			.description("This orb can be placed down to drastically increase fishing rates around you (re-usable)")
			.item(new ItemStack(Material.PLAYER_HEAD)) // TODO
			.build());
		register(TomeReward.builder()
			/*TODO*/
			.build());

		// HERBALISM
		register(McMMOResetReward.builder().skill(PrimarySkillType.HERBALISM)
			.name("Gnome Catalyst (5)")
			.description("Place this catalyst within a Gnome decoration to enhance nearby crop growth")
			.item(new ItemStack(Material.BONE_MEAL)) // TODO
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.HERBALISM)
			.name("Sprinkler (5)")
			.description("Hydrates soil in a 10x10 area")
			.item(new ItemStack(Material.IRON_INGOT)) // TODO
			.build());
		register(AttributeReward.builder().skill(PrimarySkillType.HERBALISM)
			.name("Healthier Food")
			.description("All food items will saturate you for 1.5x the normal amount")
			.icon(new ItemStack(Material.COOKED_BEEF))
			/*TODO*/
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.HERBALISM)
			.enchant(Enchant.DEMETER)
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.HERBALISM)
			.enchant(Enchant.PLOUGH)
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.HERBALISM)
			.enchant(Enchant.MIDAS_CARROTS) // TODO - Midas' Touch
			.build());

		// MACES
		register(AttributeReward.builder().skill(PrimarySkillType.MACES)
			.name("Mace on a Rope")
			.description("All maces can now be thrown by right click. Spin while thrown to do a spin attack")
			.icon(new ItemStack(Material.LEAD))
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.MACES)
			/*TODO*/
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.MACES)
			.enchant(Enchant.PROPULSION)
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.MACES)
			.enchant(Enchant.GROUND_POUND)
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.MACES)
			.enchant(Enchant.THOR)
			.build());

		// MINING
		register(McMMOResetReward.builder().skill(PrimarySkillType.MINING)
			.name("Beacon Enhancer")
			.description("Place on top of a beacon to double it's range and effect level by 1")
			.icon(new ItemStack(Material.BEACON))
			.item(new ItemStack(Material.BEACON)) // TODO
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.MINING)
			.name("Explosive Device")
			.description("This device places multiple TNT at once in different shapes")
			.item(new ItemBuilder(ItemModelType.BLOCKPARTY_POWERUPS_COLOR_STORM).build())
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.MINING)
			.name("Rock Crusher")
			.description("Crush stone blocks and extract small pieces of valuable materials")
			.icon(new ItemStack(Material.STONE))
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.MINING)
			/*TODO*/
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.MINING)
			.enchant(Enchant.GLOWING)
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.MINING)
			.enchant(Enchant.VEIN_MINER)
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.MINING)
			.enchant(Enchant.MAGNET)
			.build());

		// REPAIR
		register(McMMOResetReward.builder().skill(PrimarySkillType.REPAIR)
			.name("Enchantment Swapper")
			.description("Allows incompatible items to be placed on items and swapped between")
			.icon(new ItemStack(Material.ENCHANTED_BOOK)) // TODO
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.REPAIR)
			.enchant(Enchant.AUTOREPAIR)
			.build());

		// SPEARS
		register(AttributeReward.builder().skill(PrimarySkillType.SPEARS)
			.name("Spear Movement Speed")
			.description("Increase your movement speed while holding a spear")
			.icon(new ItemStack(Material.DIAMOND_SPEAR))
			/*TODO*/
			.build());
		register(AttributeReward.builder().skill(PrimarySkillType.SPEARS)
			.name("Lunge Hunger Buff")
			.description("Lunge no longer takes hunger when activated")
			.icon(new ItemStack(Material.COOKED_BEEF))
			/*TODO*/
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.SPEARS)
			/*TODO*/
			.build());

		// SWORDS
		register(AttributeReward.builder().skill(PrimarySkillType.SWORDS)
			.name("Parrying")
			.description("Unlock the ability to parry attacks with swords, stunning enemies on successful block")
			.icon(new ItemStack(Material.SHIELD))
			/*TODO*/
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.SWORDS)
			.description("This orb can be placed down to give nearby players strength and regeneration for a short period (re-usable)")
			.icon(new ItemStack(Material.PLAYER_HEAD))
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.SWORDS)
			/*TODO*/
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.SWORDS)
			.enchant(Enchant.FROST_ASPECT)
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.SWORDS)
			.enchant(Enchant.ORBSEEKER)
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.SWORDS)
			.enchant(Enchant.BOUNTY)
			.build());

		// TAMING
		register(McMMOResetReward.builder().skill(PrimarySkillType.TAMING)
			.name("Horse Inventory Pet")
			.description("Right click while in your inventory to summon a perfect horse")
			.icon(new ItemStack(Material.HORSE_SPAWN_EGG)) // TODO
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.TAMING)
			.name("Cat Inventory Pet")
			.description("While this pet is in your inventory, you will no longer attract phantoms")
			.icon(new ItemStack(Material.CAT_SPAWN_EGG)) // TODO
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.TAMING)
			.enchant(Enchant.FLOCK_SHEAR)
			.build());

		// TRIDENTS
		register(McMMOResetReward.builder().skill(PrimarySkillType.TRIDENTS)
			.name("Poseidon's Trident")
			.description("Shift + right-click to imbue yourself with Conduit Power, pacify Guardians, and prevent yourself from being afflicted with Mining Fatigue")
			.icon(new ItemStack(Material.TRIDENT)) // TODO
			.build());
		register(AttributeReward.builder().skill(PrimarySkillType.TRIDENTS)
			.name("Sea Affinity")
			.description("Reduce the amount of time you burn for")
			.icon(new ItemStack(Material.WATER_BUCKET))
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.TRIDENTS)
			/*TODO*/
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.TRIDENTS)
			.enchant(Enchant.THOR)
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.TRIDENTS)
			//.enchant(Enchant.UNDERTOW) TODO
			.build());

		// UNARMED
		register(McMMOResetReward.builder().skill(PrimarySkillType.UNARMED)
			.name("Extendo-Arm")
			.description("While in your offset, your block reach is doubled")
			.item(new ItemStack(Material.COPPER_BARS)) // TODO
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.UNARMED)
			.name("Grappling Hook")
			.description("This grappling hook can propel you towards wherever the hook lands, even air!")
			.item(new ItemStack(Material.FISHING_ROD)) // TODO
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.UNARMED)
			/*TODO - ARMOR*/
			.build());

		// WOODCUTTING
		register(AttributeReward.builder().skill(PrimarySkillType.WOODCUTTING)
			.name("Auto Replant Saplings")
			.description("After you chop down a tree, automatically plant a sapling in it's place")
			.icon(new ItemStack(Material.OAK_SAPLING))
			/*TODO*/
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.WOODCUTTING)
			.name("Resin Tap")
			.description("Place on a resin tree to automatically harvest resin")
			.item(new ItemStack(Material.RESIN_BRICK)) // TODO
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.WOODCUTTING)
			.name("Everliving Flower")
			.description("When placed near beehives, bees inside no longer sleep during the night")
			.item(new ItemStack(Material.GOLDEN_DANDELION)) // TODO
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.WOODCUTTING)
			/*TODO*/
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.WOODCUTTING)
			.enchant(Enchant.ENERGIZING)
			.build());
	}

	public void register(McMMOResetReward reward) {

	}

	@Data
	@SuperBuilder
	@NoArgsConstructor
	public static class McMMOResetReward {
		private PrimarySkillType skill;
		private String name;
		private String description;
		private ItemStack item;
		private ItemStack icon;
	}

	@Data
	@SuperBuilder
	@EqualsAndHashCode(callSuper = true)
	public static class PermissionReward extends McMMOResetReward {
		private String permission;
	}

	@Data
	@SuperBuilder
	@EqualsAndHashCode(callSuper = true)
	public static class AttributeReward extends McMMOResetReward {
		private Predicate<McMMOPrestigeUser> checkHook;
		private Consumer<McMMOPrestigeUser> unlockHook;
		private Class<? extends AttributeRewardHandler> handler;
	}

	@Data
	@SuperBuilder
	@EqualsAndHashCode(callSuper = true)
	public static class TomeReward extends McMMOResetReward {
		// TODO TomeType?
	}

	@Data
	@SuperBuilder
	@EqualsAndHashCode(callSuper = true)
	public static class CustomEnchantReward extends McMMOResetReward {
		private Enchantment enchant;
	}

}
