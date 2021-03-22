package me.pugabyte.nexus.features.particles.menu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.models.particle.ParticleOwner;
import me.pugabyte.nexus.models.particle.ParticleService;
import me.pugabyte.nexus.models.particle.ParticleSetting;
import me.pugabyte.nexus.models.particle.ParticleType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class EffectSettingProvider extends MenuUtils implements InventoryProvider {

	ParticleType type;
	ParticleService service = new ParticleService();

	public EffectSettingProvider(ParticleType type) {
		this.type = type;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> ParticleMenu.openMain(player));

		contents.set(0, 4, ClickableItem.from(nameItem(Material.TNT, "&cCancel Effect"), e -> {
			ParticleOwner owner = service.get(player);
			owner.cancelTasks(type);
			player.closeInventory();
		}));

		contents.set(0, 8, ClickableItem.from(nameItem(Material.END_CRYSTAL, "&eUpdate Effect"), e -> {
			ParticleOwner owner = service.get(player);
			owner.cancelTasks(type);
			type.run(player);
		}));

		for (ParticleSetting setting : ParticleSetting.values()) {
			if (setting.getApplicableEffects().contains(type)) {
				ItemStack item = nameItem(setting.getItemStack(), "&3" + setting.getTitle(), setting.getLore(player, type));
				if (setting.getValue() == Color.class) {
					LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
					meta.setColor(setting.get(new ParticleService().get(player), type));
					item.setItemMeta(meta);
				}
				contents.set(setting.getRow(), setting.getColumn(), ClickableItem.from(item,
						e -> setting.onClick(player, type)));
			}
		}
	}
}
