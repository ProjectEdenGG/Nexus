package gg.projecteden.nexus.features.wither;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.wither.models.WitherFight;
import gg.projecteden.nexus.models.witherarena.WitherArenaConfigService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Title("Select Difficulty")
@Rows(3)
public class DifficultySelectionMenu extends InventoryProvider {

	@Override
	public void init() {
		int row = 1;
		int column = 1;
		for (WitherChallenge.Difficulty difficulty : WitherChallenge.Difficulty.values()) {
			ItemStack item = new ItemBuilder(difficulty.getMenuMaterial()).name(difficulty.getTitle()).lore(difficulty.getDescription()).build();
			contents.set(row, column, ClickableItem.of(item, e -> {
				WitherFight fight;
				try {
					fight = difficulty.getWitherFightClass().getConstructor().newInstance();
				} catch (Exception ex) {
					ex.printStackTrace();
					return;
				}
				fight.setHost(viewer.getUniqueId());
				fight.setParty(new ArrayList<>(List.of(viewer.getUniqueId())));
				WitherChallenge.currentFight = fight;
				viewer.closeInventory();

				new WitherArenaConfigService().edit0(config -> config.getQueue().remove(viewer.getUniqueId()));
				JsonBuilder builder = new JsonBuilder(WitherChallenge.PREFIX + "You have challenged the wither in " + difficulty.getTitle() + " &3mode. ");
				builder.next("&3You can invite players to fight the Wither with you with &c/wither invite <player>&3.").suggest("/wither invite ").group();
				builder.next(" &3Once you are ready, ").next("&e&lClick Here to Start").command("/wither start").hover("&eThis will teleport you to the wither arena.");
				PlayerUtils.send(viewer, builder);
			}));
			column += 2;
		}

	}
}
