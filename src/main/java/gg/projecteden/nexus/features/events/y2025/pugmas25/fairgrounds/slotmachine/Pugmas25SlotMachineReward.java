package gg.projecteden.nexus.features.events.y2025.pugmas25.fairgrounds.slotmachine;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
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
public enum Pugmas25SlotMachineReward {

	VOTE_POINTS(SlotPos.of(1, 2), new ItemBuilder(Material.MOURNER_POTTERY_SHERD).name("&aVote Points")
		.lore("&3Half: &e25 &3Vote Points", "&3Full: &e50 &3Vote Points"),
		(player) -> Pugmas25SlotMachine.get().send(player, "TODO HALF REWARD - COSTUME"),
		(player) -> Pugmas25SlotMachine.get().send(player, "TODO FULL REWARD - COSTUME")
	),

	TOKENS(SlotPos.of(2, 2), new ItemBuilder(Material.PRIZE_POTTERY_SHERD).name("&aEvent Tokens")
		.lore("&3Half: &ex &3event tokens", "&3Full: &ex+n &3event tokens"),
		(player) -> Pugmas25SlotMachine.get().send("TODO HALF REWARD - TOKENS"),
		(player) -> Pugmas25SlotMachine.get().send("TODO FULL REWARD - TOKENS")
	),

	EXTRA_ROLLS(SlotPos.of(3, 2), new ItemBuilder(Material.HEART_POTTERY_SHERD).name("&aExtra Rolls")
		.lore("&3Half: &e1 &3re-roll", "&3Full: &e3 &3re-rolls"),
		(player) -> Pugmas25SlotMachine.get().send(player, "TODO HALF REWARD - EXTRA_ROLLS"),
		(player) -> Pugmas25SlotMachine.get().send(player, "TODO FULL REWARD - EXTRA_ROLLS")
	),

	//

	PICKAXE(SlotPos.of(1, 4), new ItemBuilder(Material.MINER_POTTERY_SHERD).name("&dRandom Pickaxe Enchant")
		.lore("&3Half: &eUncommon", "&3Full: &eRare", "", "&eEnchants&3: "),
		(player) -> Pugmas25SlotMachine.get().send(player, "TODO HALF REWARD - PICKAXE"),
		(player) -> Pugmas25SlotMachine.get().send(player, "TODO FULL REWARD - PICKAXE")
	),

	FISHING_ROD(SlotPos.of(2, 4), new ItemBuilder(Material.ANGLER_POTTERY_SHERD).name("&dRandom Fishing Rod Enchant")
		.lore("&3Half: &eUncommon", "&3Full: &eRare", "", "&eEnchants&3: "),
		(player) -> Pugmas25SlotMachine.get().send(player, "TODO HALF REWARD - FISHING_ROD"),
		(player) -> Pugmas25SlotMachine.get().send(player, "TODO FULL REWARD - FISHING_ROD")
	),

	EXPERIENCE(SlotPos.of(3, 4), new ItemBuilder(Material.BREWER_POTTERY_SHERD).name("&dExperience")
		.lore("&3Half: &e10 Levels", "&3Full: &e20 Levels"),
		(player) -> player.giveExp(10, true),
		(player) -> player.giveExp(20, true)
	),

	//

	COINS(SlotPos.of(1, 6), new ItemBuilder(Material.ARMS_UP_POTTERY_SHERD).name("&6Coins")
		.lore("&3Half: &ex &3coins", "&3Full: &ex+n &3coins"),
		(player) -> Pugmas25SlotMachine.get().send(player, "TODO HALF REWARD - COINS"),
		(player) -> Pugmas25SlotMachine.get().send(player, "TODO FULL REWARD - COINS")
	),

	DIAMOND_CRATE(SlotPos.of(2, 6), new ItemBuilder(Material.PLENTY_POTTERY_SHERD).name("&6Diamond Crates")
		.lore("&3Half: &e2 &3Diamond Crates", "&3Full: &e5 &3Diamond Crates"),
		(player) -> Pugmas25SlotMachine.get().send(player, "TODO HALF REWARD - DIAMOND_CRATE"),
		(player) -> Pugmas25SlotMachine.get().send(player, "TODO FULL REWARD - DIAMOND_CRATE")
	),

	GIFTS(SlotPos.of(3, 6), new ItemBuilder(Material.FRIEND_POTTERY_SHERD).name("&6Gifts")
		.lore("&3Half: &e1 &3Gift", "&3Full: &e3 &3Gifts"),
		(player) -> Pugmas25SlotMachine.get().send(player, "TODO HALF REWARD - GIFTS"),
		(player) -> Pugmas25SlotMachine.get().send(player, "TODO FULL REWARD - GIFTS")
	),
	;

	@AllArgsConstructor
	enum Pugmas25SlotMachineRewardEnchant {
		PICKAXE(List.of(Enchant.FORTUNE, Enchant.EFFICIENCY)),
		FISHING_ROD(List.of(Enchant.LUCK_OF_THE_SEA, Enchant.LURE));

		final List<Enchantment> enchants;
		private final List<Enchantment> sharedEnchants = List.of(Enchant.MENDING, Enchant.UNBREAKING);

		public static Pugmas25SlotMachineRewardEnchant of(Pugmas25SlotMachineReward reward) {
			return switch (reward) {
				case PICKAXE -> PICKAXE;
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
}
