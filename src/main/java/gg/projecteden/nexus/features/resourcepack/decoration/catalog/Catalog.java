package gg.projecteden.nexus.features.resourcepack.decoration.catalog;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationLang.DecorationError;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType.CategoryTree;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.common.Decoration;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationSpawnEvent;
import gg.projecteden.nexus.features.resourcepack.decoration.store.DecorationStoreCurrencyType;
import gg.projecteden.nexus.features.resourcepack.decoration.store.DecorationStoreType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Art;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.features.workbenches.dyestation.ColorChoice;
import gg.projecteden.nexus.features.workbenches.dyestation.ColorChoice.DyeChoice;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.Model;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class Catalog implements Listener {

	@Getter
	private static final ItemStack MASTER_CATALOG = new ItemBuilder(ItemModelType.DECORATION_CATALOG_MASTER)
			.name("&eMaster Catalog")
			.lore("&3Master Decoration Catalog")
			.build();

	public static boolean isMasterCatalog(ItemStack itemStack) {
		if (Nullables.isNullOrAir(itemStack))
			return false;

		if (MASTER_CATALOG.getType() != itemStack.getType())
			return false;

		return Objects.equals(ItemBuilder.Model.of(MASTER_CATALOG), ItemBuilder.Model.of(itemStack));
	}

	public Catalog() {
		Nexus.registerListener(this);
	}

	@AllArgsConstructor
	@NoArgsConstructor
	public enum Tab {
		INTERNAL,
		INTERNAL_ROOT,

		FLAGS(ItemModelType.FLAG_SERVER.getItem()),
		PRIDE_FLAGS(ItemModelType.FLAG_PRIDE_PRIDE.getItem()),

		BUNTING(ItemModelType.BUNTING_SERVER_LOGO.getItem()),
		PRIDE_BUNTING(ItemModelType.BUNTING_PRIDE_GAY.getItem()),

		BANNERS(ItemModelType.BANNER_STANDING_SERVER_LOGO.getItem()),
		BANNERS_STANDING(ItemModelType.BANNER_STANDING_SERVER_LOGO.getItem()),
		BANNERS_HANGING(ItemModelType.BANNER_HANGING_SERVER_LOGO.getItem()),

		COUNTERS_MENU(ItemModelType.COUNTER_BLACK_SOAPSTONE_CABINET.getItem(), ColorChoice.StainChoice.OAK.getColor()),
		MARBLE_COUNTER(ItemModelType.COUNTER_MARBLE.getItem()),
		STONE_COUNTER(ItemModelType.COUNTER_STONE.getItem()),
		SOAPSTONE_COUNTER(ItemModelType.COUNTER_SOAPSTONE.getItem()),
		WOODEN_COUNTER(ItemModelType.COUNTER_WOODEN.getItem(), ColorChoice.StainChoice.OAK.getColor()),

		CABINETS(ItemModelType.CABINET_BLACK_WOODEN.getItem(), ColorChoice.StainChoice.OAK.getColor()),
		BLACK_HANDLES(ItemModelType.HANDLE_BLACK.getItem()),
		STEEL_HANDLES(ItemModelType.HANDLE_STEEL.getItem()),
		BRASS_HANDLES(ItemModelType.HANDLE_BRASS.getItem()),

		ART(ItemModelType.ART_PAINTING_CUSTOM_SKYBLOCK.getItem()),
		ART_CUSTOM(Art.tabIcon_custom),
		ART_VANILLA(Art.tabIcon_vanilla),

		MUSIC(ItemModelType.BONGOS.getItem(), ColorChoice.DyeChoice.RED.getColor()),
		MUSIC_NOISEMAKERS(ItemModelType.DRUM_KIT.getItem(), ColorChoice.DyeChoice.RED.getColor()),

		FURNITURE(ItemModelType.COUNTER_BLACK_SOAPSTONE_CABINET.getItem(), ColorChoice.StainChoice.OAK.getColor()),
		APPLIANCES(ItemModelType.APPLIANCE_FRIDGE_MAGNETS.getItem(), ColorChoice.DyeChoice.WHITE.getColor()),
		CHAIRS(ItemModelType.CHAIR_WOODEN_BASIC.getItem(), ColorChoice.StainChoice.OAK.getColor()),
		STOOLS(ItemModelType.STOOL_WOODEN_BASIC.getItem(), ColorChoice.StainChoice.OAK.getColor()),
		STUMPS(ItemModelType.STUMP_OAK.getItem()),
		TABLES(ItemModelType.TABLE_WOODEN_1X1.getItem(), ColorChoice.StainChoice.OAK.getColor()),
		BEDS(ItemModelType.BED_GENERIC_1_SINGLE.getItem(), ColorChoice.StainChoice.OAK.getColor()),
		FIREPLACES(ItemModelType.FIREPLACE_WOODEN.getItem(), ColorChoice.StainChoice.OAK.getColor()),

		FOOD(ItemModelType.FOOD_BREAD_LOAF.getItem()),
		POTIONS(ItemModelType.POTION_FILLED_GROUP_RANDOM_2.getItem(), ColorChoice.DyeChoice.WHITE.getColor()),
		BOOKS(ItemModelType.BOOK_OPENED_1.getItem(), DyeChoice.RED.getColor()),
		FLORA(ItemModelType.FLORA_CHINESE_EVERGREEN.getItem(), ColorChoice.DyeChoice.RED.getColor()),
		KITCHENWARE(ItemModelType.KITCHENWARE_MIXING_BOWL.getItem()),
		WINDCHIMES(ItemModelType.WINDCHIMES_COPPER.getItem()),
		;

		ItemStack icon = new ItemStack(Material.AIR);
		Color color = null;

		Tab(ItemStack icon) {
			this.icon = icon;
			this.color = null;
		}

		public ItemBuilder getIcon() {
			ItemBuilder item = new ItemBuilder(icon);
			if (color == null)
				return item;

			return item.dyeColor(color);
		}
	}

	@AllArgsConstructor
	public enum Theme {
		ALL(ItemModelType.DECORATION_CATALOG_ALL, Integer.MAX_VALUE),

		GENERAL(ItemModelType.DECORATION_CATALOG_GENERAL, 100000.0),
		//
		ART(ItemModelType.DECORATION_CATALOG_ART, 65000.0),
		MUSIC(ItemModelType.DECORATION_CATALOG_MUSIC, 45000.0),
		OUTDOORS(ItemModelType.DECORATION_CATALOG_OUTDOORS, 30000.0),
		//
		HOLIDAY(ItemModelType.DECORATION_CATALOG_HOLIDAY, 20000.0),
		SPOOKY(ItemModelType.DECORATION_CATALOG_SPOOKY, 20000.0),
		PRIDE(ItemModelType.DECORATION_CATALOG_PRIDE, 20000.0),

		;

		final ItemModelType itemModelType;
		@Getter
		final double price;

		public ItemBuilder getItemBuilder() {
			return new ItemBuilder(itemModelType).name("Decoration Catalog: " + StringUtils.camelCase(this));
		}

		public ItemStack getNamedItem() {
			return getItemBuilder().build();
		}

		public ItemStack getShopItem() {
			return new ItemBuilder(itemModelType).name("&3Catalog Theme: &e" + StringUtils.camelCase(this)).build();
		}

		public boolean matchesItem(ItemStack itemStack) {
			if (Nullables.isNullOrAir(itemStack))
				return false;

			if (getItemBuilder().material() != itemStack.getType())
				return false;

			return ItemUtils.isModelMatch(getItemBuilder().clone().build(), itemStack);
		}

		public void openCatalog(Player player, DecorationStoreCurrencyType currency) {
			Catalog.openCatalog(player, this, DecorationType.getCategoryTree(), currency, null);
		}
	}

	public static void openCatalog(Player viewer, Theme theme, DecorationStoreCurrencyType currency, @Nullable InventoryProvider previousMenu) {
		openCatalog(viewer, theme, DecorationType.getCategoryTree(), currency, previousMenu);
	}

	public static void openCatalog(Player viewer, Theme theme, @NonNull CategoryTree tree, DecorationStoreCurrencyType currency, @Nullable InventoryProvider previousMenu) {
		if (theme == Theme.ALL) {
			new CatalogProvider(currency, previousMenu).open(viewer);
			return;
		}

		new CatalogThemeProvider(theme, tree, currency, previousMenu).open(viewer);
	}

	public static void openCountersCatalog(Player viewer, Theme theme, @NonNull CategoryTree tree, DecorationStoreCurrencyType currency, @NonNull InventoryProvider previousMenu) {
		new CountersProvider(theme, tree, currency, previousMenu).open(viewer);
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND)
			return;

		if (!ActionGroup.RIGHT_CLICK.applies(event))
			return;

		Player player = event.getPlayer();
		ItemStack handItem = player.getInventory().getItemInMainHand();

		if (isMasterCatalog(handItem)) {
			event.setCancelled(true);
			new MasterCatalogProvider(player, DecorationStoreCurrencyType.MONEY).open(player);
			return;
		}

		for (Theme theme : Theme.values()) {
			if (theme.matchesItem(handItem)) {
				event.setCancelled(true);
				theme.openCatalog(player, DecorationStoreCurrencyType.MONEY);
				return;
			}
		}

	}

	public static void tryBuyEventItem(Player viewer, ItemStack itemStack, WorldGroup worldGroup, ShopGroup shopGroup,
									   String eventName, DecorationStoreType storeType) {
		DecorationStoreCurrencyType currency = storeType.getCurrency();

		if (DecorationUtils.hasBypass(viewer)) {
			DecorationUtils.getSoundBuilder(Sound.ENTITY_ITEM_PICKUP).category(SoundCategory.PLAYERS).volume(0.3).receiver(viewer).play();
			PlayerUtils.giveItem(viewer, itemStack);
			return;
		}

		Integer price;
		DecorationConfig _config = DecorationConfig.of(itemStack);
		if (itemStack.getType() == Material.PLAYER_HEAD) {
			price = currency.getPriceSkull(storeType);
		} else if (_config != null) {
			price = _config.getCatalogPrice(storeType);
		} else {
			throw new InvalidInputException("Unknown decoration type of: " + itemStack.getType() + ", model = " + Model.of(itemStack));
		}

		if (price == null)
			return;

		if (!currency.hasFunds(viewer, itemStack, shopGroup, price)) {
			DecorationError.LACKING_FUNDS.send(viewer);
			return;
		}

		currency.withdraw(viewer, itemStack, shopGroup, price);
		log(viewer, shopGroup, currency, price, storeType, _config, itemStack);

		PlayerUtils.mailItem(viewer, itemStack, null, worldGroup, eventName);
	}

	public static void tryBuySurvivalItem(Player viewer, DecorationConfig config, ItemStack itemStack, DecorationStoreType storeType) {
		DecorationStoreCurrencyType currency = DecorationStoreCurrencyType.MONEY;

		boolean isSkull = false;
		Integer price;
		if (itemStack.getType() == Material.PLAYER_HEAD && Model.of(itemStack) == null) {
			price = currency.getPriceSkull(storeType);
			isSkull = true;
		} else if (config != null) {
			price = config.getCatalogPrice(storeType);
		} else {
			throw new InvalidInputException("Unknown decoration type of: " + itemStack.getType() + ", model = " + Model.of(itemStack));
		}

		if (price == null)
			return;

		DecorationSpawnEvent spawnEvent = new DecorationSpawnEvent(viewer, new Decoration(config, null), itemStack, isSkull);
		if (!spawnEvent.callEvent())
			return;

		itemStack = spawnEvent.getItemStack();

		if (DecorationUtils.hasBypass(viewer)) {
			DecorationUtils.getSoundBuilder(Sound.ENTITY_ITEM_PICKUP).category(SoundCategory.PLAYERS).volume(0.3).receiver(viewer).play();
			PlayerUtils.giveItem(viewer, itemStack);
			return;
		}

		//

		if (!WorldGroup.of(viewer).equals(WorldGroup.SURVIVAL))
			return;

		ShopGroup shopGroup = ShopGroup.SURVIVAL;
		if (!currency.hasFunds(viewer, itemStack, shopGroup, price)) {
			DecorationError.LACKING_FUNDS.send(viewer);
			return;
		}

		currency.withdraw(viewer, itemStack, shopGroup, price);
		log(viewer, shopGroup, currency, price, storeType, config, itemStack);

		if (PlayerUtils.hasRoomFor(viewer, itemStack))
			DecorationUtils.getSoundBuilder(Sound.ENTITY_ITEM_PICKUP).category(SoundCategory.PLAYERS).volume(0.3).receiver(viewer).play();

		PlayerUtils.giveItemAndMailExcess(viewer, itemStack, WorldGroup.SURVIVAL);
	}

	private static void log(Player buyer, ShopGroup shopGroup, DecorationStoreCurrencyType currencyType, int price,
							DecorationStoreType storeType, DecorationConfig config, ItemStack itemStack) {

		String productId;
		if (config != null) {
			productId = config.getId();
		} else {
			String headId = Nexus.getHeadAPI().getItemID(itemStack);
			if (headId == null)
				headId = "Player Head";

			productId = headId;
		}

		List<String> columns = List.of(
			DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()),
			buyer.getUniqueId().toString(),
			Nickname.of(buyer),
			shopGroup.name(),
			storeType.name(),
			currencyType.name(),
			String.valueOf(price),
			productId
		);

		IOUtils.csvAppend("decoration", String.join(",", columns));
	}
}
