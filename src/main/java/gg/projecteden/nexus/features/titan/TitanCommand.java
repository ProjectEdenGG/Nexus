package gg.projecteden.nexus.features.titan;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.SocialMediaSite;
import gg.projecteden.nexus.features.titan.clientbound.SaturnUpdate;
import gg.projecteden.nexus.features.titan.clientbound.UpdateState;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUser;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUserService;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

public class TitanCommand extends CustomCommand {

	public TitanCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void info() {
		send();
		send(json(PREFIX + "Titan is a Fabric mod built for the Project Eden server designed to enhance a player's " +
			"experience and streamline the loading of our resource pack Saturn. Learn more and download on our " +
			"website: ").group().next("&ehttps://projecteden.gg/titan").url("https://projecteden.gg/titan"));
		send();
	}

	@Async
	@Path("installed [page]")
	@Description("View the most popular Titan versions")
	@Permission(Group.ADMIN)
	void installed(@Arg("1") int page) {
		final var usersByVersion = getUsersByVersion();

		if (usersByVersion.isEmpty())
			error("No Titan users found");

		final BiFunction<TitanVersion, String, JsonBuilder> formatter = (version, index) ->
			json("&3" + index + " &e" + version + " &7- " + usersByVersion.get(version).size())
				.command("/titan installed with " + version)
				.group().next(" ").group()
				.next(SocialMediaSite.GITHUB.getEmoji())
				.url(version.getCommitUrl())
				.hover("Click to open on GitHub");

		new Paginator<TitanVersion>()
			.values(usersByVersion.keySet())
			.formatter(formatter)
			.command("/titan installed")
			.page(page)
			.send();
	}

	@Async
	@Path("installed with <version> [page]")
	@Description("View which players are using a specific version of Titan")
	@Permission(Group.ADMIN)
	void installed_with(TitanVersion version, @Arg("1") int page) {
		final List<UUID> users = getUsersByVersion().get(version);

		if (users.isEmpty())
			error("No users with Titan version &e" + version + " &cfound");

		final BiFunction<UUID, String, JsonBuilder> formatter = (uuid, index) ->
			json("&3" + index + " &e" + Nerd.of(uuid).getColoredName())
				.command("/titan settings " + Nickname.of(uuid))
				.hover("Click to view user's settings");

		new Paginator<UUID>()
			.values(users)
			.formatter(formatter)
			.command("/titan installed with " + version)
			.page(page)
			.send();
	}

	@Async
	@Path("settings <user>")
	@Description("View a player's Titan configuration")
	@Permission(Group.ADMIN)
	void settings(LocalResourcePackUser user) {
		send(PREFIX + "Titan settings of " + user.getNickname());
		send(toPrettyString(user.getTitanSettings()));
	}

	@HideFromHelp
	@HideFromWiki
	@Path("sendTestMessage [player]")
	@Permission(Group.ADMIN)
	void sendClientMessage(@Arg("self") Player player) {
		ClientMessage.builder()
			.players(player)
			.message(new SaturnUpdate())
			.send();

		ClientMessage.builder()
			.players(player)
			.message(UpdateState.builder()
				.worldGroup(camelCase(worldGroup()))
				.build())
			.send();

		ClientMessage.builder()
			.players(player)
			.message(UpdateState.builder()
				.mode(player().getGameMode().name())
				.build())
			.send();
	}

	@NotNull
	private Map<TitanVersion, List<UUID>> getUsersByVersion() {
		return new HashMap<>() {{
			new LocalResourcePackUserService().getAll().stream()
				.filter(user -> Nullables.isNotNullOrEmpty(user.getLastKnownTitanVersion()))
				.forEach(user -> {
					final var version = TitanVersion.of(user.getLastKnownTitanVersion());
					computeIfAbsent(version, $ -> new ArrayList<>()).add(user.getUuid());
				});
		}};
	}

	@Data
	public static class TitanVersion {
		private final String version;

		public static TitanVersion of(String version) {
			return new TitanVersion(version.replaceAll("\"", "").substring(0, 7));
		}

		@Override
		public String toString() {
			return version;
		}

		public String getCommitUrl() {
			return "https://github.com/ProjectEdenGG/Titan/commit/" + version;
		}
	}

	@ConverterFor(TitanVersion.class)
	TitanVersion convertToTitanVersion(String value) {
		return TitanVersion.of(value);
	}

	@TabCompleterFor(TitanVersion.class)
	List<String> tabCompleteTitanVersion(String filter) {
		return new LocalResourcePackUserService().getAll().stream()
			.map(user -> TitanVersion.of(user.getLastKnownTitanVersion()).toString())
			.filter(version -> version.toLowerCase().startsWith(filter.toLowerCase()))
			.toList();
	}

}
