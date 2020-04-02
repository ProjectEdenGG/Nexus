package me.pugabyte.bncore.features.particles.menu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.particleeffect.EffectOwner;
import me.pugabyte.bncore.models.particleeffect.EffectService;
import me.pugabyte.bncore.models.particleeffect.EffectType;
import me.pugabyte.bncore.models.setting.Setting;
import me.pugabyte.bncore.models.setting.SettingService;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ParticleMenuProvider extends MenuUtils implements InventoryProvider {

	SettingService service = new SettingService();

	@Override
	public void init(Player player, InventoryContents contents) {
		EffectOwner effectOwner = new EffectService().get(player);

		Pagination page = contents.pagination();

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

		ItemStack chestplate = nameItem(new ItemStack(Material.LEATHER_CHESTPLATE), "&fSet Color");
		LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
		meta.setColor(Color.fromRGB(Double.valueOf((double) json.get("r")).intValue(),
				Double.valueOf((double) json.get("g")).intValue(),
				Double.valueOf((double) json.get("b")).intValue()));
		chestplate.setItemMeta(meta);

		contents.set(1, 1, ClickableItem.from(chestplate,
				e -> ParticleMenu.openColor(player)));

		Setting rainbow = service.get(player, "particlesRainbow");
		boolean useRainbow = rainbow.getBoolean();
		ItemStack rainbowItem = new ItemBuilder(Material.MAGMA_CREAM).name("&fSet &cR&6a&ei&an&9b&bo&dw")
				.lore(useRainbow ? "&aEnabled" : "&cDisabled", "", "&eOverrides color settings").build();
		contents.set(2, 1, ClickableItem.from(rainbowItem,
				e -> {
					rainbow.setBoolean(!rainbow.getBoolean());
					service.save(rainbow);
					Tasks.wait(2, () -> ParticleMenu.openMain(player, page.getPage()));
				}));

		contents.set(3, 1, ClickableItem.from(nameItem(Material.BARRIER, "&cStop Particles"),
				e -> {
					effectOwner.cancelTasks();
					player.closeInventory();
				}));

		List<ClickableItem> items = new ArrayList<>();
		for (int i = 0; i < EffectType.values().length; i++) {
			EffectType effect = EffectType.values()[i];

			AtomicBoolean active = new AtomicBoolean(false);
			if (effectOwner.getTasks(effect).size() > 0)
				active.set(true);

			if (!player.hasPermission("particles." + effect.getCommandName())) continue;

			ItemStack item = nameItem(effect.getItemStack().clone(), "&3" + effect.getDisplayName());
			if (active.get())
				addGlowing(item);

			items.add(ClickableItem.from(item,
					e -> {
						if (active.get())
							effectOwner.cancelTasks(effect);
						else
							effect.run(player);
						ParticleMenu.openMain(player, page.getPage());
					}));
		}
		ClickableItem[] clickableItems = items.toArray(new ClickableItem[0]);

		page.setItems(clickableItems);
		page.setItemsPerPage(15);

		int row = 1;
		int column = 3;
		for (ClickableItem item : page.getPageItems()) {
			contents.set(row, column, item);
			if (column != 7)
				column++;
			else {
				column = 3;
				row++;
			}
		}


		if (!page.isLast())
			contents.set(4, 5, ClickableItem.from(nameItem(new ItemStack(Material.ARROW), "&fNext Page"), e -> ParticleMenu.openMain(player, page.next().getPage())));
		if (!page.isFirst())
			contents.set(4, 7, ClickableItem.from(nameItem(new ItemStack(Material.BARRIER), "&fPrevious Page"), e -> ParticleMenu.openMain(player, page.previous().getPage())));


	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}

}
