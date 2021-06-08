package me.pugabyte.nexus.features.trust;

import lombok.NonNull;
import me.pugabyte.nexus.features.trust.providers.TrustProvider;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.home.Home;
import me.pugabyte.nexus.models.home.HomeService;
import me.pugabyte.nexus.models.trust.Trust;
import me.pugabyte.nexus.models.trust.Trust.Type;
import me.pugabyte.nexus.models.trust.TrustService;
import me.pugabyte.nexus.utils.Name;
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
	void menu() {
		TrustProvider.openMenu(player());
	}

	@Path("lock <players>")
	void lock(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		runCommand("cmodify -" + names(players, " -"));
	}

	@Path("home <home> <players>")
	void home(Home home, @Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		players.forEach(home::remove);
		new HomeService().save(home.getOwner());
		send(PREFIX + "Untrusted &e" + names(players, "&3, &e") + " &3from home &e" + home.getName());
	}

	@Path("locks <players>")
	void locks(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		process(players, Type.LOCKS);
	}

	@Path("homes <players>")
	void homes(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		process(players, Type.HOMES);
	}

	@Path("teleports <players>")
	void teleports(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		process(players, Type.TELEPORTS);
	}

	@Path("all <players>")
	void all(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		process(players, Type.values());
	}

	@Permission("group.staff")
	@Path("admin locks <owner> <players>")
	void locks(OfflinePlayer owner, @Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		trust = service.get(owner);
		send(PREFIX + "Modifying trusts of &e" + Name.of(owner));
		process(players, Type.LOCKS);
	}

	@Permission("group.staff")
	@Path("admin homes <owner> <players>")
	void homes(OfflinePlayer owner, @Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		trust = service.get(owner);
		send(PREFIX + "Modifying trusts of &e" + Name.of(owner));
		process(players, Type.HOMES);
	}

	@Permission("group.staff")
	@Path("admin teleports <owner> <players>")
	void teleports(OfflinePlayer owner, @Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		trust = service.get(owner);
		send(PREFIX + "Modifying trusts of &e" + Name.of(owner));
		process(players, Type.TELEPORTS);
	}

	@Permission("group.staff")
	@Path("admin all <owner> <players>")
	void all(OfflinePlayer owner, @Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		trust = service.get(owner);
		send(PREFIX + "Modifying trusts of &e" + Name.of(owner));
		process(players, Type.values());
	}

	private void process(List<OfflinePlayer> players, Trust.Type... types) {
		for (Type type : types)
			players.forEach(player -> trust.get(type).remove(player.getUniqueId()));
		service.save(trust);
		String typeNames = Arrays.stream(types).map(Type::camelCase).collect(Collectors.joining("&3, &e"));
		send(PREFIX + "Untrusted &e" + names(players, "&3, &e") + " &3from &e" + typeNames);
	}

	@NotNull
	private String names(List<OfflinePlayer> players, String separator) {
		return players.stream().map(Name::of).collect(Collectors.joining(separator));
	}

}
