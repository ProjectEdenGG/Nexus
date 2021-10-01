package gg.projecteden.nexus.features.resourcepack;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import gg.projecteden.annotations.Async;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.staff.admin.BashCommand;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUser;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUserService;
import gg.projecteden.nexus.utils.HttpUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.WorldGroup;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.features.resourcepack.ResourcePack.URL;
import static gg.projecteden.nexus.features.resourcepack.ResourcePack.closeZip;
import static gg.projecteden.nexus.features.resourcepack.ResourcePack.file;
import static gg.projecteden.nexus.features.resourcepack.ResourcePack.fileName;
import static gg.projecteden.nexus.features.resourcepack.ResourcePack.hash;
import static gg.projecteden.nexus.features.resourcepack.ResourcePack.openZip;
import static gg.projecteden.nexus.utils.ItemUtils.isNullOrAir;

@Aliases("rp")
@NoArgsConstructor
public class ResourcePackCommand extends CustomCommand implements Listener {
	private static final LocalResourcePackUserService service = new LocalResourcePackUserService();

	static {
		Bukkit.getMessenger().registerIncomingPluginChannel(Nexus.getInstance(), "titan:out", new VersionsChannelListener());
	}

	@Override
	public void _shutdown() {
		Bukkit.getMessenger().unregisterOutgoingPluginChannel(Nexus.getInstance(), "titan:out");
	}

	public ResourcePackCommand(@NonNull CommandEvent event) {
		super(event);
	}

	private void resourcePack(Player player) {
		player.setResourcePack(URL, hash);
	}

	@Path
	void resourcePack() {
		if (hash == null)
			error("Resource pack hash is null");

		if (Status.DECLINED == player().getResourcePackStatus())
			error("You declined the original prompt for the resource pack. In order to use the resource pack, you must edit the server in your server list and change the \"Server Resource Packs\" option to either \"enabled\" or \"prompt\"");

		resourcePack(player());
	}

	@Path("local [enabled]")
	@Description("Tell the server you have the resource pack if you have installed it locally")
	void local(Boolean enabled) {
		if (enabled == null) {
			send(PREFIX + "If you have the resource pack installed locally, use &c/rp local true");
			return;
		}

		if (enabled && Status.DECLINED != player().getResourcePackStatus())
			error("You must decline the resource pack in order to run this command");

		service.edit(player(), user -> user.setEnabled(enabled));
		if (enabled)
			send(PREFIX + "The server will now trust that you have the resource pack installed");
		else {
			send(PREFIX + "The server will now automatically detect if you accept the resource pack download");
			if (Status.DECLINED != player().getResourcePackStatus())
				send(PREFIX + "Make sure to enable the resource pack in the server's settings in the multiplayer screen");
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		new LocalResourcePackUserService().edit(event.getPlayer(), LocalResourcePackUser::forgetVersions);
	}

	@EventHandler
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		new LocalResourcePackUserService().edit(event.getUniqueId(), LocalResourcePackUser::forgetVersions);
	}

	@HideFromHelp
	@TabCompleteIgnore
	@Path("versions [--saturn] [--titan]")
	void saturn(@Switch String saturn, @Switch String titan) {
		new LocalResourcePackUserService().edit(player(), user -> {
			user.setSaturnVersion(saturn);
			user.setTitanVersion(titan);
		});
	}

	@Permission("group.staff")
	@Path("getStatus [player]")
	void getStatus(@Arg("self") LocalResourcePackUser user) {
		send(PREFIX + "Status of &e" + user.getNickname());
		send("&6 Saturn &7- " + user.getSaturnStatus());
		send("&6 Titan &7- " + user.getTitanStatus());
	}

	@Permission("group.staff")
	@Path("getStatuses")
	void getStatuses() {
		final List<Player> players = OnlinePlayers.getAll();

		send(PREFIX + "&eStatuses");
		line();
		send("&6Saturn");
		new HashMap<String, Set<String>>() {{
			for (Player player : players)
				computeIfAbsent(service.get(player).getSaturnStatus(), $ -> new HashSet<>()).add(Nickname.of(player));
		}}.forEach((status, names) -> send("&e" + status + "&3: " + String.join(", ", names)));

		line();
		send("&6Titan");
		new HashMap<String, Set<String>>() {{
			for (Player player : players)
				computeIfAbsent(service.get(player).getTitanStatus(), $ -> new HashSet<>()).add(Nickname.of(player));
		}}.forEach((status, names) -> send("&e" + status + "&3: " + String.join(", ", names)));
	}

	@Path("getHash")
	@Permission("group.admin")
	void getHash() {
		send(json(PREFIX + "Resource pack hash: &e" + hash).hover("&eClick to copy").copy(hash));
	}

	@Async
	@Path("update")
	@Permission("group.admin")
	void update() {
		send(BashCommand.tryExecute("sudo /home/minecraft/git/Saturn/deploy.sh"));

		String newHash = Utils.createSha1(URL);

		if (hash != null && hash.equals(newHash))
			error("No resource pack update found");

		hash = newHash;

		if (hash == null)
			error("Resource pack hash is null");

		menuReload();

//		TODO: Figure out a solution that actually works, this just disables the active resource pack for all players who click it
//		for (Player player : PlayerUtils.getOnlinePlayers())
//			if (Arrays.asList(Status.ACCEPTED, Status.SUCCESSFULLY_LOADED).contains(player.getResourcePackStatus()))
//				send(player, json(PREFIX + "There's an update to the resource pack available, click to update.").command("/rp"));
	}

	@Async
	@Path("menu reload")
	@Permission("group.admin")
	void menuReload() {
		closeZip();
		file = HttpUtils.saveFile(URL, fileName);
		openZip();
		CustomModelMenu.load();
		send(PREFIX + "Menu updated");
	}

	@Path("menu [folder]")
	@Permission("group.staff")
	void menu(CustomModelFolder folder) {
		if (rank() == Rank.MODERATOR && worldGroup() != WorldGroup.STAFF)
			permissionError();

		new CustomModelMenu(folder).open(player());
	}

	@ConverterFor(CustomModelFolder.class)
	CustomModelFolder convertToCustomModelFolder(String value) {
		return ResourcePack.getRootFolder().getFolder("/" + (value == null ? "" : value));
	}

	@TabCompleterFor(CustomModelFolder.class)
	List<String> tabCompleteCustomModelFolder(String filter) {
		return ResourcePack.getFolders().stream()
				.map(CustomModelFolder::getDisplayPath)
				.filter(path -> path.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Tasks.wait(TickTime.SECOND.x(2), () -> {
			resourcePack(player);

			// Try Again if failed
			Tasks.wait(TickTime.SECOND.x(5), () -> {
				if (Status.FAILED_DOWNLOAD == player.getResourcePackStatus())
					resourcePack(player);
			});
		});
	}

	@EventHandler
	public void onResourcePackEvent(PlayerResourcePackStatusEvent event) {
		Nexus.debug("Resource Pack Status Update: " + event.getPlayer().getName() + " = " + event.getStatus());
	}

	@Path("convert balloons")
	@Permission("group.admin")
	void convert_balloons() {
		int converted = 0;
		for (ItemFrame itemFrame : location().getNearbyEntitiesByType(ItemFrame.class, 200)) {
			final ItemStack item = itemFrame.getItem();
			if (isNullOrAir(item))
				continue;

			if (item.getType() != Material.STICK)
				continue;

			final BalloonSize size = BalloonSize.ofOld(item);
			if (size == null)
				return;

			final BalloonColor color = BalloonColor.ofOld(item);

			itemFrame.setItem(new ItemBuilder(Material.LEATHER_HORSE_ARMOR)
				.customModelData(size.getNewId())
				.armorColor(color.getColor())
				.build());
			++converted;
		}

		send(PREFIX + "Converted " + converted + " balloons");
	}

	@Getter
	@AllArgsConstructor
	private enum BalloonSize {
		TALL(2, 15, 5),
		MEDIUM(16, 29, 4),
		SHORT(30, 43, 3),
		;

		private final int oldMin;
		private final int oldMax;
		private final int newId;

		public static BalloonSize ofOld(ItemStack item) {
			return ofOld(CustomModelData.of(item));
		}

		public static BalloonSize ofOld(int customModelData) {
			for (BalloonSize size : values())
				if (customModelData >= size.oldMin && customModelData <= size.oldMax)
					return size;

			return null;
		}
	}

	@Getter
	@AllArgsConstructor
	private enum BalloonColor {
		RED("#fb5449"),
		ORANGE("#fd9336"),
		YELLOW("#ffea00"),
		LIME("#55ed57"),
		GREEN("#359c27"),
		CYAN("#00aa94"),
		LIGHT_BLUE("#55ffed"),
		BLUE("#5c6bd8"),
		PURPLE("#ac5cd8"),
		MAGENTA("#d85cd3"),
		PINK("#ff9ccf"),
		BROWN("#7a4d35"),
		BLACK("#1e1e1e"),
		WHITE("#ffffff"),
		;

		private final String hex;

		private Color getColor() {
			final java.awt.Color decode = java.awt.Color.decode(hex);
			return Color.fromRGB(decode.getRed(), decode.getGreen(), decode.getBlue());
		}

		public static BalloonColor ofOld(ItemStack item) {
			return ofOld(CustomModelData.of(item));
		}

		public static BalloonColor ofOld(int customModelData) {
			return BalloonColor.values()[(customModelData - 2) % 14];
		}
	}

	// Potions
	@Path("convert potions [radius]")
	@Permission("group.admin")
	void convert_potions(@Arg("200") int radius) {
		int converted = 0;
		for (ItemFrame itemFrame : location().getNearbyEntitiesByType(ItemFrame.class, radius)) {
			final ItemStack item = itemFrame.getItem();
			if (isNullOrAir(item))
				continue;

			if (item.getType() != Material.BLUE_STAINED_GLASS_PANE)
				continue;

			final PotionSize size = PotionSize.ofOld(item);
			if (size != null) {
				itemFrame.setItem(new ItemBuilder(Material.LEATHER_HORSE_ARMOR)
					.customModelData(size.getNewId())
					.armorColor(size.getLeatherColor(CustomModelData.of(item)))
					.build());
				++converted;
			}

			final PotionGroup group = PotionGroup.ofOld(item);
			if (group != null) {
				itemFrame.setItem(new ItemBuilder(Material.LEATHER_HORSE_ARMOR)
					.customModelData(group.getNewId())
					.armorColor(group.getLeatherColor())
					.build());
				++converted;
			}
		}

		send(PREFIX + "Converted " + converted + " potions");
	}

	private static final List<String> rainbow = List.of("ff0000", "fd9336", "ffea00", "00ff00", "216118", "00ff99", "00fbff", "0095ff", "5d00ff", "aa00ff", "ff00ea");

	@Getter
	@AllArgsConstructor
	private enum PotionSize {
		SMALL_1(101, 111, 6, rainbow),
		SMALL_2(113, 123, 7, rainbow),
		SMALL_3(125, 135, 8, rainbow),
		MEDIUM_1(137, 147, 9, rainbow),
		MEDIUM_2(149, 159, 10, rainbow),
		MEDIUM_3(161, 171, 11, rainbow),
		MEDIUM_4(173, 183, 12, rainbow),
		MEDIUM_5(185, 195, 13, rainbow),
		BOTTLE(209, 211, 14, List.of("0095ff", "ff0000", "ff00ea")),
		TEAR(201, 203, 15, List.of("0095ff", "00ff00", "00ff99")),
		DONUT(205, 207, 16, List.of("0095ff", "ffea00", "aa00ff")),
		SKULL(213, 214, 17, List.of("216118", "240015")),
		_234(303, 305, 18, List.of("ff0000", "0095ff", "ffea00")),
		_678(307, 309, 21, List.of("ff0000", "0095ff", "00ff99")),
		_101112(311, 313, 26, List.of("ff0000", "0095ff", "ff00ea")),
		;

		private final int oldMin;
		private final int oldMax;
		private final int newId;
		private final List<String> colors;

		public static PotionSize ofOld(ItemStack item) {
			return ofOld(CustomModelData.of(item));
		}

		public static PotionSize ofOld(int customModelData) {
			for (PotionSize size : values())
				if (customModelData >= size.oldMin && customModelData <= size.oldMax)
					return size;

			return null;
		}

		public Color getLeatherColor(int modelData) {
			final java.awt.Color decode = java.awt.Color.decode("#" + getColors().get(modelData - getOldMin()));
			return Color.fromRGB(decode.getRed(), decode.getGreen(), decode.getBlue());
		}
	}

	@Getter
	@AllArgsConstructor
	private enum PotionGroup {
		TINY_1(300, 27),
		TINY_2(301, 28),
		_13(314, 29),
		_14(315, 30),
		_15(316, 31),
		_16(317, 32),
		_17(318, 33),
		_18(319, 34),
		_19(320, 35),
		_20(321, 36),
		;

		private final int oldId;
		private final int newId;
		private final String hexColor = "ffffff";

		public static PotionGroup ofOld(ItemStack item) {
			return ofOld(CustomModelData.of(item));
		}

		public static PotionGroup ofOld(int customModelData) {
			for (PotionGroup size : values())
				if (customModelData == size.oldId)
					return size;

			return null;
		}


		public Color getLeatherColor() {
			final java.awt.Color decode = java.awt.Color.decode("#" + hexColor);
			return Color.fromRGB(decode.getRed(), decode.getGreen(), decode.getBlue());
		}
	}

	public static class VersionsChannelListener implements PluginMessageListener {
		@Override
		public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
			if (!channel.equalsIgnoreCase("titan:out"))
				return;
			String stringMessage = new String(message);
			JsonObject json = new Gson().fromJson(stringMessage, JsonObject.class);
			String titanVersion = json.has("titan") ? json.get("titan").toString() : null;
			String saturnVersion = json.has("saturn") ? json.get("saturn").toString() : null;
			Nexus.log("Received Saturn/Titan updates from " + player.getName() + ". Saturn: " + saturnVersion + " Titan: " + titanVersion);
			new LocalResourcePackUserService().edit(player, user -> {
				user.setSaturnVersion(saturnVersion);
				user.setTitanVersion(titanVersion);
			});
		}
	}

}
