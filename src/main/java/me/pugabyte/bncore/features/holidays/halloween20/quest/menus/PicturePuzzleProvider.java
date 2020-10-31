package me.pugabyte.bncore.features.holidays.halloween20.quest.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import me.pugabyte.bncore.features.holidays.halloween20.Halloween20;
import me.pugabyte.bncore.features.holidays.halloween20.models.ComboLockNumber;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.halloween20.Halloween20Service;
import me.pugabyte.bncore.models.halloween20.Halloween20User;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PicturePuzzleProvider extends MenuUtils implements InventoryProvider {

	List<Integer> correct = new ArrayList<>();
	ComboLockNumber number;

	public PicturePuzzleProvider(ComboLockNumber number) {
		this.number = number;
	}

	public void setYellow(SlotPos pos, InventoryContents contents, Player player) {
		contents.set(pos, ClickableItem.from(new ItemBuilder(Material.YELLOW_WOOL).name(" ").build(), e -> {
			setLime(pos, contents, player);
			if (parse(contents))
				complete(player);
		}));
	}

	public void setLime(SlotPos pos, InventoryContents contents, Player player) {
		contents.set(pos, ClickableItem.from(new ItemBuilder(Material.LIME_WOOL).name(" ").build(), e -> {
			setYellow(pos, contents, player);
			if (parse(contents))
				complete(player);
		}));
	}

	public boolean parse(InventoryContents contents) {
		for (int i = 0; i < 54; i++) {
			ClickableItem item = contents.get(i).orElse(null);
			if (item == null) return false;
			if (item.getItem().getType() == Material.YELLOW_WOOL)
				if (correct.contains(i)) return false;
			if (item.getItem().getType() == Material.LIME_WOOL)
				if (!correct.contains(i)) return false;
		}
		return true;
	}

	public void complete(Player player) {
		player.closeInventory();
		Halloween20Service service = new Halloween20Service();
		Halloween20User user = service.get(player);
		user.getFoundComboLockNumbers().add(number);
		Utils.send(player, Halloween20.PREFIX + "You have found " + user.getFoundComboLockNumbers().size() + "/11 numbers for the combination lock.");
		service.save(user);
		if (user.getFoundComboLockNumbers().size() == 11)
			Utils.send(player, Halloween20.PREFIX + "You have found all the numbers for the combination lock. Return to see if you can crack the code!");
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		for (int i = 0; i < 10; i++) {
			int random;
			do {
				random = (int) (Math.random() * 54);
			} while (correct.contains(random));
			correct.add(random);
		}

		contents.fill(ClickableItem.empty(new ItemBuilder(Material.YELLOW_WOOL).name(" ").build()));

		Tasks.wait(Time.SECOND, () -> {
			for (int i : correct)
				contents.set(i, ClickableItem.empty(new ItemBuilder(Material.LIME_WOOL).name(" ").build()));
		});

		Tasks.wait(Time.SECOND.x(5), () -> {
			int row = 0;
			int column = 0;
			while (row < 7) {
				setYellow(SlotPos.of(row, column), contents, player);
				if (column != 8)
					column++;
				else {
					row++;
					column = 0;
				}
			}
		});
	}

	@Override
	public void update(Player player, InventoryContents contents) {

	}
}
