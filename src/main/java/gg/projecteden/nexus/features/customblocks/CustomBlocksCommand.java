package gg.projecteden.nexus.features.customblocks;

import gg.projecteden.nexus.features.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.customblocks.models.CustomBlockTab;
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
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.NMSUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static gg.projecteden.api.common.utils.UUIDUtils.UUID0;

public class CustomBlocksCommand extends CustomCommand {
	private static final CustomBlockTrackerService trackerService = new CustomBlockTrackerService();
	private static CustomBlockTracker tracker;

	public CustomBlocksCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			tracker = trackerService.fromWorld(location());
	}

	@Path
	void menuCreative() {
		// TODO: uncomment upon release
//		if(!rank().isSeniorStaff() && !rank().isBuilder()){
//			if(worldGroup() != WorldGroup.CREATIVE)
//				error("This command can only be used in Creative!");
//		}

		new CustomBlockCreativeMenu(null).open(player());
	}

	@Path("search <filter>")
	void searchCreative(String filter) {
		// TODO: uncomment upon release
//		if(!rank().isSeniorStaff() && !rank().isBuilder()){
//			if(worldGroup() != WorldGroup.CREATIVE)
//				error("This command can only be used in Creative!");
//		}

		List<CustomBlock> customBlocks = CustomBlock.matching(filter).stream().filter(CustomBlock::isObtainable).toList();
		if (customBlocks.isEmpty())
			error("No matching custom blocks");

		new CustomBlockSearchMenu(filter, customBlocks).open(player());
	}

	@Path("list [world]")
	@Permission(Group.ADMIN)
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
	@Permission(Group.ADMIN)
	void getAll() {
		for (CustomBlock customBlock : CustomBlock.getObtainable())
			giveItem(customBlock.get().getItemStack());
	}

	@Path("tags list <tag>")
	@Permission(Group.STAFF)
	void getTag(CustomBlockTag tag) {
		new CustomBlockTagMenu(tag).open(player());
	}

	@Path("tags of <block>")
	@Permission(Group.STAFF)
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
	@Permission(Group.ADMIN)
	void hardness() {
		Block block = getTargetBlockRequired();
		ItemStack itemStack = getTool();
		if (itemStack == null)
			itemStack = new ItemStack(Material.AIR);

		Material blockType = block.getType();
		Material itemType = itemStack.getType();
		float blockHardness = NMSUtils.getBlockHardness(block);
		float destroySpeedItem = NMSUtils.getDestroySpeed(block, itemStack);
		boolean canHarvest = NMSUtils.canHarvest(player(), block, itemStack);
		float blockDamage = NMSUtils.getBlockDamage(player(), block, itemStack);
		int breakTicks = (int) Math.ceil(1 / blockDamage);
		double breakSeconds = breakTicks / 20.0;
		boolean isBestTool = block.isPreferredTool(itemStack);

		send("= = = = =");
		send("Block: " + blockType);
		send("Block Hardness: " + blockHardness);
		line();
		send("Tool: " + itemType);
		send("Item Destroy Speed: " + destroySpeedItem);
		send("Can Harvest: " + canHarvest);
		send("Is Best Tool: " + isBestTool);
		line();
		send("Block Damage: " + blockDamage);
		send("Break Times: " + breakTicks + "t | " + breakSeconds + "s");
		send("= = = = =");
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
			customBlockTag.getValues().forEach(customBlock -> {
				ItemStack customBlockItem = customBlock.get().getItemStack();
				if (!Nullables.isNullOrAir(customBlockItem)) {
					ItemStack item = new ItemBuilder(customBlock.get().getItemStack()).lore(customBlock.name().toLowerCase()).build();
					items.add(ClickableItem.empty(item));
				}
			});

			paginator().items(items).build();
		}

	}

	public static class CustomBlockCreativeMenu extends InventoryProvider {
		@Nullable CustomBlockTab currentTab;

		public CustomBlockCreativeMenu(@Nullable CustomBlockTab tab) {
			this.currentTab = tab;
		}

		@Override
		public String getTitle() {
			if (currentTab == null)
				return "Custom Blocks";

			return currentTab.getMenuTitle();
		}

		@Override
		public void init() {
			LinkedHashSet<ClickableItem> items = new LinkedHashSet<>();

			if (currentTab == null) {
				addCloseItem();

				for (CustomBlockTab tab : CustomBlockTab.getMenuTabs()) {
					ItemStack item = new ItemBuilder(CustomBlock.getBy(tab).get(0).get().getItemStack()).name(StringUtils.camelCase(tab)).build();

					items.add(ClickableItem.of(item, e -> new CustomBlockCreativeMenu(tab).open(player)));
				}

			} else {
				addBackItem(e -> new CustomBlockCreativeMenu(null).open(player));

				LinkedHashSet<ItemStack> uniqueItems = new LinkedHashSet<>();
				for (CustomBlock customBlock : CustomBlock.getBy(currentTab)) {
					ItemStack item = customBlock.get().getItemStack();
					uniqueItems.add(item);
				}

				for (ItemStack customBlockItem : uniqueItems) {
					items.add(ClickableItem.of(customBlockItem, e -> PlayerUtils.giveItem(player, customBlockItem)));
				}
			}

			paginator().items(items.stream().toList()).build();
		}
	}

	public static class CustomBlockSearchMenu extends InventoryProvider {
		@NonNull String filter;
		@NonNull List<CustomBlock> customBlocks;

		public CustomBlockSearchMenu(@NonNull String filter, @NonNull List<CustomBlock> customBlocks) {
			this.filter = filter;
			this.customBlocks = customBlocks;
		}

		@Override
		public String getTitle() {
			return "Custom Blocks: \"" + filter + "\"";
		}

		@Override
		public void init() {
			addCloseItem();

			LinkedHashSet<ClickableItem> items = new LinkedHashSet<>();
			for (CustomBlock customBlock : customBlocks) {
				ItemStack item = new ItemBuilder(customBlock.get().getItemStack()).build();
				items.add(ClickableItem.of(item, e -> PlayerUtils.giveItem(player, item)));
			}

			paginator().items(items.stream().toList()).build();
		}
	}
}
