package gg.projecteden.nexus.features.titan;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.SocialMediaSite;
import gg.projecteden.nexus.features.titan.clientbound.SaturnUpdate;
import gg.projecteden.nexus.features.titan.clientbound.UpdateState;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
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

import static gg.projecteden.api.common.utils.Nullables.isNotNullOrEmpty;

@Permission(Group.ADMIN)
public class TitanCommand extends CustomCommand {

	public TitanCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@Path("installed [page]")
	@Description("View the most popular Titan versions")
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

		paginate(usersByVersion.keySet(), formatter, "/titan installed", page);
	}

	@Async
	@Path("installed with <version> [page]")
	@Description("View which players are using a specific version of Titan")
	void installed_with(TitanVersion version, @Arg("1") int page) {
		final List<UUID> users = getUsersByVersion().get(version);

		if (users.isEmpty())
			error("No users with Titan version &e" + version + " &cfound");

		final BiFunction<UUID, String, JsonBuilder> formatter = (uuid, index) ->
			json("&3" + index + " &e" + Nerd.of(uuid).getColoredName())
				.command("/titan settings " + Nickname.of(uuid))
				.hover("Click to view user's settings");

		paginate(users, formatter, "/titan installed with " + version, page);
	}

	@Async
	@Path("settings <user>")
	@Description("View a player's Titan configuration")
	void settings(LocalResourcePackUser user) {
		send(PREFIX + "Titan settings of " + user.getNickname());
		send(toPrettyString(user.getTitanSettings()));
	}

	@Path("sendMessage [player]")
	void sendClientMessage(@Arg("self") Player player) {
		ClientMessage.builder()
			.players(player)
			.message(new SaturnUpdate())
			.send();

		ClientMessage.builder()
			.players(player)
			.message(UpdateState.builder()
				.worldGroup(worldGroup().name())
				.build())
			.send();
	}

	@NotNull
	private Map<TitanVersion, List<UUID>> getUsersByVersion() {
		return new HashMap<>() {{
			new LocalResourcePackUserService().getAll().stream()
				.filter(user -> isNotNullOrEmpty(user.getLastKnownTitanVersion()))
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
