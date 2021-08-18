package gg.projecteden.nexus.features.wither;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.wither.models.WitherFight;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class DifficultySelectionMenu extends MenuUtils implements InventoryProvider {

	@Override
	public void open(Player player) {
		SmartInventory.builder()
			.size(3, 9)
			.provider(this)
			.title("Select Difficulty")
			.build()
			.open(player);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		int row = 1;
		int column = 1;
		for (WitherChallenge.Difficulty difficulty : WitherChallenge.Difficulty.values()) {
			ItemStack item = new ItemBuilder(difficulty.menuMaterial).name(difficulty.getTitle()).lore(difficulty.description).build();
			contents.set(row, column, ClickableItem.from(item, e -> {
				WitherFight fight;
				try {
					fight = difficulty.witherFightClass.getConstructor().newInstance();
				} catch (Exception ex) {
					ex.printStackTrace();
					return;
				}
				fight.setHost(player.getUniqueId());
				fight.setParty(new ArrayList<>() {{
					add(player.getUniqueId());
				}});
				WitherChallenge.currentFight = fight;
				player.closeInventory();
				WitherChallenge.queue.remove(player.getUniqueId());
				JsonBuilder builder = new JsonBuilder(WitherChallenge.PREFIX + "You have challenge the wither in " + difficulty.getTitle() + " &3mode. ");
				builder.next("&3You can invite players to fight the Wither with you with &c/wither invite <player>&3.").suggest("/wither invite ").group();
				builder.next(" &3Once you are ready, ").next("&e&lClick Here to Start").command("/wither start").hover("&eThis will teleport you\n&eto the wither arena.");
				PlayerUtils.send(player, builder);
			}));
			column += 2;
		}

	}
}
