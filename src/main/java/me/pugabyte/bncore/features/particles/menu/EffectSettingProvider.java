package me.pugabyte.bncore.features.particles.menu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.particle.ParticleSetting;
import me.pugabyte.bncore.models.particle.ParticleType;
import org.bukkit.entity.Player;

public class EffectSettingProvider extends MenuUtils implements InventoryProvider {

	ParticleType type;

	public EffectSettingProvider(ParticleType type) {
		this.type = type;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		for (ParticleSetting setting : ParticleSetting.values()) {
			if (setting.getApplicableEffects().contains(type))
				contents.add(ClickableItem.from(nameItem(setting.getItemStack(), setting.getTitle(), setting.getLore(player, type)),
						e -> {
							setting.onClick(player, type);
						}));
		}
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}
}
