package gg.projecteden.nexus.features.trust;

import gg.projecteden.nexus.features.trust.providers.TrustPlayerProvider;
import gg.projecteden.nexus.features.trust.providers.TrustProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.home.Home;
import gg.projecteden.nexus.models.home.HomeService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.trust.Trust;
import gg.projecteden.nexus.models.trust.Trust.Type;
import gg.projecteden.nexus.models.trust.TrustService;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Aliases({"trusts", "allow"})
public class TrustCommand extends CustomCommand {
	private final TrustService service = new TrustService();
	private Trust trust;

	public TrustCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			trust = service.get(player());
	}

	@Path
	@Description("Open the trust menu")
	void run() {
		new TrustProvider().open(player());
	}

	@Path("<player>")
	@Description("Open the trust menu for the specified player")
	void menu(OfflinePlayer player) {
		new TrustPlayerProvider(player).open(player());
	}

	@Path("lock <players>")
	@Description("Allow specified player(s) to a specific lock")
	void lock(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		runCommand("cmodify " + names(players, " "));
	}

	@Path("home <home> <players>")
	@Description("Allow specified player(s) to a specific home")
	void home(Home home, @Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		players.forEach(home::allow);
		new HomeService().save(home.getOwner());
		send(PREFIX + "Trusted &e" + nicknames(players, "&3, &e") + " &3to home &e" + home.getName());
	}

	@Path("locks <players>")
	@Description("Allow specified player(s) to all locks")
	void locks(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		process(trust, players, Type.LOCKS);
	}

	@Path("homes <players>")
	@Description("Allow specified player(s) to all homes")
	void homes(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		process(trust, players, Type.HOMES);
	}

	@Path("teleports <players>")
	@Description("Allow specified player(s) to teleport to you at any time")
	void teleports(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		process(trust, players, Type.TELEPORTS);
	}

	@Path("decorations <players>")
	@Description("Allow specified player(s) to modify decorations")
	void decorations(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		process(trust, players, Type.DECORATIONS);
	}

	@Path("all <players>")
	@Description("Allow specified player(s) to everything")
	void all(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		process(trust, players, Type.values());
	}

	@Permission(Group.MODERATOR)
	@Path("admin debug <player> [type]")
	@Description("Display the player's trusts")
	void admin_debug(@Arg(type = OfflinePlayer.class) OfflinePlayer player, Trust.Type type) {
		trust = service.get(player);

		for (Type _type : trust.getTrusts().keySet()) {
			if (type == null || type == _type) {
				send("&3" + StringUtils.camelCase(_type) + ":");
				trust.getTrusts().get(_type)
					.forEach(uuid -> send(" &3- &e" + Nickname.of(uuid)));
			}
		}
	}

	@Permission(Group.MODERATOR)
	@Path("admin locks <owner> <players>")
	@Description("Give a player access to another player's locks")
	void admin_locks(Trust trust, @Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		send(PREFIX + "Modifying trusts of &e" + trust.getName());
		process(trust, players, Type.LOCKS);
	}

	@Permission(Group.MODERATOR)
	@Path("admin homes <owner> <players>")
	@Description("Give a player access to another player's homes")
	void admin_homes(Trust trust, @Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		send(PREFIX + "Modifying trusts of &e" + trust.getName());
		process(trust, players, Type.HOMES);
	}

	@Permission(Group.MODERATOR)
	@Path("admin teleports <owner> <players>")
	@Description("Give a player access to teleport to another player without asking")
	void admin_teleports(Trust trust, @Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		send(PREFIX + "Modifying trusts of &e" + trust.getName());
		process(trust, players, Type.TELEPORTS);
	}

	@Permission(Group.MODERATOR)
	@Path("admin decorations <owner> <players>")
	@Description("Give a player access to another player's decorations")
	void admin_decorations(Trust trust, @Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		send(PREFIX + "Modifying trusts of &e" + trust.getName());
		process(trust, players, Type.DECORATIONS);
	}

	@Permission(Group.MODERATOR)
	@Path("admin all <owner> <players>")
	@Description("Give a player access to all of another player's trusts")
	void admin_all(Trust trust, @Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		send(PREFIX + "Modifying trusts of &e" + trust.getName());
		process(trust, players, Type.values());
	}

	private void process(Trust trust, List<OfflinePlayer> players, Trust.Type... types) {
		for (Type type : types)
			players.forEach(player -> trust.add(type, player.getUniqueId()));
		service.save(trust);
		String typeNames = Arrays.stream(types).map(Type::camelCase).collect(Collectors.joining("&3, &e"));
		send(PREFIX + "Trusted &e" + nicknames(players, "&3, &e") + " &3to &e" + typeNames);
	}

	@NotNull
	private String nicknames(List<OfflinePlayer> players, String separator) {
		return players.stream().map(Nickname::of).collect(Collectors.joining(separator));
	}

	@NotNull
	private String names(List<OfflinePlayer> players, String separator) {
		return players.stream().map(Nickname::of).collect(Collectors.joining(separator));
	}

}
