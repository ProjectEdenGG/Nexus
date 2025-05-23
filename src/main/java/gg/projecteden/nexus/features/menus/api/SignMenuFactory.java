package gg.projecteden.nexus.features.menus.api;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.MathUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import gg.projecteden.nexus.utils.nms.PacketUtils;
import io.papermc.paper.adventure.AdventureComponent;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class SignMenuFactory {
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

	public Menu lines(String... lines) {
		if (lines == null)
			return blank();

		if (lines[0] == null) lines[0] = "";
		if (lines[1] == null) lines[1] = "";
		if (lines[2] == null) lines[2] = "";
		if (lines[3] == null) lines[3] = "";

		return new Menu(Arrays.asList(lines));
	}

	public Menu lines(List<String> lines) {
		if (lines == null || lines.size() != 4)
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
			final Location location = player.getLocation();
			location.add(2, location.getY() > location.getWorld().getMinHeight() + 4 ? -4 : 4, 2);
			location.setY(MathUtils.clamp(location.getY(), location.getWorld().getMinHeight(), location.getWorld().getMaxHeight() - 1));

			List<Component> lines = this.lines.stream()
				.map(line -> colorize ? new JsonBuilder(line).build().asComponent() : Component.text(line))
				.collect(Collectors.toList());

			while (lines.size() < 4)
				lines.add(Component.text(""));

			BlockPos pos = NMSUtils.toNMS(location);

			SignBlockEntity sign = new SignBlockEntity(pos, Blocks.OAK_SIGN.defaultBlockState());
			SignText signText = sign.getText(true);

			for (int i = 0; i < lines.size(); i++)
				signText = signText.setMessage(i, new AdventureComponent(lines.get(i)));
			sign.setText(signText, true);

			player.sendBlockChange(location, Material.OAK_SIGN.createBlockData());
			sign.setLevel(NMSUtils.toNMS(player.getWorld()));
			PacketUtils.sendPacket(player, sign.getUpdatePacket());
			sign.setLevel(null);
			PacketUtils.sendPacket(player, new ClientboundOpenSignEditorPacket(pos, true));

			BlockPosition position = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
			Nexus.getSignMenuFactory().signLocations.put(player, position);
			Nexus.getSignMenuFactory().inputReceivers.putIfAbsent(player, this);
		}
	}
}
