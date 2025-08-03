package gg.projecteden.nexus.features.events.y2025.pugmas25.quests;

import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Sidebar.Pugmas25SidebarLine;
import gg.projecteden.nexus.features.quests.QuestItem;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemFlags;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public enum Pugmas25QuestItem implements QuestItem {

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

	FISHING_ROD_WOOD(new ItemBuilder(ItemModelType.FISHING_ROD_WOOD).name("Wood Fishing Rod")),
	FISHING_ROD_REINFORCED(new ItemBuilder(ItemModelType.FISHING_ROD_REINFORCED).name("Reinforced Fishing Rod").enchant(Enchant.UNBREAKING, 2)),
	FISHING_ROD_GOLDEN(new ItemBuilder(ItemModelType.FISHING_ROD_GOLDEN).name("Golden Fishing Rod").lore("&7Unbreakable").unbreakable().glow()),

	// TODO: probably build these into fishing instead --> SWITCH TO HEAD DB HEADS
	CRATE_IRON(new ItemBuilder(ItemModelType.EVENT_CRATE_IRON).name("Iron Crate")), // Head ID = 46944
	CRATE_GOLD(new ItemBuilder(ItemModelType.EVENT_CRATE_GOLDEN).name("Golden Crate")), // Head ID = 46945
	CRATE_DIAMOND(new ItemBuilder(ItemModelType.EVENT_CRATE_DIAMOND).name("Diamond Crate")), // Head ID = 46946

	SHRINK_POTION(new ItemBuilder(Material.POTION).potionEffectColor(ColorType.LIGHT_GREEN.getBukkitColor()).name("&fPotion of Shrinking").lore("&9Shrinking (08:00)", "", "&5When Applied:", "&9-50% Scale")),
	SNOWMAN_DECORATIONS(new ItemBuilder(Material.CARROT).name("Snowman Decorations").interactable()),
	;

	private final ItemBuilder itemBuilder;

	public ItemBuilder getItemBuilder() {
		return itemBuilder.clone().itemFlags(ItemFlags.HIDE_ALL);
	}

	@Override
	public ItemStack get() {
		return getItemBuilder().build();
	}

	public ItemModelType getItemModel() {
		return ItemModelType.of(get());
	}

	public boolean isInInventory(HasUniqueId uuid) {
		return Quester.of(uuid).has(get());
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
