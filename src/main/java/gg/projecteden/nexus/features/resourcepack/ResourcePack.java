package gg.projecteden.nexus.features.resourcepack;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUserService;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import me.lexikiq.OptionalPlayerLike;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@NoArgsConstructor
public class ResourcePack extends Feature implements Listener {
	static final String URL = "http://cdn." + Nexus.DOMAIN + "/ResourcePack.zip";
	static final String fileName = "ResourcePack.zip";
	@Getter
	static final String fileRegex = "[\\w]+";
	@Getter
	static String hash = Utils.createSha1(URL);
	@Getter
	static File file = IOUtils.getPluginFile(fileName);

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
	static final URI fileUri = URI.create("jar:" + ResourcePack.getFile().toURI());
	@Getter
	static final String subdirectory = "/assets/minecraft/models/item";
	@Getter
	private static FileSystem zipFile;
	@Getter
	private static final CompletableFuture<Void> loader = new CompletableFuture<>();

	@Override
	public void onStart() {
		Tasks.async(() -> {
			openZip();
			CustomModelMenu.load();
			loader.complete(null);
		});
	}

	@Override
	@SneakyThrows
	public void onStop() {
		closeZip();
	}

	@SneakyThrows
	static void openZip() {
		try {
			FileSystem existing = FileSystems.getFileSystem(fileUri);
			if (existing != null && existing.isOpen())
				existing.close();
		} catch (FileSystemNotFoundException ignore) {}

		zipFile = FileSystems.newFileSystem(fileUri, Collections.emptyMap());
	}

	@SneakyThrows
	static void closeZip() {
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
