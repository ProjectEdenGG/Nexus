package me.pugabyte.nexus.features.particles.menu;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.particles.effects.WingsEffect;
import me.pugabyte.nexus.models.particle.ParticleOwner;
import me.pugabyte.nexus.models.particle.ParticleService;
import me.pugabyte.nexus.models.particle.ParticleSetting;
import me.pugabyte.nexus.models.particle.ParticleType;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WingsTypeProvider extends MenuUtils implements InventoryProvider {

	List<String> wordToInt = new ArrayList<>(Arrays.asList("zero", "one", "two", "three", "four", "five", "six", "seven",
			"eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen"));

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> ParticleMenu.openSettingEditor(player, ParticleType.WINGS));

		int row = 1;
		int column = 1;
		for (WingsEffect.WingStyle style : WingsEffect.WingStyle.values()) {
			if (!player.hasPermission("wings.style." + wordToInt.indexOf(style.name().toLowerCase()))) continue;
			ParticleService service = new ParticleService();
			ParticleOwner owner = service.get(player);
			ItemStack item = nameItem(new ItemStack(Material.ELYTRA).clone(), "&3Style #" + (style.ordinal() + 1));
			if (ParticleSetting.WINGS_STYLE.get(owner, ParticleType.WINGS).equals(style))
				addGlowing(item);
			contents.set(row, column, ClickableItem.from(item,
					e -> {
						owner.getSettings(ParticleType.WINGS).put(ParticleSetting.WINGS_STYLE, style);
						service.save(owner);
						Tasks.wait(5, () -> ParticleMenu.openWingsStyle(player));
					}));

			if (column == 7) {
				row++;
				column = 1;
			} else
				column++;
		}

	}

}
