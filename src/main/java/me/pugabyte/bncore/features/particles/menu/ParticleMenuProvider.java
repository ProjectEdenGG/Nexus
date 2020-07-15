package me.pugabyte.bncore.features.particles.menu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.particle.ParticleOwner;
import me.pugabyte.bncore.models.particle.ParticleService;
import me.pugabyte.bncore.models.particle.ParticleType;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicBoolean;

public class ParticleMenuProvider extends MenuUtils implements InventoryProvider {
	ParticleService particleService = new ParticleService();

	@Override
	public void init(Player player, InventoryContents contents) {
		ParticleOwner owner = particleService.get(player);

		addCloseItem(contents);

		contents.set(0, 8, ClickableItem.from(nameItem(Material.TNT, "&cStop All Effects"),
				e -> {
					owner.cancelTasks();
					owner.getActiveParticles().clear();
					new ParticleService().save(owner);
					player.closeInventory();
				}));

		contents.set(1, 0, ClickableItem.empty(nameItem(Material.MAP, "&3Shapes")));
		contents.set(3, 0, ClickableItem.empty(nameItem(Material.MAP, "&3Presets")));

		if (player.hasPermission("particles.shapes")) {
			int i = 1;
			for (ParticleType type : ParticleType.getShapes()) {
				ItemStack item = new ItemBuilder(type.getItemStack().clone()).name("&3" + type.getDisplayName())
						.lore("&eLeft Click to toggle||&7&oRight click to edit settings").itemFlags(ItemFlag.HIDE_ATTRIBUTES).build();
				AtomicBoolean active = new AtomicBoolean(false);
				if (owner.getTasks(type).size() > 0) {
					active.set(true);
					addGlowing(item);
				}
				contents.set(1, i++, ClickableItem.from(item,
						e -> {
							if (((InventoryClickEvent) e.getEvent()).isLeftClick()) {
								if (active.get()) owner.cancelTasks(type);
								else type.run(player);
								ParticleMenu.openMain(player);
							} else ParticleMenu.openSettingEditor(player, type);
						}));
			}
		}

		int row = (player.hasPermission("particles.shapes")) ? 3 : 1;
		int column = 1;

		for (ParticleType type : ParticleType.getPresets()) {
			if (type == ParticleType.WINGS)
				if (!player.hasPermission("wings.use"))
					continue;
				else if (!player.hasPermission("particles." + type.getCommandName()))
					continue;

			ItemStack item = new ItemBuilder(type.getItemStack().clone()).name("&3" + type.getDisplayName())
					.lore("&eLeft Click to toggle||&7&oRight click to edit settings").itemFlags(ItemFlag.HIDE_ATTRIBUTES).build();
			AtomicBoolean active = new AtomicBoolean(false);

			if (owner.getTasks(type).size() > 0) {
				active.set(true);
				addGlowing(item);
			}

			contents.set(row, column, ClickableItem.from(item,
					e -> {
						if (((InventoryClickEvent) e.getEvent()).isLeftClick()) {
							if (active.get()) owner.cancelTasks(type);
							else type.run(player);
							ParticleMenu.openMain(player);
						} else ParticleMenu.openSettingEditor(player, type);
					}));

			if (column != 7)
				column++;
			else {
				column = 1;
				row++;
			}
		}

//		ParticleOwner particleOwner = new ParticleService().get(player);
//
//		Pagination page = contents.pagination();
//
//		Setting rgb = service.get(player, "particlesRGB");
//		Map<String, Object> json = rgb.getJson();
//		if (!json.containsKey("r"))
//			json = new HashMap<String, Object>() {{
//				put("r", 0);
//				put("g", 0);
//				put("b", 0);
//			}};
//		rgb.setJson(json);
//		service.save(rgb);
//
//		ItemStack chestplate = nameItem(new ItemStack(Material.LEATHER_CHESTPLATE), "&fSet Color");
//		LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
//		meta.setColor(Color.fromRGB(Double.valueOf((double) json.get("r")).intValue(),
//				Double.valueOf((double) json.get("g")).intValue(),
//				Double.valueOf((double) json.get("b")).intValue()));
//		chestplate.setItemMeta(meta);
//
//		contents.set(1, 1, ClickableItem.from(chestplate,
//				e -> ParticleMenu.openColor(player)));
//
//		Setting rainbow = service.get(player, "particlesRainbow");
//		boolean useRainbow = rainbow.getBoolean();
//		ItemStack rainbowItem = new ItemBuilder(Material.MAGMA_CREAM).name("&fSet &cR&6a&ei&an&9b&bo&dw")
//				.lore(useRainbow ? "&aEnabled" : "&cDisabled", "", "&eOverrides color settings").build();
//		contents.set(2, 1, ClickableItem.from(rainbowItem,
//				e -> {
//					rainbow.setBoolean(!rainbow.getBoolean());
//					service.save(rainbow);
//					Tasks.wait(2, () -> ParticleMenu.openMain(player, page.getPage()));
//				}));
//
//		contents.set(3, 1, ClickableItem.from(nameItem(Material.BARRIER, "&cStop Particles"),
//				e -> {
//					particleOwner.cancelTasks();
//					player.closeInventory();
//				}));
//
//		List<ClickableItem> items = new ArrayList<>();
//		for (int i = 0; i < ParticleType.values().length; i++) {
//			ParticleType effect = ParticleType.values()[i];
//
//			AtomicBoolean active = new AtomicBoolean(false);
//			if (particleOwner.getTasks(effect).size() > 0)
//				active.set(true);
//
//			if (!player.hasPermission("particles." + effect.getCommandName())) continue;
//
//			ItemStack item = nameItem(effect.getItemStack().clone(), "&3" + effect.getDisplayName());
//			if (active.get())
//				addGlowing(item);
//
//			items.add(ClickableItem.from(item,
//					e -> {
//						ParticleMenu.openSettingEditor(player, effect);
////						if (active.get())
////							particleOwner.cancelTasks(effect);
////						else
////							effect.run(player);
////						ParticleMenu.openMain(player, page.getPage());
//					}));
//		}
//		ClickableItem[] clickableItems = items.toArray(new ClickableItem[0]);
//
//		page.setItems(clickableItems);
//		page.setItemsPerPage(15);
//
//		int row = 1;
//		int column = 3;
//		for (ClickableItem item : page.getPageItems()) {
//			contents.set(row, column, item);
//			if (column != 7)
//				column++;
//			else {
//				column = 3;
//				row++;
//			}
//		}
//
//
//		if (!page.isLast())
//			contents.set(4, 5, ClickableItem.from(nameItem(new ItemStack(Material.ARROW), "&fNext Page"), e -> ParticleMenu.openMain(player, page.next().getPage())));
//		if (!page.isFirst())
//			contents.set(4, 7, ClickableItem.from(nameItem(new ItemStack(Material.BARRIER), "&fPrevious Page"), e -> ParticleMenu.openMain(player, page.previous().getPage())));
//

	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}

}
