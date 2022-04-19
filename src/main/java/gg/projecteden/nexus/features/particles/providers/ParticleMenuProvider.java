package gg.projecteden.nexus.features.particles.providers;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.particle.ParticleOwner;
import gg.projecteden.nexus.models.particle.ParticleService;
import gg.projecteden.nexus.models.particle.ParticleType;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ParticleMenuProvider extends InventoryProvider {
	private final ParticleService particleService = new ParticleService();

	@Override
	public void open(Player player, int page) {
		SmartInventory.builder()
				.title("Particles")
				.maxSize()
				.provider(this)
				.build()
				.open(player);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		ParticleOwner owner = particleService.get(player);

		addCloseItem(contents);

		contents.set(0, 8, ClickableItem.of(Material.TNT, "&cStop All Effects", e -> {
			owner.cancel();
			owner.getActiveParticles().clear();
			new ParticleService().save(owner);
			open(player);
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

					open(player);
				} else
					new EffectSettingProvider(type).open(player);
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
