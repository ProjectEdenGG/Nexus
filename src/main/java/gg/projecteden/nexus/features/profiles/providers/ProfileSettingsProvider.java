package gg.projecteden.nexus.features.profiles.providers;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.api.content.SlotPos;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.models.profile.ProfileUser;
import gg.projecteden.nexus.models.profile.ProfileUserService;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ItemFlags;
import gg.projecteden.nexus.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Rows(3)
@Title("Profile Settings")
public class ProfileSettingsProvider extends InventoryProvider {
	InventoryProvider previousMenu = null;
	ProfileUser user;


	public ProfileSettingsProvider(Player viewer, @Nullable InventoryProvider previousMenu, ProfileUser user) {
		this.viewer = viewer;
		this.previousMenu = previousMenu;
		this.user = user;
	}

	/*
		TODO:
			- Friends privacy - {Public, Friends Only, Private}
			- Social Media privacy - {Public, Friends Only, Private}
	 */

	@Override
	public void init() {
		addBackItem(e -> new ProfileProvider(viewer, this).open(viewer));

		backgroundColor();
	}

	//

	private void backgroundColor() {
		Color backgroundColor = user.getBukkitBackgroundColor();
		ItemBuilder optionBackgroundColor = new ItemBuilder(CustomMaterial.DYE_STATION_BUTTON_DYE)
			.name("&eChange Background Color")
			.dyeColor(user.getBukkitBackgroundColor())
			.itemFlags(ItemFlags.HIDE_ALL)
			.lore(List.of(
				"&cR: " + backgroundColor.getRed(),
				"&aG: " + backgroundColor.getGreen(),
				"&bB: " + backgroundColor.getBlue(),
				"",
				"&3Hex: &e" + StringUtils.toHex(backgroundColor)
			));

		contents.set(SlotPos.of(1, 0), ClickableItem.of(optionBackgroundColor, e -> {
			new ColorCreatorProvider(viewer, this, user.getBackgroundColor(), _color -> {
				applyColor(_color, viewer);

				optionBackgroundColor.dyeColor(user.getBukkitBackgroundColor())
					.lore(List.of(
						"&cR: " + backgroundColor.getRed(),
						"&aG: " + backgroundColor.getGreen(),
						"&bB: " + backgroundColor.getBlue(),
						"",
						"&3Hex: &e" + StringUtils.toHex(backgroundColor)
					));

				refresh();
			}).open(viewer);
		}));
	}

	private static void applyColor(Color color, Player player) {
		ProfileUserService userService = new ProfileUserService();
		ProfileUser user = userService.get(player);
		user.setBackgroundColor(getChatColor(color));
		userService.save(user);
	}

	private static ChatColor getChatColor(Color color) {
		return ChatColor.of(ColorType.toJava(color));
	}
}
