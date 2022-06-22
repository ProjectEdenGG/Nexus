package gg.projecteden.nexus.features.menus;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Tasks;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class SignMenuFactory {
	private static final int ACTION_INDEX = 9;
	private static final int SIGN_LINES = 4;

	private static final String NBT_FORMAT = "{\"text\":\"%s\"}";
	private static final String NBT_BLOCK_ID = "minecraft:sign";

	private static final List<String> BLANK = Arrays.asList("", "", "", "");
	public static final String ARROWS = "^ ^ ^ ^ ^ ^ ^ ^";

	private final Plugin plugin;

	@Getter
	private final Map<Player, Menu> inputReceivers;
	private final Map<Player, BlockPosition> signLocations;

	public SignMenuFactory(Plugin plugin) {
		this.plugin = plugin;
		this.inputReceivers = new HashMap<>();
		this.signLocations = new HashMap<>();
		this.listen();
	}

	public Menu blank() {
		return new Menu(BLANK);
	}

	public Menu lines(String... linesArray) {
		if (linesArray == null)
			return blank();
		return new Menu(Arrays.asList(linesArray));
	}

	public Menu lines(List<String> lines) {
		if (lines == null)
			return blank();
		return new Menu(lines);
	}

	private void listen() {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.plugin, PacketType.Play.Client.UPDATE_SIGN) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				Player player = event.getPlayer();
				String[] input = packet.getStringArrays().read(0);

				Menu menu = inputReceivers.remove(player);
				BlockPosition blockPosition = signLocations.remove(player);

				if (menu == null || blockPosition == null)
					return;

				event.setCancelled(true);

				Tasks.sync(() -> {
					try {
						menu.response.accept(input);
					} catch (Exception ex) {
						if (menu.onError != null)
							menu.onError.accept(input, ex);
						MenuUtils.handleException(player, menu.prefix, ex);
					}
				});

				Location location = blockPosition.toLocation(player.getWorld());
				player.sendBlockChange(location, location.getBlock().getType().createBlockData());
			}
		});
	}

	public static final class Menu {
		private final List<String> lines;
		private Consumer<String[]> response;
		private BiConsumer<String[], Exception> onError;
		private String prefix; // for error handler
		private boolean colorize = true;

		Menu(List<String> lines) {
			this.lines = lines;
		}

		public Menu prefix(String prefix) {
			this.prefix = prefix;
			return this;
		}

		public Menu response(Consumer<String[]> response) {
			this.response = response;
			return this;
		}

		public Menu onError(Runnable onError) {
			this.onError = (lines, ex) -> onError.run();
			return this;
		}

		public Menu onError(BiConsumer<String[], Exception> onError) {
			this.onError = onError;
			return this;
		}

		public Menu colorize(boolean colorize) {
			this.colorize = colorize;
			return this;
		}

		public void open(Player player) {
			Location location = player.getLocation().add(0, -4, 0);

			List<Component> lines = this.lines.stream().map(line -> new JsonBuilder(line).build().asComponent()).collect(Collectors.toList());
			while (lines.size() < 4)
				lines.add(Component.text(""));

			player.sendBlockChange(location, Material.OAK_SIGN.createBlockData());
			player.sendSignChange(location, lines);

			PacketContainer openSign = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR);
			BlockPosition position = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
			openSign.getBlockPositionModifier().write(0, position);
			try {
				ProtocolLibrary.getProtocolManager().sendServerPacket(player, openSign);
			} catch (InvocationTargetException ex) {
				ex.printStackTrace();
			}

			Nexus.getSignMenuFactory().signLocations.put(player, position);
			Nexus.getSignMenuFactory().inputReceivers.putIfAbsent(player, this);
		}
	}
}
