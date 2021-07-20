package me.pugabyte.nexus.features.particles.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.particles.effects.WingsEffect;
import me.pugabyte.nexus.models.particle.ParticleOwner;
import me.pugabyte.nexus.models.particle.ParticleService;
import me.pugabyte.nexus.models.particle.ParticleSetting;
import me.pugabyte.nexus.models.particle.ParticleType;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class WingsTypeProvider extends MenuUtils implements InventoryProvider {


	@Override
	public void open(Player player, int page) {
		SmartInventory.builder()
				.title("Wings Style")
				.size(5, 9)
				.provider(this)
				.build()
				.open(player);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> new EffectSettingProvider(ParticleType.WINGS).open(player));

		ParticleService service = new ParticleService();
		ParticleOwner owner = service.get(player);

		int row = 1;
		int column = 1;
		for (WingsEffect.WingStyle style : WingsEffect.WingStyle.values()) {
			if (!style.canBeUsedBy(player))
				continue;

			ItemStack item = style.getDisplayItem().build();

			if (ParticleSetting.WINGS_STYLE.get(owner, ParticleType.WINGS).equals(style))
				addGlowing(item);

			contents.set(row, column, ClickableItem.from(item, e -> {
				owner.getSettings(ParticleType.WINGS).put(ParticleSetting.WINGS_STYLE, style);
				service.save(owner);
				Tasks.wait(5, () -> new WingsTypeProvider().open(player));
			}));

			if (column == 7) {
				row++;
				column = 1;
			} else
				column++;
		}

	}

}
