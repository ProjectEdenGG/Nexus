package gg.projecteden.nexus.features.events.y2025.pugmas25.quests;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.equipment.skins.ArmorSkin;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Sidebar.Pugmas25SidebarLine;
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

	TRAIN_TICKET(new ItemBuilder(ItemModelType.VOUCHER).name("&3&oTrain Ticket").lore("&3Destination: &e" + Pugmas25.EVENT_NAME).dyeColor(Color.WHITE).itemFlags(ItemFlags.HIDE_ALL)),

	// Quest Progress Items
	// TODO: BOX OF DECORATION MODELS
	BOX_OF_DECORATIONS(new ItemBuilder(ItemModelType.EMPTY_BOX).name("Box of Decorations").undroppable().untrashable().unframeable()),
	BOX_OF_DECORATIONS_EMPTY(new ItemBuilder(ItemModelType.EMPTY_BOX).name("Empty Box of Decorations").undroppable().untrashable().unframeable()),

	//	RED_BALLOON(new ItemBuilder(ItemModelType.EVENT_RED_BALLOON).name("&oRed Balloon").lore("&7Negates fall damage")),
	MAGIC_MIRROR(new ItemBuilder(ItemModelType.EVENT_MAGIC_MIRROR).name("&oMagic Mirror").lore("&7Opens the waystone warp menu")),
	LUCKY_HORSESHOE(new ItemBuilder(ItemModelType.EVENT_LUCKY_HORSESHOE).name("&oLucky Horseshoe").lore("&7Higher chance to gain additional rewards")),

	ADVENTURE_POCKET_GUIDE(new ItemBuilder(ItemModelType.EVENT_ADVENTURE_POCKET_GUIDE).name("&oAdventurer's Pocket Guide").lore("&7Displays your area designation")),
	GOLD_WATCH(new ItemBuilder(ItemModelType.EVENT_GOLD_WATCH).name("&oGold Watch").lore("&7Displays the current time")),
	COMPASS(new ItemBuilder(ItemModelType.EVENT_COMPASS).name("&oCompass").lore("&7Displays Direction")), // to start it, call same method as compass#onjoin
	GPS(new ItemBuilder(ItemModelType.EVENT_GPS).name("&oGPS").lore("&7Displays your direction, area designation, and the current time")),

	FISHING_POCKET_GUIDE(new ItemBuilder(ItemModelType.EVENT_FISHING_POCKET_GUIDE).name("&oFisherman's Pocket Guide").lore("&7Displays your fishing luck")),
	WEATHER_RADIO(new ItemBuilder(ItemModelType.EVENT_WEATHER_RADIO).name("&oWeather Radio").lore("&7Displays the current weather")),
	SEXTANT(new ItemBuilder(ItemModelType.EVENT_SEXTANT).name("&oSextant").lore("&7Displays coordinates")),
	FISH_FINDER(new ItemBuilder(ItemModelType.EVENT_FISH_FINDER).name("&oFish Finder").lore("&7Increases fishing luck by 5", "&7Displays your fishing luck, height, and the current weather")),

	PDA(new ItemBuilder(ItemModelType.EVENT_PDA).name("&oPDA").lore("&7Increases fishing luck by 5", "&7Opens the waystone warp menu", "&7Displays direction, height, area designation, time, weather and fishing luck")),

	FISHING_ROD_WOOD(new ItemBuilder(ItemModelType.FISHING_ROD_WOOD).name("Wood Fishing Rod").enchant(Enchantment.LURE, 1)),
	FISHING_ROD_REINFORCED(new ItemBuilder(ItemModelType.FISHING_ROD_REINFORCED).name("Reinforced Fishing Rod").lore("&7Increases fishing luck by 5").enchant(Enchant.UNBREAKING, 2).enchant(Enchantment.LURE, 2)),
	FISHING_ROD_GOLDEN(new ItemBuilder(ItemModelType.FISHING_ROD_GOLDEN).name("Golden Fishing Rod").lore("&7Increases fishing luck by 15").enchant(Enchantment.LURE, 3).unbreakable().glow()),

	ANGLER_HAT(ArmorSkin.FISHING.apply(new ItemBuilder(Material.LEATHER_HELMET).name("&oAngler Hat").lore("&7Increases fishing luck by 4").unbreakable())),
	ANGLER_VEST(ArmorSkin.FISHING.apply(new ItemBuilder(Material.LEATHER_CHESTPLATE).name("&oAngler Vest").lore("&7Increases fishing luck by 4").unbreakable())),
	ANGLER_PANTS(ArmorSkin.FISHING.apply(new ItemBuilder(Material.LEATHER_LEGGINGS).name("&oAngler Pants").lore("&7Increases fishing luck by 4").unbreakable())),

	SHOCK_ABSORBENT_BOOTS(ArmorSkin.WIZARD.apply(new ItemBuilder(Material.LEATHER_BOOTS).name("&oShock Absorbent Sandals").lore("&7Negates fall damage, but takes durability"))),

	CRATE_IRON(new ItemBuilder(ItemModelType.EVENT_CRATE_IRON).name("Iron Crate").lore("&7RClick while holding to open")),
	CRATE_GOLD(new ItemBuilder(ItemModelType.EVENT_CRATE_GOLDEN).name("Golden Crate").lore("&7RClick while holding to open")),
	CRATE_DIAMOND(new ItemBuilder(ItemModelType.EVENT_CRATE_DIAMOND).name("Diamond Crate").lore("&7RClick while holding to open")),

	SUSPICIOUS_DEBRIS(new ItemBuilder(Material.SUSPICIOUS_GRAVEL).name("Suspicious Debris").lore("&7Used in the Extractinator")),

	GIFT(new ItemBuilder(Material.CHEST).name("Gift")), // TODO
	SLOT_MACHINE_TOKEN(new ItemBuilder(ItemModelType.EVENT_TOKEN).name("Slot Machine Token").lore("&7Used to roll the slot machine")),

	GNOMIFIER(new ItemBuilder(ItemModelType.GNOMIFIER).name("Gnomifier").lore("&7Shrinks the holder to 50% scale").untrashable().unframeable()
		.attribute(Attribute.SCALE, new AttributeModifier(new NamespacedKey(Nexus.getInstance(), "gnomifier"), -0.5, Operation.ADD_NUMBER, EquipmentSlotGroup.HAND))),

	BALLOON_PAINTBRUSH(new ItemBuilder(ItemModelType.EVENT_PAINTBRUSH)
		.name("&eBlock Replacer Brush")
		.lore("&3Block: " + ColorType.RED.getBukkitColor() + "Red Wool", "", "&3How to use:", "&eLClick &3a block to change the brush color", "&eRClick &3wool to replace it")
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
