package me.pugabyte.nexus.features.events.y2020.pugmas20.quests;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.pugabyte.nexus.features.events.models.QuestStage;
import me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20;
import me.pugabyte.nexus.models.pugmas20.Pugmas20User;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.SoundUtils;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class Quests {
	public static final String fullInvError = Pugmas20.getPREFIX() + "&cYour inventory is too full to open this!";
	public static final String leftoverItems = Pugmas20.getPREFIX() + "Giving leftover items...";

	public Quests() {
		new Reflections(getClass().getPackage().getName()).getSubTypesOf(Listener.class).forEach(Utils::tryRegisterListener);
	}

	@Accessors(fluent = true)
	public enum Pugmas20Quest {
		GIFT_GIVER(Pugmas20User::getGiftGiverStage, Pugmas20User::setGiftGiverStage),
		LIGHT_THE_TREE(Pugmas20User::getLightTreeStage, Pugmas20User::setLightTreeStage),
		ORNAMENT_VENDOR(Pugmas20User::getOrnamentVendorStage, Pugmas20User::setOrnamentVendorStage),
		THE_MINES(Pugmas20User::getMinesStage, Pugmas20User::setMinesStage),
		TOY_TESTING(Pugmas20User::getToyTestingStage, Pugmas20User::setToyTestingStage);

		@Getter
		private final Function<Pugmas20User, QuestStage> getter;
		@Getter
		private final BiConsumer<Pugmas20User, QuestStage> setter;

		Pugmas20Quest(Function<Pugmas20User, QuestStage> getQuestStage, BiConsumer<Pugmas20User, QuestStage> setQuestStage) {
			this.getter = getQuestStage;
			this.setter = setQuestStage;
		}
	}

	public static void sound_obtainItem(Player player) {
		SoundUtils.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 2F);
	}

	public static void sound_villagerNo(Player player) {
		SoundUtils.playSound(player, Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
	}

	public static void sound_npcAlert(Player player) {
		SoundUtils.playSound(player, Sound.BLOCK_NOTE_BLOCK_BIT, 0.5F, 1F);
	}

	public static boolean hasRoomFor(Player player, ItemStack... items) {
		List<ItemStack> itemList = new ArrayList<>();
		for (ItemStack item : new ArrayList<>(Arrays.asList(items))) {
			if (!ItemUtils.isNullOrAir(item))
				itemList.add(item);
		}

		return hasRoomFor(player, itemList.size());
	}

	public static boolean hasRoomFor(Player player, int slots) {
		ItemStack[] contents = player.getInventory().getContents();
		int slotsUsed = 0;
		for (ItemStack content : contents) {
			if (!ItemUtils.isNullOrAir(content))
				slotsUsed++;
		}

		return (slotsUsed <= (36 - slots));
	}
}
