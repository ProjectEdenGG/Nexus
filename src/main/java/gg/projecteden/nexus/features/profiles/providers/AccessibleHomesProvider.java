package gg.projecteden.nexus.features.profiles.providers;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.models.home.Home;
import gg.projecteden.nexus.models.home.HomeOwner;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Rows(6)
public class AccessibleHomesProvider extends InventoryProvider {
	InventoryProvider previousMenu = null;
	private final HomeOwner targetOwner;

	public AccessibleHomesProvider(HomeOwner targetOwner, @Nullable InventoryProvider previousMenu) {
		this(targetOwner);
		this.previousMenu = previousMenu;
	}

	public AccessibleHomesProvider(HomeOwner targetOwner) {
		this.targetOwner = targetOwner;
	}

	@Override
	public String getTitle() {
		return "Accessible homes:";
	}

	@Override
	public void init() {
		addBackOrCloseItem(previousMenu);

		List<ClickableItem> items = new ArrayList<>();

		for (Home home : getAccessibleHomes(viewer, targetOwner)) {
			ItemBuilder homeItem = new ItemBuilder(CustomMaterial.GUI_PROFILE_ICON_HOMES)
				.name(home.getName())
				.lore("&eClick &3to teleport");

			if (home.hasItem())
				homeItem.material(home.getItem().getType());

			items.add(ClickableItem.of(homeItem, e ->
				PlayerUtils.runCommand(viewer, "home " + targetOwner.getName() + " " + home.getName())));
		}

		paginate(items);
	}

	public static List<Home> getAccessibleHomes(Player viewer, HomeOwner homeOwner) {
		List<Home> homes = new ArrayList<>();
		for (Home home : homeOwner.getHomes()) {
			if (home.hasAccess(viewer))
				homes.add(home);
		}

		return homes;
	}
}
