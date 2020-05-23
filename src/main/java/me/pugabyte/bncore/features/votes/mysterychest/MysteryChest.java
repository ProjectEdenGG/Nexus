package me.pugabyte.bncore.features.votes.mysterychest;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.ArrayList;
import java.util.List;

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
		} catch (Exception ignore) {
		}
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

		ItemStack banner1 = new ItemStack(Material.LIGHT_BLUE_BANNER, 2);
		BannerMeta meta1 = (BannerMeta) banner1.getItemMeta();
		List<Pattern> patterns1 = new ArrayList<>();
		patterns1.add(new Pattern(DyeColor.LIGHT_GRAY, PatternType.STRIPE_BOTTOM));
		patterns1.add(new Pattern(DyeColor.CYAN, PatternType.TRIANGLE_BOTTOM));
		patterns1.add(new Pattern(DyeColor.BLACK, PatternType.CREEPER));
		patterns1.add(new Pattern(DyeColor.BLACK, PatternType.FLOWER));
		patterns1.add(new Pattern(DyeColor.GREEN, PatternType.BORDER));
		patterns1.add(new Pattern(DyeColor.BROWN, PatternType.CIRCLE_MIDDLE));
		patterns1.add(new Pattern(DyeColor.WHITE, PatternType.CROSS));
		patterns1.add(new Pattern(DyeColor.BROWN, PatternType.SKULL));
		patterns1.add(new Pattern(DyeColor.WHITE, PatternType.TRIANGLE_BOTTOM));
		patterns1.add(new Pattern(DyeColor.GREEN, PatternType.TRIANGLE_TOP));
		patterns1.add(new Pattern(DyeColor.GREEN, PatternType.TRIANGLE_TOP));
		patterns1.add(new Pattern(DyeColor.GREEN, PatternType.STRIPE_TOP));
		meta1.setPatterns(patterns1);
		banner1.setItemMeta(meta1);
		box.getInventory().addItem(banner1);

		ItemStack banner2 = new ItemStack(Material.LIGHT_BLUE_BANNER, 2);
		BannerMeta meta2 = (BannerMeta) banner2.getItemMeta();
		List<Pattern> patterns2 = new ArrayList<>();
		patterns2.add(new Pattern(DyeColor.WHITE, PatternType.BRICKS));
		patterns2.add(new Pattern(DyeColor.BROWN, PatternType.STRIPE_BOTTOM));
		patterns2.add(new Pattern(DyeColor.LIGHT_BLUE, PatternType.BORDER));
		patterns2.add(new Pattern(DyeColor.BROWN, PatternType.TRIANGLE_BOTTOM));
		patterns2.add(new Pattern(DyeColor.PINK, PatternType.RHOMBUS_MIDDLE));
		patterns2.add(new Pattern(DyeColor.BLACK, PatternType.FLOWER));
		patterns2.add(new Pattern(DyeColor.BLACK, PatternType.CREEPER));
		patterns2.add(new Pattern(DyeColor.BROWN, PatternType.SKULL));
		patterns2.add(new Pattern(DyeColor.LIGHT_BLUE, PatternType.TRIANGLE_TOP));
		patterns2.add(new Pattern(DyeColor.LIGHT_BLUE, PatternType.STRIPE_TOP));
		patterns2.add(new Pattern(DyeColor.BROWN, PatternType.CIRCLE_MIDDLE));
		patterns2.add(new Pattern(DyeColor.BROWN, PatternType.CIRCLE_MIDDLE));
		meta2.setPatterns(patterns2);
		banner2.setItemMeta(meta2);
		box.getInventory().addItem(banner2);

		ItemStack banner3 = new ItemStack(Material.YELLOW_BANNER, 2);
		BannerMeta meta3 = (BannerMeta) banner3.getItemMeta();
		List<Pattern> patterns3 = new ArrayList<>();
		patterns3.add(new Pattern(DyeColor.CYAN, PatternType.STRIPE_LEFT));
		patterns3.add(new Pattern(DyeColor.CYAN, PatternType.STRIPE_RIGHT));
		patterns3.add(new Pattern(DyeColor.WHITE, PatternType.FLOWER));
		meta3.setPatterns(patterns3);
		banner3.setItemMeta(meta3);
		box.getInventory().addItem(banner3);

		ItemStack banner4 = new ItemStack(Material.CYAN_BANNER, 2);
		BannerMeta meta4 = (BannerMeta) banner4.getItemMeta();
		List<Pattern> patterns4 = new ArrayList<>();
		patterns4.add(new Pattern(DyeColor.YELLOW, PatternType.STRIPE_LEFT));
		patterns4.add(new Pattern(DyeColor.YELLOW, PatternType.STRIPE_RIGHT));
		patterns4.add(new Pattern(DyeColor.WHITE, PatternType.FLOWER));
		meta4.setPatterns(patterns4);
		banner4.setItemMeta(meta4);
		box.getInventory().addItem(banner4);

		meta.setBlockState(box);
		item.setItemMeta(meta);
		return item;
	}

}
