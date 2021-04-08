package me.pugabyte.nexus.features.commands.staff.admin;

import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.features.discord.DiscordId.TextChannel;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Confirm;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.changelog.Changelog;
import me.pugabyte.nexus.models.changelog.Changelog.ChangelogEntry;
import me.pugabyte.nexus.models.changelog.ChangelogService;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.StringUtils.shortDateTimeFormat;

@Permission("group.admin")
public class ChangelogCommand extends CustomCommand {
	private final ChangelogService service = new ChangelogService();
	private final Changelog changelog = service.get(Nexus.getUUID0());

	public ChangelogCommand(@NonNull CommandEvent event) {
		super(event);
	}

	private void save() {
		service.save(changelog);
	}

	@Path("generate")
	void generate() {
		changelog.generate();
		save();
		send(PREFIX + "Generated");
	}

	@Confirm
	@Path("diff [from] [to]")
	void diff(ChangelogEntry from, ChangelogEntry to) {
		Discord.send(getMessage(from, to), TextChannel.CHANGELOG);
	}

	@Path("diff test [from] [to]")
	void diffTest(ChangelogEntry from, ChangelogEntry to) {
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
	void list(@Arg("1") int page) {
		if (changelog.getEntries().isEmpty())
			error("No snapshots have been created");

		send(PREFIX + "Changelog entries");
		BiFunction<ChangelogEntry, String, JsonBuilder> formatter = (entry, index) -> {
			String timestamp = shortDateTimeFormat(entry.getTimestamp());
			String timestampIso = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(entry.getTimestamp());
			return json("&3" + index + " &e" + timestamp + " &7- &3" + entry.getMinecraftVersion() + " #" + entry.getPaperVersion())
					.addHover("&3Plugins: &e" + entry.getPluginVersions().size())
					.addHover("&3Plugin Notes: &e" + entry.getPluginNotes().size())
					.addHover("&3Commits: &e" + entry.getCommits().size())
					.command("/changelog database debug " + timestampIso);
		};
		paginate(changelog.getEntries(), formatter, "/changelog list ", page);
	}

	@Path("database debug <entry>")
	void databaseDebug(ChangelogEntry entry) {
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
