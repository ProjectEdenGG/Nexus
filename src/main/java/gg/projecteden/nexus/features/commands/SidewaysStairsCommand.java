package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.blockorientation.BlockOrientationUser;
import gg.projecteden.nexus.models.blockorientation.BlockOrientationUser.Stairs;
import gg.projecteden.nexus.models.blockorientation.BlockOrientationUser.Stairs.StairAction;
import gg.projecteden.nexus.models.blockorientation.BlockOrientationUser.Stairs.StairDirection;
import gg.projecteden.nexus.models.blockorientation.BlockOrientationUser.Stairs.StairModification;
import gg.projecteden.nexus.models.blockorientation.BlockOrientationUser.Stairs.StairSlope;
import gg.projecteden.nexus.models.blockorientation.BlockOrientationUserService;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

@Aliases("sws")
@NoArgsConstructor
public class SidewaysStairsCommand extends CustomCommand implements Listener {
	private static final BlockOrientationUserService service = new BlockOrientationUserService();
	private BlockOrientationUser user;

	public SidewaysStairsCommand(@NonNull CommandEvent event) {
		super(event);
		user = service.get(player());
	}

	private Stairs stairs() {
		return user.getStairs();
	}

	@Path("<state>")
	@Description("Toggle stair orientation handling")
	void toggle(Boolean state) {
		if (state == null)
			state = !stairs().isEnabled();

		stairs().setEnabled(state);
		service.save(user);
		send(PREFIX + (stairs().isEnabled() ? "Enabled" : "Disabled"));
	}

	@Path("reset")
	@Description("Remove all stair orientation configuration")
	void reset() {
		stairs().setAction(null);
		stairs().setDirection(null);
		stairs().setSlope(null);
		service.save(user);
		send(PREFIX + "Stair configuration reset");
	}

	@Path("set direction <direction>")
	@Description("Lock placement of stairs to a certain direction")
	void set_direction(StairDirection direction) {
		stairs().setEnabled(true);
		stairs().setDirection(direction);
		service.save(user);
		send(PREFIX + "Direction set to " + direction);
	}

	@Path("set slope [orientation]")
	@Description("Lock placement of stairs to normal or inverted")
	void set_slope(StairSlope half) {
		stairs().setEnabled(true);
		stairs().setSlope(half);
		service.save(user);
		send(PREFIX + "Locked placement of stairs " + camelCase(half));
	}

	@Path("copy [modification]")
	@Description("Copy the direction and slope of an existing stair")
	void copy(StairModification modification) {
		stairs().setAction(modification == null ? StairAction.COPY : modification.getCopyAction());
		service.save(user);
		send(PREFIX + "Right click a stair block to copy its direction and slope");
	}

	@EventHandler
	public void on(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND)
			return;
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
			return;

		final Block block = event.getClickedBlock();
		if (Nullables.isNullOrAir(block))
			return;

		final Player player = event.getPlayer();
		final BlockOrientationUser user = new BlockOrientationUserService().get(player);
		final Stairs stairs = user.getStairs();

		if (stairs.getAction() == null)
			return;

		if (!stairs.getAction().toString().contains("COPY"))
			return;

		if (!MaterialTag.STAIRS.isTagged(block.getType())) {
			PlayerUtils.send(player, PREFIX + "&cCan only copy angle of a stair block");
			return;
		}

		event.setCancelled(true);

		final String direction = BlockUtils.getBlockProperty(block, "facing");
		final String half = BlockUtils.getBlockProperty(block, "half");

		Runnable copyDirection = () -> stairs.setDirection(StairDirection.valueOf(direction.toUpperCase()));
		Runnable copySlope = () -> stairs.setSlope(StairSlope.from(half));

		switch (stairs.getAction()) {
			case COPY_DIRECTION -> copyDirection.run();
			case COPY_SLOPE -> copySlope.run();
			case COPY -> {
				copyDirection.run();
				copySlope.run();
			}
		}

		stairs.setAction(null);
		stairs.setEnabled(true);
		service.save(user);
		send(player, PREFIX + "Stair direction successfully copied (Direction: &e%s&3, Slope: &e%s&3)".formatted(stairs.getDirection(), stairs.getSlope()));
	}

	@EventHandler
	public void on(PlayerQuitEvent event) {
		new BlockOrientationUserService().edit(event.getPlayer(), user -> user.getStairs().setAction(null));
	}

	@EventHandler
	public void onStairPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Stairs stairs = new BlockOrientationUserService().get(player).getStairs();

		if (!MaterialTag.STAIRS.isTagged(block.getType()))
			return;

		if (stairs == null || !stairs.isEnabled())
			return;

		if (stairs.getDirection() != null)
			BlockUtils.updateBlockProperty(block, "facing", stairs.getDirection().toString());

		if (stairs.getSlope() != null)
			BlockUtils.updateBlockProperty(block, "half", stairs.getSlope().getNbt());
	}

}
