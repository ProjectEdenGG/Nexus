package gg.projecteden.nexus.features.resourcepack;

import de.tr7zw.nbtapi.NBTItem;
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
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.utils.Env;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import me.lexikiq.OptionalPlayerLike;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static gg.projecteden.nexus.features.resourcepack.models.files.CustomModelGroup.addAudioFile;
import static gg.projecteden.nexus.features.resourcepack.models.files.CustomModelGroup.addCustomModel;

@NoArgsConstructor
public class ResourcePack extends Feature implements Listener {
	public static final String ENV_SUFFIX = Nexus.getEnv() == Env.PROD ? "" : "-" + Nexus.getEnv();
	public static final String FILE_NAME = "ResourcePack%s.zip".formatted(ENV_SUFFIX);
	public static final String URL = "http://cdn.%s/%s".formatted(Nexus.DOMAIN, FILE_NAME);
	@Getter
	static final String fileRegex = "[\\w]+";
	@Getter
	public static String hash = Utils.createSha1(URL);
	@Getter
	public static File file = IOUtils.getPluginFile(FILE_NAME);

	@Getter
	@Setter
	private static List<CustomModelGroup> modelGroups;
	@Getter
	@Setter
	private static List<CustomModelFolder> folders;
	@Getter
	@Setter
	private static Map<String, CustomModel> models;
	@Getter
	@Setter
	private static CustomModelFolder rootFolder;
	@Getter
	@Setter
	private static SoundsFile soundsFile;
	@Getter
	@Setter
	private static FontFile fontFile;

	@Getter
	static final URI fileUri = URI.create("jar:" + ResourcePack.getFile().toURI());
	@Getter
	static final String subdirectory = "/assets/minecraft/models/item";
	@Getter
	private static FileSystem zipFile;

	@Override
	public void onStart() {
		Bukkit.getMessenger().registerIncomingPluginChannel(Nexus.getInstance(), "titan:out", new VersionsChannelListener());

		read();
	}

	public static void read() {

		Tasks.async(() -> {
			new ResourcePackUpdateStartEvent().callEvent();
			try {
				openZip();
				CustomModelMenu.load();
				new ResourcePackUpdateCompleteEvent().callEvent();
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				closeZip();
			}
		});
	}

	static void readAllFiles() {
		try {
			for (Path root : ResourcePack.getZipFile().getRootDirectories()) {
				Files.walk(root).forEach(path -> {
					try {
						final String uri = path.toUri().toString();
						if (uri.contains(ResourcePack.getSubdirectory()))
							addCustomModel(path);
						if (uri.endsWith("minecraft/sounds.json"))
							soundsFile = Utils.getGson().fromJson("{\"sounds\":" + String.join("", Files.readAllLines(path)) + "}", SoundsFile.class);
						if (uri.endsWith("font/default.json"))
							fontFile = Utils.getGson().fromJson(String.join("", Files.readAllLines(path)), FontFile.class);
						if (uri.contains(".ogg"))
							addAudioFile(path);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				});
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	@SneakyThrows
	public void onStop() {
		closeZip();
		Bukkit.getMessenger().unregisterOutgoingPluginChannel(Nexus.getInstance(), "titan:out");
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
		return item != null && !MaterialTag.ALL_AIR.isTagged(item.getType()) && new NBTItem(item).hasKey(CustomModel.NBT_KEY);
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (isCustomItem(event.getItemInHand()))
			event.setCancelled(true);
	}

	public static boolean isEnabledFor(Player player) {
		return player.getResourcePackStatus() == Status.SUCCESSFULLY_LOADED || new LocalResourcePackUserService().get(player).isEnabled();
	}

	public static boolean isEnabledFor(OptionalPlayerLike player) {
		if (!player.isOnline() || player.getPlayer() == null)
			return false;

		return isEnabledFor(player.getPlayer());
	}

}
