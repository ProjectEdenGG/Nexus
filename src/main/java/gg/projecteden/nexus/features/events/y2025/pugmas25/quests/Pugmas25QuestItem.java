package gg.projecteden.nexus.features.events.y2025.pugmas25.quests;

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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor
public enum Pugmas25QuestItem implements QuestItem {

	TEST_FISH_A(new ItemBuilder(Material.COD).name("Test Fish A")),
	TEST_FISH_B(new ItemBuilder(Material.COD).name("Test Fish B")),

	TRAIN_TICKET(new ItemBuilder(ItemModelType.VOUCHER).name("&3&oTrain Ticket").lore("&3Destination: &e" + Pugmas25.EVENT_NAME).dyeColor(Color.WHITE).itemFlags(ItemFlags.HIDE_ALL)),

	// Quest Progress Items
	BOX_OF_DECORATIONS(new ItemBuilder(ItemModelType.EMPTY_BOX).name("Box of Decorations").undroppable().untrashable().unframeable()),
	BOX_OF_DECORATIONS_EMPTY(new ItemBuilder(ItemModelType.EMPTY_BOX).name("Empty Box of Decorations").undroppable().untrashable().unframeable()),

	RED_BALLOON(new ItemBuilder(ItemModelType.EVENT_RED_BALLOON).name("&oRed Balloon").lore("&7Negates fall damage")),
	MAGIC_MIRROR(new ItemBuilder(ItemModelType.EVENT_MAGIC_MIRROR).name("&oMagic Mirror").lore("&7Opens the waystone warp menu")),
	LUCKY_HORSESHOE(new ItemBuilder(ItemModelType.EVENT_LUCKY_HORSESHOE).name("&oLucky Horseshoe").lore("&7Higher chance to gain additional coins")),

	ADVENTURE_POCKET_GUIDE(new ItemBuilder(ItemModelType.EVENT_ADVENTURE_POCKET_GUIDE).name("&oAdventurer's Pocket Guide").lore("&7Displays your area designation")),
	GOLD_WATCH(new ItemBuilder(ItemModelType.EVENT_GOLD_WATCH).name("&oGold Watch").lore("&7Displays the current time")),
	COMPASS(new ItemBuilder(ItemModelType.EVENT_COMPASS).name("&oCompass").lore("&7Displays Direction")), // to start it, call same method as compass#onjoin
	GPS(new ItemBuilder(ItemModelType.EVENT_GPS).name("&oGPS").lore("&7Displays your direction, area designation, and the current time")),

	FISHING_POCKET_GUIDE(new ItemBuilder(ItemModelType.EVENT_FISHING_POCKET_GUIDE).name("&oFisherman's Pocket Guide").lore("&7Displays your fishing luck")),
	WEATHER_RADIO(new ItemBuilder(ItemModelType.EVENT_WEATHER_RADIO).name("&oWeather Radio").lore("&7Displays the current weather")),
	SEXTANT(new ItemBuilder(ItemModelType.EVENT_SEXTANT).name("&oSextant").lore("&7Displays coordinates")),
	FISH_FINDER(new ItemBuilder(ItemModelType.EVENT_FISH_FINDER).name("&oFish Finder").lore("&7Displays your fishing luck, height, and the current weather")),

	PDA(new ItemBuilder(ItemModelType.EVENT_PDA).name("&oPDA").lore("&7Displays direction, height, area designation, time, weather and fishing luck")),

	FISHING_ROD_WOOD(new ItemBuilder(ItemModelType.FISHING_ROD_WOOD).name("Wood Fishing Rod")),
	FISHING_ROD_REINFORCED(new ItemBuilder(ItemModelType.FISHING_ROD_REINFORCED).name("Reinforced Fishing Rod").enchant(Enchant.UNBREAKING, 2)),
	FISHING_ROD_GOLDEN(new ItemBuilder(ItemModelType.FISHING_ROD_GOLDEN).name("Golden Fishing Rod").lore("&7Unbreakable").unbreakable().glow()),

	// TODO: probably build these into fishing instead --> SWITCH TO HEAD DB HEADS
	CRATE_IRON(new ItemBuilder(ItemModelType.EVENT_CRATE_IRON).name("Iron Crate")), // Head ID = 46944
	CRATE_GOLD(new ItemBuilder(ItemModelType.EVENT_CRATE_GOLDEN).name("Golden Crate")), // Head ID = 46945
	CRATE_DIAMOND(new ItemBuilder(ItemModelType.EVENT_CRATE_DIAMOND).name("Diamond Crate")), // Head ID = 46946

	SHRINK_POTION(new ItemBuilder(Material.POTION).potionEffectColor(ColorType.LIGHT_GREEN.getBukkitColor()).name("&fPotion of Shrinking").lore("&9Shrinking (08:00)", "", "&5When Applied:", "&9-50% Scale")),
	SNOWMAN_DECORATIONS(new ItemBuilder(Material.CARROT).name("Snowman Decorations").interactable()),

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
		return new ItemBuilder(itemBuilder).itemFlags(ItemFlags.HIDE_ALL);
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
