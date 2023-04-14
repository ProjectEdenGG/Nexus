package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.api.discord.DiscordId.TextChannel;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.framework.commandsv2.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commandsv2.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.Confirm;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
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

import static gg.projecteden.api.common.utils.TimeUtils.shortDateTimeFormat;

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

	@Description("Generate a changelog")
	void generate() {
		changelog.generate();
		save();
		send(PREFIX + "Generated");
	}

	@Confirm
	@Description("Post a changelog to Discord")
	void diff(ChangelogEntry from, ChangelogEntry to) {
		Discord.send(getMessage(from, to), TextChannel.CHANGELOG);
	}

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

	@Description("View available changelog entries")
	void list(@Optional("1") int page) {
		if (changelog.getEntries().isEmpty())
			error("No snapshots have been created");

		send(PREFIX + "Changelog entries");
		BiFunction<ChangelogEntry, String, JsonBuilder> formatter = (entry, index) -> {
			String timestamp = shortDateTimeFormat(entry.getTimestamp());
			String timestampIso = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(entry.getTimestamp());
			return json(index + " &e" + timestamp + " &7- &3" + entry.getMinecraftVersion() + " #" + entry.getPaperVersion())
					.hover("&3Plugins: &e" + entry.getPluginVersions().size())
					.hover("&3Plugin Notes: &e" + entry.getPluginNotes().size())
					.hover("&3Commits: &e" + entry.getCommits().size())
					.command("/changelog database debug " + timestampIso);
		};
		paginate(changelog.getEntries(), formatter, "/changelog list ", page);
	}

	@Description("Print a raw changelog entry")
	void database_debug(ChangelogEntry entry) {
		send(StringUtils.toPrettyString(entry));
	}

	@ConverterFor(ChangelogEntry.class)
	ChangelogEntry convertToChangelogEntry(String value) {
		LocalDateTime timestamp = convertToLocalDateTime(value);
		return changelog.getEntries().stream().filter(entry -> entry.getTimestamp().isEqual(timestamp)).findAny()
				.orElseThrow(() -> new InvalidInputException("Changelog entry not found with timestamp " + shortDateTimeFormat(timestamp)));
	}

	@TabCompleterFor(ChangelogEntry.class)
	List<String> tabCompleteChangelogEntry(String filter) {
		return changelog.getEntries().stream()
				.map(entry -> DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(entry.getTimestamp()))
				.filter(timestamp -> timestamp.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}
}
