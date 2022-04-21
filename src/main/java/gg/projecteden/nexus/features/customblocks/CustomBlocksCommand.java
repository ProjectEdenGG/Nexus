package gg.projecteden.nexus.features.customblocks;

import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.customblocks.models.CustomBlockTag;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.customblock.CustomBlockData;
import gg.projecteden.nexus.models.customblock.CustomBlockTracker;
import gg.projecteden.nexus.models.customblock.CustomBlockTrackerService;
import gg.projecteden.nexus.utils.NMSUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static gg.projecteden.utils.UUIDUtils.UUID0;

@Permission(Group.ADMIN)
public class CustomBlocksCommand extends CustomCommand {
	private static final CustomBlockTrackerService trackerService = new CustomBlockTrackerService();
	private static CustomBlockTracker tracker;

	public CustomBlocksCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			tracker = trackerService.fromWorld(location());
	}

	@Path("list [world]")
	void list(@Arg("current") World world) {
		tracker = trackerService.fromWorld(world);
		Map<Location, CustomBlockData> locationMap = tracker.getLocationMap();
		if (locationMap.isEmpty())
			throw new InvalidInputException("This world has no saved custom blocks");

		send("World: " + world.getName());

		for (Location location : locationMap.keySet()) {
			CustomBlockData data = locationMap.get(location);
			CustomBlock customBlock = data.getCustomBlock();
			if (customBlock == null)
				continue;

			UUID uuid = data.getPlacerUUID();
			String playerName = "Unknown";
			if (!UUID0.equals(uuid))
				playerName = PlayerUtils.getPlayer(uuid).getName();

			send(" " + StringUtils.getCoordinateString(location) + ": " + StringUtils.camelCase(customBlock.name()) + " - " + playerName);
		}
	}

	@Path("getAll")
	void getAll() {
		for (CustomBlock customBlock : CustomBlock.values())
			giveItem(customBlock.get().getItemStack());
	}

	@Path("tags list <tag>")
	void getTag(CustomBlockTag tag) {
		new CustomBlockTagMenu(tag).open(player());
	}

	@Path("tags of <block>")
	void materialTag(CustomBlock customBlock) {
		send(PREFIX + "Applicable tags of " + camelCase(customBlock)
			+ ": &e" + String.join("&3, &e", CustomBlockTag.getApplicable(customBlock).keySet()));
	}

	@ConverterFor(CustomBlockTag.class)
	CustomBlockTag convertToMaterialTag(String value) {
		if (CustomBlockTag.getTags().containsKey(value.toUpperCase()))
			return (CustomBlockTag) CustomBlockTag.getTags().get(value.toUpperCase());
		throw new InvalidInputException("CustomBlockTag from " + value + " not found");
	}

	@TabCompleterFor(CustomBlockTag.class)
	List<String> tabCompleteMaterialTag(String filter) {
		return CustomBlockTag.getTags().keySet().stream()
			.map(String::toLowerCase)
			.filter(s -> s.startsWith(filter.toLowerCase()))
			.toList();
	}

	/*
		https://minecraft.fandom.com/wiki/Breaking

		If the tool and enchantments immediately equal or exceeds the hardness times 30, the block
		breaks with no delay; otherwise a 6 tick (3⁄10 second) delay occurs before the next block begins to break.
	 */
	@Path("getBlockHardness")
	void hardness() {
		Block block = getTargetBlockRequired();
		ItemStack itemStack = getTool();

		Material blockType = block.getType();
		Material itemType = itemStack == null ? Material.AIR : itemStack.getType();
		float blockDurability = NMSUtils.getBlockDurability(block);
		float blockHardness = NMSUtils.getBlockHardness(block);
		float destroySpeedItem = NMSUtils.getDestroySpeed(block, itemStack);
		float destroySpeedItem1 = -1;
		if (itemType != Material.AIR)
			destroySpeedItem1 = block.getDestroySpeed(itemStack, true);

		send("Tool: " + itemType);
		send("Block: " + blockType);
		send("Block Hardness: " + blockHardness);
		send("Block Durability: " + blockDurability);
		send("Item Destroy Speed: " + destroySpeedItem);
		send("Item Destroy Speed Alt: " + destroySpeedItem1);
		send("");
		send("Block Damage: " + NMSUtils.getBlockDamage(player(), block, itemStack));
	}


	public static class CustomBlockTagMenu extends InventoryProvider {
		private final CustomBlockTag customBlockTag;

		public CustomBlockTagMenu(CustomBlockTag tag) {
			this.customBlockTag = tag;
		}

		@Override
		public String getTitle() {
			return "&3" + StringUtils.camelCase(customBlockTag.getKey().getKey());
		}

		@Override
		public void init() {
			addCloseItem();

			List<ClickableItem> items = new ArrayList<>();
			customBlockTag.getValues().forEach(customBlock -> items.add(ClickableItem.empty(customBlock.get().getItemStack())));

			paginator().items(items).build();
		}

	}
}
