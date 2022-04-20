package gg.projecteden.nexus.features.particles.providers;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.particles.effects.WingsEffect;
import gg.projecteden.nexus.models.particle.ParticleOwner;
import gg.projecteden.nexus.models.particle.ParticleService;
import gg.projecteden.nexus.models.particle.ParticleSetting;
import gg.projecteden.nexus.models.particle.ParticleType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Tasks;

@Rows(5)
@Title("Wings Style")
public class WingsTypeProvider extends InventoryProvider {

	@Override
	public void init() {
		addBackItem(e -> new EffectSettingProvider(ParticleType.WINGS).open(player));

		ParticleService service = new ParticleService();
		ParticleOwner owner = service.get(player);

		int row = 1;
		int column = 1;
		for (WingsEffect.WingStyle style : WingsEffect.WingStyle.values()) {
			if (!style.canBeUsedBy(player))
				continue;

			ItemBuilder item = style.getDisplayItem().glow(ParticleSetting.WINGS_STYLE.get(owner, ParticleType.WINGS).equals(style));

			contents.set(row, column, ClickableItem.of(item, e -> {
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
