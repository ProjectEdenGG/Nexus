package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.nexus.features.commands.staff.admin.CouponCommand;
import gg.projecteden.nexus.features.events.y2025.pugmas25.features.Pugmas25GiftGiver;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.coupon.CouponService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public enum Pugmas25GiftGiverReward {
	TIER_1(4, List.of(
		CouponCommand.getGenericCoupon("mcmmo", 2),
		CouponCommand.getGenericCoupon("event_tokens", 25),
		CouponCommand.getGenericCoupon("eco", 1000),
		CouponCommand.getGenericCoupon("song", 1),
		DecorationType.BAUBLE_ORNAMENT.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(2).build(),
		DecorationType.BAUBLE_ACCENT_ORNAMENT.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(2).build(),
		DecorationType.CANDY_CANE_ORNAMENT.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(2).build(),
		DecorationType.GINGERBREAD_ORNAMENT.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(2).build(),
		DecorationType.SNOWFLAKE_ORNAMENT.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(2).build(),
		DecorationType.SNOWMAN_ORNAMENT.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(2).build(),
		DecorationType.STAR_ORNAMENT.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(2).build(),
		new ItemBuilder(ItemModelType.PUGMAS21_CANDY_CANE_GREEN).name("Green Candy Cane").lore("&ePugmas 2025 Item").amount(16).build(),
		new ItemBuilder(ItemModelType.PUGMAS21_CANDY_CANE_RED).name("Red Candy Cane").lore("&ePugmas 2025 Item").amount(16).build(),
		new ItemBuilder(ItemModelType.PUGMAS21_CANDY_CANE_YELLOW).name("Yellow Candy Cane").lore("&ePugmas 2025 Item").amount(16).build(),
		new ItemBuilder(ItemModelType.COOKIES_GINGER_ALEX).name("Gingerbread Alex").lore("&ePugmas 2025 Item").amount(16).build(),
		new ItemBuilder(ItemModelType.COOKIES_GINGER_CREEPER).name("Gingerbread Creeper").lore("&ePugmas 2025 Item").amount(16).build(),
		new ItemBuilder(ItemModelType.COOKIES_GINGER_STEVE).name("Gingerbread Steve").lore("&ePugmas 2025 Item").amount(16).build()
	)),
	TIER_2(9, List.of(
		CouponCommand.getGenericCoupon("mcmmo", 5),
		CouponCommand.getGenericCoupon("event_tokens", 75),
		CouponCommand.getGenericCoupon("eco", 2000),
		CouponCommand.getGenericCoupon("song", 1),
		DecorationType.BAUBLE_ORNAMENT.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(4).build(),
		DecorationType.BAUBLE_ACCENT_ORNAMENT.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(4).build(),
		DecorationType.CANDY_CANE_ORNAMENT.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(4).build(),
		DecorationType.GINGERBREAD_ORNAMENT.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(4).build(),
		DecorationType.SNOWFLAKE_ORNAMENT.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(4).build(),
		DecorationType.SNOWMAN_ORNAMENT.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(4).build(),
		DecorationType.STAR_ORNAMENT.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(4).build(),
		DecorationType.NUTCRACKER_TALL.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(2).build(),
		DecorationType.GIANT_CANDLE_THREE_UNLIT_DYEABLE.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(2).build(),
		DecorationType.GIANT_CANDY_CANE.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(2).build(),
		new ItemBuilder(Material.WHITE_SHULKER_BOX).name("&f&oPortable Igloo").build(),
		new ItemBuilder(ItemModelType.PUGMAS21_CANDY_CANE_GREEN).name("Green Candy Cane").lore("&ePugmas 2025 Item").amount(32).build(),
		new ItemBuilder(ItemModelType.PUGMAS21_CANDY_CANE_RED).name("Red Candy Cane").lore("&ePugmas 2025 Item").amount(32).build(),
		new ItemBuilder(ItemModelType.PUGMAS21_CANDY_CANE_YELLOW).name("Yellow Candy Cane").lore("&ePugmas 2025 Item").amount(32).build(),
		new ItemBuilder(ItemModelType.COOKIES_GINGER_CREEPER).name("Gingerbread Creeper").lore("&ePugmas 2025 Item").amount(32).build(),
		new ItemBuilder(ItemModelType.COOKIES_GINGER_ALEX).name("Gingerbread Alex").lore("&ePugmas 2025 Item").amount(32).build(),
		new ItemBuilder(ItemModelType.COOKIES_GINGER_STEVE).name("Gingerbread Steve").lore("&ePugmas 2025 Item").amount(32).build()
	)),
	TIER_3(14, List.of(
		CouponCommand.getGenericCoupon("mcmmo", 5),
		CouponCommand.getGenericCoupon("costume", 1),
		CouponCommand.getGenericCoupon("event_tokens", 125),
		CouponCommand.getGenericCoupon("eco", 3000),
		CouponCommand.getGenericCoupon("song", 1),
		DecorationType.BAUBLE_ORNAMENT.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(8).build(),
		DecorationType.BAUBLE_ACCENT_ORNAMENT.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(8).build(),
		DecorationType.CANDY_CANE_ORNAMENT.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(8).build(),
		DecorationType.GINGERBREAD_ORNAMENT.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(8).build(),
		DecorationType.SNOWFLAKE_ORNAMENT.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(8).build(),
		DecorationType.SNOWMAN_ORNAMENT.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(8).build(),
		DecorationType.STAR_ORNAMENT.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(8).build(),
		DecorationType.NUTCRACKER_TALL.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(4).build(),
		DecorationType.GIANT_CANDLE_THREE_UNLIT_DYEABLE.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(4).build(),
		DecorationType.GIANT_CANDY_CANE.getConfig().getItemBuilder().lore("&ePugmas 2025 Item").amount(4).build(),
		new ItemBuilder(Material.WHITE_SHULKER_BOX).name("&f&oPortable Igloo").build(),
		new ItemBuilder(ItemModelType.PUGMAS21_CANDY_CANE_GREEN).name("Green Candy Cane").lore("&ePugmas 2025 Item").amount(64).build(),
		new ItemBuilder(ItemModelType.PUGMAS21_CANDY_CANE_RED).name("Red Candy Cane").lore("&ePugmas 2025 Item").amount(64).build(),
		new ItemBuilder(ItemModelType.PUGMAS21_CANDY_CANE_YELLOW).name("Yellow Candy Cane").lore("&ePugmas 2025 Item").amount(64).build(),
		new ItemBuilder(ItemModelType.COOKIES_GINGER_CREEPER).name("Gingerbread Creeper").lore("&ePugmas 2025 Item").amount(64).build(),
		new ItemBuilder(ItemModelType.COOKIES_GINGER_ALEX).name("Gingerbread Alex").lore("&ePugmas 2025 Item").amount(64).build(),
		new ItemBuilder(ItemModelType.COOKIES_GINGER_STEVE).name("Gingerbread Steve").lore("&ePugmas 2025 Item").amount(64).build()

	)),
	;

	private final int max;
	@Getter
	private final List<ItemStack> items;

	Pugmas25GiftGiverReward(List<ItemStack> items) {
		this.max = Integer.MAX_VALUE;
		this.items = items;
	}

	Pugmas25GiftGiverReward(int max, List<ItemStack> items) {
		this.max = max;
		this.items = items;
	}

	public static Pugmas25GiftGiverReward of(ItemStack item) {
		return of(Pugmas25GiftGiver.getPlayerHistory(item).size());
	}

	public static Pugmas25GiftGiverReward of(int timesGifted) {
		for (Pugmas25GiftGiverReward reward : values())
			if (timesGifted <= reward.max)
				return reward;
		return null;
	}

	public List<ItemStack> getRandomItems() {
		int amount = RandomUtils.randomInt(2, 4);
		List<ItemStack> result = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			result.add(RandomUtils.randomElement(items));
		}

		return result;
	}
}

