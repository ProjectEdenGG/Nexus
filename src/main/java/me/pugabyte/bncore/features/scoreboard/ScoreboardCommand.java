package me.pugabyte.bncore.features.scoreboard;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.Hand;
import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.scoreboard.ScoreboardService;
import me.pugabyte.bncore.models.scoreboard.ScoreboardUser;
import me.pugabyte.bncore.utils.JsonBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

@Permission("group.staff")
public class ScoreboardCommand extends CustomCommand implements Listener {
	private final ScoreboardService service = new ScoreboardService();
	private final ScoreboardUser user;

	public ScoreboardCommand(@NonNull CommandEvent event) {
		super(event);
		user = service.get(player());
	}

	@Path("create <line...>")
	void create(@Arg(type = ScoreboardLine.class) List<ScoreboardLine> lines) {
		user.setLines(lines);
		user.start();
	}

	@Path("render")
	void render() {
		user.render();
	}

	@Path("delete")
	void delete() {
		service.delete(user);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		ScoreboardService service = new ScoreboardService();
		ScoreboardUser user = service.get(event.getPlayer());
		if (user.isActive())
			user.start();
	}

	@Path("book")
	void book() {
		ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta meta = (BookMeta) book.getItemMeta();
		meta.spigot().addPage(new JsonBuilder("hi").command("/echo hi").build());
		book.setItemMeta(meta);

		ItemStack original = player().getInventory().getItemInMainHand();

		try {
			player().getInventory().setItemInMainHand(book);
			PacketContainer packet = BNCore.getProtocolManager().createPacket(PacketType.Play.Server.OPEN_BOOK);
			packet.getModifier().writeDefaults();
			packet.getHands().write(0, Hand.MAIN_HAND);
			BNCore.getProtocolManager().sendServerPacket(player(), packet);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			player().getInventory().setItemInMainHand(original);
		}


	}

}
