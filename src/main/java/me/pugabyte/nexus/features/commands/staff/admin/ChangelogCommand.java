package me.pugabyte.nexus.features.commands.staff.admin;

import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.changelog.Changelog;
import me.pugabyte.nexus.models.changelog.Changelog.ChangelogEntry;
import me.pugabyte.nexus.models.changelog.ChangelogService;
import me.pugabyte.nexus.utils.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.StringUtils.shortDateTimeFormat;

public class ChangelogCommand extends CustomCommand {
	private final ChangelogService service = new ChangelogService();
	private final Changelog changelog = service.get(Nexus.getUUID0());

	public ChangelogCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("generate")
	void generate() {
		changelog.generate();
		send(PREFIX + "Generated");
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
