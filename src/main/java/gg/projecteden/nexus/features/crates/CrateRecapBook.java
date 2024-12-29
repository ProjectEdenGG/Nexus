package gg.projecteden.nexus.features.crates;

import com.google.api.client.util.Strings;
import com.google.common.collect.Lists;
import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.models.crate.CrateConfig.CrateLoot;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.utils.JsonBuilder;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrateRecapBook {

	CrateType type;
	int count = 0;
	Map<CrateLoot, Integer> amounts = new HashMap<>();

	public CrateRecapBook(CrateType type) {
		this.type = type;
	}

	public void add(CrateLoot loot) {
		this.count++;
		amounts.merge(loot, 1, (i, i2) -> i + 1);
	}

	/*
	 * This currently struggles with words that are longer than 20 characters
	 * This could be fixed, but going off this: https://i.redd.it/vhv1j2pu2p5z.png
	 * I don't think it will be much of an issue
	 */
	public void open(Player player) {
		List<Component> lines = new ArrayList<>();
		lines.add(new JsonBuilder("&d&lCrate Loot Recap").build());
		lines.add(new JsonBuilder("&0" + this.count + " crates opened").build());
		lines.add(Component.empty());
		lines.add(new JsonBuilder("&d&lRewards:").build());
		amounts.forEach((loot, value) -> {
			if (Strings.isNullOrEmpty(loot.getTitle())) {
				for (ItemStack item : loot.getItems()) {
					int amount = value * item.getAmount();
					List<String> rewardLines = gg.projecteden.nexus.utils.StringUtils.loreize("&3" + amount + " &6x &3" + StringUtils.camelCase(item.getType()), 20);
					for (String rewardLine : rewardLines)
						lines.add(new JsonBuilder(rewardLine).build());
				}
			} else {
				List<String> rewardLines = gg.projecteden.nexus.utils.StringUtils.loreize("&3" + value + " &6x &3" + loot.getTitle(), 20);
				for (String rewardLine : rewardLines) {
					JsonBuilder json = new JsonBuilder(rewardLine);
					if (loot.getItems().size() == 1)
						json.hover(loot.getItems().get(0));
					else if (!loot.getItems().isEmpty()) {
						List<String> lore = new ArrayList<>();
						for (ItemStack item : loot.getItems())
							lore.add("&3" + value * item.getAmount() + " &6x &3" + StringUtils.camelCase(item.getType()));
						json.hover(lore);
					}
					lines.add(json.build());
				}
			}
		});

		List<Component> pages = Lists.partition(lines, 14).stream().map(list -> {
			Component page = Component.empty();
			for (Component comp : list)
				page =  page.append(comp).append(Component.text("\n"));
			return page;
		}).toList();
		player.openBook(Book.builder().pages(pages).build());
	}


}
