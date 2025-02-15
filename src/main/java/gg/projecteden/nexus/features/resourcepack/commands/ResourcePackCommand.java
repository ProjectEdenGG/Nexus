package gg.projecteden.nexus.features.resourcepack.commands;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.features.resourcepack.ItemModelMenu;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.models.Saturn;
import gg.projecteden.nexus.features.resourcepack.models.files.ItemModelFolder;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUser;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUserService;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
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

@Aliases("rp")
@NoArgsConstructor
public class ResourcePackCommand extends CustomCommand implements Listener {
	private static final LocalResourcePackUserService service = new LocalResourcePackUserService();

	public ResourcePackCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Download the resource pack")
	void run() {
		if (ResourcePack.hash == null)
			error("Resource pack hash is null");

		if (Status.DECLINED == player().getResourcePackStatus())
			error("You declined the original prompt for the resource pack. In order to use the resource pack, you must edit the server in your server list and change the \"Server Resource Packs\" option to either \"enabled\" or \"prompt\"");

		ResourcePack.send(player());
	}

	@Path("local [enabled]")
	@Description("Tell the server you have the resource pack installed locally")
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

	@Path("status [player]")
	@Description("View a player's resource pack status and version")
	void getStatus(@Arg(value = "self", permission = Group.STAFF) LocalResourcePackUser user) {
		send(PREFIX + "Status of &e" + user.getNickname());
		send(json("&6 Saturn &7- " + user.getSaturnStatus()).url(user.getSaturnCommitUrl()));
		send(json("&6 Titan &7- " + user.getTitanStatus()).url(user.getTitanCommitUrl()));
	}

	@Permission(Group.STAFF)
	@Path("statuses")
	@Description("View the resource pack status of all online players")
	void getStatuses() {
		final List<Player> players = OnlinePlayers.getAll();

		send(PREFIX + "&eStatuses");
		line();
		send("&6Saturn");
		new HashMap<String, Set<String>>() {{
			for (Player player : players)
				computeIfAbsent(service.get(player).getSaturnStatus(), $ -> new HashSet<>()).add(Nickname.of(player));
		}}.forEach((status, names) -> send(json("&e- " + status + "&3: " + String.join(", ", names))));

		line();
		send("&6Titan");
		new HashMap<String, Set<String>>() {{
			for (Player player : players)
				computeIfAbsent(service.get(player).getTitanStatus(), $ -> new HashSet<>()).add(Nickname.of(player));
		}}.forEach((status, names) -> send(json("&e- " + status + "&3: " + String.join(", ", names))));
	}

	@Async
	@Path("hash [--update]")
	@Permission(Group.ADMIN)
	@Description("Force update the resource pack hash")
	void hash(@Switch boolean update) {
		if (update) {
			send(PREFIX + "Hashing...");
			Saturn.updateHash();
		}

		send(json(PREFIX + "Resource pack hash: &e" + ResourcePack.hash).hover("&eClick to copy").copy(ResourcePack.hash));
	}

	@Async
	@Path("deploy [--force] [--silent]")
	@Permission(Group.ADMIN)
	@Description("Compress and update the resource pack")
	void deploy(@Switch boolean force, @Switch boolean silent) {
		send(PREFIX + "Deploying...");
		Saturn.deploy(force, silent);
		send(PREFIX + "Deployed");

		reload();
	}

	@Async
	@Path("reload")
	@Permission(Group.ADMIN)
	@Description("Update the resource pack cache")
	void reload() {
		ResourcePack.read();
		send(PREFIX + "Reloaded");
	}

	@Path("menu [folder]")
	@Description("Open the resource pack menu")
	@Permission(Group.STAFF)
	void menu(ItemModelFolder folder) {
		if (rank() == Rank.MODERATOR && worldGroup() != WorldGroup.STAFF)
			permissionError();

		new ItemModelMenu(folder).open(player());
	}

	@ConverterFor(ItemModelFolder.class)
	ItemModelFolder convertToCustomModelFolder(String value) {
		return ResourcePack.getRootFolder().getFolder("/" + (value == null ? "" : value));
	}

	@TabCompleterFor(ItemModelFolder.class)
	List<String> tabCompleteCustomModelFolder(String filter) {
		return ResourcePack.getFolders().stream()
				.map(ItemModelFolder::getDisplayPath)
				.filter(path -> path.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}

}
