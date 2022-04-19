package gg.projecteden.nexus.features.particles.providers;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.models.particle.ParticleOwner;
import gg.projecteden.nexus.models.particle.ParticleService;
import gg.projecteden.nexus.models.particle.ParticleSetting;
import gg.projecteden.nexus.models.particle.ParticleType;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

@AllArgsConstructor
@RequiredArgsConstructor
public class EffectSettingProvider extends MenuUtils implements InventoryProvider {
	private final ParticleService service = new ParticleService();
	private final ParticleType type;
	private HumanEntity displayEntity;

	@Override
	public void open(Player player, int page) {
		SmartInventory.builder()
				.title("Particle Settings")
				.rows(5)
				.provider(this)
				.build()
				.open(player);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		// TODO Should receive previousMenu
		if (displayEntity != null)
			addCloseItem(contents);
		else
			addBackItem(contents, e -> new ParticleMenuProvider().open(player));

		if (displayEntity == null)
			displayEntity = player;

		contents.set(0, 4, ClickableItem.of(nameItem(Material.TNT, "&cCancel Effect"), e -> {
			ParticleOwner owner = service.get(player);
			owner.cancel(type);
			player.closeInventory();
		}));

		contents.set(0, 8, ClickableItem.of(nameItem(Material.END_CRYSTAL, "&eUpdate Effect"), e -> {
			ParticleOwner owner = service.get(player);
			owner.cancel(type);
			type.run(owner, displayEntity);
		}));

		for (ParticleSetting setting : ParticleSetting.values()) {
			if (!setting.getApplicableEffects().contains(type))
				continue;

			final ItemBuilder builder = new ItemBuilder(setting.getItemStack())
				.name("&3" + setting.getTitle())
				.lore(setting.getLore(player, type));

			if (setting.getValue() == Color.class)
				builder.dyeColor((Color) setting.get(new ParticleService().get(player), type));

			contents.set(setting.getRow(), setting.getColumn(), ClickableItem.of(builder.build(),
					e -> setting.onClick(player, type)));
		}
	}
}
