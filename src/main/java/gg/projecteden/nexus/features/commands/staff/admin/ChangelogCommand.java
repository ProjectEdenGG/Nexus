package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.discord.DiscordId.TextChannel;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.changelog.Changelog;
import gg.projecteden.nexus.models.changelog.Changelog.ChangelogEntry;
import gg.projecteden.nexus.models.changelog.ChangelogService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Permission(Group.ADMIN)
public class ChangelogCommand extends CustomCommand {
	private final ChangelogService service = new ChangelogService();
	private final Changelog changelog = service.get0();

	public ChangelogCommand(@NonNull CommandEvent event) {
		super(event);
	}

	private void save() {
		service.save(changelog);
	}

	@Path("generate")
	@Description("Generate a changelog")
	void generate() {
		changelog.generate();
		save();
		send(PREFIX + "Generated");
	}

	@Confirm
	@Path("diff [from] [to]")
	@Description("Post a changelog to Discord")
	void diff(ChangelogEntry from, ChangelogEntry to) {
		Discord.send(getMessage(from, to), TextChannel.CHANGELOG);
	}

	@Path("testDiff [from] [to]")
	@Description("Post a changelog to Griffin's private test Discord")
	void testDiff(ChangelogEntry from, ChangelogEntry to) {
		Discord.send(getMessage(from, to), TextChannel.TEST);
	}

	private String getMessage(ChangelogEntry from, ChangelogEntry to) {
		if (from == null)
			from = changelog.getEntries().get(1);
		if (to == null)
			to = changelog.getEntries().get(0);

		return changelog.diff(from, to);
	}

	@Path("list [page]")
	@Description("View available changelog entries")
	void list(@Arg("1") int page) {
		if (changelog.getEntries().isEmpty())
			error("No snapshots have been created");

		send(PREFIX + "Changelog entries");
		BiFunction<ChangelogEntry, String, JsonBuilder> formatter = (entry, index) -> {
			String timestamp = TimeUtils.shortDateTimeFormat(entry.getTimestamp());
			String timestampIso = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(entry.getTimestamp());
			return json(index + " &e" + timestamp + " &7- &3" + entry.getMinecraftVersion() + " #" + entry.getPaperVersion())
					.hover("&3Plugins: &e" + entry.getPluginVersions().size())
					.hover("&3Plugin Notes: &e" + entry.getPluginNotes().size())
					.hover("&3Commits: &e" + entry.getCommits().size())
					.command("/changelog database debug " + timestampIso);
		};
		new Paginator<ChangelogEntry>()
			.values(changelog.getEntries())
			.formatter(formatter)
			.command("/changelog list ")
			.page(page)
			.send();
	}

	@Path("database debug <entry>")
	@Description("Print a raw changelog entry")
	void databaseDebug(ChangelogEntry entry) {
		send(StringUtils.toPrettyString(entry));
	}

	@ConverterFor(ChangelogEntry.class)
	ChangelogEntry convertToChangelogEntry(String value) {
		LocalDateTime timestamp = convertToLocalDateTime(value);
		return changelog.getEntries().stream().filter(entry -> entry.getTimestamp().isEqual(timestamp)).findAny()
				.orElseThrow(() -> new InvalidInputException("Changelog entry not found with timestamp " + TimeUtils.shortDateTimeFormat(timestamp)));
	}

	@TabCompleterFor(ChangelogEntry.class)
	List<String> tabCompleteChangelogEntry(String filter) {
		return changelog.getEntries().stream()
				.map(entry -> DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(entry.getTimestamp()))
				.filter(timestamp -> timestamp.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}
}
