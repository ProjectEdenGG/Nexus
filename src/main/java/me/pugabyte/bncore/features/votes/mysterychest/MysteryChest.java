package me.pugabyte.bncore.features.votes.mysterychest;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class MysteryChest {

	SettingService service = new SettingService();

	public static SmartInventory INV = SmartInventory.builder()
			.size(3, 9)
			.title("Mystery Chest")
			.provider(new MysteryChestProvider())
			.closeable(false)
			.build();

	public MysteryChest(OfflinePlayer player) {
		givePlayer(player, 1);
	}

	public MysteryChest(OfflinePlayer player, int amount) {
		givePlayer(player, amount);
	}

	public void givePlayer(OfflinePlayer player, int amount) {
		Setting setting = service.get(player, "mysteryChest");
		int chests = 0;
		try {
			chests = Integer.parseInt(setting.getValue());
		} catch (Exception ignore) {}
		chests += amount;
		setting.setValue("" + chests);
		service.save(setting);
	}

	public static ItemStack getSecondLootBox() {
		ItemStack item = new ItemBuilder(Material.CYAN_SHULKER_BOX).name("Flower Power Box").build();
		BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
		ShulkerBox box = (ShulkerBox) meta.getBlockState();

		box.getInventory().addItem(
				new ItemBuilder(Material.PLAYER_HEAD).name("&eCoupon for 1 HDB Head").lore("&5Mystery Chest Loot").build(),
				new ItemStack(Material.FLOWER_POT, 9),
				new ItemStack(Material.ITEM_FRAME, 4),
				new ItemStack(Material.PAINTING, 4),
				new ItemStack(Material.CACTUS),
				new ItemStack(Material.OXEYE_DAISY, 3),
				new ItemStack(Material.WHITE_TULIP, 2),
				new ItemStack(Material.RED_TULIP, 2),
				new ItemStack(Material.ORANGE_TULIP, 3),
				new ItemStack(Material.PINK_TULIP, 2),
				new ItemStack(Material.AZURE_BLUET, 2),
				new ItemStack(Material.ALLIUM, 2),
				new ItemStack(Material.BLUE_ORCHID, 2),
				new ItemStack(Material.POPPY, 2),
				new ItemStack(Material.DANDELION, 2)
		);

		ItemBuilder banner1 = new ItemBuilder(Material.LIGHT_BLUE_BANNER, 2)
				.pattern(DyeColor.LIGHT_GRAY, PatternType.STRIPE_BOTTOM)
				.pattern(DyeColor.CYAN, PatternType.TRIANGLE_BOTTOM)
				.pattern(DyeColor.BLACK, PatternType.CREEPER)
				.pattern(DyeColor.BLACK, PatternType.FLOWER)
				.pattern(DyeColor.GREEN, PatternType.BORDER)
				.pattern(DyeColor.BROWN, PatternType.CIRCLE_MIDDLE)
				.pattern(DyeColor.WHITE, PatternType.CROSS)
				.pattern(DyeColor.BROWN, PatternType.SKULL)
				.pattern(DyeColor.WHITE, PatternType.TRIANGLE_BOTTOM)
				.pattern(DyeColor.GREEN, PatternType.TRIANGLE_TOP)
				.pattern(DyeColor.GREEN, PatternType.TRIANGLE_TOP)
				.pattern(DyeColor.GREEN, PatternType.STRIPE_TOP);

		ItemBuilder banner2 = new ItemBuilder(Material.LIGHT_BLUE_BANNER, 2)
				.pattern(DyeColor.WHITE, PatternType.BRICKS)
				.pattern(DyeColor.BROWN, PatternType.STRIPE_BOTTOM)
				.pattern(DyeColor.LIGHT_BLUE, PatternType.BORDER)
				.pattern(DyeColor.BROWN, PatternType.TRIANGLE_BOTTOM)
				.pattern(DyeColor.PINK, PatternType.RHOMBUS_MIDDLE)
				.pattern(DyeColor.BLACK, PatternType.FLOWER)
				.pattern(DyeColor.BLACK, PatternType.CREEPER)
				.pattern(DyeColor.BROWN, PatternType.SKULL)
				.pattern(DyeColor.LIGHT_BLUE, PatternType.TRIANGLE_TOP)
				.pattern(DyeColor.LIGHT_BLUE, PatternType.STRIPE_TOP)
				.pattern(DyeColor.BROWN, PatternType.CIRCLE_MIDDLE)
				.pattern(DyeColor.BROWN, PatternType.CIRCLE_MIDDLE);

		ItemBuilder banner3 = new ItemBuilder(Material.YELLOW_BANNER, 2)
				.pattern(DyeColor.CYAN, PatternType.STRIPE_LEFT)
				.pattern(DyeColor.CYAN, PatternType.STRIPE_RIGHT)
				.pattern(DyeColor.WHITE, PatternType.FLOWER);

		ItemBuilder banner4 = new ItemBuilder(Material.CYAN_BANNER, 2)
				.pattern(DyeColor.YELLOW, PatternType.STRIPE_LEFT)
				.pattern(DyeColor.YELLOW, PatternType.STRIPE_RIGHT)
				.pattern(DyeColor.WHITE, PatternType.FLOWER);

		box.getInventory().addItem(banner1.build());
		box.getInventory().addItem(banner2.build());
		box.getInventory().addItem(banner3.build());
		box.getInventory().addItem(banner4.build());

		meta.setBlockState(box);
		item.setItemMeta(meta);
		return item;
	}

}
