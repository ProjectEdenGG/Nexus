package gg.projecteden.nexus.features.resourcepack.commands;

import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.features.resourcepack.CustomContentUtils;
import gg.projecteden.nexus.features.resourcepack.customblocks.customblockbreaking.BrokenBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.listeners.ConversionListener;
import gg.projecteden.nexus.features.resourcepack.customblocks.menus.CustomBlockCatalogMenu;
import gg.projecteden.nexus.features.resourcepack.customblocks.menus.CustomBlockSearchMenu;
import gg.projecteden.nexus.features.resourcepack.customblocks.menus.CustomBlockTagMenu;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlockTab;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.CustomBlockTag;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.common.ICustomBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.noteblocks.common.ICustomNoteBlock;
import gg.projecteden.nexus.features.resourcepack.customblocks.models.tripwire.common.ICustomTripwire;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.Debug;
import gg.projecteden.nexus.utils.Debug.DebugType;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.nms.NMSUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Environments(Env.TEST)
public class CustomBlocksCommand extends CustomCommand {

	public CustomBlocksCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		viewBlocks(CustomBlockTab.ALL);
	}

	@Path("catalog [tab]")
	@Description("Open the catalog menu")
	void viewBlocks(@Arg("all") CustomBlockTab tab) {
		checkPermissions();

		new CustomBlockCatalogMenu(tab).open(player());
	}

	@Path("search <filter>")
	@Description("Search for custom blocks containing the input")
	void searchCreative(String filter) {
		checkPermissions();

		List<CustomBlock> customBlocks = CustomBlock.matching(filter).stream().filter(CustomBlock::isObtainable).toList();
		if (customBlocks.isEmpty())
			error("No matching custom blocks");

		new CustomBlockSearchMenu(filter, customBlocks).open(player());
	}

	// STAFF COMMANDS

	@Path("tags list <tag>")
	@Description("List the custom blocks with the applicable tag")
	@Permission(Group.STAFF)
	void getTag(CustomBlockTag tag) {
		new CustomBlockTagMenu(tag).open(player());
	}

	@Path("tags of <block>")
	@Description("Get the applicable tags of the custom block")
	@Permission(Group.STAFF)
	void materialTag(CustomBlock customBlock) {
		send(PREFIX + "Applicable tags of " + camelCase(customBlock)
				+ ": &e" + String.join("&3, &e", CustomBlockTag.getApplicable(customBlock).keySet()));
	}

	@Path("debug [enabled]")
	@Permission(Group.STAFF)
	@Description("Toggle debugging custom blocks")
	void debug1(Boolean enabled) {
		if (enabled == null)
			enabled = !Debug.isEnabled(player(), DebugType.CUSTOM_BLOCKS);

		Debug.setEnabled(player(), DebugType.CUSTOM_BLOCKS, enabled);
		send(PREFIX + "Debug " + (enabled ? "&aEnabled" : "&cDisabled"));
	}

	// ADMIN COMMANDS

	@Path("getTargetInfo")
	@Permission(Group.ADMIN)
	void targetInfo() {
		Block block = getTargetBlockRequired();
		CustomBlock customBlock = CustomBlock.from(block);
		if (customBlock == null)
			error("That is not a valid custom block");

		ICustomBlock iCustomBlock = customBlock.get();

		send("Type: " + customBlock.getType());
		send("Item Name: " + iCustomBlock.getItemName());
		send("Item Model: " + iCustomBlock.getModel());
		send("Item Material: " + iCustomBlock.getVanillaItemMaterial());
		send("Creative Tab: " + customBlock.getCreativeTab());
		line();
		send("Block Info:");
		send("- Material: " + iCustomBlock.getVanillaBlockMaterial());
		send("- Hardness: " + iCustomBlock.getBlockHardness());
		send("- Piston Push Action: " + iCustomBlock.getPistonPushAction());
		send("- Piston Pull Action: " + iCustomBlock.getPistonPullAction());
		line();
		if (iCustomBlock instanceof ICustomNoteBlock iNoteBlock) {
			send(iNoteBlock.getStringBlockData(block.getBlockData()));
			line();
			send("Sounds: ");
			send("- Place: " + iNoteBlock.getPlaceSound());
			send("- Break: " + iNoteBlock.getBreakSound());
			send("- Hit: " + iNoteBlock.getHitSound());
			send("= Step: " + iNoteBlock.getStepSound());
			send("- Fall: " + iNoteBlock.getFallSound());

		} else if (iCustomBlock instanceof ICustomTripwire iTripWire) {
			send(iTripWire.getStringBlockData(block.getBlockData()));
			line();
			send("Sounds: ");
			send("- Place: " + iTripWire.getPlaceSound());
			send("- Break: " + iTripWire.getBreakSound());
			send("- Hit: " + iTripWire.getHitSound());
			send("- Step: " + iTripWire.getStepSound());
			send("- Fall: " + iTripWire.getFallSound());
		}
	}

	@Path("convertChunk")
	@Permission(Group.ADMIN)
	void convertChunk() {
		ConversionListener.convertCustomBlocks(player().getChunk(), true);
	}

	@Path("getAll")
	@Description("Spawn all custom blocks")
	@Permission(Group.ADMIN)
	void getAll() {
		for (CustomBlock customBlock : CustomBlock.getObtainable())
			giveItem(customBlock.get().getItemStack());
	}

	@Path("getBlockDirectional")
	@Description("Get the faces of the targeted directional custom block")
	@Permission(Group.ADMIN)
	void directionalBlock() {
		Block block = getTargetBlockRequired();
		if (!(block.getBlockData() instanceof MultipleFacing multipleFacing)) {
			error("Block is not directional");
			return;
		}

		send("Faces = " + multipleFacing.getAllowedFaces());
		line();
		send("Facing = " + multipleFacing.getFaces());
	}

	@Path("getBlockHardness")
	@Description("Get the block hardness of the targeted custom block")
	@Permission(Group.ADMIN)
	void hardness() {
		Block block = getTargetBlockRequired();
		ItemStack tool = getTool();
		if (tool == null)
			tool = new ItemStack(Material.AIR);

		CustomBlock customBlock = CustomBlock.from(block);
		boolean isCustomBlock = customBlock != null;

		String blockType = StringUtils.camelCase(block.getType());
		float blockHardness = BlockUtils.getBlockHardness(block);

		boolean canHarvest = BlockUtils.canHarvestWith(block, tool, player());

		Material itemType = tool.getType();
		float destroySpeedItem = NMSUtils.getDestroySpeed(block, tool);
		if (isCustomBlock) {
			blockType = StringUtils.camelCase(customBlock.name());
			destroySpeedItem = (float) customBlock.get().getSpeedMultiplier(tool);
		}

		BrokenBlock brokenBlock = new BrokenBlock(block, isCustomBlock, player(), tool, Bukkit.getCurrentTick());
		float blockDamage = brokenBlock.getBlockDamage(tool);
		int breakTicks = brokenBlock.getBreakTicks();
		double breakSeconds = breakTicks / 20.0;

		send("");
		send("Block: " + blockType);
		send("Block Hardness: " + blockHardness);
		line();
		send("Tool: " + StringUtils.camelCase(itemType));
		send("Item Destroy Speed: " + destroySpeedItem);
		send("Can Harvest: " + canHarvest);
		send("Block Damage: " + blockDamage);
		line();
		send("Break Time: " + breakTicks + "t | " + breakSeconds + "s");
		send("");
	}

	//

	@SuppressWarnings("UnusedReturnValue")
	private boolean checkPermissions() {
		if (isAdmin())
			return true;

		if (!CustomContentUtils.hasBypass(player())) {
			if (isStaff())
				error("You cannot use this command outside of creative/staff");
			else
				error("You cannot use this outside of creative");

			return false;
		}

		return true;
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
}
