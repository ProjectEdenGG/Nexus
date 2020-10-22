package me.pugabyte.bncore.features.holidays.halloween20.quest.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.RandomUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FlashCardPuzzleProvider extends MenuUtils implements InventoryProvider {

	List<Material> validMaterials = Arrays.asList(Material.OAK_PLANKS, Material.STONE, Material.WHITE_TERRACOTTA,
			Material.DIAMOND_ORE, Material.IRON_ORE, Material.DIRT, Material.WHITE_WOOL, Material.GRASS_BLOCK,
			Material.OAK_LOG, Material.OAK_LEAVES);

	@Override
	public void init(Player player, InventoryContents contents) {
		List<Material> usedCards = new ArrayList<>();
		List<Material> correctOrder = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
			Material mat;
			do {
				mat = RandomUtils.randomElement(validMaterials);
			}
			while (usedCards.contains(mat));
			usedCards.add(mat);
		}
		for (int i = 0; i < 5; i++) {
			Material mat;
			do {
				mat = RandomUtils.randomElement(usedCards);
			}
			while (correctOrder.contains(mat));
			correctOrder.add(mat);
			contents.set(1, i + 2, ClickableItem.empty(new ItemBuilder(correctOrder.get(i)).name(" ").build()));
		}

		Collections.shuffle(usedCards);
		Tasks.wait(Time.SECOND.x(5), () -> {
			int i = 1;
			AtomicInteger index = new AtomicInteger(0);
			for (Material mat : usedCards)
				contents.set(1, i++, ClickableItem.from(new ItemBuilder(mat).name(" ").build(), e -> {
					if (e.getItem().getType() != correctOrder.get(index.getAndIncrement())) {
						contents.fill(ClickableItem.empty(new ItemBuilder(Material.RED_WOOL).name("&cIncorrect").build()));
						Tasks.wait(Time.SECOND.x(2), () -> Halloween20Menus.openFlashCardPuzzle(player));
					} else {
						addGlowing(e.getItem());
						if (index.get() == 5)
							complete(player);
					}
				}));
		});

	}

	public void complete(Player player) {
		player.closeInventory();
		;
		player.sendMessage("complete");
		// TODO
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {
	}
}
