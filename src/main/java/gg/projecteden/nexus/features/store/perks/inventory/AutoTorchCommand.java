package gg.projecteden.nexus.features.store.perks.inventory;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.WikiConfig;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.models.autotorch.AutoTorchService;
import gg.projecteden.nexus.models.autotorch.AutoTorchUser;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.CompletableTask;
import gg.projecteden.nexus.utils.GameModeWrapper;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardFlagUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor
@Permission("nexus.autotorch")
@Description("Automatically place torches when it gets too dark")
@WikiConfig(rank = "Store", feature = "Inventory")
public class AutoTorchCommand extends CustomCommand {
	public static final String PERMISSION = "nexus.autotorch";

	private static final AutoTorchService service = new AutoTorchService();
	private AutoTorchUser autoTorch;

	public AutoTorchCommand(@NonNull CommandEvent event) {
		super(event);
		autoTorch = service.get(player());
	}

	@NoLiterals
	@Path("<on|off>")
	@Description("Toggle automatically placing torches when it gets too dark")
	void toggle(Boolean state) {
		if (state == null)
			state = !autoTorch.isEnabled();

		autoTorch.setEnabled(state);
		service.save(autoTorch);
		send(PREFIX + (state ? "&aEnabled" : "&cDisabled"));
	}

	@Path("lightlevel [level]")
	@Description("Adjust the light level at which torches are placed")
	void lightlevel(@Arg(min = 0, max = 15) int level) {
		autoTorch.setLightLevel(level);
		service.save(autoTorch);
		send(PREFIX + "Torches will now be automatically placed at your feet at light level &e"+level+"&3 or lower");
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

				// ensures the player has a torch
				ItemStack item = PlayerUtils.getNonNullInventoryContents(player).stream()
					.filter(itemStack -> itemStack.getType() == Material.TORCH && itemStack.getAmount() > 0)
					.findAny()
					.orElse(null);
				if (item == null) return;

				AutoTorchUser autoTorchUser = service.get(player);
				Block block = player.getLocation().getBlock();

				CompletableTask.supplySync(() -> {
					if (!autoTorchUser.applies(player, block)) // tests light level and for valid torch placing location
						return false;

					if (!BlockUtils.tryPlaceEvent(player, block, block.getRelative(0, -1, 0), Material.TORCH))
						return false;

					return true;
				}).thenAccept(success -> {
					if (success) {
						// play sound
						new SoundBuilder(Sound.BLOCK_WOOD_PLACE).location(block).category(SoundCategory.BLOCKS).play();

						// remove a torch from player's inventory
						if (gameMode.isSurvival())
							item.subtract();
					}
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
