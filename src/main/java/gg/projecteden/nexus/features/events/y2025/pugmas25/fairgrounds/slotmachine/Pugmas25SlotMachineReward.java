package gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.slotmachine;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestItem;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import gg.projecteden.nexus.utils.Currency;
import gg.projecteden.nexus.utils.Currency.Price;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.FireworkLauncher;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@AllArgsConstructor
public enum Pugmas25SlotMachineReward {

	// TODO: SLOT MACHINE REWARDS - COINS

	VOTE_POINTS(SlotPos.of(1, 2), new ItemBuilder(Material.MOURNER_POTTERY_SHERD).name("&aVote Points")
		.lore("&3Half: &e25 &3Vote Points", "&3Full: &e50 &3Vote Points"),
		(player) -> {
			int preAmount = 25;
			int maxAmount = 50;
			int finalAmount = Pugmas25.getLuckyHorseshoeAmount(player, preAmount, maxAmount);
			int additional = finalAmount - preAmount;
			Currency.VOTE_POINTS.deposit(player, Price.of(finalAmount));
			if (additional > 0)
				PlayerUtils.send(player, Pugmas25SlotMachine.PREFIX + "&3+&e" + additional + " &3vote points given from Lucky Horseshoe");
		},
		(player) -> {
			int preAmount = 50;
			int maxAmount = 75;
			int finalAmount = Pugmas25.getLuckyHorseshoeAmount(player, preAmount, maxAmount);
			int additional = finalAmount - preAmount;
			Currency.VOTE_POINTS.deposit(player, Price.of(finalAmount));
			if (additional > 0)
				PlayerUtils.send(player, Pugmas25SlotMachine.PREFIX + "&3+&e" + additional + " &3vote points given from Lucky Horseshoe");
		}
	),

	TOKENS(SlotPos.of(2, 2), new ItemBuilder(Material.PRIZE_POTTERY_SHERD).name("&aEvent Tokens")
		.lore("&3Half: &e250 &3event tokens", "&3Full: &e500 &3event tokens"),
		(player) -> {
			int preAmount = 250;
			int maxAmount = 500;
			int finalAmount = Pugmas25.getLuckyHorseshoeAmount(player, preAmount, maxAmount);
			int additional = finalAmount - preAmount;
			Currency.EVENT_TOKENS.deposit(player, Price.of(finalAmount));
			if (additional > 0)
				PlayerUtils.send(player, Pugmas25SlotMachine.PREFIX + "&3+&e" + additional + " &3event tokens given from Lucky Horseshoe");
		},
		(player) -> {
			int preAmount = 500;
			int maxAmount = 750;
			int finalAmount = Pugmas25.getLuckyHorseshoeAmount(player, preAmount, maxAmount);
			int additional = finalAmount - preAmount;
			Currency.EVENT_TOKENS.deposit(player, Price.of(finalAmount));
			if (additional > 0)
				PlayerUtils.send(player, Pugmas25SlotMachine.PREFIX + "&3+&e" + additional + " &3event tokens given from Lucky Horseshoe");
		}
	),

	EXTRA_ROLLS(SlotPos.of(3, 2), new ItemBuilder(Material.HEART_POTTERY_SHERD).name("&aExtra Rolls")
		.lore("&3Half: &e1 &3re-roll", "&3Full: &e3 &3re-rolls"),
		(player) -> {
			int preAmount = 1;
			int maxAmount = 3;
			int finalAmount = Pugmas25.getLuckyHorseshoeAmount(player, preAmount, maxAmount);
			int additional = finalAmount - preAmount;
			PlayerUtils.giveItem(player, Pugmas25QuestItem.SLOT_MACHINE_TOKEN.getItemBuilder().clone().amount(finalAmount).build());
			if (additional > 0)
				PlayerUtils.send(player, Pugmas25SlotMachine.PREFIX + "&3+&e" + additional + " &3re-rolls given from Lucky Horseshoe");
		},
		(player) -> {
			int preAmount = 3;
			int maxAmount = 5;
			int finalAmount = Pugmas25.getLuckyHorseshoeAmount(player, preAmount, maxAmount);
			int additional = finalAmount - preAmount;
			PlayerUtils.giveItem(player, Pugmas25QuestItem.SLOT_MACHINE_TOKEN.getItemBuilder().clone().amount(finalAmount).build());
			if (additional > 0)
				PlayerUtils.send(player, Pugmas25SlotMachine.PREFIX + "&3+&e" + additional + " &3re-rolls given from Lucky Horseshoe");
		}
	),

	//

	PICKAXE(SlotPos.of(1, 4), new ItemBuilder(Material.MINER_POTTERY_SHERD).name("&dRandom Pickaxe Enchant")
		.lore("&3Half: &eUncommon", "&3Full: &aRare", "", "&3Enchants: "),
		(player) -> PlayerUtils.giveItem(player, getEnchantedBook(Pugmas25SlotMachineRewardType.HALF, Enchantment.EFFICIENCY, Enchantment.FORTUNE)),
		(player) -> PlayerUtils.giveItem(player, getEnchantedBook(Pugmas25SlotMachineRewardType.FULL, Enchantment.EFFICIENCY, Enchantment.FORTUNE))
	),

	FISHING_ROD(SlotPos.of(2, 4), new ItemBuilder(Material.ANGLER_POTTERY_SHERD).name("&dRandom Fishing Rod Enchant")
		.lore("&3Half: &eUncommon", "&3Full: &aRare", "", "&3Enchants: "),
		(player) -> PlayerUtils.giveItem(player, getEnchantedBook(Pugmas25SlotMachineRewardType.HALF, Enchantment.LURE, Enchantment.LUCK_OF_THE_SEA)),
		(player) -> PlayerUtils.giveItem(player, getEnchantedBook(Pugmas25SlotMachineRewardType.FULL, Enchantment.LURE, Enchantment.LUCK_OF_THE_SEA))
	),

	SWORD(SlotPos.of(3, 4), new ItemBuilder(Material.BLADE_POTTERY_SHERD).name("&dRandom Sword Enchant")
		.lore("&3Half: &eUncommon", "&3Full: &aRare", "", "&3Enchants: "),
		(player) -> PlayerUtils.giveItem(player, getEnchantedBook(Pugmas25SlotMachineRewardType.HALF, Enchantment.SHARPNESS, Enchantment.LOOTING)),
		(player) -> PlayerUtils.giveItem(player, getEnchantedBook(Pugmas25SlotMachineRewardType.FULL, Enchantment.SHARPNESS, Enchantment.LOOTING))
	),

	//

	COINS(SlotPos.of(1, 6), new ItemBuilder(Material.ARMS_UP_POTTERY_SHERD).name("&6Coins")
		.lore("&3Half: &e10 &3coins", "&3Full: &e30 &3coins"), // TODO: Coins
		(player) -> {
			int preAmount = 10; // TODO: COINS
			int maxAmount = 30; // TODO: COINS
			int finalAmount = Pugmas25.getLuckyHorseshoeAmount(player, preAmount, maxAmount);
			int additional = finalAmount - preAmount;
			PlayerUtils.send(player, Pugmas25SlotMachine.PREFIX + "&3Deposited &e" + additional + " &3coins to your Coin Pouch");
			Currency.COIN_POUCH.deposit(player, Price.of(finalAmount));
			if (additional > 0)
				PlayerUtils.send(player, Pugmas25SlotMachine.PREFIX + "&3+&e" + additional + " &3coins given from Lucky Horseshoe");
		},
		(player) -> {
			int preAmount = 30; // TODO: COINS
			int maxAmount = 50; // TODO: COINS
			int finalAmount = Pugmas25.getLuckyHorseshoeAmount(player, preAmount, maxAmount);
			int additional = finalAmount - preAmount;
			PlayerUtils.send(player, Pugmas25SlotMachine.PREFIX + "&3Deposited &e" + additional + " &3coins to your Coin Pouch");
			Currency.COIN_POUCH.deposit(player, Price.of(finalAmount));
			if (additional > 0)
				PlayerUtils.send(player, Pugmas25SlotMachine.PREFIX + "&3+&e" + additional + " &3coins given from Lucky Horseshoe");
		}
	),

	DIAMOND_TRUNK(SlotPos.of(2, 6), new ItemBuilder(Material.PLENTY_POTTERY_SHERD).name("&6Diamond Trunks")
		.lore("&3Half: &e2 &3Diamond Trunks", "&3Full: &e5 &3Diamond Trunks"),
		(player) -> {
			int preAmount = 2;
			int maxAmount = 3;
			int finalAmount = Pugmas25.getLuckyHorseshoeAmount(player, preAmount, maxAmount);
			int additional = finalAmount - preAmount;
			PlayerUtils.giveItem(player, Pugmas25QuestItem.TRUNK_DIAMOND.getItemBuilder().clone().amount(finalAmount).build());
			if (additional > 0)
				PlayerUtils.send(player, Pugmas25SlotMachine.PREFIX + "&3+&e" + additional + " &3diamond trunks given from Lucky Horseshoe");
		},
		(player) -> {
			int preAmount = 3;
			int maxAmount = 5;
			int finalAmount = Pugmas25.getLuckyHorseshoeAmount(player, preAmount, maxAmount);
			int additional = finalAmount - preAmount;
			PlayerUtils.giveItem(player, Pugmas25QuestItem.TRUNK_DIAMOND.getItemBuilder().clone().amount(finalAmount).build());
			if (additional > 0)
				PlayerUtils.send(player, Pugmas25SlotMachine.PREFIX + "&3+&e" + additional + " &3diamond trunks given from Lucky Horseshoe");
		}
	),

	GIFTS(SlotPos.of(3, 6), new ItemBuilder(Material.FRIEND_POTTERY_SHERD).name("&6Gifts")
		.lore("&3Half: &e1 &3Gift", "&3Full: &e3 &3Gifts"),
		(player) -> {
			int preAmount = 1;
			int maxAmount = 3;
			int finalAmount = Pugmas25.getLuckyHorseshoeAmount(player, preAmount, maxAmount);
			int additional = finalAmount - preAmount;
			PlayerUtils.giveItem(player, Pugmas25QuestItem.GIFT_INITIAL.getItemBuilder().clone().amount(finalAmount).build());
			if (additional > 0)
				PlayerUtils.send(player, Pugmas25SlotMachine.PREFIX + "&3+&e" + additional + " &3gifts given from Lucky Horseshoe");
		},
		(player) -> {
			int preAmount = 3;
			int maxAmount = 5;
			int finalAmount = Pugmas25.getLuckyHorseshoeAmount(player, preAmount, maxAmount);
			int additional = finalAmount - preAmount;
			PlayerUtils.giveItem(player, Pugmas25QuestItem.GIFT_INITIAL.getItemBuilder().clone().amount(finalAmount).build());
			if (additional > 0)
				PlayerUtils.send(player, Pugmas25SlotMachine.PREFIX + "&3+&e" + additional + " &3gifts given from Lucky Horseshoe");
		}
	),
	;

	@AllArgsConstructor
	enum Pugmas25SlotMachineRewardEnchant {
		PICKAXE(Enchant.EFFICIENCY, Enchant.FORTUNE),
		FISHING_ROD(Enchant.LURE, Enchant.LUCK_OF_THE_SEA),
		SWORD(Enchant.SHARPNESS, Enchant.LOOTING),
		;

		final Enchantment uncommon;
		final Enchantment rare;

		public static Pugmas25SlotMachineRewardEnchant of(Pugmas25SlotMachineReward reward) {
			return switch (reward) {
				case PICKAXE -> PICKAXE;
				case FISHING_ROD -> FISHING_ROD;
				case SWORD -> SWORD;
				default -> null;
			};
		}

		public List<Enchantment> getUncommonEnchants() {
			return new ArrayList<>(List.of(this.uncommon, Enchant.UNBREAKING));
		}

		public List<Enchantment> getRareEnchants() {
			return new ArrayList<>(List.of(this.rare, Enchant.MENDING));
		}

		public List<String> getEnchantStrings() {
			List<String> result = new ArrayList<>();
			for (Enchantment uncommon : getUncommonEnchants()) {
				result.add("&e" + StringUtils.camelCase(uncommon.getKey().getKey()));
			}

			for (Enchantment rare : getRareEnchants()) {
				result.add("&a" + StringUtils.camelCase(rare.getKey().getKey()));
			}

			return result;
		}
	}

	@Getter
	final SlotPos displaySlot;
	final ItemBuilder displayItem;
	final Consumer<Player> halfReward;
	final Consumer<Player> fullReward;

	public ItemBuilder getDisplayItem() {
		return displayItem.clone();
	}

	public static List<Material> getAllSherds() {
		return Arrays.stream(values()).map(reward -> reward.getDisplayItem().material()).toList();
	}

	public static @Nullable Pugmas25SlotMachineReward of(Material sherd) {
		for (Pugmas25SlotMachineReward reward : values()) {
			if (reward.getDisplayItem().material() == sherd)
				return reward;
		}
		return null;
	}

	private final List<Location> fireworkLocations = List.of(
		Pugmas25SlotMachine.get().location(-732.5, 82.2, -2907.5),
		Pugmas25SlotMachine.get().location(-736.5, 82.2, -2907.5));

	public void give(Player player, Pugmas25SlotMachineRewardType type) {
		switch (type) {
			case HALF -> {
				new SoundBuilder(Sound.ENTITY_VILLAGER_CELEBRATE).location(Pugmas25SlotMachine.get().soundLocation).volume(0.5).play();
				halfReward.accept(player);
			}
			case FULL -> {
				new SoundBuilder(CustomSound.PARTY_HOORAY).location(Pugmas25SlotMachine.get().soundLocation).volume(0.5).play();
				fireworkLocations.forEach(location -> new FireworkLauncher(location).power(1).detonateAfter(TickTime.TICK.x(13)).rainbow().flickering(true).type(Type.BALL).launch());
				fullReward.accept(player);
			}
		}
	}

	public enum Pugmas25SlotMachineRewardType {
		FULL, HALF;
	}

	private static ItemStack getEnchantedBook(Pugmas25SlotMachineRewardType type, Enchantment uncommon, Enchantment rare) {
		List<Enchantment> enchantments = new ArrayList<>(List.of(uncommon, Enchantment.UNBREAKING));
		if (type == Pugmas25SlotMachineRewardType.FULL)
			enchantments = new ArrayList<>(List.of(rare, Enchantment.MENDING));

		Enchantment enchant = RandomUtils.randomElement(enchantments);
		int max = enchant.getMaxLevel();
		int level = 1;
		if (max > 1) {
			if (type == Pugmas25SlotMachineRewardType.HALF) {
				level = RandomUtils.randomInt(1, (int) Math.ceil((1 + max) / 2.0));
			} else {
				level = max;
			}
		}

		return new ItemBuilder(Material.ENCHANTED_BOOK).enchant(enchant, level).build();
	}
}
