package gg.projecteden.nexus.features.events.y2024.pugmas24.fairgrounds.slotmachine;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.commands.staff.HealCommand;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24;
import gg.projecteden.nexus.features.events.y2024.pugmas24.Pugmas24.Pugmas24DeathCause;
import gg.projecteden.nexus.features.events.y2024.pugmas24.quests.Pugmas24QuestItem;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.models.CustomSound;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.FireworkLauncher;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.SoundBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@AllArgsConstructor
public enum Pugmas24SlotMachineReward {
	JACKPOT(SlotPos.of(1, 2), new ItemBuilder(Material.PRIZE_POTTERY_SHERD).name("&bTODO")
		.lore("&3Half: &eTODO", "&3Full: &eTODO"),
		(player) -> {
			Pugmas24SlotMachine.get().send("TODO HALF REWARD - JACKPOT");
		},
		(player) -> {
			Pugmas24SlotMachine.get().send("TODO FULL REWARD - JACKPOT");
		}
	),

	HEARTS(SlotPos.of(2, 2), new ItemBuilder(Material.HEART_POTTERY_SHERD).name("&bHeart Crystals")
		.lore("&3Half: &a2 &eHeart Crystals", "&3Full: &a5 &eHeart Crystals"),
		(player) -> Pugmas24SlotMachine.get().give(Pugmas24QuestItem.HEART_CRYSTAL.getItemBuilder().amount(2)),
		(player) -> Pugmas24SlotMachine.get().give(Pugmas24QuestItem.HEART_CRYSTAL.getItemBuilder().amount(5))
	),

	COINS(SlotPos.of(3, 2), new ItemBuilder(Material.ARMS_UP_POTTERY_SHERD).name("&bCoins")
		.lore("&3Half: &eTODO", "&3Full: &eTODO"),
		(player) -> {
			Pugmas24SlotMachine.get().send(player, "TODO HALF REWARD - CURRENCY");
		},
		(player) -> {
			Pugmas24SlotMachine.get().send(player, "TODO FULL REWARD - CURRENCY");
		}
	),

	PICKAXE(SlotPos.of(1, 4), new ItemBuilder(Material.MINER_POTTERY_SHERD).name("&dRandom Pickaxe Enchant")
		.lore("&3Half: &eUncommon", "&3Full: &eRare", "", "&eEnchants&3: "),
		(player) -> {
			Pugmas24SlotMachine.get().send(player, "TODO HALF REWARD - PICKAXE");
		},
		(player) -> {
			Pugmas24SlotMachine.get().send(player, "TODO FULL REWARD - PICKAXE");
		}
	),

	FISHING_ROD(SlotPos.of(2, 4), new ItemBuilder(Material.ANGLER_POTTERY_SHERD).name("&dRandom Fishing Rod Enchant")
		.lore("&3Half: &eUncommon", "&3Full: &eRare", "", "&eEnchants&3: "),
		(player) -> {
			Pugmas24SlotMachine.get().send(player, "TODO HALF REWARD - FISHING_ROD");
		},
		(player) -> {
			Pugmas24SlotMachine.get().send(player, "TODO FULL REWARD - FISHING_ROD");
		}
	),

	SWORD(SlotPos.of(3, 4), new ItemBuilder(Material.BLADE_POTTERY_SHERD).name("&dRandom Sword Enchant")
		.lore("&3Half: &eUncommon", "&3Full: &eRare", "", "&eEnchants&3: "),
		(player) -> {
			Pugmas24SlotMachine.get().send(player, "TODO HALF REWARD - SWORD");
		},
		(player) -> {
			Pugmas24SlotMachine.get().send(player, "TODO FULL REWARD - SWORD");
		}
	),

	INSTANT_DEATH(SlotPos.of(1, 6), new ItemBuilder(Material.DANGER_POTTERY_SHERD).name("&cInstant Death")
		.lore("&3Half: &aHealth &3set to &c50%", "&3Full: &aHealth &3set to &c0%"),
		(player) -> player.setHealth(player.getHealth() / 2),
		(player) -> Pugmas24.get().onDeath(player, Pugmas24DeathCause.INSTANT_DEATH)
	),

	HALF_MAX_HEALTH(SlotPos.of(2, 6), new ItemBuilder(Material.HEARTBREAK_POTTERY_SHERD).name("&cHalve Max Health")
		.lore("&3Half: &aMax Health &3set to &c75%", "&3Full: &aMax Health &3set to &c50%"),
		(player) -> Pugmas24.get().setMaxHealth(player, HealCommand.getMaxHealth(player) * 0.75),
		(player) -> Pugmas24.get().setMaxHealth(player, HealCommand.getMaxHealth(player) * 0.50)
	),

	HALF_CURRENCY(SlotPos.of(3, 6), new ItemBuilder(Material.MOURNER_POTTERY_SHERD).name("&cHalve Currency")
		.lore("&3Half: &aCoin Pouch &3set to &c75% Coins", "&3Full: &aCoin Pouch &3set to &c50% Coins"),
		(player) -> {
			Pugmas24SlotMachine.get().send(player, "TODO HALF REWARD - HALF_CURRENCY");
		},
		(player) -> {
			Pugmas24SlotMachine.get().send(player, "TODO FULL REWARD - HALF_CURRENCY");
		}
	),
	;

	@AllArgsConstructor
	enum Pugmas24SlotMachineRewardEnchant {
		PICKAXE(List.of(Enchant.FORTUNE, Enchant.EFFICIENCY)),
		SWORD(List.of(Enchant.LOOTING, Enchant.SHARPNESS)),
		FISHING_ROD(List.of(Enchant.LUCK_OF_THE_SEA, Enchant.LURE));

		final List<Enchantment> enchants;
		private final List<Enchantment> sharedEnchants = List.of(Enchant.MENDING, Enchant.UNBREAKING);

		public static Pugmas24SlotMachineRewardEnchant of(Pugmas24SlotMachineReward reward) {
			return switch (reward) {
				case PICKAXE -> PICKAXE;
				case SWORD -> SWORD;
				case FISHING_ROD -> FISHING_ROD;
				default -> null;
			};
		}

		public List<Enchantment> getEnchants() {
			List<Enchantment> result = new ArrayList<>(sharedEnchants);
			result.addAll(this.enchants);
			return result;
		}

		public List<String> getEnchantStrings() {
			List<String> result = new ArrayList<>();
			for (Enchantment enchant : getEnchants()) {
				result.add(enchant.getKey().getKey());
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

	public static @Nullable Pugmas24SlotMachineReward of(Material sherd) {
		for (Pugmas24SlotMachineReward reward : values()) {
			if (reward.getDisplayItem().material() == sherd)
				return reward;
		}
		return null;
	}

	private final List<Location> fireworkLocations = List.of(
		Pugmas24SlotMachine.get().location(-732.5, 82.2, -2907.5),
		Pugmas24SlotMachine.get().location(-736.5, 82.2, -2907.5));

	public void give(Player player, Pugmas24SlotMachineRewardType type) {
		switch (type) {
			case HALF -> {
				new SoundBuilder(Sound.ENTITY_VILLAGER_CELEBRATE).location(Pugmas24SlotMachine.get().soundLocation).volume(0.5).play();
				halfReward.accept(player);
			}
			case FULL -> {
				new SoundBuilder(CustomSound.PARTY_HOORAY).location(Pugmas24SlotMachine.get().soundLocation).volume(0.5).play();
				fireworkLocations.forEach(location -> new FireworkLauncher(location).power(1).detonateAfter(TickTime.TICK.x(13)).rainbow().flickering(true).type(Type.BALL).launch());
				fullReward.accept(player);
			}
		}
	}

	public enum Pugmas24SlotMachineRewardType {
		FULL, HALF;
	}
}
