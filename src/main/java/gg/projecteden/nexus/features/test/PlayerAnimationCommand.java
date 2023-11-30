//package gg.projecteden.nexus.features.test;
//
//import com.ticxo.playeranimator.api.PlayerAnimator;
//import gg.projecteden.nexus.framework.commands.models.CustomCommand;
//import gg.projecteden.nexus.framework.commands.models.annotations.Path;
//import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
//import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
//import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
//import lombok.NonNull;
//
//@Permission(Group.ADMIN)
//public class PlayerAnimationCommand extends CustomCommand {
//
//	public PlayerAnimationCommand(@NonNull CommandEvent event) {
//		super(event);
//	}
//
//	@Path("start <id>")
//	void start(String id){
//		PlayerAnimation.start(player(), id);
//	}
//
//	@Path("stop")
//	void stop(){
//		PlayerAnimation.stop(player());
//	}
//
//	@Path("list")
//	void list(){
//		for (String s : PlayerAnimator.api.getAnimationManager().getRegistry().keySet()) {
//			send(" - " + s);
//		}
//	}
//}
