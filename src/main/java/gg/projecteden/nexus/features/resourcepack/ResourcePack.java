package gg.projecteden.nexus.features.resourcepack;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.resourcepack.LocalResourcePackUserService;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Utils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
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

@NoArgsConstructor
public class ResourcePack extends Feature implements Listener {
	static final String URL = "http://cdn.projecteden.gg/ResourcePack.zip";
	static final String fileName = "ResourcePack.zip";
	@Getter
	static final String fileRegex = "[a-zA-Z0-9_]+";
	@Getter
	static String hash = Utils.createSha1(URL);
	@Getter
	static File file = Nexus.getFile(fileName);

	@Getter
	@Setter
	private static List<CustomModelGroup> modelGroups;
	@Getter
	@Setter
	private static List<CustomModelFolder> folders;
	@Getter
	@Setter
	private static List<CustomModel> models;
	@Getter
	@Setter
	private static CustomModelFolder rootFolder;

	@Getter
	static final URI fileUri = URI.create("jar:" + ResourcePack.getFile().toURI());
	@Getter
	static final String subdirectory = "/assets/minecraft/models/item";
	@Getter
	private static FileSystem zipFile;

	@Override
	public void onStart() {
		openZip();
		CustomModelMenu.load();
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
}
