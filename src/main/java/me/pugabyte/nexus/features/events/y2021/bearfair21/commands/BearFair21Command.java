package me.pugabyte.nexus.features.events.y2021.bearfair21.commands;

import me.pugabyte.nexus.features.events.y2021.bearfair21.Fairgrounds;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Interactables;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Seeker;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.ClientsideContentManager;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Confirm;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.bearfair21.BearFair21User;
import me.pugabyte.nexus.models.bearfair21.BearFair21UserService;
import me.pugabyte.nexus.models.bearfair21.ClientsideContent;
import me.pugabyte.nexus.models.bearfair21.ClientsideContent.Content;
import me.pugabyte.nexus.models.bearfair21.ClientsideContentService;
import me.pugabyte.nexus.utils.BlockUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;

import java.util.List;

@Permission("group.staff")
@Aliases("bf21")
public class BearFair21Command extends CustomCommand {
	ClientsideContentService contentService = new ClientsideContentService();
	ClientsideContent clientsideContent = contentService.get();
	List<Content> contentList = clientsideContent.getContentList();

	public BearFair21Command(CommandEvent event) {
		super(event);
	}

	@Path
	void warp() {
		runCommand("bearfair21warp");
	}

	@Path("strengthTest")
	@Permission("group.admin")
	void strengthTest() {
		commandBlock();
		Interactables.strengthTest();
	}

	@Path("seeker")
	void seeker() {
		send("Find the crimson button");
		Seeker.addPlayer(player());
	}

	@Path("rides enable")
	void ridesEnable() {
		for (String ride : Fairgrounds.rides)
			PlayerUtils.runCommandAsConsole("rideadm bf21_" + ride + " enable");
	}

	@Path("rides disable")
	void ridesDisable() {
		for (String ride : Fairgrounds.rides)
			PlayerUtils.runCommandAsConsole("rideadm bf21_" + ride + " disable");
	}

	@Path("clientside <boolean>")
	void clientside(boolean bool) {
		ClientsideContentManager.active = bool;
		send("Set ClientsideContentManager.active to " + bool);
	}

	@Path("clientside clear")
	@Confirm
	void clientsideClear() {
		contentService.clearCache();
		contentService.deleteAll();
		contentService.clearCache();
		send("deleted all");

		BearFair21UserService service = new BearFair21UserService();
		for (BearFair21User user : service.getAll()) {
			user.getClientsideLocations().clear();
			service.save(user);
		}

	}

	@Path("clientside addAll")
	void clientsideAddAll() {
		BearFair21UserService service = new BearFair21UserService();
		BearFair21User user = service.get(uuid());
		for (Content content : contentList) {
			user.getClientsideLocations().add(content.getLocation());
		}
		service.save(user);
		send("user can now see " + user.getClientsideLocations().size() + " locations");
	}

	@Path("clientside list")
	void clientsideList() {
		for (Content content : contentList) {
			send(StringUtils.getShortLocationString(content.getLocation()));
		}
	}

	@Path("clientside add")
	void clientsideSelect() {
		Entity entity = getTargetEntity();
		if (entity == null) {
			Block block = getTargetBlock();
			if (BlockUtils.isNullOrAir(block))
				error("Entity is null && Block is null or air");

			setupBlockContent(block);
			send("Added block: " + block.getType());
		} else if (entity instanceof ItemFrame) {
			setupItemFrameContent((ItemFrame) entity);
			send("Added item frame");
		} else {
			error("That's not a supported entity type: " + entity.getType().name());
		}
	}

	private void setupBlockContent(Block block) {
		ClientsideContent.Content content = new ClientsideContent.Content();
		content.setMaterial(block.getType());
		content.setLocation(block.getLocation().toBlockLocation());
		addContent(content);
	}

	private void setupItemFrameContent(ItemFrame itemFrame) {
		ClientsideContent.Content content = new ClientsideContent.Content();
		content.setMaterial(Material.ITEM_FRAME);
		content.setLocation(itemFrame.getLocation().toBlockLocation());
		content.setItemStack(itemFrame.getItem());
		content.setBlockFace(itemFrame.getFacing());
		content.setRotation(itemFrame.getRotation());
		addContent(content);

	}

	private void addContent(ClientsideContent.Content content) {
		for (Content _content : contentList) {
			if (_content.getLocation().equals(content.getLocation()))
				error("Duplicate content location");
		}

		contentList.add(content);
		clientsideContent.setContentList(contentList);
		contentService.save(clientsideContent);
	}
}
