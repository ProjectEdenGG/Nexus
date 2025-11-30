package gg.projecteden.nexus.features.minigolf.menus;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigolf.MiniGolf;
import gg.projecteden.nexus.features.minigolf.models.GolfBallParticle;
import gg.projecteden.nexus.models.minigolf.MiniGolfConfig.MiniGolfCourse;
import gg.projecteden.nexus.models.minigolf.MiniGolfUser;
import gg.projecteden.nexus.models.minigolf.MiniGolfUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

@RequiredArgsConstructor
@Title("Choose your golf ball particle")
public class GolfBallParticleMenu extends InventoryProvider {
	private final MiniGolfUserService service = new MiniGolfUserService();
	private final MiniGolfUser user;
	private final MiniGolfCourse course;

	@Override
	public void init() {
		addCloseItem();
		var items = new ArrayList<ClickableItem>();

		try {
			for (GolfBallParticle particle : GolfBallParticle.values()) {
				ItemStack item = new ItemBuilder(particle.getDisplay()).name(StringUtils.camelCase(particle)).build();
				items.add(ClickableItem.of(item, e -> {
					try {
						user.setParticle(course, particle);
						service.save(user);
						user.sendMessage(MiniGolf.PREFIX + "Activated " + StringUtils.camelCase(particle) + " Golf Ball Particle");
					} catch (Exception ex) {
						MenuUtils.handleException(viewer, MiniGolf.PREFIX, ex);
					}
				}));
			}
		} catch (Exception ex) {
			MenuUtils.handleException(viewer, MiniGolf.PREFIX, ex);
		}

		paginate(items);
	}
}
