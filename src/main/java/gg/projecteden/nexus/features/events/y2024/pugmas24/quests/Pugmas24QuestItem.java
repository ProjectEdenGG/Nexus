package gg.projecteden.nexus.features.events.y2024.pugmas24.quests;

import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.features.quests.QuestItem;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemFlags;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public enum Pugmas24QuestItem implements QuestItem {

	RED_BALLOON(new ItemBuilder(CustomMaterial.EVENT_RED_BALLOON).name("&oRed Balloon").lore("&7Negates fall damage")),
	MAGIC_MIRROR(new ItemBuilder(CustomMaterial.EVENT_MAGIC_MIRROR).name("&oMagic Mirror").lore("&7Opens the waystone warp menu")),
	HEART_CRYSTAL(new ItemBuilder(CustomMaterial.EVENT_HEART_CRYSTAL).name("&oHeart Crystal").lore("&7Permanently increases max", "&7health by &d1 heart")),
	LUCKY_HORSESHOE(new ItemBuilder(CustomMaterial.EVENT_LUCKY_HORSESHOE).name("&oLucky Horseshoe").lore("&7Higher chance to gain additional coins")),

	ADVENTURE_POCKET_GUIDE(new ItemBuilder(CustomMaterial.EVENT_ADVENTURE_POCKET_GUIDE).name("&oAdventurer's Pocket Guide").lore("&7Displays your area designation")),
	GOLD_WATCH(new ItemBuilder(CustomMaterial.EVENT_GOLD_WATCH).name("&oGold Watch").lore("&7Displays the current time")),
	COMPASS(new ItemBuilder(CustomMaterial.EVENT_COMPASS).name("&oCompass").lore("&7Ability to see compass", "&7Displays Coordinates")), // to start it, call same method as compass#onjoin
	GPS(new ItemBuilder(CustomMaterial.EVENT_GPS).name("&oGPS").lore("&7Ability to see compass", "&7- Displays your coordinates, area designation, and the current time")),

	FISHING_POCKET_GUIDE(new ItemBuilder(CustomMaterial.EVENT_FISHING_POCKET_GUIDE).name("&oFisherman's Pocket Guide").lore("&7Displays your fishing power")),
	WEATHER_RADIO(new ItemBuilder(CustomMaterial.EVENT_WEATHER_RADIO).name("&oWeather Radio").lore("&7Displays the current weather")),
	SEXTANT(new ItemBuilder(CustomMaterial.EVENT_SEXTANT).name("&oSextant").lore("&7Displays the moon phase")),
	FISH_FINDER(new ItemBuilder(CustomMaterial.EVENT_FISH_FINDER).name("&oFish Finder").lore("&7Displays your fishing power, the moon phase, and the current weather")),

	FISHING_ROD_WOOD(new ItemBuilder(CustomMaterial.FISHING_ROD_WOOD).name("Wood Fishing Rod")),
	FISHING_ROD_REINFORCED(new ItemBuilder(CustomMaterial.FISHING_ROD_REINFORCED).name("Reinforced Fishing Rod").enchant(Enchant.UNBREAKING, 2)),
	FISHING_ROD_GOLDEN(new ItemBuilder(CustomMaterial.FISHING_ROD_GOLDEN).name("Golden Fishing Rod").lore("&7Unbreakable").unbreakable().glow()),

	// TODO: probably build these into fishing instead
	CRATE_WOOD(new ItemBuilder(CustomMaterial.EVENT_CRATE_WOODEN).name("Wooden Crate")),
	CRATE_IRON(new ItemBuilder(CustomMaterial.EVENT_CRATE_IRON).name("Iron Crate")),
	CRATE_GOLD(new ItemBuilder(CustomMaterial.EVENT_CRATE_GOLDEN).name("Golden Crate")),
	CRATE_DIAMOND(new ItemBuilder(CustomMaterial.EVENT_CRATE_DIAMOND).name("Diamond Crate")),

	;

	private final ItemBuilder itemBuilder;

	public ItemBuilder getItemBuilder() {
		return itemBuilder.clone().itemFlags(ItemFlags.HIDE_ALL);
	}

	@Override
	public ItemStack get() {
		return getItemBuilder().build();
	}

	public CustomMaterial getCustomMaterial() {
		return CustomMaterial.of(get());
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
		return COMPASS.isInInventory(player) || GPS.isInInventory(player);
	}
}
