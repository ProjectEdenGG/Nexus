package gg.projecteden.nexus.features.particles.providers;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.particle.ParticleOwner;
import gg.projecteden.nexus.models.particle.ParticleService;
import gg.projecteden.nexus.models.particle.ParticleType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ParticleMenuProvider extends MenuUtils implements InventoryProvider {
	private final ParticleService particleService = new ParticleService();

	@Override
	public void open(Player player, int page) {
		SmartInventory.builder()
				.title("Particles")
				.size(6, 9)
				.provider(this)
				.build()
				.open(player);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		ParticleOwner owner = particleService.get(player);

		addCloseItem(contents);

		contents.set(0, 8, ClickableItem.from(nameItem(Material.TNT, "&cStop All Effects"), e -> {
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

			ItemStack item = type.getDisplayItem().lore("&eLeft click to toggle||&7Right click to edit settings").build();
			boolean active = owner.isActive(type);

			if (active)
				addGlowing(item);

			contents.set(row, column, ClickableItem.from(item, e -> {
				if (isLeftClick(e)) {
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
