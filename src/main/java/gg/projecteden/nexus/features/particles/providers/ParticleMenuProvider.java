package gg.projecteden.nexus.features.particles.providers;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.particle.ParticleOwner;
import gg.projecteden.nexus.models.particle.ParticleService;
import gg.projecteden.nexus.models.particle.ParticleType;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;

@Title("Particles")
public class ParticleMenuProvider extends InventoryProvider {
	private final ParticleService particleService = new ParticleService();

	@Override
	public void init() {
		ParticleOwner owner = particleService.get(viewer);

		addCloseItem();

		contents.set(0, 8, ClickableItem.of(Material.TNT, "&cStop All Effects", e -> {
			owner.cancel();
			owner.getActiveParticles().clear();
			new ParticleService().save(owner);
			open(viewer);
		}));

		int row = 1;
		int column = 1;

		for (ParticleType type : ParticleType.values()) {
			if (!owner.canUse(type))
				continue;

			boolean active = owner.isActive(type);
			ItemBuilder item = type.getDisplayItem().lore("&eLeft click to toggle", "&7Right click to edit settings").glow(active);

			contents.set(row, column, ClickableItem.of(item, e -> {
				if (e.isLeftClick()) {
					if (active)
						owner.cancel(type);
					else
						owner.start(type);

					open(viewer);
				} else
					new EffectSettingProvider(type).open(viewer);
			}));

			if (column != 7)
				column++;
			else {
				column = 1;
				row++;
			}
		}

	}

}
