package gg.projecteden.nexus.features.minigolf.menus;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigolf.MiniGolf;
import gg.projecteden.nexus.features.minigolf.MiniGolfUtils;
import gg.projecteden.nexus.features.minigolf.models.GolfBallStyle;
import gg.projecteden.nexus.models.minigolf.MiniGolfConfig.MiniGolfCourse;
import gg.projecteden.nexus.models.minigolf.MiniGolfUser;
import gg.projecteden.nexus.models.minigolf.MiniGolfUserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

@RequiredArgsConstructor
@Title("Choose your golf ball")
public class GolfBallStyleMenu extends InventoryProvider {
	private final MiniGolfUserService service = new MiniGolfUserService();
	private final MiniGolfUser user;
	private final MiniGolfCourse course;

	@Override
	public void init() {
		addCloseItem();
		var items = new ArrayList<ClickableItem>();

		try {
			for (GolfBallStyle availableStyle : user.getAvailableStyles(course)) {
				var golfBall = new ItemBuilder(MiniGolfUtils.getGolfBall(availableStyle))
					.name(StringUtils.camelCase(availableStyle) + " Golf Ball");

				items.add(ClickableItem.of(golfBall, e -> {
					try {
						user.setStyle(user.getCurrentCourseRequired(), availableStyle);
						service.save(user);
						user.giveGolfBall();
						user.sendMessage(MiniGolf.PREFIX + "Activated " + StringUtils.camelCase(availableStyle) + " Golf Ball");
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
