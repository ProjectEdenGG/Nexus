package me.pugabyte.nexus.features.test;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.particles.effects.DotEffect;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.ColorType;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Listener;

import java.util.List;

@NoArgsConstructor
@Permission("group.staff")
//@Disabled
public class TestCommand extends CustomCommand implements Listener {
//	List<Player> controlMinecart = new ArrayList<>();

	public TestCommand(CommandEvent event) {
		super(event);
	}

	@Path("start")
	public void point1() {
		CurveTest.start = location();
		send("P1: " + StringUtils.getShorterLocationString(CurveTest.start));
		location().getBlock().setType(Material.RED_STAINED_GLASS);
	}

	@Path("control1")
	public void point3() {
		CurveTest.startControl = location();
		send("P3: " + StringUtils.getShorterLocationString(CurveTest.startControl));
		location().getBlock().setType(Material.PINK_STAINED_GLASS);
	}

	@Path("end")
	public void point2() {
		CurveTest.end = location();
		send("P2: " + StringUtils.getShorterLocationString(CurveTest.end));
		location().getBlock().setType(Material.LIGHT_BLUE_STAINED_GLASS);
	}

	@Path("control2")
	public void point4() {
		CurveTest.endControl = location();
		send("P4: " + StringUtils.getShorterLocationString(CurveTest.endControl));
		location().getBlock().setType(Material.BLUE_STAINED_GLASS);
	}

	@Path("showPoints")
	void showPoints() {
		CurveTest.start.getBlock().setType(Material.RED_STAINED_GLASS);
		CurveTest.startControl.getBlock().setType(Material.PINK_STAINED_GLASS);
		CurveTest.end.getBlock().setType(Material.LIGHT_BLUE_STAINED_GLASS);
		CurveTest.endControl.getBlock().setType(Material.BLUE_STAINED_GLASS);
	}

	@Path("display <segments>")
	public void display(int segments) {
		CurveTest.startControl.getBlock().setType(Material.AIR);
		CurveTest.endControl.getBlock().setType(Material.AIR);
		CurveTest.start.getBlock().setType(Material.AIR);
		CurveTest.end.getBlock().setType(Material.AIR);

		DotEffect.builder().player(player()).location(CurveTest.start).speed(0.1).ticks(Time.SECOND.x(10)).color(ColorType.LIGHT_RED.getColor()).start();
		DotEffect.builder().player(player()).location(CurveTest.startControl).speed(0.1).ticks(Time.SECOND.x(10)).color(ColorType.PINK.getColor()).start();
		DotEffect.builder().player(player()).location(CurveTest.end).speed(0.1).ticks(Time.SECOND.x(10)).color(ColorType.LIGHT_BLUE.getColor()).start();
		DotEffect.builder().player(player()).location(CurveTest.endControl).speed(0.1).ticks(Time.SECOND.x(10)).color(ColorType.BLUE.getColor()).start();

		List<Location> curve;
		if (CurveTest.endControl != null)
			curve = CurveTest.bezierCurve(segments, CurveTest.start, CurveTest.startControl, CurveTest.end, CurveTest.endControl);
		else
			curve = CurveTest.bezierCurve(segments, CurveTest.start, CurveTest.startControl, CurveTest.end);

		for (Location point : curve) {
			DotEffect.builder()
					.player(player())
					.location(point)
					.speed(0.1)
					.ticks(Time.SECOND.x(10))
					.color(ColorType.PURPLE.getColor())
					.start();
		}
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
