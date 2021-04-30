package me.pugabyte.nexus.features.resourcepack;

import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.features.Feature;
import me.pugabyte.nexus.utils.PlayerUtils.Dev;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class ResourcePack extends Feature implements Listener {
	static final String URL = "http://cdn.bnn.gg/BearNationResourcePack.zip";
	static final String fileName = "ResourcePack.zip";
	@Getter
	static final String fileRegex = "[a-zA-Z0-9_]+";
	@Getter
	static String hash = Utils.createSha1(URL);
	@Getter
	static File file = Nexus.getFile(fileName);

	@Getter
	private static final List<CustomModelGroup> customModelGroups = new ArrayList<>();
	@Getter
	private static final List<CustomModel> customModels = new ArrayList<>();
	@Getter
	private static final CustomModelFolder rootFolder = new CustomModelFolder("/");

	static {
		CustomModelFolder.load();
	}

	public static boolean isCustomItem(ItemStack item) {
		return new NBTItem(item).hasKey("CustomModelData");
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (isCustomItem(event.getItemInHand()))
			event.setCancelled(true);
	}

	public static boolean isEnabledFor(Player player) {
		return player.getResourcePackStatus() == Status.SUCCESSFULLY_LOADED || Dev.WAKKA.is(player) || Dev.GRIFFIN.is(player);
	}
}
