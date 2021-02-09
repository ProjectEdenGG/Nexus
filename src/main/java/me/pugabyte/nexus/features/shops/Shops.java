package me.pugabyte.nexus.features.shops;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.shops.update.ShopDisabler;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.models.shop.Shop;
import me.pugabyte.nexus.models.shop.Shop.ExchangeType;
import me.pugabyte.nexus.models.shop.Shop.Product;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.models.shop.ShopService;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Shops extends Feature {
	public static final String PREFIX = StringUtils.getPrefix("Shops");

	@Override
	public void onStart() {
		new ShopDisabler();
		new Market();
	}

	private static class Market {

		public Market() {
			Tasks.async(this::setup);
		}

		private void setup() {
			ShopService service = new ShopService();

			Shop market = service.getMarket();

			market.getProducts().clear();
			addItems();

			service.save(market);
		}

		private void addItems() {
			addSellItem(ShopGroup.SURVIVAL, Material.COBBLESTONE, 32, 50);
			addSellItem(ShopGroup.SURVIVAL, Material.STONE_BRICKS, 32, 120);
			addSellItem(ShopGroup.SURVIVAL, Material.MOSSY_STONE_BRICKS, 32, 150);
			addSellItem(ShopGroup.SURVIVAL, Material.CRACKED_STONE_BRICKS, 32, 150);
			addSellItem(ShopGroup.SURVIVAL, Material.CHISELED_STONE_BRICKS, 32, 300);
			addSellItem(ShopGroup.SURVIVAL, Material.DIRT, 32, 40);
			addSellItem(ShopGroup.SURVIVAL, Material.COARSE_DIRT, 32, 100);
			addSellItem(ShopGroup.SURVIVAL, Material.MYCELIUM, 32, 500);
			addSellItem(ShopGroup.SURVIVAL, Material.PODZOL, 32, 400);
			addSellItem(ShopGroup.SURVIVAL, Material.GRASS_BLOCK, 32, 100);
			addSellItem(ShopGroup.SURVIVAL, Material.SAND, 32, 60);
			addSellItem(ShopGroup.SURVIVAL, Material.GRAVEL, 32, 50);
			addSellItem(ShopGroup.SURVIVAL, Material.BRICKS, 32, 300);
			addSellItem(ShopGroup.SURVIVAL, Material.IRON_ORE, 32, 1200);
			addSellItem(ShopGroup.SURVIVAL, Material.GOLD_ORE, 32, 1800);
			addSellItem(ShopGroup.SURVIVAL, Material.GLOWSTONE, 32, 400);
			addSellItem(ShopGroup.SURVIVAL, Material.COAL_BLOCK, 32, 640);
			addSellItem(ShopGroup.SURVIVAL, Material.QUARTZ_BLOCK, 32, 600);
			addSellItem(ShopGroup.SURVIVAL, Material.SEA_LANTERN, 1, 65);
			addSellItem(ShopGroup.SURVIVAL, Material.REDSTONE_BLOCK, 1, 40);
			addSellItem(ShopGroup.SURVIVAL, Material.LAPIS_BLOCK, 1, 100);
			addSellItem(ShopGroup.SURVIVAL, Material.IRON_BLOCK, 1, 180);
			addSellItem(ShopGroup.SURVIVAL, Material.GOLD_BLOCK, 1, 200);
			addSellItem(ShopGroup.SURVIVAL, Material.EMERALD_BLOCK, 1, 1575);
			addSellItem(ShopGroup.SURVIVAL, Material.DIAMOND_BLOCK, 1, 2200);
			addSellItem(ShopGroup.SURVIVAL, Material.SMOOTH_RED_SANDSTONE, 32, 250);
			addSellItem(ShopGroup.SURVIVAL, Material.CHISELED_RED_SANDSTONE, 32, 250);
			addSellItem(ShopGroup.SURVIVAL, Material.RED_SANDSTONE, 32, 200);
			addSellItem(ShopGroup.SURVIVAL, Material.DANDELION, 8, 50);
			addSellItem(ShopGroup.SURVIVAL, Material.POPPY, 8, 50);
			addSellItem(ShopGroup.SURVIVAL, Material.BLUE_ORCHID, 8, 125);
			addSellItem(ShopGroup.SURVIVAL, Material.ALLIUM, 8, 175);
			addSellItem(ShopGroup.SURVIVAL, Material.AZURE_BLUET, 8, 150);
			addSellItem(ShopGroup.SURVIVAL, Material.RED_TULIP, 8, 150);
			addSellItem(ShopGroup.SURVIVAL, Material.PINK_TULIP, 8, 150);
			addSellItem(ShopGroup.SURVIVAL, Material.WHITE_TULIP, 8, 150);
			addSellItem(ShopGroup.SURVIVAL, Material.ORANGE_TULIP, 8, 150);
			addSellItem(ShopGroup.SURVIVAL, Material.OXEYE_DAISY, 8, 150);
			addSellItem(ShopGroup.SURVIVAL, Material.CORNFLOWER, 8, 150);
			addSellItem(ShopGroup.SURVIVAL, Material.LILY_OF_THE_VALLEY, 8, 175);
			addSellItem(ShopGroup.SURVIVAL, Material.SUNFLOWER, 8, 250);
			addSellItem(ShopGroup.SURVIVAL, Material.LILAC, 8, 250);
			addSellItem(ShopGroup.SURVIVAL, Material.ROSE_BUSH, 8, 250);
			addSellItem(ShopGroup.SURVIVAL, Material.PEONY, 8, 250);
			addSellItem(ShopGroup.SURVIVAL, Material.INK_SAC, 8, 150);
			addSellItem(ShopGroup.SURVIVAL, Material.CACTUS, 8, 100);
			addSellItem(ShopGroup.SURVIVAL, Material.SEA_PICKLE, 4, 250);
			addSellItem(ShopGroup.SURVIVAL, Material.LILY_PAD, 8, 125);
			addSellItem(ShopGroup.SURVIVAL, Material.SMOOTH_SANDSTONE, 32, 75);
			addSellItem(ShopGroup.SURVIVAL, Material.CHISELED_SANDSTONE, 32, 75);
			addSellItem(ShopGroup.SURVIVAL, Material.SANDSTONE, 32, 60);
			addSellItem(ShopGroup.SURVIVAL, Material.DARK_OAK_LOG, 32, 75);
			addSellItem(ShopGroup.SURVIVAL, Material.ACACIA_LOG, 32, 75);
			addSellItem(ShopGroup.SURVIVAL, Material.JUNGLE_LOG, 32, 75);
			addSellItem(ShopGroup.SURVIVAL, Material.BIRCH_LOG, 32, 75);
			addSellItem(ShopGroup.SURVIVAL, Material.SPRUCE_LOG, 32, 75);
			addSellItem(ShopGroup.SURVIVAL, Material.OAK_LOG, 32, 75);
			addSellItem(ShopGroup.SURVIVAL, Material.CREEPER_HEAD, 1, 5000);
			addSellItem(ShopGroup.SURVIVAL, Material.SKELETON_SKULL, 1, 5000);
			addSellItem(ShopGroup.SURVIVAL, Material.WITHER_SKELETON_SKULL, 1, 6000);
			addSellItem(ShopGroup.SURVIVAL, Material.ZOMBIE_HEAD, 1, 5000);
			addSellItem(ShopGroup.SURVIVAL, Material.BOOKSHELF, 1, 300);
			addSellItem(ShopGroup.SURVIVAL, Material.NAUTILUS_SHELL, 1, 10000);
			addSellItem(ShopGroup.SURVIVAL, Material.BEACON, 1, 200000);
			addSellItem(ShopGroup.SURVIVAL, Material.DRAGON_HEAD, 1, 50000);
			addSellItem(ShopGroup.SURVIVAL, Material.ELYTRA, 1, 10000);
			addSellItem(ShopGroup.SURVIVAL, Material.GLASS, 32, 50);
			addSellItem(ShopGroup.SURVIVAL, Material.TERRACOTTA, 32, 100);
			addSellItem(ShopGroup.SURVIVAL, Material.WHITE_WOOL, 16, 45);
			addSellItem(ShopGroup.SURVIVAL, Material.NETHERRACK, 32, 64);
			addSellItem(ShopGroup.SURVIVAL, Material.SOUL_SAND, 32, 128);
			addSellItem(ShopGroup.SURVIVAL, Material.NETHER_BRICKS, 32, 200);
			addSellItem(ShopGroup.SURVIVAL, Material.RED_NETHER_BRICKS, 32, 300);
			addSellItem(ShopGroup.SURVIVAL, Material.PRISMARINE, 32, 100);
			addSellItem(ShopGroup.SURVIVAL, Material.PRISMARINE_BRICKS, 32, 250);
			addSellItem(ShopGroup.SURVIVAL, Material.DARK_PRISMARINE, 32, 350);
			addSellItem(ShopGroup.SURVIVAL, Material.PURPUR_BLOCK, 32, 250);
			addSellItem(ShopGroup.SURVIVAL, Material.END_STONE, 32, 200);
			addSellItem(ShopGroup.SURVIVAL, Material.END_STONE_BRICKS, 32, 250);
			addSellItem(ShopGroup.SURVIVAL, Material.GRANITE, 32, 75);
			addSellItem(ShopGroup.SURVIVAL, Material.DIORITE, 32, 75);
			addSellItem(ShopGroup.SURVIVAL, Material.ANDESITE, 32, 75);
			addSellItem(ShopGroup.SURVIVAL, Material.POLISHED_GRANITE, 32, 75);
			addSellItem(ShopGroup.SURVIVAL, Material.POLISHED_DIORITE, 32, 75);
			addSellItem(ShopGroup.SURVIVAL, Material.POLISHED_ANDESITE, 32, 75);
			addSellItem(ShopGroup.SURVIVAL, Material.TUBE_CORAL_BLOCK, 16, 300);
			addSellItem(ShopGroup.SURVIVAL, Material.BRAIN_CORAL_BLOCK, 16, 300);
			addSellItem(ShopGroup.SURVIVAL, Material.BUBBLE_CORAL_BLOCK, 16, 300);
			addSellItem(ShopGroup.SURVIVAL, Material.FIRE_CORAL_BLOCK, 16, 300);
			addSellItem(ShopGroup.SURVIVAL, Material.HORN_CORAL_BLOCK, 16, 300);

			addBuyItem(ShopGroup.SURVIVAL, Material.COBBLESTONE, 64, 30);
			addBuyItem(ShopGroup.SURVIVAL, Material.STONE_BRICKS, 64, 60);
			addBuyItem(ShopGroup.SURVIVAL, Material.MOSSY_STONE_BRICKS, 64, 70);
			addBuyItem(ShopGroup.SURVIVAL, Material.CRACKED_STONE_BRICKS, 64, 70);
			addBuyItem(ShopGroup.SURVIVAL, Material.CHISELED_STONE_BRICKS, 64, 80);
			addBuyItem(ShopGroup.SURVIVAL, Material.IRON_ORE, 32, 600);
			addBuyItem(ShopGroup.SURVIVAL, Material.GOLD_ORE, 32, 900);
			addBuyItem(ShopGroup.SURVIVAL, Material.COAL_BLOCK, 32, 120);
			addBuyItem(ShopGroup.SURVIVAL, Material.QUARTZ_BLOCK, 32, 300);
			addBuyItem(ShopGroup.SURVIVAL, Material.SEA_LANTERN, 1, 20);
			addBuyItem(ShopGroup.SURVIVAL, Material.REDSTONE_BLOCK, 32, 500);
			addBuyItem(ShopGroup.SURVIVAL, Material.LAPIS_BLOCK, 32, 800);
			addBuyItem(ShopGroup.SURVIVAL, Material.IRON_BLOCK, 8, 65);
			addBuyItem(ShopGroup.SURVIVAL, Material.GOLD_BLOCK, 16, 100);
			addBuyItem(ShopGroup.SURVIVAL, Material.EMERALD_BLOCK, 16, 144);
			addBuyItem(ShopGroup.SURVIVAL, Material.DIAMOND_BLOCK, 3, 800);
			addBuyItem(ShopGroup.SURVIVAL, Material.DARK_OAK_LOG, 32, 40);
			addBuyItem(ShopGroup.SURVIVAL, Material.ACACIA_LOG, 32, 45);
			addBuyItem(ShopGroup.SURVIVAL, Material.JUNGLE_LOG, 32, 40);
			addBuyItem(ShopGroup.SURVIVAL, Material.BIRCH_LOG, 32, 45);
			addBuyItem(ShopGroup.SURVIVAL, Material.SPRUCE_LOG, 32, 40);
			addBuyItem(ShopGroup.SURVIVAL, Material.OAK_LOG, 32, 45);
			addBuyItem(ShopGroup.SURVIVAL, Material.CREEPER_HEAD, 1, 1000);
			addBuyItem(ShopGroup.SURVIVAL, Material.SKELETON_SKULL, 1, 1000);
			addBuyItem(ShopGroup.SURVIVAL, Material.WITHER_SKELETON_SKULL, 1, 1500);
			addBuyItem(ShopGroup.SURVIVAL, Material.ZOMBIE_HEAD, 1, 1000);
			addBuyItem(ShopGroup.SURVIVAL, Material.BEACON, 1, 50000);
			addBuyItem(ShopGroup.SURVIVAL, Material.ELYTRA, 1, 1000);
			addBuyItem(ShopGroup.SURVIVAL, Material.GLASS, 32, 25);
			addBuyItem(ShopGroup.SURVIVAL, Material.WHITE_WOOL, 32, 60);
			addBuyItem(ShopGroup.SURVIVAL, Material.NETHERRACK, 64, 32);
			addBuyItem(ShopGroup.SURVIVAL, Material.SOUL_SAND, 64, 64);
			addBuyItem(ShopGroup.SURVIVAL, Material.NETHER_BRICKS, 64, 120);
			addBuyItem(ShopGroup.SURVIVAL, Material.RED_NETHER_BRICKS, 64, 150);
			addBuyItem(ShopGroup.SURVIVAL, Material.PRISMARINE, 64, 50);
			addBuyItem(ShopGroup.SURVIVAL, Material.PRISMARINE_BRICKS, 64, 80);
			addBuyItem(ShopGroup.SURVIVAL, Material.DARK_PRISMARINE, 64, 120);
			addBuyItem(ShopGroup.SURVIVAL, Material.PURPUR_BLOCK, 64, 120);
			addBuyItem(ShopGroup.SURVIVAL, Material.END_STONE, 64, 80);
			addBuyItem(ShopGroup.SURVIVAL, Material.END_STONE_BRICKS, 64, 100);

			addSellItem(ShopGroup.RESOURCE, Material.IRON_SHOVEL, 1, 50);
			addSellItem(ShopGroup.RESOURCE, Material.IRON_PICKAXE, 1, 150);
			addSellItem(ShopGroup.RESOURCE, Material.IRON_AXE, 1, 100);
			addSellItem(ShopGroup.RESOURCE, Material.IRON_SWORD, 1, 100);
			addSellItem(ShopGroup.RESOURCE, Material.IRON_HELMET, 1, 250);
			addSellItem(ShopGroup.RESOURCE, Material.LEATHER_CHESTPLATE, 1, 150);
			addSellItem(ShopGroup.RESOURCE, Material.LEATHER_LEGGINGS, 1, 150);
			addSellItem(ShopGroup.RESOURCE, Material.IRON_BOOTS, 1, 200);
			addSellItem(ShopGroup.RESOURCE, Material.BREAD, 4, 125);
			addSellItem(ShopGroup.RESOURCE, Material.TORCH, 8, 100);

			addBuyItem(ShopGroup.RESOURCE, Material.TERRACOTTA, 32, 75);
			addBuyItem(ShopGroup.RESOURCE, Material.CLAY, 32, 200);
			addBuyItem(ShopGroup.RESOURCE, Material.PODZOL, 32, 150);
			addBuyItem(ShopGroup.RESOURCE, Material.MYCELIUM, 32, 250);
			addBuyItem(ShopGroup.RESOURCE, Material.DIORITE, 64, 45);
			addBuyItem(ShopGroup.RESOURCE, Material.ANDESITE, 64, 45);
			addBuyItem(ShopGroup.RESOURCE, Material.GRANITE, 64, 45);
			addBuyItem(ShopGroup.RESOURCE, Material.COAL_ORE, 32, 200);
			addBuyItem(ShopGroup.RESOURCE, Material.IRON_ORE, 32, 900);
			addBuyItem(ShopGroup.RESOURCE, Material.GOLD_ORE, 32, 1350);
			addBuyItem(ShopGroup.RESOURCE, Material.LAPIS_ORE, 8, 500);
			addBuyItem(ShopGroup.RESOURCE, Material.REDSTONE_ORE, 32, 200);
			addBuyItem(ShopGroup.RESOURCE, Material.DIAMOND_ORE, 8, 900);
			addBuyItem(ShopGroup.RESOURCE, Material.NETHER_QUARTZ_ORE, 32, 250);
			addBuyItem(ShopGroup.RESOURCE, Material.GLOWSTONE, 32, 120);
			addBuyItem(ShopGroup.RESOURCE, Material.SAND, 64, 30);
			addBuyItem(ShopGroup.RESOURCE, Material.RED_SAND, 64, 45);
			addBuyItem(ShopGroup.RESOURCE, Material.COARSE_DIRT, 64, 35);
			addBuyItem(ShopGroup.RESOURCE, Material.BLUE_ICE, 16, 75);
			addBuyItem(ShopGroup.RESOURCE, Material.LILY_PAD, 16, 175);
			addBuyItem(ShopGroup.RESOURCE, Material.GRAVEL, 64, 40);
			addBuyItem(ShopGroup.RESOURCE, Material.GRASS_BLOCK, 64, 75);
			addBuyItem(ShopGroup.RESOURCE, Material.TUBE_CORAL_BLOCK, 8, 110);
			addBuyItem(ShopGroup.RESOURCE, Material.BRAIN_CORAL_BLOCK, 8, 110);
			addBuyItem(ShopGroup.RESOURCE, Material.BUBBLE_CORAL_BLOCK, 8, 110);
			addBuyItem(ShopGroup.RESOURCE, Material.FIRE_CORAL_BLOCK, 8, 110);
			addBuyItem(ShopGroup.RESOURCE, Material.HORN_CORAL_BLOCK, 8, 110);
		}

		private void addSellItem(ShopGroup shopGroup, Material material, int quantity, double price) {
			addSellItem(shopGroup, new ItemStack(material, quantity), price);
		}

		private void addSellItem(ShopGroup shopGroup, ItemStack item, double price) {
			add(new Product(Nexus.getUUID0(), shopGroup, item, -1, ExchangeType.SELL, price));
		}

		private void addBuyItem(ShopGroup shopGroup, Material material, int quantity, double price) {
			addBuyItem(shopGroup, new ItemStack(material, quantity), price);
		}

		private void addBuyItem(ShopGroup shopGroup, ItemStack item, double price) {
			add(new Product(Nexus.getUUID0(), shopGroup, item, -1, ExchangeType.BUY, price));
		}

		private void add(Product product) {
			new ShopService().getMarket().getProducts().add(product);
		}

	}

}
