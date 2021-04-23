package me.pugabyte.nexus.features.test;

import eden.annotations.Disabled;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.event.Listener;

@NoArgsConstructor
@Permission("group.staff")
@Disabled
public class TestCommand extends CustomCommand implements Listener {
//	List<Player> controlMinecart = new ArrayList<>();

	public TestCommand(CommandEvent event) {
		super(event);
	}

//		Tasks.repeat(10, 5, () -> {
//			List<Player> players = new ArrayList<>(controlMinecart);
//			for (Player player : players) {
//				if(player.getVehicle() == null || !player.getVehicle().getType().equals(EntityType.MINECART)) {
//					controlMinecart.remove(player);
//					send(player, "removed - got out of minecart");
//				}
//
//				Entity minecart = player.getVehicle();
//				Vector unitVector = player.getLocation().getDirection();
//				minecart.setVelocity((minecart.getVelocity().add(unitVector.multiply(0.2))).setY(0));
//			}
//		});
//	}

//	@Path("gravity fix")
//	public void fixgravity() {
//		player().setGravity(true);
//	}

//	@Path("minecart join")
//	public void minecartJoin(){
//		if(!controlMinecart.contains(player()))
//			controlMinecart.add(player());
//		send("joined");
//	}
//
//	@Path("minecart leave")
//	public void minecartLeave(){
//		controlMinecart.remove(player());
//		send("left");
//	}


}
