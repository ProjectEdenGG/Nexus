package gg.projecteden.nexus.features.shops;

import gg.projecteden.nexus.models.boost.BoostConfig;
import gg.projecteden.nexus.models.boost.Boostable;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.models.shop.Shop.ExchangeType;
import gg.projecteden.nexus.models.shop.Shop.Product;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.shop.ShopService;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.api.common.utils.UUIDUtils.UUID0;

public class Market {
	private static final ShopService service = new ShopService();
	public static final List<Product> RESOURCE_WORLD_PRODUCTS = new ArrayList<>();

	public static void load() {
		final Shop market = service.getMarket();

		market.getProducts().clear();
		RESOURCE_WORLD_PRODUCTS.clear();

		addItems();
	}

	private static void addItems() {
	/*
		addSellItem(ShopGroup.SURVIVAL, false, Material.COBBLESTONE, 32, 50);
		addSellItem(ShopGroup.SURVIVAL, false, Material.STONE_BRICKS, 32, 120);
		addSellItem(ShopGroup.SURVIVAL, false, Material.MOSSY_STONE_BRICKS, 32, 150);
		addSellItem(ShopGroup.SURVIVAL, false, Material.CRACKED_STONE_BRICKS, 32, 150);
		addSellItem(ShopGroup.SURVIVAL, false, Material.CHISELED_STONE_BRICKS, 32, 300);
		addSellItem(ShopGroup.SURVIVAL, false, Material.DIRT, 32, 40);
		addSellItem(ShopGroup.SURVIVAL, false, Material.COARSE_DIRT, 32, 100);
		addSellItem(ShopGroup.SURVIVAL, false, Material.MYCELIUM, 32, 500);
		addSellItem(ShopGroup.SURVIVAL, false, Material.PODZOL, 32, 400);
		addSellItem(ShopGroup.SURVIVAL, false, Material.GRASS_BLOCK, 32, 100);
		addSellItem(ShopGroup.SURVIVAL, false, Material.SAND, 32, 60);
		addSellItem(ShopGroup.SURVIVAL, false, Material.GRAVEL, 32, 50);
		addSellItem(ShopGroup.SURVIVAL, false, Material.BRICKS, 32, 300);
		addSellItem(ShopGroup.SURVIVAL, false, Material.IRON_ORE, 32, 1200);
		addSellItem(ShopGroup.SURVIVAL, false, Material.GOLD_ORE, 32, 1800);
		addSellItem(ShopGroup.SURVIVAL, false, Material.DEEPSLATE_IRON_ORE, 32, 2400);
		addSellItem(ShopGroup.SURVIVAL, false, Material.DEEPSLATE_GOLD_ORE, 32, 3600);
		addSellItem(ShopGroup.SURVIVAL, false, Material.GLOWSTONE, 32, 400);
		addSellItem(ShopGroup.SURVIVAL, false, Material.COAL_BLOCK, 32, 640);
		addSellItem(ShopGroup.SURVIVAL, false, Material.QUARTZ_BLOCK, 32, 600);
		addSellItem(ShopGroup.SURVIVAL, false, Material.SEA_LANTERN, 1, 65);
		addSellItem(ShopGroup.SURVIVAL, false, Material.REDSTONE_BLOCK, 1, 40);
		addSellItem(ShopGroup.SURVIVAL, false, Material.LAPIS_BLOCK, 1, 100);
		addSellItem(ShopGroup.SURVIVAL, false, Material.IRON_BLOCK, 1, 180);
		addSellItem(ShopGroup.SURVIVAL, false, Material.GOLD_BLOCK, 1, 200);
		addSellItem(ShopGroup.SURVIVAL, false, Material.EMERALD_BLOCK, 1, 1575);
		addSellItem(ShopGroup.SURVIVAL, false, Material.DIAMOND_BLOCK, 1, 2200);
		addSellItem(ShopGroup.SURVIVAL, false, Material.SMOOTH_RED_SANDSTONE, 32, 250);
		addSellItem(ShopGroup.SURVIVAL, false, Material.CHISELED_RED_SANDSTONE, 32, 250);
		addSellItem(ShopGroup.SURVIVAL, false, Material.RED_SANDSTONE, 32, 200);
		addSellItem(ShopGroup.SURVIVAL, false, Material.DANDELION, 8, 50);
		addSellItem(ShopGroup.SURVIVAL, false, Material.POPPY, 8, 50);
		addSellItem(ShopGroup.SURVIVAL, false, Material.BLUE_ORCHID, 8, 125);
		addSellItem(ShopGroup.SURVIVAL, false, Material.ALLIUM, 8, 175);
		addSellItem(ShopGroup.SURVIVAL, false, Material.AZURE_BLUET, 8, 150);
		addSellItem(ShopGroup.SURVIVAL, false, Material.RED_TULIP, 8, 150);
		addSellItem(ShopGroup.SURVIVAL, false, Material.PINK_TULIP, 8, 150);
		addSellItem(ShopGroup.SURVIVAL, false, Material.WHITE_TULIP, 8, 150);
		addSellItem(ShopGroup.SURVIVAL, false, Material.ORANGE_TULIP, 8, 150);
		addSellItem(ShopGroup.SURVIVAL, false, Material.OXEYE_DAISY, 8, 150);
		addSellItem(ShopGroup.SURVIVAL, false, Material.CORNFLOWER, 8, 150);
		addSellItem(ShopGroup.SURVIVAL, false, Material.LILY_OF_THE_VALLEY, 8, 175);
		addSellItem(ShopGroup.SURVIVAL, false, Material.SUNFLOWER, 8, 250);
		addSellItem(ShopGroup.SURVIVAL, false, Material.LILAC, 8, 250);
		addSellItem(ShopGroup.SURVIVAL, false, Material.ROSE_BUSH, 8, 250);
		addSellItem(ShopGroup.SURVIVAL, false, Material.PEONY, 8, 250);
		addSellItem(ShopGroup.SURVIVAL, false, Material.INK_SAC, 8, 150);
		addSellItem(ShopGroup.SURVIVAL, false, Material.CACTUS, 8, 100);
		addSellItem(ShopGroup.SURVIVAL, false, Material.SEA_PICKLE, 4, 250);
		addSellItem(ShopGroup.SURVIVAL, false, Material.LILY_PAD, 8, 125);
		addSellItem(ShopGroup.SURVIVAL, false, Material.SMOOTH_SANDSTONE, 32, 75);
		addSellItem(ShopGroup.SURVIVAL, false, Material.CHISELED_SANDSTONE, 32, 75);
		addSellItem(ShopGroup.SURVIVAL, false, Material.SANDSTONE, 32, 60);
		addSellItem(ShopGroup.SURVIVAL, false, Material.DARK_OAK_LOG, 32, 75);
		addSellItem(ShopGroup.SURVIVAL, false, Material.ACACIA_LOG, 32, 75);
		addSellItem(ShopGroup.SURVIVAL, false, Material.JUNGLE_LOG, 32, 75);
		addSellItem(ShopGroup.SURVIVAL, false, Material.BIRCH_LOG, 32, 75);
		addSellItem(ShopGroup.SURVIVAL, false, Material.SPRUCE_LOG, 32, 75);
		addSellItem(ShopGroup.SURVIVAL, false, Material.OAK_LOG, 32, 75);
		addSellItem(ShopGroup.SURVIVAL, false, Material.CRIMSON_STEM, 32, 75);
		addSellItem(ShopGroup.SURVIVAL, false, Material.WARPED_STEM, 32, 75);
		addSellItem(ShopGroup.SURVIVAL, false, MobHeadType.CREEPER.getNamedSkull(), 5000);
		addSellItem(ShopGroup.SURVIVAL, false, MobHeadType.SKELETON.getNamedSkull(), 5000);
		addSellItem(ShopGroup.SURVIVAL, false, MobHeadType.WITHER_SKELETON.getNamedSkull(), 6000);
		addSellItem(ShopGroup.SURVIVAL, false, MobHeadType.ZOMBIE.getNamedSkull(), 5000);
		addSellItem(ShopGroup.SURVIVAL, false, Material.BOOKSHELF, 1, 300);
		addSellItem(ShopGroup.SURVIVAL, false, Material.NAUTILUS_SHELL, 1, 10000);
		addSellItem(ShopGroup.SURVIVAL, false, Material.BEACON, 1, 200000);
		addSellItem(ShopGroup.SURVIVAL, false, Material.DRAGON_HEAD, 1, 50000);
		addSellItem(ShopGroup.SURVIVAL, false, Material.ELYTRA, 1, 10000);
		addSellItem(ShopGroup.SURVIVAL, false, Material.GLASS, 32, 50);
		addSellItem(ShopGroup.SURVIVAL, false, Material.HONEY_BLOCK, 4, 1000);
		addSellItem(ShopGroup.SURVIVAL, false, Material.TERRACOTTA, 32, 100);
		addSellItem(ShopGroup.SURVIVAL, false, Material.WHITE_WOOL, 16, 45);
		addSellItem(ShopGroup.SURVIVAL, false, Material.NETHERRACK, 32, 64);
		addSellItem(ShopGroup.SURVIVAL, false, Material.SOUL_SAND, 32, 128);
		addSellItem(ShopGroup.SURVIVAL, false, Material.NETHER_BRICKS, 32, 200);
		addSellItem(ShopGroup.SURVIVAL, false, Material.RED_NETHER_BRICKS, 32, 300);
		addSellItem(ShopGroup.SURVIVAL, false, Material.PRISMARINE, 32, 100);
		addSellItem(ShopGroup.SURVIVAL, false, Material.PRISMARINE_BRICKS, 32, 250);
		addSellItem(ShopGroup.SURVIVAL, false, Material.DARK_PRISMARINE, 32, 350);
		addSellItem(ShopGroup.SURVIVAL, false, Material.PURPUR_BLOCK, 32, 250);
		addSellItem(ShopGroup.SURVIVAL, false, Material.END_STONE, 32, 200);
		addSellItem(ShopGroup.SURVIVAL, false, Material.END_STONE_BRICKS, 32, 250);
		addSellItem(ShopGroup.SURVIVAL, false, Material.GRANITE, 32, 75);
		addSellItem(ShopGroup.SURVIVAL, false, Material.DIORITE, 32, 75);
		addSellItem(ShopGroup.SURVIVAL, false, Material.ANDESITE, 32, 75);
		addSellItem(ShopGroup.SURVIVAL, false, Material.POLISHED_GRANITE, 32, 75);
		addSellItem(ShopGroup.SURVIVAL, false, Material.POLISHED_DIORITE, 32, 75);
		addSellItem(ShopGroup.SURVIVAL, false, Material.POLISHED_ANDESITE, 32, 75);
		addSellItem(ShopGroup.SURVIVAL, false, Material.TUBE_CORAL_BLOCK, 16, 300);
		addSellItem(ShopGroup.SURVIVAL, false, Material.BRAIN_CORAL_BLOCK, 16, 300);
		addSellItem(ShopGroup.SURVIVAL, false, Material.BUBBLE_CORAL_BLOCK, 16, 300);
		addSellItem(ShopGroup.SURVIVAL, false, Material.FIRE_CORAL_BLOCK, 16, 300);
		addSellItem(ShopGroup.SURVIVAL, false, Material.HORN_CORAL_BLOCK, 16, 300);
		addSellItem(ShopGroup.SURVIVAL, false, Material.COBBLED_DEEPSLATE, 32, 75);
		addSellItem(ShopGroup.SURVIVAL, false, Material.TUFF, 32, 100);
		addSellItem(ShopGroup.SURVIVAL, false, Material.TINTED_GLASS, 8, 500);
		addSellItem(ShopGroup.SURVIVAL, false, Material.MOSS_BLOCK, 8, 250);
		addSellItem(ShopGroup.SURVIVAL, false, Material.SMALL_DRIPLEAF, 16, 250);
		addSellItem(ShopGroup.SURVIVAL, false, Material.FLOWERING_AZALEA, 8, 1000);
		addSellItem(ShopGroup.SURVIVAL, false, Material.ROOTED_DIRT, 32, 250);

		addBuyItem(ShopGroup.SURVIVAL, false, Material.COBBLESTONE, 64, 10);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.STONE_BRICKS, 64, 15);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.MOSSY_STONE_BRICKS, 64, 20);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.CRACKED_STONE_BRICKS, 64, 20);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.CHISELED_STONE_BRICKS, 64, 25);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.IRON_ORE, 32, 600);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.GOLD_ORE, 32, 900);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.DEEPSLATE_IRON_ORE, 32, 900);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.DEEPSLATE_GOLD_ORE, 32, 1350);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.COAL_BLOCK, 32, 120);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.QUARTZ_BLOCK, 32, 300);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.SEA_LANTERN, 3, 7.5);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.REDSTONE_BLOCK, 32, 500);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.LAPIS_BLOCK, 32, 800);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.IRON_BLOCK, 8, 35);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.GOLD_BLOCK, 16, 75);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.RAW_IRON_BLOCK, 8, 45);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.RAW_GOLD_BLOCK, 16, 100);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.EMERALD_BLOCK, 16, 144);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.DIAMOND_BLOCK, 3, 800);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.COPPER_BLOCK, 8, 25);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.RAW_COPPER_BLOCK, 8, 40);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.POISONOUS_POTATO, 16, 30);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.HAY_BLOCK, 64, 10);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.DARK_OAK_LOG, 32, 40);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.ACACIA_LOG, 32, 45);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.JUNGLE_LOG, 32, 40);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.BIRCH_LOG, 32, 45);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.SPRUCE_LOG, 32, 40);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.OAK_LOG, 32, 45);
		addBuyItem(ShopGroup.SURVIVAL, false, MobHeadType.CREEPER.getNamedSkull(), 1000);
		addBuyItem(ShopGroup.SURVIVAL, false, MobHeadType.SKELETON.getNamedSkull(), 1000);
		addBuyItem(ShopGroup.SURVIVAL, false, MobHeadType.WITHER_SKELETON.getNamedSkull(), 1500);
		addBuyItem(ShopGroup.SURVIVAL, false, MobHeadType.ZOMBIE.getNamedSkull(), 1000);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.ELYTRA, 1, 1000);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.GLASS, 32, 25);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.HONEY_BLOCK, 4, 60);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.WHITE_WOOL, 32, 60);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.NETHERRACK, 64, 32);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.SOUL_SAND, 64, 64);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.NETHER_BRICKS, 64, 120);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.RED_NETHER_BRICKS, 64, 150);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.PRISMARINE, 64, 30);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.PRISMARINE_BRICKS, 64, 65);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.DARK_PRISMARINE, 64, 100);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.PURPUR_BLOCK, 64, 120);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.END_STONE, 64, 80);
		addBuyItem(ShopGroup.SURVIVAL, false, Material.END_STONE_BRICKS, 64, 100);
	*/

		addBuyItem(ShopGroup.SURVIVAL, true, Material.TERRACOTTA, 1, 1);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.CLAY, 1, 6);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.CLAY_BALL, 1, 1.5);
//		addBuyItem(ShopGroup.SURVIVAL, true, Material.PODZOL, 1, 1);
//		addBuyItem(ShopGroup.SURVIVAL, true, Material.MYCELIUM, 1, 6.5);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.DIORITE, 1, 0.3);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.ANDESITE, 1, 0.3);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.GRANITE, 1, 0.3);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.COAL_ORE, 1, 6);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.IRON_ORE, 1, 25);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.GOLD_ORE, 1, 40);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.LAPIS_ORE, 1, 60);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.REDSTONE_ORE, 1, 18);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.COPPER_ORE, 1, 12);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.DIAMOND_ORE, 1, 110);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.EMERALD_ORE, 1, 125);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.DEEPSLATE_COAL_ORE, 1, 9);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.DEEPSLATE_IRON_ORE, 1, 37.5);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.DEEPSLATE_GOLD_ORE, 1, 60);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.DEEPSLATE_LAPIS_ORE, 1, 90);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.DEEPSLATE_REDSTONE_ORE, 1, 27);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.DEEPSLATE_COPPER_ORE, 1, 18);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.DEEPSLATE_DIAMOND_ORE, 1, 165);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.DEEPSLATE_EMERALD_ORE, 1, 187.5);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.NETHER_GOLD_ORE, 1, 9);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.NETHER_QUARTZ_ORE, 1, 8);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.GLOWSTONE, 1, 3.75);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.GLOWSTONE_DUST, 1, 0.9);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.SAND, 1, 0.3);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.RED_SAND, 1, 0.45);
//		addBuyItem(ShopGroup.SURVIVAL, true, Material.COARSE_DIRT, 1, 0.5);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.BLUE_ICE, 1, 3.75);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.PACKED_ICE, 1, 0.5);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.GRAVEL, 1, 0.6);
//		addBuyItem(ShopGroup.SURVIVAL, true, Material.GRASS_BLOCK, 1, 1);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.TUBE_CORAL_BLOCK, 1, 4);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.BRAIN_CORAL_BLOCK, 1, 4);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.BUBBLE_CORAL_BLOCK, 1, 4);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.FIRE_CORAL_BLOCK, 1, 4);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.HORN_CORAL_BLOCK, 1, 4);
		addBuyItem(ShopGroup.SURVIVAL, true, Material.AMETHYST_SHARD, 1, .5);
	}

	private static void addSellItem(ShopGroup shopGroup, boolean isResourceWorld, Material material, int quantity, double price) {
		addSellItem(shopGroup, isResourceWorld, new ItemStack(material, quantity), price);
	}

	private static void addSellItem(ShopGroup shopGroup, boolean isResourceWorld, ItemStack item, double price) {
		add(new Product(UUID0, shopGroup, isResourceWorld, item, -1, ExchangeType.SELL, price));
	}

	private static void addBuyItem(ShopGroup shopGroup, boolean isResourceWorld, Material material, int quantity, double price) {
		addBuyItem(shopGroup, isResourceWorld, new ItemStack(material, quantity), price);
	}

	private static void addBuyItem(ShopGroup shopGroup, boolean isResourceWorld, ItemStack item, double price) {
		final Product product = new Product(UUID0, shopGroup, isResourceWorld, item, -1, ExchangeType.BUY, price * BoostConfig.multiplierOf(Boostable.MARKET_SELL_PRICES));

		if (product.isResourceWorld())
			RESOURCE_WORLD_PRODUCTS.add(product);

		add(product);
	}

	private static void add(Product product) {
		service.getMarket().getProducts().add(product);
	}

}
