package me.pugabyte.bncore.features.holidays.halloween20.quest.menus;

import fr.minuskube.inv.SmartInventory;
import org.bukkit.entity.Player;

public class Halloween20Menus {

	public static void openPicturePuzzle(Player player) {
		SmartInventory.builder()
				.title("Picture Puzzle")
				.size(6, 9)
				.provider(new PicturePuzzleProvider())
				.build().open(player);
	}

	public static void openFlashCardPuzzle(Player player) {
		SmartInventory.builder()
				.title("Flash Card Puzzle")
				.size(3, 9)
				.provider(new FlashCardPuzzleProvider())
				.build().open(player);
	}

	public static void openComboLock(Player player) {
		SmartInventory.builder()
				.title("Combination Lock")
				.size(6, 9)
				.provider(new CombinationLockProvider())
				.build().open(player);
	}

}
