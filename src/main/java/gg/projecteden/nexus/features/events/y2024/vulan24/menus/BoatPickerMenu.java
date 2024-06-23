package gg.projecteden.nexus.features.events.y2024.vulan24.menus;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Rows(3)
@Title("Pick a boat")
public class BoatPickerMenu extends InventoryProvider {

	@Override
	public void init() {
		addCloseItem();

		List<ClickableItem> items = new ArrayList<>();
		for (Material boatType : MaterialTag.BOATS.getValues()) {
			ItemBuilder boat = new ItemBuilder(boatType);
			items.add(ClickableItem.of(boat.build(), e -> {
				replaceBoat(e.getPlayer(), boatType);
			}));
		}

		paginate(items);
	}

	private void replaceBoat(Player player, Material boatType) {
		// TODO: Take old boat from player
		// TODO: Save new boat to database
		PlayerUtils.giveItem(player, boatType);
	}
}
