package me.pugabyte.nexus.features.events.y2021.bearfair21.commands;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.y2021.bearfair21.islands.IslandType;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.MapPointerDirection;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapCursor.Type;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.pugabyte.nexus.utils.MapPointerDirection.SOUTH;
import static me.pugabyte.nexus.utils.Utils.isWithinBounds;

@NoArgsConstructor
@Permission("group.admin")
public class BearFair21MapCommand extends CustomCommand implements Listener {
	private static final Map<UUID, BearFair21Renderer> renderers = new HashMap<>();
	private BearFair21Renderer myRenderer;

	@NotNull
	private static BearFair21Renderer getRenderer(UUID uuid) {
		return renderers.computeIfAbsent(uuid, $ -> new BearFair21Renderer(true));
	}

	public BearFair21MapCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			myRenderer = getRenderer(uuid());
	}

	@Path
	void init() {
		final MapView view = getView();
		for (MapRenderer mapRenderer : view.getRenderers())
			if (mapRenderer instanceof BearFair21Renderer renderer)
				renderer.deactivate();
		view.getRenderers().clear();
		view.addRenderer(myRenderer);
	}

	@NotNull
	private MapMeta getMap() {
		final ItemStack tool = getToolRequired();
		if (tool.getType() != Material.FILLED_MAP)
			error("Not a map");
		return (MapMeta) tool.getItemMeta();
	}

	@NotNull
	private MapView getView() {
		final MapView view = getMap().getMapView();
		if (view == null)
			error("Map view is null");
		return view;
	}

	@Path("set coords <x> <y>")
	void set_coords(byte x, byte y) {
		for (MapRenderer mapRenderer : getView().getRenderers())
			if (mapRenderer instanceof BearFair21Renderer renderer) {
				renderer.getCursor().setX(x);
				renderer.getCursor().setY(y);
				send(PREFIX + "Set coords to " + x + " / " + y);
				return;
			}
		send(PREFIX + "Could not find Bear Fair Renderer");
	}

	@Path("get coords")
	void get_coords() {
		for (MapRenderer mapRenderer : getView().getRenderers())
			if (mapRenderer instanceof BearFair21Renderer renderer) {
				byte x = renderer.getCursor().getX();
				byte y = renderer.getCursor().getY();
				send(PREFIX + "Current coords are " + x + " / " + y);
				return;
			}
		send(PREFIX + "Could not find Bear Fair Renderer");
	}

	@Path("toggle updating")
	void toggle_updating() {
		myRenderer.setUpdating(!myRenderer.updating);
		send(PREFIX + "Map updating " + (myRenderer.updating ? "&aenabled" : "&cdisabled"));
	}

	@Path("deactivate")
	void deactivate() {
		myRenderer.deactivate();
		send(PREFIX + "&cDeactivated");
	}

	static {
		BearFair21Renderer.init();
	}

	@Override
	public void _shutdown() {
		renderers.values().forEach(BearFair21Renderer::deactivate);
	}

	public static class BearFair21Renderer extends MapRenderer {

		private static BufferedImage image;

		static {
			try {
				image = ImageIO.read(new URL("https://i.imgur.com/wuZTLcH.png"));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		static void init() {}

		@Getter private final MapCursor cursor = new MapCursor((byte) 0, (byte) 0, SOUTH.b(), Type.WHITE_POINTER, true, "You");
		@Getter @Setter private boolean initialized = false;
		@Getter @Setter private boolean active = true;
		@Getter @Setter private boolean updating = true;

		public BearFair21Renderer(boolean contextual) {
			super(contextual);
		}

		@Override
		public void render(@NotNull MapView view, @NotNull MapCanvas canvas, @NotNull Player player) {
			final MapCursorCollection cursors = canvas.getCursors();
			cursor.setVisible(active);

			if (!active)
				return;

			if (!initialized) {
				view.setScale(Scale.CLOSEST);
				view.setUnlimitedTracking(false);
				canvas.drawImage(0, 0, image);
				cursors.addCursor(cursor);
				initialized = true;
			}

			if (updating) {
				final Location location = player.getLocation();
				IslandType island = IslandType.of(location);
				if (island == null)
					island = IslandType.MAIN;
				if (island.getCenter().getX() == 0 && island.getCenter().getZ() == 0)
					island = IslandType.MAIN;

				Location center = island.getCenter();
				double xDiff = location.getX() - center.getX();
				double yDiff = location.getZ() - center.getZ();

				int xOffset = (int) Math.round(xDiff * .6) + (int) center.getX();
				int yOffset = (int) Math.round(yDiff * .6) + (int) center.getZ() + 140; // TODO Why 140
				if (!isWithinBounds(xOffset, Byte.class) || !isWithinBounds(xOffset, Byte.class)) {
					cursor.setVisible(false);
				} else {
					cursor.setVisible(true);
					cursor.setX((byte) xOffset);
					cursor.setY((byte) yOffset);
					cursor.setDirection(MapPointerDirection.of(player.getLocation()).b());
				}
			}
		}

		public void deactivate() {
			Nexus.log("Deactivating map renderer");
			active = false;
			cursor.setVisible(false);
		}

	}

}
