package gg.projecteden.nexus.features.resourcepack;

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
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.WorldGroup;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;

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

@Aliases("rp")
@NoArgsConstructor
public class ResourcePackCommand extends CustomCommand implements Listener {
	private static final LocalResourcePackUserService service = new LocalResourcePackUserService();

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
		final List<Player> players = PlayerUtils.getOnlinePlayers();

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
}
