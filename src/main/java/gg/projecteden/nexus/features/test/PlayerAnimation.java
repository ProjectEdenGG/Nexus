//package gg.projecteden.nexus.features.test;
//
//import com.ticxo.playeranimator.PlayerAnimatorImpl;
//import com.ticxo.playeranimator.api.PlayerAnimator;
//import com.ticxo.playeranimator.api.model.player.PlayerModel;
//import gg.projecteden.nexus.Nexus;
//import gg.projecteden.nexus.framework.features.Feature;
//import gg.projecteden.nexus.utils.IOUtils;
//import lombok.SneakyThrows;
//import org.bukkit.entity.Player;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.player.PlayerJoinEvent;
//import org.bukkit.event.player.PlayerQuitEvent;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//public class PlayerAnimation extends Feature {
//	static Map<UUID, PlayerModel> playerModelMap = new HashMap<>();
//
//	@SneakyThrows
//	@Override
//	public void onStart() {
//		PlayerAnimatorImpl.initialize(Nexus.getInstance());
//
//		PlayerAnimator.api.getAnimationManager().clearRegistry();
//
//		final File animation = IOUtils.getPluginFile("animator/packs/starter/gestures/animations/steve.bbmodel");
//		if(!animation.exists()) {
//			return;
//		}
//
//		// key.fileName.animation
//
//		PlayerAnimator.api.getAnimationManager().importAnimations("pack", animation);
//	}
//
//	public static void start(Player player, String animation){
//		PlayerModel playerModel = new PlayerModel(player);
//
//		playerModelMap.put(player.getUniqueId(), playerModel);
//
//		playerModel.playAnimation(animation);
//	}
//
//	public static void stop(Player player){
//		playerModelMap.remove(player.getUniqueId()).despawn();
//	}
//
//	@EventHandler
//	public void on(PlayerJoinEvent event){
//		PlayerAnimator.api.injectPlayer(event.getPlayer());
//	}
//
//	@EventHandler
//	public void on(PlayerQuitEvent event){
//		PlayerAnimator.api.removePlayer(event.getPlayer());
//	}
//
//
//}
