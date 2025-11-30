package gg.projecteden.nexus.features.events.y2025.pugmas25.quests;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.equipment.skins.ArmorSkin;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25SidebarLine;
import gg.projecteden.nexus.features.quests.QuestItem;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemFlags;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor
public enum Pugmas25QuestItem implements QuestItem {
	TRAIN_TICKET(new ItemBuilder(ItemModelType.VOUCHER)
		.name("&3&oTrain Ticket")
		.lore("&3Destination: &e" + Pugmas25.EVENT_NAME)
		.dyeColor(Color.WHITE)
		.itemFlags(ItemFlags.HIDE_ALL)),

	BOX_OF_DECORATIONS(new ItemBuilder(ItemModelType.BOX_OF_DECORATIONS)
		.name("&oBox of Decorations")
		.lore("&3Quest Item")
		.undroppable()
		.untrashable()
		.unframeable()),

	BOX_OF_DECORATIONS_EMPTY(new ItemBuilder(ItemModelType.BOX_OF_DECORATIONS_EMPTY)
		.name("&oEmpty Box of Decorations")
		.lore("&3Quest Item")
		.undroppable()
		.untrashable()
		.unframeable()),

	MAGIC_MIRROR(new ItemBuilder(ItemModelType.EVENT_MAGIC_MIRROR)
		.name("&oMagic Mirror")
		.lore("&3Opens the waystone warp menu")),

	LUCKY_HORSESHOE(new ItemBuilder(ItemModelType.EVENT_LUCKY_HORSESHOE)
		.name("&oLucky Horseshoe")
		.lore("&3Gives chance to gain")
		.lore("&3additional rewards in:")
		.lore("&3- Fishing")
		.lore("&3- Extractinator")
		.lore("&3- Slot Machine")),

	ADVENTURE_POCKET_GUIDE(new ItemBuilder(ItemModelType.EVENT_ADVENTURE_POCKET_GUIDE)
		.name("&oAdventurer's Pocket Guide")
		.lore("&3Displays your area designation")),

	GOLD_WATCH(new ItemBuilder(ItemModelType.EVENT_GOLD_WATCH)
		.name("&oGold Watch")
		.lore("&3Displays the current time")),

	COMPASS(new ItemBuilder(ItemModelType.EVENT_COMPASS)
		.name("&oCompass")
		.lore("&3Displays Direction")), // to start it, call same method as compass#onjoin

	GPS(new ItemBuilder(ItemModelType.EVENT_GPS)
		.name("&oGPS")
		.lore("&3Displays your direction, area designation, and the current time")),

	FISHING_POCKET_GUIDE(new ItemBuilder(ItemModelType.EVENT_FISHING_POCKET_GUIDE)
		.name("&oFisherman's Pocket Guide")
		.lore("&3Displays your fishing luck")),

	WEATHER_RADIO(new ItemBuilder(ItemModelType.EVENT_WEATHER_RADIO)
		.name("&oWeather Radio")
		.lore("&3Displays the current weather")),

	SEXTANT(new ItemBuilder(ItemModelType.EVENT_SEXTANT)
		.name("&oSextant")
		.lore("&3Displays coordinates")),

	FISH_FINDER(new ItemBuilder(ItemModelType.EVENT_FISH_FINDER)
		.name("&oFish Finder")
		.lore("&3Displays your fishing luck, height, and the current weather")
		.lore("")
		.lore("&3Increases fishing luck by &e5")),

	PDA(new ItemBuilder(ItemModelType.EVENT_PDA)
		.name("&oPDA")
		.lore("&3Displays direction, height, area designation, time, weather and fishing luck")
		.lore("&3Opens the waystone warp menu")
		.lore("")
		.lore("&3Increases fishing luck by &e5")),

	FISHING_ROD_WOOD(new ItemBuilder(ItemModelType.FISHING_ROD_WOOD)
		.name("&oWood Fishing Rod")
		.enchant(Enchantment.LURE, 1)),

	FISHING_ROD_REINFORCED(new ItemBuilder(ItemModelType.FISHING_ROD_REINFORCED)
		.name("&oReinforced Fishing Rod")
		.lore("&3Increases fishing luck by &e5")
		.enchant(Enchant.UNBREAKING, 2)
		.enchant(Enchantment.LURE, 2)),

	FISHING_ROD_GOLDEN(new ItemBuilder(ItemModelType.FISHING_ROD_GOLDEN)
		.name("&oGolden Fishing Rod")
		.lore("&3Increases fishing luck by &e15")
		.enchant(Enchantment.LURE, 3)
		.unbreakable()
		.glow()),

	ANGLER_HAT(ArmorSkin.FISHING.apply(new ItemBuilder(Material.LEATHER_HELMET)
		.name("&oAngler Hat")
		.lore("&3Increases fishing luck by &e4")
		.unbreakable())),

	ANGLER_VEST(ArmorSkin.FISHING.apply(new ItemBuilder(Material.LEATHER_CHESTPLATE)
		.name("&oAngler Vest")
		.lore("&3Increases fishing luck by &e4")
		.unbreakable())),

	ANGLER_PANTS(ArmorSkin.FISHING.apply(new ItemBuilder(Material.LEATHER_LEGGINGS)
		.name("&oAngler Pants")
		.lore("&3Increases fishing luck by &e4")
		.unbreakable())),

	SHOCK_ABSORBENT_BOOTS(ArmorSkin.WIZARD.apply(new ItemBuilder(Material.LEATHER_BOOTS)
		.name("&oShock Absorbent Sandals")
		.lore("&3Negates fall damage, but takes durability"))),

	TRUNK_IRON(new ItemBuilder(ItemModelType.EVENT_TRUNK_IRON)
		.name("&oIron Trunk")
		.lore("&eRClick &3while holding to open")),

	TRUNK_GOLD(new ItemBuilder(ItemModelType.EVENT_TRUNK_GOLDEN)
		.name("&oGolden Trunk")
		.lore("&eRClick &3while holding to open")),

	TRUNK_DIAMOND(new ItemBuilder(ItemModelType.EVENT_TRUNK_DIAMOND)
		.name("&oDiamond Trunk")
		.lore("&eRClick &3while holding to open")),

	SUSPICIOUS_DEBRIS(new ItemBuilder(Material.SUSPICIOUS_GRAVEL)
		.name("&oSuspicious Debris")
		.lore("&3Used in the Extractinator")),

	GIFT(new ItemBuilder(ItemModelType.PUGMAS_GIFT)
		.name("&ePugmas Gift")
		.lore("&eGive this gift to your friends!")
		.lore("&fThe more this gift is passed around,")
		.lore("&fthe better the reward will be!")),

	SLOT_MACHINE_TOKEN(new ItemBuilder(ItemModelType.EVENT_TOKEN)
		.name("&oSlot Machine Token")
		.lore("&3Used to roll the slot machine")),

	GNOMIFIER(new ItemBuilder(ItemModelType.GNOMIFIER)
		.name("&oGnomifier")
		.lore("&3Shrinks the holder to 50% scale")
		.untrashable()
		.unframeable()
		.attribute(Attribute.SCALE, new AttributeModifier(new NamespacedKey(Nexus.getInstance(), "gnomifier"), -0.5, Operation.ADD_NUMBER, EquipmentSlotGroup.HAND))),

	BALLOON_PAINTBRUSH(new ItemBuilder(ItemModelType.EVENT_PAINTBRUSH)
		.name("&eBlock Replacer Brush")
		.lore("&3Block: " + ColorType.RED.getChatColor() + "Red Wool", "", "&3How to use:", "&eLClick &3a block to change the brush color", "&eRClick &3wool to replace it")
		.dyeColor(ColorType.RED)
		.undroppable().unframeable().unplaceable().unstorable().untrashable().untradeable()
		.itemFlags(ItemBuilder.ItemFlags.HIDE_ALL)
		.updateDecorationLore(false)
		.glow()
	),
	;

	private final ItemBuilder itemBuilder;

	public ItemBuilder getItemBuilder() {
		return new ItemBuilder(itemBuilder).itemFlags(ItemFlag.HIDE_DYE);
	}

	@Override
	public ItemStack get() {
		return getItemBuilder().build();
	}

	public @Nullable ItemModelType getItemModel() {
		return ItemModelType.of(itemBuilder);
	}

	public static Location getCompassLocation(Location location) {
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();

		return new Location(location.getWorld(), x + 688, y - 8, z + 2965);
	}

	public static boolean canUseCompass(Player player) {
		return Pugmas25SidebarLine.DIRECTION.canRender(player);
	}
}
