package gg.projecteden.nexus.features.trust;

import gg.projecteden.nexus.features.trust.providers.TrustsMenu;
import gg.projecteden.nexus.features.trust.providers.TrustsPlayerMenu;
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
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.trust.TrustsUser;
import gg.projecteden.nexus.models.trust.TrustsUser.TrustType;
import gg.projecteden.nexus.models.trust.TrustsUserService;
import gg.projecteden.nexus.utils.Name;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static gg.projecteden.api.common.utils.StringUtils.asOxfordList;
import static java.util.stream.Collectors.joining;

@Aliases("trusts")
public class TrustCommand extends CustomCommand {
	private final TrustsUserService service = new TrustsUserService();
	private TrustsUser user;

	public TrustCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Path
	@Description("Open the trust menu")
	void run() {
		new TrustsMenu(user).open(player());
	}

	@Path("<player>")
	@Description("Open the trust menu for the specified player")
	void menu(Nerd player) {
		new TrustsPlayerMenu(player(), player).open(player());
	}

	@Path("add home <home> <players>")
	@Description("Allow specified player(s) to a specific home")
	void add_home(Home home, @Arg(type = TrustsUser.class) List<TrustsUser> players) {
		players.forEach(home::allow);
		new HomeService().save(home.getOwner());
		send(PREFIX + "Trusted &e" + nicknames(players, "&3, &e") + " &3to home &e" + home.getName());
	}

	@Path("remove home <home> <players>")
	@Description("Allow specified player(s) to a specific home")
	void remove_home(Home home, @Arg(type = TrustsUser.class) List<TrustsUser> players) {
		players.forEach(home::remove);
		new HomeService().save(home.getOwner());
		send(PREFIX + "Removed &e" + nicknames(players, "&3, &e") + " &3from home &e" + home.getName());
	}

	@Path("<action> <type> <players>")
	@Description("Allow or deny specified player(s) to a specified trust")
	void trust(Action action, TrustTypeArgument type, @Arg(type = TrustsUser.class) List<TrustsUser> players) {
		if (type == TrustTypeArgument.LOCK) {
			var names = players.stream().map(Name::of).collect(joining(" "));
			runCommand("cmodify " + (action == Action.REMOVE ? "-" : "") + names);
		} else {
			process(user, action, players, type.getTrustTypes());
		}
	}

	@Permission(Group.MODERATOR)
	@Path("admin <player> [action] [type] [players]")
	@Description("Modify another player's trusts")
	void admin(TrustsUser player, Action action, TrustTypeArgument type, @Arg(type = TrustsUser.class) List<TrustsUser> players) {
		if (action == null)
			new TrustsMenu(player).open(player());
		else if (type != null && !players.isEmpty()) {
			send(PREFIX + "Modifying trusts of &e" + player.getName());
			process(player, action, players, type.getTrustTypes());
		} else
			showUsage();
	}

	@Permission(Group.MODERATOR)
	@Path("admin debug <player> [type]")
	@Description("Display a player's trusts")
	void admin_debug(TrustsUser player, TrustType type) {
		user = service.get(player);

		for (TrustType _type : user.getTrusts().keySet()) {
			if (type == null || type == _type) {
				send("&3" + StringUtils.camelCase(_type) + ":");
				user.getTrusts().get(_type).forEach(uuid -> send(" &3- &e" + Nickname.of(uuid)));
			}
		}
	}

	private void process(TrustsUser user, Action action, List<TrustsUser> players, List<TrustType> types) {
		var separator = "&3, &e";
		var typeNames = asOxfordList(types.stream().map(TrustType::camelCase).toList(), separator);
		var nicknames = nicknames(players, separator);

		switch (action) {
			case ADD -> {
				players.forEach(player -> types.forEach(type -> user.add(type, player.getUniqueId())));
				service.save(user);
				send(PREFIX + "Trusted &e" + nicknames + " &3to &e" + typeNames);
			}
			case REMOVE -> {
				players.forEach(player -> types.forEach(type -> user.remove(type, player.getUniqueId())));
				service.save(user);
				send(PREFIX + "Removed &e" + nicknames + " &3from &e" + typeNames);
			}
		}
	}

	@NotNull
	private String nicknames(List<TrustsUser> players, String separator) {
		return asOxfordList(players.stream().map(Nickname::of).toList(), separator);
	}

	@Getter
	@AllArgsConstructor
	public enum TrustTypeArgument {
		LOCK,
		LOCKS(TrustType.LOCKS),
		HOMES(TrustType.HOMES),
		TELEPORTS(TrustType.TELEPORTS),
		DECORATIONS(TrustType.DECORATIONS),
		ALL(TrustType.values());

		private final List<TrustType> trustTypes;

		TrustTypeArgument(TrustType... trustTypes) {
			this.trustTypes = List.of(trustTypes);
		}
	}

	private enum Action {
		ADD,
		REMOVE
	}

}
