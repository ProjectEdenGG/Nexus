package gg.projecteden.nexus.features.events.y2020.halloween20.quest.menus;

import fr.minuskube.inv.SmartInventory;
import gg.projecteden.nexus.features.events.y2020.halloween20.models.ComboLockNumber;
import org.bukkit.entity.Player;

public class Halloween20Menus {

	public static void openPicturePuzzle(Player player, ComboLockNumber number) {
		SmartInventory.builder()
				.title("Picture Puzzle")
				.size(6, 9)
				.provider(new PicturePuzzleProvider(number))
				.build().open(player);
	}

	public static void openFlashCardPuzzle(Player player, ComboLockNumber number) {
		SmartInventory.builder()
				.title("Flash Card Puzzle")
				.size(3, 9)
				.provider(new FlashCardPuzzleProvider(number))
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
