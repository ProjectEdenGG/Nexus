package gg.projecteden.nexus.features.survival.decorationstore;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.MenuUtils.SurvivalNPCShopMenu;
import gg.projecteden.nexus.features.menus.MenuUtils.SurvivalNPCShopMenu.Product;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationUtils;
import gg.projecteden.nexus.features.resourcepack.decoration.catalog.Catalog;
import gg.projecteden.nexus.features.resourcepack.decoration.store.DecorationStoreType;
import gg.projecteden.nexus.features.survival.Survival;
import gg.projecteden.nexus.features.survival.avontyre.AvontyreNPCs;
import gg.projecteden.nexus.features.workbenches.dyestation.DyeStation;
import gg.projecteden.nexus.models.decoration.DecorationUser;
import gg.projecteden.nexus.models.decoration.DecorationUserService;
import gg.projecteden.nexus.models.decorationstore.DecorationStoreConfig;
import gg.projecteden.nexus.models.decorationstore.DecorationStoreConfigService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class DecorationStore implements Listener {
	public static final String PREFIX = StringUtils.getPrefix("DecorationStore");
	@Getter
	private static final DecorationStoreConfigService configService = new DecorationStoreConfigService();
	private static DecorationStoreConfig config = configService.get();
	//
	@Getter
	private static final String storeRegion = "spawn_decor_store";
	@Getter
	private static final String storeRegionSchematic = storeRegion + "_schem";
	@Getter
	private static final Location warpLocation = new Location(Survival.getWorld(), 358.5, 72.00, 28.5, -90, 0);
	//

	public DecorationStore() {
		Nexus.registerListener(this);
	}

	public static DecorationStoreConfig getConfig() {
		if (config == null)
			config = configService.get();

		return config;
	}

	public static void saveConfig() {
		configService.save(config);
	}

	public static void setActive(boolean bool) {
		DecorationStoreConfig config = configService.get();
		config.setActive(bool);
		saveConfig();

		if (!bool)
			DecorationStoreType.SURVIVAL.resetPlayerData();
	}

	@EventHandler
	public void on(NPCRightClickEvent event) {
		if (!AvontyreNPCs.DECORATION__NULL.is(event.getNPC()))
			return;

		// TODO DECORATIONS: REMOVE
		if (!DecorationUtils.hasBypass(event.getClicker()))
			return;
		//

		if (DecorationStoreLayouts.isAnimating()) {
			PlayerUtils.send(event.getClicker(), DecorationStore.PREFIX + "The store is currently being remodelled, come back shortly!");
			return;
		}

		openDecorationShop(event.getClicker());
	}

	private void openDecorationShop(Player player) {
		SurvivalNPCShopMenu.builder()
				.npcId(AvontyreNPCs.DECORATION__NULL.getNPCId())
				.title("Decoration Shop")
				.products(getProducts(player))
				.open(player);
	}

	private List<Product> getProducts(Player player) {
		DecorationUserService service = new DecorationUserService();
		List<SurvivalNPCShopMenu.Product> result = new ArrayList<>();

		SurvivalNPCShopMenu.Product.ProductBuilder masterCatalog = SurvivalNPCShopMenu.Product.builder()
				.itemStack(Catalog.getMASTER_CATALOG())
				.price(500); // TODO DECORATION: PRICE


		DecorationUser user = service.get(player);
		if (user.isBoughtMasterCatalog()) {
			if (!PlayerUtils.playerHas(player, Catalog.getMASTER_CATALOG()))
				result.add(masterCatalog.price(0).build());

			for (Catalog.Theme theme : Catalog.Theme.values()) {
				if (theme == Catalog.Theme.ALL)
					continue;

				if (user.getOwnedThemes().contains(theme))
					continue;

				result.add(
						SurvivalNPCShopMenu.Product.builder()
								.displayItemStack(theme.getShopItem())
								.price(theme.getPrice())
								.consumer((_player, provider) -> {
									DecorationUserService _service = new DecorationUserService();
									DecorationUser _user = _service.get(_player);
									_user.addOwnedThemes(theme);
									_service.save(_user);

									openDecorationShop(player);
								})
								.build()
				);
			}
		} else {
			result.add(
					masterCatalog
							.consumer((_player, provider) -> {
								DecorationUserService _service = new DecorationUserService();
								DecorationUser _user = _service.get(_player);
								_user.setBoughtMasterCatalog(true);
								_service.save(_user);

								openDecorationShop(player);
							})
							.build()
			);
		}

		result.add(
				SurvivalNPCShopMenu.Product.builder()
						.itemStack(DyeStation.getPaintbrush().build())
						.price(500) // TODO DECORATION: PRICE
						.build()
		);

		return result;
	}

}
