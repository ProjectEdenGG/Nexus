package me.pugabyte.nexus.models.changelog;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.LocalDateTimeConverter;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.pugabyte.nexus.utils.TimeUtils.shortDateTimeFormat;

@Data
@Builder
@Entity("changelog")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class Changelog implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<ChangelogEntry> entries = new ArrayList<>();

	private static transient final String nl = System.lineSeparator();

	public void generate() {
		entries.add(0, new ChangelogEntry(entries.size() + 1));
	}

	public String diff() {
		return diff(entries.get(1), entries.get(0));
	}

	@SuppressWarnings("StringConcatenationInLoop")
	public String diff(ChangelogEntry from, ChangelogEntry to) {
		if (from.getTimestamp().isAfter(to.getTimestamp()))
			throw new InvalidInputException("First entry cannot be before second entry");

		String message = "**Changelog** " + shortDateTimeFormat(to.getTimestamp()) + nl + nl;
		if (!from.getMinecraftVersion().equals(to.getMinecraftVersion()))
			message += "**Minecraft version updated:** " + to.getMinecraftVersion() + nl;
		if (!from.getPaperVersion().equals(to.getPaperVersion()))
			message += "**Parchment version updated:** " + to.getPaperVersion() + nl + nl;

		List<String> pluginsAdded = new ArrayList<>();
		List<String> pluginsRemoved = new ArrayList<>();
		List<String> pluginsUpdated = new ArrayList<>();

		from.getPluginVersions().forEach((plugin, version) -> {
			if (!to.getPluginVersions().containsKey(plugin))
				pluginsRemoved.add(plugin);
		});
		to.getPluginVersions().forEach((plugin, version) -> {
			if (!from.getPluginVersions().containsKey(plugin))
				pluginsAdded.add(plugin);

			else if (!from.getPluginVersions().get(plugin).equals(version))
				pluginsUpdated.add(plugin);
		});

		if (!pluginsAdded.isEmpty()) {
			message += "**Plugins added**" + nl;
			for (String plugin : pluginsAdded)
				message += plugin + " (" + to.getPluginVersions().get(plugin) + ")" + nl;
			message += nl;
		}

		if (!pluginsRemoved.isEmpty()) {
			message += "**Plugins removed**" + nl;
			for (String plugin : pluginsRemoved)
				message += plugin + " (" + from.getPluginVersions().get(plugin) + ")" + nl;
			message += nl;
		}

		if (!pluginsUpdated.isEmpty()) {
			message += "**Plugins updated**" + nl;
			for (String plugin : pluginsUpdated)
				message += plugin + " (" + to.getPluginVersions().get(plugin) + ")" + nl;
			message += nl;
		}

		return message;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Converters(LocalDateTimeConverter.class)
	public static class ChangelogEntry {
		private int id;
		private LocalDateTime timestamp;
		private String minecraftVersion;
		private String paperVersion;
		private Map<String, String> pluginVersions = new HashMap<>();
		private Map<String, String> pluginNotes = new HashMap<>();
		private List<Commit> commits = new ArrayList<>();

		public ChangelogEntry(int id) {
			this.id = id;
			this.timestamp = LocalDateTime.now().withNano(0);
			this.minecraftVersion = Bukkit.getMinecraftVersion().split("-")[0];
			this.paperVersion = Bukkit.getVersion().split(" ")[0].split("-", 3)[2].replace("\"", "");
			for (Plugin plugin : Bukkit.getPluginManager().getPlugins())
				pluginVersions.put(plugin.getName(), plugin.getDescription().getVersion());
		}

		@Data
		@NoArgsConstructor
		@AllArgsConstructor
		@Converters(UUIDConverter.class)
		public static class Commit {
			@NonNull
			private UUID author;
			@NonNull
			private String repository;
			@NonNull
			private String hash;
			@NonNull
			private String description;

		}
	}

}
