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

	@NotNull
	private static BearFair21Renderer getRenderer(UUID uuid) {
		return renderers.computeIfAbsent(uuid, $ -> new BearFair21Renderer(true));
	}

	private BearFair21Renderer myRenderer;

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

	@Path("set coords <x> <y>")
	void set_coords(byte x, byte y) {
		myRenderer.getCursor().setX(x);
		myRenderer.getCursor().setY(y);
		send(PREFIX + "Set coords to " + x + " / " + y);
	}

	@Path("get coords")
	void get_coords() {
		byte x = myRenderer.getCursor().getX();
		byte y = myRenderer.getCursor().getY();
		send(PREFIX + "Current coords are " + x + " / " + y);
	}

	@Path("toggle updating")
	void toggle_updating() {
		myRenderer.setUpdating(!myRenderer.isUpdating());
		send(PREFIX + "Map updating " + (myRenderer.isUpdating() ? "&aenabled" : "&cdisabled"));
	}

	@Path("deactivate")
	void deactivate() {
		myRenderer.deactivate();
		send(PREFIX + "&cDeactivated");
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
				final Location center = getIslandCenter(location);
				int xOffset = getXOffset(location, center);
				int yOffset = getYOffset(location, center);

				boolean withinBounds = isWithinBounds(xOffset, Byte.class) && isWithinBounds(yOffset, Byte.class);

				if (withinBounds) {
					cursor.setVisible(true);
					cursor.setX((byte) xOffset);
					cursor.setY((byte) yOffset);
					cursor.setDirection(MapPointerDirection.of(player).b());
				} else {
					cursor.setVisible(false);
				}
			}
		}

		private int getXOffset(Location location, Location center) {
			double xDiff = location.getX() - center.getX();
			return (int) Math.round(xDiff * .6) + (int) center.getX();
		}

		private int getYOffset(Location location, Location center) {
			double yDiff = location.getZ() - center.getZ();
			return (int) Math.round(yDiff * .6) + (int) center.getZ() + 140; // TODO Why 140
		}

		private Location getIslandCenter(Location location) {
			IslandType island = IslandType.of(location);
			if (island == null)
				island = IslandType.MAIN;
			if (island.getCenter().getX() == 0 && island.getCenter().getZ() == 0)
				island = IslandType.MAIN;

			return island.getCenter();
		}

		public void deactivate() {
			Nexus.log("Deactivating map renderer");
			active = false;
			cursor.setVisible(false);
		}

	}

}
