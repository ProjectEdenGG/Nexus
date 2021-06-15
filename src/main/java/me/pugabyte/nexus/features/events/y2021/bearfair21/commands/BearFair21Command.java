package me.pugabyte.nexus.features.events.y2021.bearfair21.commands;

import eden.utils.TimeUtils.Time;
import eden.utils.Utils;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Interactables;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.Seeker;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.clientside.ClientsideContentManager;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.Collector;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Confirm;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.bearfair21.BearFair21Config;
import me.pugabyte.nexus.models.bearfair21.BearFair21ConfigService;
import me.pugabyte.nexus.models.bearfair21.BearFair21User;
import me.pugabyte.nexus.models.bearfair21.BearFair21UserService;
import me.pugabyte.nexus.models.bearfair21.ClientsideContent;
import me.pugabyte.nexus.models.bearfair21.ClientsideContent.Content;
import me.pugabyte.nexus.models.bearfair21.ClientsideContent.Content.ContentCategory;
import me.pugabyte.nexus.models.bearfair21.ClientsideContentService;
import me.pugabyte.nexus.utils.BlockUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static me.pugabyte.nexus.utils.StringUtils.bool;

@Aliases("bf21")
public class BearFair21Command extends CustomCommand {
	ClientsideContentService contentService = new ClientsideContentService();
	ClientsideContent clientsideContent = contentService.get0();

	BearFair21UserService userService = new BearFair21UserService();

	BearFair21ConfigService configService = new BearFair21ConfigService();
	BearFair21Config config = configService.get0();

	List<Content> contentList = clientsideContent.getContentList();

	public BearFair21Command(CommandEvent event) {
		super(event);
	}

	@Permission("group.staff")
	@Path
	void warp() {
		runCommand("bearfair21warp");
	}

	@Permission("group.admin")
	@Path("strengthTest")
	void strengthTest() {
		commandBlock();
		Interactables.strengthTest();
	}

	@Permission("group.admin")
	@Path("seeker")
	void seeker() {
		send("Find the crimson button");
		Seeker.addPlayer(player());
	}

	@Path("toCollector")
	@Permission("group.admin")
	public void toCollector() {
		player().teleportAsync(Collector.getCurrentLoc());
	}

	// Database

	@Confirm
	@Path("database delete [player]")
	@Permission("group.admin")
	public void databaseDelete(@Arg("self") Player player) {
		BearFair21User user = userService.get(player);
		user.cancelActiveTask();
		userService.delete(user);
		send("deleted bearfair21 user: " + player.getName());
	}

	@Path("database debug [player]")
	@Permission("group.admin")
	public void databaseDebug(@Arg("self") Player player) {
		BearFair21User user = userService.get(player);

		send("BearFair21 User: " + user.getName());
		send("Visible Categories: " + Arrays.toString(user.getContentCategories().toArray()));
		send("Junk Weight: " + user.getJunkWeight());
		send("Recycled Items: " + user.getRecycledItems());
		send("Met NPCs: " + Arrays.toString(user.getMetNPCs().stream().map(id -> BearFair21NPC.of(id).getName()).toArray()));
		send("Next Step NPCs: " + Arrays.toString(user.getNextStepNPCs().stream().map(id -> BearFair21NPC.of(id).getName()).toArray()));
		send("Active Task Id: " + user.getActiveTaskId());
		send("Quests:");
		send("  Main: " + user.getQuestStage_Main());
		send("    Recycle: " + user.getQuestStage_Recycle());
		send("    Bee: " + user.getQuestStage_BeeKeeper());
		send("    LumberJack: " + user.getQuestStage_Lumberjack());
		line();
		send("  MGN: " + user.getQuestStage_MGN());
		line();
		send("  Pugmas: " + user.getQuestStage_Pugmas());
		send("    Present Index: " + user.getPresentNdx());
		line();
		send("  Halloween: " + user.getQuestStage_Halloween());
		line();
		send("  SDU: " + user.getQuestStage_SDU());
		line();
	}

	// Config

	@Permission("group.admin")
	@Path("config enableWarp <boolean>")
	void configQuests(boolean bool) {
		config.setEnableWarp(bool);
		configService.save(config);
		send("Set enableWarp to: " + bool(config.isEnableWarp()));
	}

	@Permission("group.admin")
	@Path("config enableRides <boolean>")
	void configRides(boolean bool) {
		config.setEnableRides(bool);
		configService.save(config);
		send("Set enableRides to: " + bool(config.isEnableRides()));
	}

	@Permission("group.admin")
	@Path("config enableQuests <boolean>")
	void configWarp(boolean bool) {
		config.setEnableQuests(bool);
		configService.save(config);
		send("Set enableQuests to: " + bool(config.isEnableQuests()));
	}

	@Permission("group.admin")
	@Path("config giveDailyPoints <boolean>")
	void configDailyPoints(boolean bool) {
		config.setGiveDailyPoints(bool);
		configService.save(config);
		send("Set giveDailyPoints to: " + bool(config.isGiveDailyPoints()));
	}

	// Command Blocks

	@Path("moveCollector")
	@Permission("group.admin")
	public void moveCollector() {
		commandBlock();
		Collector.move();
	}

	@Path("yachtHorn")
	@Permission("group.admin")
	public void yachtHorn() {
		commandBlock();
		BlockCommandSender sender = (BlockCommandSender) event.getSender();
		Location loc = sender.getBlock().getLocation();
		World world = loc.getWorld();
		if (world == null)
			return;

		world.playSound(loc, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 4F, 0.1F);
		Tasks.wait(Time.SECOND.x(2), () -> world.playSound(loc, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 4F, 0.1F));
	}

	@Path("metNPCs")
	@Permission("group.admin")
	public void metNPCs() {
		Set<Integer> npcs = userService.get(player()).getMetNPCs();
		if (Utils.isNullOrEmpty(npcs))
			error("has not met any npcs");

		send("Has met: ");
		for (Integer metNPC : npcs) {
			send(" - " + metNPC);
		}
	}

	@Path("metNPCs clear")
	@Permission("group.admin")
	public void metNPCsClear() {
		BearFair21User user = userService.get(player());
		user.getMetNPCs().clear();
		userService.save(user);
		send("cleared met NPCs");
	}

	@Confirm
	@Permission("group.admin")
	@Path("clientside clearUser [category]")
	void clientsideClear(ContentCategory category) {
		BearFair21User user = userService.get(uuid());
		if (category == null) {
			user.getContentCategories().clear();
			userService.save(user);

			send("removed all locations from " + user.getNickname());
			return;
		} else {
			Set<ContentCategory> categories = user.getContentCategories();
			categories.remove(category);
			user.setContentCategories(categories);
		}

		userService.save(user);
		send("removed " + category + " Content Category");
	}

	@Permission("group.admin")
	@Path("clientside add <category> [player]")
	void clientsideAddAll(ContentCategory category, @Arg("self") Player player) {

		BearFair21User user = userService.get(player);
		Set<ContentCategory> categories = user.getContentCategories();

		categories.add(category);
		user.setContentCategories(categories);
		userService.save(user);

		send(player.getName() + " visible categories: " + Arrays.toString(user.getContentCategories().toArray()));

		ClientsideContentManager.sendSpawnContent(player, contentService.getList(category));
	}

	@Permission("group.admin")
	@Path("clientside new <category>")
	void clientsideSelect(ContentCategory category) {
		Entity entity = getTargetEntity();
		if (entity == null) {
			Block block = getTargetBlock();
			if (BlockUtils.isNullOrAir(block))
				error("Entity is null && Block is null or air");

			setupBlockContent(block, category);
			send("Added block: " + block.getType());
		} else if (entity instanceof ItemFrame) {
			setupItemFrameContent((ItemFrame) entity, category);
			send("Added item frame");
		} else {
			error("That's not a supported entity type: " + entity.getType().name());
		}
	}

	@Permission("group.admin")
	@Path("clientside list")
	void clientsideList() {
		List<Content> food = new ArrayList<>();
		List<Content> balloons = new ArrayList<>();
		List<Content> festoon = new ArrayList<>();
		List<Content> banners = new ArrayList<>();
		List<Content> presents = new ArrayList<>();
		List<Content> sawmill = new ArrayList<>();
		List<Content> cable = new ArrayList<>();
		List<Content> unlisted = new ArrayList<>();

		for (Content content : contentList) {
			switch (content.getCategory()) {
				case FOOD -> food.add(content);
				case BALLOON -> balloons.add(content);
				case FESTOON -> festoon.add(content);
				case BANNER -> banners.add(content);
				case PRESENT -> presents.add(content);
				case SAWMILL -> sawmill.add(content);
				case CABLE -> cable.add(content);
				default -> unlisted.add(content);
			}
		}

		send("Food: " + food.size());
		send("Balloons: " + balloons.size());
		send("Festoon: " + festoon.size());
		send("Banner: " + banners.size());
		send("Present: " + presents.size());
		send("SawMill: " + sawmill.size());
		send("Cable: " + cable.size());
		send("Unlisted: " + unlisted.size());

		List<Content> contentList = new ArrayList<>();
		contentList.addAll(food);
		contentList.addAll(balloons);
		contentList.addAll(festoon);
		contentList.addAll(banners);
		contentList.addAll(presents);
		contentList.addAll(sawmill);
		contentList.addAll(cable);
		contentList.addAll(unlisted);

		StringBuilder string = new StringBuilder();
		for (Content content : contentList) {
			string.append("\n")
					.append(StringUtils.camelCase(content.getCategory().name()))
					.append(": ")
					.append(StringUtils.camelCase(content.getMaterial().name()))
					.append(" - ")
					.append(StringUtils.getShortLocationString(content.getLocation()));
		}

		String url = StringUtils.paste(string.toString());
		send(json("&e&l[Click to Open]").url(url).hover(url));
	}

//	@Permission("group.admin")
//	@Path("clientside remove")
//	void clientsideRemove() {
//		int count = 0;
//		for (Content content : contentList) {
//			if (content.getLocation().equals(location().toBlockLocation())) {
//				contentList.remove(content);
//				count++;
//			}
//		}
//
//		if (count == 0)
//			error("There is no content at " + StringUtils.getShortLocationString(location().toBlockLocation()));
//
//		clientsideContent.setContentList(contentList);
//		contentService.save(clientsideContent);
//	}

	private void setupBlockContent(Block block, ContentCategory category) {
		ClientsideContent.Content content = new ClientsideContent.Content();
		content.setLocation(block.getLocation().toBlockLocation());
		content.setCategory(category);
		//
		content.setMaterial(block.getType());
		addContent(content);
	}

	private void setupItemFrameContent(ItemFrame itemFrame, ContentCategory category) {
		ClientsideContent.Content content = new ClientsideContent.Content();
		content.setLocation(itemFrame.getLocation().toBlockLocation());
		content.setCategory(category);
		//
		content.setMaterial(Material.ITEM_FRAME);
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
