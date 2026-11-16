package gg.projecteden.nexus.features.mcmmo.mastery;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.crates.gemcrafter.GemCommand;
import gg.projecteden.nexus.features.crates.gemcrafter.TomeItem;
import gg.projecteden.nexus.features.crates.gemcrafter.TomeItem.TomeLevel;
import gg.projecteden.nexus.features.crates.gemcrafter.TomeItem.TomeType;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.acrobatics.ElytraTemplate;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.acrobatics.SlimeBoots;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.alchemy.AdvancedAlchemyHandler;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.alchemy.PotionLauncher;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.archery.HeadshotHandler;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.archery.HeliosBow;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.archery.Quiver;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.axes.AxeEquipTimeHandler;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.axes.TomahawkHandler;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.crossbows.AccuracyIncrease;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.crossbows.turret.TurretManager;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.fishing.FishingOrb;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.fishing.SpeedBoat;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.herbalism.HealthierFood;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.mining.ExplosiveDevice;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.repair.ExperienceBottler;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.spear.LungeHungerBuff;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.spear.SpearMovementSpeed;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.swords.CombatOrb;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.swords.Parrying;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.taming.CatInventoryPet;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.taming.HorseInventoryPet;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.tridents.PoseidonsTrident;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.tridents.SeaAffinity;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.unarmed.ExtendoArm;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.unarmed.GrapplingHook;
import gg.projecteden.nexus.features.mcmmo.mastery.skills.woodcutting.AutoReplantSaplings;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.BackpackCommand;
import gg.projecteden.nexus.features.recipes.functionals.backpacks.Backpacks.BackpackTier;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.framework.features.Features;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.LuckPermsUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.SuperBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Environments(value = Env.UPDATE)
public class McMMOPrestige extends Feature {

	public static McMMOPrestige get() {
		return Features.get(McMMOPrestige.class);
	}

	@Getter
	private final List<McMMOResetReward> rewards = new ArrayList<>();

	@Override
	public void onStart() {
		Nexus.registerListener(new McMMOPrestigeListener());

		// ACROBATICS
		register(McMMOResetReward.builder().skill(PrimarySkillType.ACROBATICS)
			.name("Elytra Template")
			.description("Allows you to combine an elytra and chestplate into one item in a smithing table")
			.icon(new ItemStack(Material.ELYTRA))
			.item(ElytraTemplate.ITEM)
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.ACROBATICS)
			.name("Slime Boots")
			.description("Boots that allow you to bounce on the ground instead of taking fall damage")
			.item(SlimeBoots.ITEM)
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.ACROBATICS)
			.tome(TomeType.BOOTS)
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
			.description("Stack potions in your inventory using &e/stackpotions&3, and launch them with this item at enemies")
			.permission("nexus.mcmmo.alchemy.stack_potions")
			.item(PotionLauncher.ITEM)
			.build());
		register(PermissionReward.builder().skill(PrimarySkillType.ALCHEMY)
			.name("Advanced Alchemy License")
			.description("Become a Master Alchemist and learn how to brew level 3 potions, super-extended potions, or extended level 2 potions!")
			.permission(AdvancedAlchemyHandler.PERMISSION)
			.handler(AdvancedAlchemyHandler.class)
			.icon(new ItemBuilder(Material.POTION).build())
			.build());

		// ARCHERY
		register(McMMOResetReward.builder().skill(PrimarySkillType.ARCHERY)
			.name("Helios Bow")
			.description("Fires arrows imbued with solar energy, placing a torch at the impact point")
			.item(HeliosBow.getItem())
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.ARCHERY)
			.name("Quiver")
			.description("Stores up to 9 stacks of arrows in a single slot. Bows will pull from this when firing")
			.item(Quiver.get())
			.onGive(item -> BackpackCommand.fix(item, BackpackTier.QUIVER))
			.build());
		register(PermissionReward.builder().skill(PrimarySkillType.ARCHERY)
			.name("Headshots")
			.description("Unlock the ability to headshot mobs with shots, dealing double damage")
			.permission(HeadshotHandler.PERMISSION)
			.icon(new ItemStack(Material.ZOMBIE_HEAD))
			.handler(HeadshotHandler.class)
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.ARCHERY)
			.tome(TomeType.BOWS)
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.ARCHERY)
			.enchant(Enchant.BARRAGE)
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.ARCHERY)
			.enchant(Enchant.OVERCHARGE)
			.build());

		// AXES
		register(PermissionReward.builder().skill(PrimarySkillType.AXES)
			.name("Faster Equip Time")
			.description("All axes will now charge to their fully equipped state in half the time")
			.icon(new ItemStack(Material.CLOCK))
			.permission(AxeEquipTimeHandler.PERMISSION)
			.handler(AxeEquipTimeHandler.class)
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.AXES)
			.name("Tomahawk Upgrade")
			.description("Upgrade an existing axe into a tomahawk, allowing you to throw it to damage mobs in front of you")
			.icon(new ItemStack(Material.DIAMOND_AXE))
			.item(TomahawkHandler.TEMPLATE)
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.AXES)
			.tome(TomeType.MELEE)
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.AXES)
			.enchant(Enchant.BEHEADING)
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.AXES)
			.enchant(Enchant.THOR)
			.build());

		// CROSSBOWS
		register(PermissionReward.builder().skill(PrimarySkillType.CROSSBOWS)
			.name("Accuracy Increase")
			.description("All shots fired from crossbows are much more accurate")
			.icon(new ItemStack(Material.CROSSBOW))
			.handler(AccuracyIncrease.class)
			.permission(AccuracyIncrease.PERMISSION)
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.CROSSBOWS)
			.name("Turret (4)")
			.description("A placeable ranged weapon that automatically fires at nearby mobs")
			.icon(TurretManager.ITEM)
			.item(new ItemBuilder(TurretManager.ITEM).amount(4).build())
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.CROSSBOWS)
			.tome(TomeType.CROSSBOWS)
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
			.item(DecorationType.SIEVE.getConfig().getItem())
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.EXCAVATION)
			.tome(TomeType.TOOLS)
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
			.item(SpeedBoat.ITEM)
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.FISHING)
			.name("Fishing Orb")
			.description("This orb can be placed down to drastically increase fishing rates around you (re-usable)")
			.item(new FishingOrb().getItem())
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.FISHING)
			.tome(TomeType.FISHING_RODS)
			.build());

		// HERBALISM
		register(McMMOResetReward.builder().skill(PrimarySkillType.HERBALISM)
			.name("Sprinkler (5)")
			.description("Hydrates soil and increases crop growth rate up to 10 blocks away")
			.item(new ItemBuilder(ItemModelType.SPRINKLER).amount(5).name("&eSprinkler").build())
			.build());
		register(PermissionReward.builder().skill(PrimarySkillType.HERBALISM)
			.name("Healthier Food")
			.description("All food items will saturate you for 1.5x the normal amount")
			.icon(new ItemStack(Material.COOKED_BEEF))
			.handler(HealthierFood.class)
			.permission(HealthierFood.PERMISSION)
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
		register(TomeReward.builder().skill(PrimarySkillType.MACES)
			.tome(TomeType.MACES)
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
			.description("Place on top of a beacon to double its range and effect level by 1")
			.icon(new ItemStack(Material.BEACON))
			.item(DecorationType.BEACON_ENHANCER.getConfig().getItem())
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.MINING)
			.name("Explosive Device")
			.description("This device places multiple TNT at once in different shapes")
			.item(ExplosiveDevice.ITEM)
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.MINING)
			.name("Rock Crusher")
			.description("Crush stone blocks and extract small pieces of valuable materials")
			.item(DecorationType.ROCK_CRUSHER.getConfig().getItem())
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.MINING)
			.tome(TomeType.TOOLS)
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
			.name("Experience Bottler")
			.description("Store XP from your bar and retrieve it at any time")
			.item(ExperienceBottler.ITEM)
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.REPAIR)
			.tome(TomeType.DURABILITY)
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.REPAIR)
			.enchant(Enchant.AUTOREPAIR)
			.build());

		// SPEARS
		register(PermissionReward.builder().skill(PrimarySkillType.SPEARS)
			.name("Spear Movement Speed")
			.description("Increase your movement speed while holding a spear")
			.icon(new ItemStack(Material.DIAMOND_SPEAR))
			.permission(SpearMovementSpeed.PERMISSION)
			.handler(SpearMovementSpeed.class)
			.build());
		register(PermissionReward.builder().skill(PrimarySkillType.SPEARS)
			.name("Lunge Hunger Buff")
			.description("Lunge no longer takes hunger when activated")
			.icon(new ItemStack(Material.COOKED_BEEF))
			.permission(LungeHungerBuff.PERMISSION)
			.handler(LungeHungerBuff.class)
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.SPEARS)
			.tome(TomeType.MELEE)
			.build());

		// SWORDS
		register(PermissionReward.builder().skill(PrimarySkillType.SWORDS)
			.name("Parrying")
			.description("Unlock the ability to parry attacks with swords, stunning enemies on successful block")
			.icon(new ItemStack(Material.SHIELD))
			.handler(Parrying.class)
			.permission(Parrying.PERMISSION)
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.SWORDS)
			.name("Combat Orb")
			.description("This orb can be placed down to give nearby players strength and regeneration for a short period (re-usable)")
			.item(new CombatOrb().getItem())
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.SWORDS)
			.tome(TomeType.MELEE)
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
			.item(HorseInventoryPet.ITEM)
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.TAMING)
			.name("Cat Inventory Pet")
			.description("While this pet is in your inventory, you will no longer attract phantoms")
			.item(CatInventoryPet.ITEM)
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.TAMING)
			.enchant(Enchant.FLOCK_SHEAR)
			.build());

		// TRIDENTS
		register(McMMOResetReward.builder().skill(PrimarySkillType.TRIDENTS)
			.name("Poseidon's Trident")
			.description("Shift + right-click to imbue yourself with Conduit Power, pacify Guardians, and prevent yourself from being afflicted with Mining Fatigue")
			.item(PoseidonsTrident.ITEM)
			.build());
		register(PermissionReward.builder().skill(PrimarySkillType.TRIDENTS)
			.name("Sea Affinity")
			.description("Reduce the amount of time you burn for")
			.permission(SeaAffinity.PERMISSION)
			.icon(new ItemStack(Material.WATER_BUCKET))
			.handler(SeaAffinity.class)
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.TRIDENTS)
			.tome(TomeType.MELEE)
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.TRIDENTS)
			.enchant(Enchant.THOR)
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.TRIDENTS)
			.enchant(Enchant.UNDERTOW)
			.build());

		// UNARMED
		register(McMMOResetReward.builder().skill(PrimarySkillType.UNARMED)
			.name("Extendo-Arm")
			.description("While in your off hand, your block reach is doubled")
			.item(ExtendoArm.ITEM)
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.UNARMED)
			.name("Grappling Hook")
			.description("This grappling hook can propel you towards wherever the hook lands, even air!")
			.item(GrapplingHook.ITEM)
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.UNARMED)
			.tome(TomeType.ARMOR)
			.build());

		// WOODCUTTING
		register(PermissionReward.builder().skill(PrimarySkillType.WOODCUTTING)
			.name("Auto Replant Saplings")
			.description("After you chop down a tree, automatically plant a sapling in it's place")
			.icon(new ItemStack(Material.OAK_SAPLING))
			.permission(AutoReplantSaplings.PERMISSION)
			.handler(AutoReplantSaplings.class)
			.build());
		register(McMMOResetReward.builder().skill(PrimarySkillType.WOODCUTTING)
			.name("Resin Tap (5)")
			.description("Place on a resin tree to automatically harvest resin")
			.item(DecorationType.RESIN_TAP.getConfig().getItem().clone().add(4))
			.build());
		register(TomeReward.builder().skill(PrimarySkillType.WOODCUTTING)
			.tome(TomeType.TOOLS)
			.build());
		register(CustomEnchantReward.builder().skill(PrimarySkillType.WOODCUTTING)
			.enchant(Enchant.ENERGIZING)
			.build());
	}

	@SneakyThrows
	public void register(McMMOResetReward reward) {
		if (reward instanceof PermissionReward permissionReward) {
			if (permissionReward.getHandler() != null) {
				Constructor constructor = permissionReward.getHandler().getConstructor();
				Nexus.registerListener((Listener) constructor.newInstance());
			}
		}

		rewards.add(reward);
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
		private Function<ItemStack, ItemStack> onGive;

		public boolean give(Player player, boolean admin) {
			ItemStack item = this.item.clone();
			if (onGive != null)
				item = onGive.apply(item);
			player.getInventory().addItem(item);
			player.closeInventory();
			return true;
		}

		public String id() {
			return name.replaceAll("\\p{P}", "").replace(' ', '_').toLowerCase();
		}
	}

	@Data
	@SuperBuilder
	@EqualsAndHashCode(callSuper = true)
	public static class PermissionReward extends McMMOResetReward {
		private String permission;
		private Class<? extends Listener> handler;

		@Override
		public boolean give(Player player, boolean admin) {
			if (admin) return false;
			if (LuckPermsUtils.hasPermission(player, permission)) return false;

			LuckPermsUtils.PermissionChange.set().permissions(permission).player(player).worldGroup(WorldGroup.SURVIVAL).value(true).runAsync();
			player.closeInventory();
			return true;
		}
	}

	@Data
	@SuperBuilder
	@EqualsAndHashCode(callSuper = true)
	public static class TomeReward extends McMMOResetReward {
		TomeType tome;

		@Override
		public boolean give(Player player, boolean admin) {
			player.getInventory().addItem(new TomeItem(tome, TomeLevel.MASTER).toItemStack());
			player.closeInventory();
			return true;
		}

		@Override
		public String id() {
			return "tome_" + tome.name().toLowerCase();
		}
	}

	@Data
	@SuperBuilder
	@EqualsAndHashCode(callSuper = true)
	public static class CustomEnchantReward extends McMMOResetReward {
		private Enchantment enchant;

		@Override
		public boolean give(Player player, boolean admin) {
			player.getInventory().addItem(GemCommand.makeGem(enchant));
			player.closeInventory();
			return true;
		}

		@Override
		public String id() {
			return "gem_" + enchant.getKey().value().toLowerCase();
		}
	}

}
