package gg.projecteden.nexus.features.resourcepack;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateCompleteEvent;
import gg.projecteden.nexus.features.resourcepack.models.events.ResourcePackUpdateStartEvent;
import gg.projecteden.nexus.features.resourcepack.models.files.CustomModelFolder;
import gg.projecteden.nexus.features.resourcepack.models.files.CustomModelGroup;
import gg.projecteden.nexus.features.resourcepack.models.files.FontFile;
import gg.projecteden.nexus.features.resourcepack.models.files.SoundsFile;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUserService;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.ItemBuilder.CustomModelData;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.utils.Env;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import me.lexikiq.OptionalPlayerLike;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gg.projecteden.nexus.features.resourcepack.models.files.CustomModelGroup.addCustomModel;

@NoArgsConstructor
public class ResourcePack extends Feature implements Listener {
	public static final String ENV_SUFFIX = Nexus.getEnv() == Env.PROD ? "" : "-" + Nexus.getEnv();
	public static final String FILE_NAME = "ResourcePack%s.zip".formatted(ENV_SUFFIX);
	public static final String URL = "http://cdn.%s/%s".formatted(Nexus.DOMAIN, FILE_NAME);
	@Getter
	static final String fileRegex = "[\\w-]+";
	@Getter
	public static String hash = Utils.createSha1(URL);
	@Getter
	public static File file = IOUtils.getPluginFile(FILE_NAME);
	@Getter
	static final URI fileUri = URI.create("jar:" + file.toURI());
	@Getter
	private static FileSystem zipFile;

	@Getter
	private static List<CustomModelGroup> modelGroups;
	@Getter
	private static List<CustomModelFolder> folders;
	@Getter
	private static Map<String, CustomModel> models;
	@Getter
	private static CustomModelFolder rootFolder;
	@Getter
	private static SoundsFile soundsFile;
	@Getter
	private static FontFile fontFile;

	@Getter
	private static boolean reloading;

	@Override
	public void onStart() {
		new ResourcePackListener();
		read();
	}

	@Override
	@SneakyThrows
	public void onStop() {
		closeZip();
		Bukkit.getMessenger().unregisterOutgoingPluginChannel(Nexus.getInstance(), "titan:out");
	}

	public static void read() {
		Tasks.async(() -> {
			try {
				new ResourcePackUpdateStartEvent().callEvent();
				reloading = true;

				openZip();

				setup();
				readAllFiles();
				CustomModelMenu.load();

				reloading = false;
				new ResourcePackUpdateCompleteEvent().callEvent();
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				closeZip();
			}
		});
	}

	private static void setup() {
		modelGroups = new ArrayList<>();
		folders = new ArrayList<>();
		models = new HashMap<>();
		rootFolder = new CustomModelFolder("/");
	}

	static void readAllFiles() {
		try {
			for (Path root : zipFile.getRootDirectories()) {
				Files.walk(root).forEach(path -> {
					try {
						final String uri = path.toUri().toString();

						if (uri.contains(CustomModel.getVanillaSubdirectory()))
							addCustomModel(path);

						if (uri.endsWith(FontFile.getPath()))
							fontFile = FontFile.of(path);

						if (uri.endsWith(SoundsFile.getPath()))
							soundsFile = SoundsFile.of(path);

						if (uri.contains(".ogg"))
							SoundsFile.addAudioFile(path);

					} catch (Exception ex) {
						ex.printStackTrace();
					}
				});
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@SneakyThrows
	public static void openZip() {
		try {
			FileSystem existing = FileSystems.getFileSystem(fileUri);
			if (existing != null && existing.isOpen())
				existing.close();
		} catch (FileSystemNotFoundException ignore) {}

		zipFile = FileSystems.newFileSystem(fileUri, Collections.emptyMap());
	}

	@SneakyThrows
	public static void closeZip() {
		if (zipFile != null && zipFile.isOpen())
			zipFile.close();
	}

	public static boolean isCustomItem(@Nullable ItemStack item) {
		return item != null && !MaterialTag.ALL_AIR.isTagged(item.getType()) && CustomModelData.of(item) > 0;
	}

	public static boolean isEnabledFor(Player player) {
		return player.getResourcePackStatus() == Status.SUCCESSFULLY_LOADED || new LocalResourcePackUserService().get(player).isEnabled();
	}

	public static boolean isEnabledFor(OptionalPlayerLike player) {
		if (!player.isOnline() || player.getPlayer() == null)
			return false;

		return isEnabledFor(player.getPlayer());
	}

	public static void send(Player player) {
		player.setResourcePack(URL, hash);
	}

}
