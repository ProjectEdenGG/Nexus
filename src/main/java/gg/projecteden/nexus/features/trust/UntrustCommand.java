package gg.projecteden.nexus.features.trust;

import gg.projecteden.nexus.features.trust.providers.TrustProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
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
import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class UntrustCommand extends CustomCommand {
	Trust trust;
	TrustService service = new TrustService();

	public UntrustCommand(@NonNull CommandEvent event) {
		super(event);
		trust = service.get(player());
	}

	@Path
	@Description("Open the trust menu")
	void menu() {
		new TrustProvider().open(player());
	}

	@Path("lock <players>")
	@Description("Remove a player from a lock")
	void lock(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		runCommand("cmodify -" + names(players, " -"));
	}

	@Path("home <home> <players>")
	@Description("Remove a player's access to a home")
	void home(Home home, @Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		players.forEach(home::remove);
		new HomeService().save(home.getOwner());
		send(PREFIX + "Untrusted &e" + names(players, "&3, &e") + " &3from home &e" + home.getName());
	}

	@Path("locks <players>")
	@Description("Remove a player's access to your locks")
	void locks(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		process(players, Type.LOCKS);
	}

	@Path("homes <players>")
	@Description("Remove a player's access to your homes")
	void homes(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		process(players, Type.HOMES);
	}

	@Path("teleports <players>")
	@Description("Remove a player's ability to teleport to you without asking")
	void teleports(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		process(players, Type.TELEPORTS);
	}

	@Path("decorations <players>")
	@Description("Remove a player's access to your decorations")
	void decorations(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		process(players, Type.DECORATIONS);
	}

	@Path("all <players>")
	@Description("Remove a player from all trusts")
	void all(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		process(players, Type.values());
	}

	@Permission(Group.STAFF)
	@Path("admin locks <owner> <players>")
	@Description("Remove a player's access to another player's locks")
	void locks(OfflinePlayer owner, @Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		trust = service.get(owner);
		send(PREFIX + "Modifying trusts of &e" + Nickname.of(owner));
		process(players, Type.LOCKS);
	}

	@Permission(Group.STAFF)
	@Path("admin homes <owner> <players>")
	@Description("Remove a player's access to another player's homes")
	void homes(OfflinePlayer owner, @Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		trust = service.get(owner);
		send(PREFIX + "Modifying trusts of &e" + Nickname.of(owner));
		process(players, Type.HOMES);
	}

	@Permission(Group.STAFF)
	@Path("admin teleports <owner> <players>")
	@Description("Remove a player's ability to teleport to another player without asking")
	void teleports(OfflinePlayer owner, @Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		trust = service.get(owner);
		send(PREFIX + "Modifying trusts of &e" + Nickname.of(owner));
		process(players, Type.TELEPORTS);
	}

	@Permission(Group.STAFF)
	@Path("admin decorations <owner> <players>")
	@Description("Remove a player's access to another player's decorations")
	void decorations(OfflinePlayer owner, @Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		trust = service.get(owner);
		send(PREFIX + "Modifying trusts of &e" + Nickname.of(owner));
		process(players, Type.DECORATIONS);
	}

	@Permission(Group.STAFF)
	@Path("admin all <owner> <players>")
	@Description("Remove a player from all of another player's trusts")
	void all(OfflinePlayer owner, @Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		trust = service.get(owner);
		send(PREFIX + "Modifying trusts of &e" + Nickname.of(owner));
		process(players, Type.values());
	}

	private void process(List<OfflinePlayer> players, Trust.Type... types) {
		for (Type type : types)
			players.forEach(player -> trust.remove(type, player.getUniqueId()));
		service.save(trust);
		String typeNames = Arrays.stream(types).map(Type::camelCase).collect(Collectors.joining("&3, &e"));
		send(PREFIX + "Untrusted &e" + names(players, "&3, &e") + " &3from &e" + typeNames);
	}

	@NotNull
	private String names(List<OfflinePlayer> players, String separator) {
		return players.stream().map(Nickname::of).collect(Collectors.joining(separator));
	}

}
