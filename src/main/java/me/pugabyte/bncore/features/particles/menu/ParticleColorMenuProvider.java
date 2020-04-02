package me.pugabyte.bncore.features.particles.menu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.Getter;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ParticleColorMenuProvider extends MenuUtils implements InventoryProvider {

	SettingService service = new SettingService();

	@Getter
	enum ColorItem {
		RED(1, 0, ColorType.RED),
		ORANGE(1, 1, ColorType.ORANGE),
		YELLOW(1, 2, ColorType.YELLOW),
		LIME(2, 0, ColorType.LIGHT_GREEN),
		GREEN(2, 1, ColorType.GREEN),
		LIGHT_BLUE(2, 2, ColorType.LIGHT_BLUE),
		BLUE(3, 0, ColorType.BLUE),
		PURPLE(3, 1, ColorType.PURPLE),
		PINK(3, 2, ColorType.PINK);

		ColorType colorType;
		int column, row;

		ColorItem(int column, int row, ColorType colorType) {
			this.colorType = colorType;
			this.column = column;
			this.row = row;
		}

	}

	@Getter
	enum RGB {
		R(1),
		G(10),
		B(4);

		int data;

		RGB(int data) {
			this.data = data;
		}
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> ParticleMenu.openMain(player, 0));

		Setting rgb = service.get(player, "particlesRGB");
		Map<String, Object> json = rgb.getJson();
		if (!json.containsKey("r"))
			json = new HashMap<String, Object>() {{
				put("r", 0);
				put("g", 0);
				put("b", 0);
			}};
		rgb.setJson(json);
		service.save(rgb);

		ItemStack chestplate = nameItem(new ItemStack(Material.LEATHER_CHESTPLATE), "&fCurrent Color");
		LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
		meta.setColor(Color.fromRGB(Double.valueOf((double) json.get("r")).intValue(),
				Double.valueOf((double) json.get("g")).intValue(),
				Double.valueOf((double) json.get("b")).intValue()));
		chestplate.setItemMeta(meta);
		ItemBuilder.addLore(chestplate,
				"&cR: " + json.get("r"),
				"&aG: " + json.get("g"),
				"&bB: " + json.get("b"));

		contents.set(2, 4, ClickableItem.from(chestplate,
				e -> ParticleMenu.openColor(player)));

		for (ColorItem color : ColorItem.values()) {
			String name = color.getColorType().getChatColor() + StringUtils.camelCase(color.name().replace("_", " "));
			contents.set(color.getColumn(), color.getRow(), ClickableItem.from(
					new ItemBuilder(Material.INK_SACK).name(name).dyeColor(color.getColorType()).build(),
					e -> {
						Map<String, Object> json2 = rgb.getJson();
						json2.put("r", color.getColorType().getColor().getRed());
						json2.put("g", color.getColorType().getColor().getGreen());
						json2.put("b", color.getColorType().getColor().getBlue());
						rgb.setJson(json2);
						service.save(rgb);
						Tasks.wait(5, () -> ParticleMenu.openColor(player));
					}));
		}

		int[] slots = new int[]{6, 7, 8};
		int[] amount = new int[]{1, 10, 64};
		for (int i = 0; i < RGB.values().length; i++) {
			for (int j = 0; j < 3; j++) {
				AtomicInteger color = new AtomicInteger(i);
				AtomicInteger index = new AtomicInteger(j);
				contents.set(i + 1, slots[j], ClickableItem.from(nameItem(
						new ItemStack(Material.INK_SACK, 1, (byte) RGB.values()[i].getData()),
						"+/- " + amount[j]),
						e -> {
							Map<String, Object> json2 = rgb.getJson();
							String name = RGB.values()[color.get()].name().toLowerCase();
							if (((InventoryClickEvent) e.getEvent()).isLeftClick()) {
								json2.put(name,
										Math.min((Double.valueOf((double) json2.get(name)).intValue() + amount[index.get()]), 255));
							} else
								json2.put(name,
										Math.max((Double.valueOf((double) json2.get(name)).intValue() - amount[index.get()]), 0));
							rgb.setJson(json2);
							service.save(rgb);
							Tasks.wait(5, () -> ParticleMenu.openColor(player));
						}));
			}
		}

	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}
}
