package gg.projecteden.nexus.features.store.perks.inventory;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.autotorch.AutoTorchService;
import gg.projecteden.nexus.models.autotorch.AutoTorchUser;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.GameModeWrapper;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.MaterialTag.MatchMode;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardFlagUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Aliases("at")
@NoArgsConstructor
@Permission("nexus.autotorch")
@Description("Automatically place a light source when it gets too dark")
@WikiConfig(rank = "Store", feature = "Inventory")
public class AutoTorchCommand extends CustomCommand {
	public static final String PERMISSION = "nexus.autotorch";

	public static final MaterialTag AUTO_TORCH_TYPES = new MaterialTag(Material.GLOWSTONE, Material.SHROOMLIGHT, Material.END_ROD, Material.SEA_PICKLE, Material.REDSTONE_LAMP, Material.LIGHT)
		.append(MaterialTag.TORCHES, MaterialTag.LANTERNS, MaterialTag.FROGLIGHT, MaterialTag.CAMPFIRES)
		.exclude("WALL_", MatchMode.CONTAINS);

	private static final AutoTorchService service = new AutoTorchService();
	private AutoTorchUser autoTorch;

	public AutoTorchCommand(@NonNull CommandEvent event) {
		super(event);
		autoTorch = service.get(player());
	}

	@Path("[on|off]")
	@Description("Toggle automatically placing light blocks when it gets too dark")
	void toggle(Boolean state) {
		if (state == null)
			state = !autoTorch.isEnabled();

		autoTorch.setEnabled(state);
		service.save(autoTorch);
		send(PREFIX + (state ? "&aEnabled" : "&cDisabled"));
	}

	@Path("lightLevel [level]")
	@Description("Adjust the light level at which the light blocks are placed")
	void lightLevel(@Arg(min = 0, max = 15) int level) {
		autoTorch.setLightLevel(level);
		service.save(autoTorch);
		send(PREFIX + "A &e" + autoTorch.getTorchMaterialName() + " &3will now be automatically placed at your feet at light level &e" + level + "&3 or lower");
	}

	@Path("lightBlock <lightBlock>")
	@Description("Set the block placed")
	void material(LightBlock lightBlock) {
		autoTorch.setTorchMaterial(lightBlock.getMaterial());
		service.save(autoTorch);
		send(PREFIX + "Set the placed light block to &e" + autoTorch.getTorchMaterialName());
	}

	@Data
	@AllArgsConstructor
	public static class LightBlock {
		private Material material;
	}

	@ConverterFor(LightBlock.class)
	LightBlock convertToLightBlock(String value) {
		Material material = Material.matchMaterial(value);

		if (material == null)
			throw new InvalidInputException("Material from " + value + " not found");

		if (!AUTO_TORCH_TYPES.getValues().contains(material))
			throw new InvalidInputException("Light Block Material from " + value + " not found");

		return new LightBlock(material);
	}

	@TabCompleterFor(LightBlock.class)
	List<String> tabCompleteLightBlock(String filter) {
		return Arrays.stream(Material.class.getEnumConstants())
			.filter(material -> AUTO_TORCH_TYPES.getValues().contains(material))
			.map(defaultTabCompleteEnumFormatter())
			.filter(material -> material.toLowerCase().startsWith(filter.toLowerCase()))
			.collect(Collectors.toList());
	}

	private static int taskId = -1;

	static {
		taskId = Tasks.repeatAsync(5, 5, () -> {
			OnlinePlayers.getAll().forEach(player -> {
				GameModeWrapper gameMode = GameModeWrapper.of(player);
				// basic checks to ensure player can use the command and is in survival + the survival world.
				// also checks world guard to avoid spam in player's chat of "hey! you can't do that here"
				if (!gameMode.canBuild())
					return;
				if (!WorldGroup.SURVIVAL.contains(player.getWorld()))
					return;
				if (!player.hasPermission(AutoTorchCommand.PERMISSION))
					return;
				if (!WorldGuardFlagUtils.canPlace(player))
					return;

				AutoTorchUser autoTorchUser = service.get(player);

				// ensures the player has a light block
				ItemStack item = PlayerUtils.getNonNullInventoryContents(player).stream()
					.filter(itemStack -> itemStack.getType() == autoTorchUser.getTorchMaterial() && itemStack.getAmount() > 0)
					.findAny()
					.orElse(null);
				if (item == null)
					return;

				Block block = player.getLocation().getBlock();

				Tasks.wait(5, () -> {
					if (!autoTorchUser.applies(block)) // tests light level and for valid torch placing location
						return;

					if (!BlockUtils.tryPlaceEvent(player, block, block.getRelative(0, -1, 0), autoTorchUser.getTorchMaterial(), new ItemStack(autoTorchUser.getTorchMaterial())))
						return;

					new SoundBuilder(Sound.BLOCK_WOOD_PLACE).location(block).category(SoundCategory.BLOCKS).play();

					// remove a torch from player's inventory
					if (gameMode.isSurvival())
						item.subtract();
				});
			});
		});
	}

	@Override
	public void _shutdown() {
		if (taskId != -1) {
			Tasks.cancel(taskId);
			taskId = -1;
		}
	}

}
