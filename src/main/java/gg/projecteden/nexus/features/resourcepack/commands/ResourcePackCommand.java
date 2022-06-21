package gg.projecteden.nexus.features.resourcepack.commands;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.features.commands.staff.admin.BashCommand;
import gg.projecteden.nexus.features.resourcepack.CustomModelMenu;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.models.Saturn;
import gg.projecteden.nexus.features.resourcepack.models.files.CustomModelFolder;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUser;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUserService;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.features.resourcepack.ResourcePack.URL;
import static gg.projecteden.nexus.features.resourcepack.ResourcePack.hash;

@Aliases("rp")
@NoArgsConstructor
public class ResourcePackCommand extends CustomCommand implements Listener {
	private static final LocalResourcePackUserService service = new LocalResourcePackUserService();

	public ResourcePackCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		if (hash == null)
			error("Resource pack hash is null");

		if (Status.DECLINED == player().getResourcePackStatus())
			error("You declined the original prompt for the resource pack. In order to use the resource pack, you must edit the server in your server list and change the \"Server Resource Packs\" option to either \"enabled\" or \"prompt\"");

		ResourcePack.send(player());
	}

	@Path("local [enabled]")
	@Description("Tell the server you have the resource pack if you have installed it locally")
	void local(Boolean enabled) {
		if (enabled == null) {
			send(PREFIX + "If you have the resource pack installed locally, use &c/rp local true");
			return;
		}

		service.edit(player(), user -> user.setEnabled(enabled));
		if (enabled)
			send(PREFIX + "The server will now trust that you have the resource pack installed");
		else {
			send(PREFIX + "The server will now automatically detect if you accept the resource pack download");
			if (Status.DECLINED == player().getResourcePackStatus())
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

	@Permission(Group.STAFF)
	@Path("status [player]")
	void getStatus(@Arg("self") LocalResourcePackUser user) {
		send(PREFIX + "Status of &e" + user.getNickname());
		send("&6 Saturn &7- " + user.getSaturnStatus());
		send("&6 Titan &7- " + user.getTitanStatus());
	}

	@Permission(Group.STAFF)
	@Path("statuses")
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
	@Permission(Group.ADMIN)
	void getHash() {
		send(json(PREFIX + "Resource pack hash: &e" + hash).hover("&eClick to copy").copy(hash));
	}

	@Async
	@Path("deploy")
	@Permission(Group.ADMIN)
	void deploy() {
		send(BashCommand.tryExecute("sudo /home/minecraft/git/Saturn/deploy.sh"));

		String newHash = Utils.createSha1(URL);

		if (newHash == null)
			error("Hash is null");

		if (hash.equals(newHash))
			send(PREFIX + "&3Resource pack hash unchanged");

		hash = newHash;

		send(PREFIX + "Deployed");

		reload();

//		TODO: Figure out a solution that actually works, this just disables the active resource pack for all players who click it
//		for (Player player : PlayerUtils.getOnlinePlayers())
//			if (Arrays.asList(Status.ACCEPTED, Status.SUCCESSFULLY_LOADED).contains(player.getResourcePackStatus()))
//				send(player, json(PREFIX + "There's an update to the resource pack available, click to update.").command("/rp"));
	}

	@Async
	@Path("newdeploy")
	@Permission(Group.ADMIN)
	void newdeploy() {
		Saturn.deploy();
		send(PREFIX + "Deployed");

		reload();
	}

	@Async
	@Path("reload")
	@Permission(Group.ADMIN)
	void reload() {
		ResourcePack.read();
		send(PREFIX + "Reloaded");
	}

	@Path("(menu|meny) [folder]")
	@Permission(Group.STAFF)
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

}
