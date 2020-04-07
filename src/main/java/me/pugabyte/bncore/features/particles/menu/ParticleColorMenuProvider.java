package me.pugabyte.bncore.features.particles.menu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.Getter;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.particle.ParticleOwner;
import me.pugabyte.bncore.models.particle.ParticleService;
import me.pugabyte.bncore.models.particle.ParticleSetting;
import me.pugabyte.bncore.models.particle.ParticleType;
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

import java.util.concurrent.atomic.AtomicInteger;

public class ParticleColorMenuProvider extends MenuUtils implements InventoryProvider {

	ParticleService service = new ParticleService();
	ParticleType type;
	ParticleSetting setting;

	public ParticleColorMenuProvider(ParticleType type, ParticleSetting setting) {
		this.type = type;
		this.setting = setting;
	}

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
		R(ColorType.RED),
		G(ColorType.LIGHT_GREEN),
		B(ColorType.BLUE);

		ColorType colorType;

		RGB(ColorType colorType) {
			this.colorType = colorType;
		}
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> ParticleMenu.openSettingEditor(player, type));

		ParticleOwner owner = service.get(player);
		Color color = setting.get(owner, type);

		ItemStack chestplate = nameItem(new ItemStack(Material.LEATHER_CHESTPLATE), "&fCurrent Color", setting.getLore(player, type));
		LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
		meta.setColor(color);
		chestplate.setItemMeta(meta);

		contents.set(2, 4, ClickableItem.empty(chestplate));

		for (ColorItem colorItem : ColorItem.values()) {
			String name = colorItem.getColorType().getChatColor() + StringUtils.camelCase(colorItem.name().replace("_", " "));
			contents.set(colorItem.getColumn(), colorItem.getRow(), ClickableItem.from(
					new ItemBuilder(colorItem.getColorType().getDye()).name(name).build(),
					e -> {
						owner.getSettings(type).put(setting, colorItem.getColorType().getColor());
						service.save(owner);
						Tasks.wait(5, () -> ParticleMenu.openColor(player, type, setting));
					}));
		}

		int[] slots = new int[]{6, 7, 8};
		int[] amount = new int[]{1, 10, 64};
		for (int i = 0; i < RGB.values().length; i++) {
			for (int j = 0; j < 3; j++) {
				AtomicInteger dye = new AtomicInteger(i);
				AtomicInteger index = new AtomicInteger(j);
				contents.set(i + 1, slots[j], ClickableItem.from(new ItemBuilder(RGB.values()[i].getColorType().getDye())
								.amount(amount[index.get()])
								.name("+/- " + amount[j])
								.build(),
						e -> {
							Color newColor = null;
							switch (RGB.values()[dye.get()]) {
								case R:
									if (((InventoryClickEvent) e.getEvent()).isLeftClick())
										newColor = color.setRed(Math.min(color.getRed() + amount[index.get()], 255));
									else
										newColor = color.setRed(Math.max(color.getRed() - amount[index.get()], 0));
									break;
								case G:
									if (((InventoryClickEvent) e.getEvent()).isLeftClick())
										newColor = color.setGreen(Math.min(color.getGreen() + amount[index.get()], 255));
									else
										newColor = color.setGreen(Math.max(color.getGreen() - amount[index.get()], 0));
									break;
								case B:
									if (((InventoryClickEvent) e.getEvent()).isLeftClick())
										newColor = color.setBlue(Math.min(color.getBlue() + amount[index.get()], 255));
									else
										newColor = color.setBlue(Math.max(color.getBlue() - amount[index.get()], 0));
									break;
								default:
							}
							owner.getSettings(type).put(setting, newColor);
							service.save(owner);
							Tasks.wait(5, () -> ParticleMenu.openColor(player, type, setting));
						}));
			}
		}

	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}
}
