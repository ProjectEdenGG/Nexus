package me.pugabyte.bncore.features.trust;

import lombok.NonNull;
import me.pugabyte.bncore.features.trust.providers.TrustPlayerProvider;
import me.pugabyte.bncore.features.trust.providers.TrustProvider;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Description;
import me.pugabyte.bncore.framework.commands.models.annotations.HideFromHelp;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.home.Home;
import me.pugabyte.bncore.models.home.HomeService;
import me.pugabyte.bncore.models.trust.Trust;
import me.pugabyte.bncore.models.trust.Trust.Type;
import me.pugabyte.bncore.models.trust.TrustService;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Aliases({"trusts", "allow"})
public class TrustCommand extends CustomCommand {
	Trust trust;
	TrustService service = new TrustService();

	public TrustCommand(@NonNull CommandEvent event) {
		super(event);
		trust = service.get(player());
	}

	@Description("Open the trust menu")
	@Path
	void run() {
		TrustProvider.open(player());
	}

	@HideFromHelp
	@Path("edit")
	void edit() {
		TrustProvider.open(player());
	}

	@Description("Open the trust menu for the specified player")
	@Path("<player>")
	void menu(OfflinePlayer player) {
		TrustPlayerProvider.open(player(), player);
	}

	@Description("Allow specified player(s) to a specific lock")
	@Path("lock <players...>")
	void lock(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		runCommand("cmodify " + names(players, " "));
	}

	@Description("Allow specified player(s) to a specific home")
	@Path("home <home> <players...>")
	void home(Home home, @Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		players.forEach(home::allow);
		new HomeService().save(home.getOwner());
		send(PREFIX + "Trusted &e" + names(players, "&3, &e") + " &3to home &e" + home.getName());
	}

	@Description("Allow specified player(s) to all locks")
	@Path("locks <players...>")
	void locks(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		process(players, Type.LOCKS);
	}

	@Description("Allow specified player(s) to all homes")
	@Path("homes <players...>")
	void homes(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		process(players, Type.HOMES);
	}

	@Description("Allow specified player(s) to everything")
	@Path("all <players...>")
	void all(@Arg(type = OfflinePlayer.class) List<OfflinePlayer> players) {
		process(players, Type.values());
	}

	private void process(List<OfflinePlayer> players, Trust.Type... types) {
		for (Type type : types)
			players.forEach(player -> trust.get(type).add(player.getUniqueId()));
		service.save(trust);
		String typeNames = Arrays.stream(types).map(Type::camelCase).collect(Collectors.joining("&3, &e"));
		send(PREFIX + "Trusted &e" + names(players, "&3, &e") + " &3to &e" + typeNames);
	}

	@NotNull
	private String names(List<OfflinePlayer> players, String separator) {
		return players.stream().map(OfflinePlayer::getName).collect(Collectors.joining(separator));
	}

}
