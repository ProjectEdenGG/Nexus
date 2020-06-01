package me.pugabyte.bncore.features.votes.mysterychest;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;

public class MysteryChest {
	private final SettingService service = new SettingService();
	private final OfflinePlayer player;

	public static SmartInventory INV = SmartInventory.builder()
			.size(3, 9)
			.title("Mystery Chest")
			.provider(new MysteryChestProvider())
			.closeable(false)
			.build();

	public MysteryChest(OfflinePlayer player) {
		this.player = player;
	}

	public int give(int amount) {
		Setting setting = getSetting();
		setting.setInt(setting.getInt() + amount);
		service.save(setting);
		return setting.getInt();
	}

	public int take(int amount) {
		Setting setting = getSetting();
		setting.setInt(setting.getInt() - amount);
		service.save(setting);
		return setting.getInt();
	}

	public Setting getSetting() {
		return service.get(player, "mysteryChest");
	}

	public static ItemStack getSecondLootBox() {
		return new ItemBuilder(Material.CYAN_SHULKER_BOX).name("Flower Power Box").shulkerBox(
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
				new ItemStack(Material.DANDELION, 2),

				new ItemBuilder(Material.LIGHT_BLUE_BANNER, 2)
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
						.pattern(DyeColor.GREEN, PatternType.STRIPE_TOP)
						.build(),

				new ItemBuilder(Material.LIGHT_BLUE_BANNER, 2)
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
						.pattern(DyeColor.BROWN, PatternType.CIRCLE_MIDDLE)
						.build(),

				new ItemBuilder(Material.YELLOW_BANNER, 2)
						.pattern(DyeColor.CYAN, PatternType.STRIPE_LEFT)
						.pattern(DyeColor.CYAN, PatternType.STRIPE_RIGHT)
						.pattern(DyeColor.WHITE, PatternType.FLOWER)
						.build(),

				new ItemBuilder(Material.CYAN_BANNER, 2)
						.pattern(DyeColor.YELLOW, PatternType.STRIPE_LEFT)
						.pattern(DyeColor.YELLOW, PatternType.STRIPE_RIGHT)
						.pattern(DyeColor.WHITE, PatternType.FLOWER)
						.build()
		).build();
	}

}
