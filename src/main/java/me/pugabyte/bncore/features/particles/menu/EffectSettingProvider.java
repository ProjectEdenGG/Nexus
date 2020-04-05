package me.pugabyte.bncore.features.particles.menu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.particle.ParticleOwner;
import me.pugabyte.bncore.models.particle.ParticleService;
import me.pugabyte.bncore.models.particle.ParticleSetting;
import me.pugabyte.bncore.models.particle.ParticleType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

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
			if (setting.getApplicableEffects().contains(type))
				contents.set(setting.getRow(), setting.getColumn(), ClickableItem.from(nameItem(setting.getItemStack(), "&3" + setting.getTitle(), setting.getLore(player, type)),
						e -> setting.onClick(player, type)));
		}
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}
}
