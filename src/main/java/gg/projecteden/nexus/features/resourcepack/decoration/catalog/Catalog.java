package gg.projecteden.nexus.features.resourcepack.decoration.catalog;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType.CategoryTree;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.common.DecorationConfig;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Art;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.workbenches.DyeStation.DyeStationMenu.DyeChoice;
import gg.projecteden.nexus.features.workbenches.DyeStation.DyeStationMenu.StainChoice;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

public class Catalog implements Listener {

	@Getter
	private static final ItemStack MASTER_CATALOG = new ItemBuilder(CustomMaterial.DECORATION_CATALOG_MASTER)
			.name("Master Catalog")
			.lore("&eMaster Decoration Catalog", "&eContains all owned catalog themes")
			.build();

	public static boolean isMasterCatalog(ItemStack itemStack) {
		if (Nullables.isNullOrAir(itemStack))
			return false;

		if (MASTER_CATALOG.getType() != itemStack.getType())
			return false;

		return ItemBuilder.ModelId.of(MASTER_CATALOG) == ItemBuilder.ModelId.of(itemStack);
	}

	public Catalog() {
		Nexus.registerListener(this);
	}

	@AllArgsConstructor
	@NoArgsConstructor
	public enum Tab {
		INTERNAL,
		INTERNAL_ROOT,

		FLAGS(CustomMaterial.FLAG_PRIDE_GAY.getItem()), // TODO: CHANGE ITEM -> Make a Server Flag
		PRIDE_FLAGS(CustomMaterial.FLAG_PRIDE_GAY.getItem()),

		BUNTING(CustomMaterial.BUNTING_SERVER_LOGO.getItem()),
		PRIDE_BUNTING(CustomMaterial.BUNTING_PRIDE_GAY.getItem()),

		BANNERS(CustomMaterial.BANNER_STANDING_SERVER_LOGO.getItem()),
		BANNERS_STANDING(CustomMaterial.BANNER_STANDING_SERVER_LOGO.getItem()),
		BANNERS_HANGING(CustomMaterial.BANNER_HANGING_SERVER_LOGO.getItem()),

		COUNTERS_MENU(CustomMaterial.COUNTER_BLACK_SOAPSTONE_CABINET.getItem(), StainChoice.OAK.getColor()),
		MARBLE_COUNTER(CustomMaterial.COUNTER_MARBLE.getItem()),
		STONE_COUNTER(CustomMaterial.COUNTER_STONE.getItem()),
		SOAPSTONE_COUNTER(CustomMaterial.COUNTER_SOAPSTONE.getItem()),
		WOODEN_COUNTER(CustomMaterial.COUNTER_WOODEN.getItem(), StainChoice.OAK.getColor()),

		CABINETS(CustomMaterial.CABINET_BLACK_WOODEN.getItem(), StainChoice.OAK.getColor()),
		BLACK_HANDLES(CustomMaterial.HANDLE_BLACK.getItem()),
		STEEL_HANDLES(CustomMaterial.HANDLE_STEEL.getItem()),
		BRASS_HANDLES(CustomMaterial.HANDLE_BRASS.getItem()),

		ART(CustomMaterial.ART_PAINTING_CUSTOM_SKYBLOCK.getItem()),
		ART_CUSTOM(Art.tabIcon_custom),
		ART_VANILLA(Art.tabIcon_vanilla),

		MUSIC(CustomMaterial.BONGOS.getItem(), DyeChoice.RED.getColor()),
		MUSIC_NOISEMAKERS(CustomMaterial.DRUM_KIT.getItem(), DyeChoice.RED.getColor()),

		FURNITURE(CustomMaterial.COUNTER_BLACK_SOAPSTONE_CABINET.getItem(), StainChoice.OAK.getColor()),
		APPLIANCES(CustomMaterial.APPLIANCE_FRIDGE_MAGNETS.getItem(), DyeChoice.WHITE.getColor()),
		CHAIRS(CustomMaterial.CHAIR_WOODEN_BASIC.getItem(), StainChoice.OAK.getColor()),
		STOOLS(CustomMaterial.STOOL_WOODEN_BASIC.getItem(), StainChoice.OAK.getColor()),
		STUMPS(CustomMaterial.STUMP_OAK.getItem()),
		TABLES(CustomMaterial.TABLE_WOODEN_1X1.getItem(), StainChoice.OAK.getColor()),
		BEDS(CustomMaterial.BED_GENERIC_1_SINGLE.getItem(), StainChoice.OAK.getColor()),

		FOOD(CustomMaterial.FOOD_BREAD_LOAF.getItem()),
		POTIONS(CustomMaterial.POTION_FILLED_GROUP_RANDOM_2.getItem(), DyeChoice.WHITE.getColor()),
		FLORA(CustomMaterial.FLORA_CHINESE_EVERGREEN.getItem(), DyeChoice.RED.getColor()),
		KITCHENWARE(CustomMaterial.KITCHENWARE_MIXING_BOWL.getItem()),
		WINDCHIMES(CustomMaterial.WINDCHIMES_COPPER.getItem()),
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
		ALL(CustomMaterial.DECORATION_CATALOG_ALL, -1),

		GENERAL(CustomMaterial.DECORATION_CATALOG_GENERAL, 100000),
		ART(CustomMaterial.DECORATION_CATALOG_ART, 65000),
		MUSIC(CustomMaterial.DECORATION_CATALOG_MUSIC, 45000),
		HOLIDAY(CustomMaterial.DECORATION_CATALOG_HOLIDAY, 25000),
		SPOOKY(CustomMaterial.DECORATION_CATALOG_SPOOKY, 25000),
		PRIDE(CustomMaterial.DECORATION_CATALOG_PRIDE, 25000),
		;

		final CustomMaterial customMaterial;
		@Getter
		final int price;

		public ItemBuilder getItemBuilder() {
			return new ItemBuilder(customMaterial).name("Decoration Catalog: " + StringUtils.camelCase(this));
		}

		public ItemStack getNamedItem() {
			return getItemBuilder().build();
		}

		public ItemStack getShopItem() {
			return new ItemBuilder(customMaterial).name("&3Theme: &e" + StringUtils.camelCase(this)).build();
		}

		public boolean matchesItem(ItemStack itemStack) {
			if (Nullables.isNullOrAir(itemStack))
				return false;

			if (getItemBuilder().material() != itemStack.getType())
				return false;

			return ItemBuilder.ModelId.of(getItemBuilder()) == ItemBuilder.ModelId.of(itemStack);
		}

		public void openCatalog(Player player) {
			Catalog.openCatalog(player, this, DecorationType.getCategoryTree(), null);
		}
	}

	public static void openCatalog(Player viewer, Theme theme, @Nullable InventoryProvider previousMenu) {
		openCatalog(viewer, theme, DecorationType.getCategoryTree(), previousMenu);
	}

	public static void openCatalog(Player viewer, Theme theme, @NonNull CategoryTree tree, @Nullable InventoryProvider previousMenu) {
		if (theme == Theme.ALL) {
			new CatalogProvider(previousMenu).open(viewer);
			return;
		}

		new CatalogThemeProvider(theme, tree, previousMenu).open(viewer);
	}

	public static void openCountersCatalog(Player viewer, Theme theme, @NonNull CategoryTree tree, @NonNull InventoryProvider previousMenu) {
		new CountersProvider(theme, tree, previousMenu).open(viewer);
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
			new MasterCatalogProvider(player).open(player);
			return;
		}

		for (Theme theme : Theme.values()) {
			if (theme.matchesItem(handItem)) {
				event.setCancelled(true);
				theme.openCatalog(player);
				return;
			}
		}

	}

	public static void buyItem(Player viewer, ItemStack itemStack, TransactionCause transactionCause) {
		DecorationConfig config = DecorationConfig.of(itemStack);
		if (config == null)
			return;

		itemStack = config.getItem();

		if (DecorationUtils.hasBypass(viewer)) {
			new SoundBuilder(Sound.ENTITY_ITEM_PICKUP).volume(0.3).receiver(viewer).play();
			PlayerUtils.giveItem(viewer, itemStack);
			return;
		}

		if (!WorldGroup.of(viewer).equals(WorldGroup.SURVIVAL))
			return;

		Double price = config.getCatalogPrice();
		if (price == null)
			return;

		BankerService bankerService = new BankerService();
		ShopGroup shopGroup = ShopGroup.SURVIVAL;
		WorldGroup worldGroup = WorldGroup.SURVIVAL;

		if (!bankerService.has(viewer, price, shopGroup)) {
			PlayerUtils.send(viewer, DecorationUtils.getPrefix() + "&cYou don't have enough money to buy this.");
			return;
		}

		bankerService.withdraw(transactionCause.of(null, viewer, BigDecimal.valueOf(-price), shopGroup, "<item name>"));

		if (PlayerUtils.hasRoomFor(viewer, itemStack))
			new SoundBuilder(Sound.ENTITY_ITEM_PICKUP).volume(0.3).receiver(viewer).play();

		PlayerUtils.giveItemAndMailExcess(viewer, itemStack, worldGroup);
	}

}
