package gg.projecteden.nexus.utils;

import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.Nexus;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class IOUtils {
	public final static String PLUGIN_ROOT = "plugins/" + Nexus.class.getSimpleName() + "/";
	public final static String LOGS_ROOT = PLUGIN_ROOT + "logs/";

	public static void fileAppend(String file, String message) {
		write(LOGS_ROOT + file + ".log", List.of(StandardOpenOption.APPEND), writer ->
			writer.append(String.format("%n[%s] %s", TimeUtils.shortDateTimeFormat(LocalDateTime.now()), message)));
	}

	public static void csvAppend(String file, String message) {
		write(LOGS_ROOT + file + ".csv", List.of(StandardOpenOption.APPEND), writer ->
			writer.append(String.format("%n%s", message)));
	}

	// Overwrites existing content
	public static void fileWrite(String file, BiConsumer<BufferedWriter, List<String>> consumer) {
		write(file, List.of(), writer -> {
			final List<String> outputs = new ArrayList<>();
			consumer.accept(writer, outputs);
			writer.write(String.join(System.lineSeparator(), outputs));
		});
	}

	private static void write(String fileName, List<StandardOpenOption> openOptions, UncheckedConsumer<BufferedWriter> consumer) {
		Tasks.async(() -> {
			synchronized (Nexus.getInstance()) {
				try {
					final Path path = Paths.get(fileName);
					final File file = path.toFile();
					if (!file.exists()) {
						file.getParentFile().mkdirs();
						file.createNewFile();
					}

					final StandardOpenOption[] options = openOptions.toArray(StandardOpenOption[]::new);
					try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, options)) {
						consumer.accept(writer);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} catch (Exception ex) {
					Nexus.severe("Error creating file " + fileName);
					ex.printStackTrace();
				}
			}
		});
	}

	@SneakyThrows
	public static File getPluginFile(String path) {
		return getFile(PLUGIN_ROOT + path);
	}

	@SneakyThrows
	public static File getPluginFolder(String path) {
		return getFolder(PLUGIN_ROOT + path);
	}

	@SneakyThrows
	public static File getLogsFile(String path) {
		return getFile(LOGS_ROOT + path);
	}

	@SneakyThrows
	public static File getLogsFolder(String path) {
		return getFolder(LOGS_ROOT + path);
	}

	@SneakyThrows
	public static File getFile(String path) {
		File file = Paths.get(path).toFile();
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		return file;
	}

	@SneakyThrows
	public static File getFolder(String path) {
		File file = Paths.get(path).toFile();
		if (!file.exists()) file.mkdir();
		return file;
	}

	@SneakyThrows
	public static YamlConfiguration getNexusConfig(String path) {
		return YamlConfiguration.loadConfiguration(getPluginFile(path));
	}

	@SneakyThrows
	public static YamlConfiguration getConfig(String path) {
		return YamlConfiguration.loadConfiguration(getFile(path));
	}

}
