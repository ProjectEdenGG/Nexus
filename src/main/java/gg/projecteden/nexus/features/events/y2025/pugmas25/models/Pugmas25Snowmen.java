package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.clientside.models.ClientSideItemFrame;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.events.DecorationInteractEvent;
import gg.projecteden.nexus.models.clientside.ClientSideConfig;
import gg.projecteden.nexus.models.clientside.ClientSideConfig.ClientSideItemFrameModifier;
import gg.projecteden.nexus.models.clientside.ClientSideUser;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.utils.LocationUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class Pugmas25Snowmen implements Listener {

	private final Pugmas25UserService userService = new Pugmas25UserService();

	public Pugmas25Snowmen() {
		Nexus.registerListener(this);

		ClientSideConfig.registerItemFrameModifier(new ClientSideItemFrameModifier() {
			@Override
			public ItemStack modify(ClientSideUser user, ClientSideItemFrame itemFrame) {
				Pugmas25Snowman snowman = Pugmas25Snowman.fromFrameLocation(itemFrame.getLocation());
				if (snowman == null)
					return itemFrame.content();

				DecorationType display = DecorationType.SNOWMAN_PLAIN;
				var pugmasUser = new Pugmas25UserService().get(user);
				if (pugmasUser.getDecoratedSnowmen().contains(snowman))
					display = DecorationType.SNOWMAN_FANCY;

				return display.getConfig().getItemBuilder().resetName().build();
			}
		});
	}

	@Getter
	@AllArgsConstructor
	public enum Pugmas25Snowman {
		PLAYER_CABIN(loc(-707, 118, -3122)),
		WEST_VILLAGE(loc(-719, 118, -3153)),
		BRIDGE(loc(-674, 108, -3156)),
		EAST_VILLAGE_HIGH(loc(-623, 108, -3156)),
		EAST_VILLAGE_LOW(loc(-619, 104, -3116)),
		CABINS(loc(-537, 86, -3090)),
		FOUNTAIN(loc(-690, 79, -2939));

		private final Location frameLoc;

		private static Location loc(int x, int y, int z) {
			return Pugmas25.get().location(x, y, z);
		}

		public static @Nullable Pugmas25Snowman fromFrameLocation(Location location) {
			return Arrays.stream(values())
				.filter(snowman -> LocationUtils.isFuzzyEqual(snowman.getFrameLoc(), location))
				.findFirst()
				.orElse(null);
		}
	}

	@EventHandler
	public void on(DecorationInteractEvent event) {
		if (!Pugmas25.get().shouldHandle(event.getPlayer()))
			return;

		if (event.getDecorationType() != DecorationType.SNOWMAN_PLAIN && event.getDecorationType() != DecorationType.SNOWMAN_FANCY)
			return;

		Pugmas25Snowman snowman = Pugmas25Snowman.fromFrameLocation(event.getDecoration().getClientsideLocation());
		if (snowman == null)
			return;

		// TODO: REQUIRE CORRECT TOOL

		Pugmas25User pugmasUser = userService.get(event.getPlayer());
		if (pugmasUser.getDecoratedSnowmen().contains(snowman))
			return;

		pugmasUser.decorateSnowman(snowman);
		userService.save(pugmasUser);
	}

}
