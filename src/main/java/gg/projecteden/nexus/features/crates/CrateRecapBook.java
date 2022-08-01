package gg.projecteden.nexus.features.crates;

import com.google.api.client.util.Strings;
import com.google.common.collect.Lists;
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

import static gg.projecteden.api.common.utils.StringUtils.camelCase;

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

	public void open(Player player) {
		List<Component> lines = new ArrayList<>();
		lines.add(new JsonBuilder("&d&lCrate Loot Recap").build());
		lines.add(new JsonBuilder("&0" + this.count + " crates opened").build());
		lines.add(Component.empty());
		lines.add(new JsonBuilder("&d&lRewards:").build());
		for (Map.Entry<CrateLoot, Integer> entry : amounts.entrySet()) {
			if (Strings.isNullOrEmpty(entry.getKey().getTitle())) {
				for (ItemStack item : entry.getKey().getItems()) {
					int amount = entry.getValue() * item.getAmount();
					lines.add(new JsonBuilder("&3" + amount + " x " + camelCase(item.getType())).build());
				}
			}
			else {
				JsonBuilder json = new JsonBuilder("&3" + entry.getValue() +  " &5x " + entry.getKey().getTitle());
				if (entry.getKey().getItems().size() == 1)
					json.hover(entry.getKey().getItems().get(0));
				else {
					List<String> lore = new ArrayList<>();
					for (ItemStack item : entry.getKey().getItems())
						lore.add("&3" + entry.getValue() * item.getAmount() + " &5x &3" + camelCase(item.getType()));
					json.hover(lore);
				}
				lines.add(json.build());
			}
		}
		List<Component> pages = Lists.partition(lines, 14).stream().map(list -> {
			Component page = Component.empty();
			for (Component comp : list)
				page =  page.append(Component.text("\n")).append(comp);
			return page;
		}).toList();
		player.openBook(Book.builder().pages(pages).build());
	}


}
