package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.EdenSocialMediaSite;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.hallofhistory.HallOfHistory.RankHistory;
import gg.projecteden.nexus.models.hallofhistory.HallOfHistoryService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.staffhall.StaffHallConfig;
import gg.projecteden.nexus.models.staffhall.StaffHallConfig.StaffHallRankGroup;
import gg.projecteden.nexus.models.staffhall.StaffHallConfigService;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static gg.projecteden.api.common.utils.Nullables.isNotNullOrEmpty;
import static gg.projecteden.api.common.utils.TimeUtils.shortDateFormat;
import static gg.projecteden.api.common.utils.TimeUtils.shortDateTimeFormat;
import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@NoArgsConstructor
public class StaffHallCommand extends CustomCommand implements Listener {
	private final StaffHallConfigService service = new StaffHallConfigService();
	private final StaffHallConfig config = service.get0();

	public StaffHallCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		Tasks.repeat(TickTime.MINUTE, TickTime.HOUR.x(6), StaffHallCommand::writeHtml);
	}

	@Path
	@Description("Teleport to the staff hall")
	void tp() {
		runCommand("warp staffhall");
	}

	@Path("view <player>")
	@Description("View information about a staff member")
	void view(Nerd nerd) {
		line(4);

		send("&e&lNickname: &3" + nerd.getNickname());
		send("&e&lIGN: &3" + nerd.getName());
		send("&e&lRank: &3" + nerd.getRank().getColoredName());
		if (isNotNullOrEmpty(nerd.getPronouns()))
			send("&e&lPronouns: &3" + String.join(",", nerd.getPronouns().stream().map(Enum::toString).toList()));
		if (isNotNullOrEmpty(nerd.getFilteredPreferredNames()))
			send(plural("&e&lPreferred name", nerd.getFilteredPreferredNames().size()) + ": &3" + String.join(", ", nerd.getFilteredPreferredNames()));
		if (nerd.getBirthday() != null)
			send("&e&lBirthday: &3" + shortDateFormat(nerd.getBirthday()) + " (" + nerd.getBirthday().until(LocalDate.now()).getYears() + " years)");
		if (nerd.getFirstJoin() != null)
			send("&e&lJoin date: &3" + shortDateTimeFormat(nerd.getFirstJoin()));
		if (nerd.getPromotionDate() != null)
			send("&e&lPromotion date: &3" + shortDateFormat(nerd.getPromotionDate()));

		line();

		if (!isNullOrEmpty(nerd.getAbout())) {
			send("&e&lAbout me: &3" + nerd.getAbout());
			line();
		}
		if (nerd.isMeetMeVideo()) {
			send(json("&eMeet me! &c" + EdenSocialMediaSite.WEBSITE.getUrl() + "/meet/" + nerd.getName().toLowerCase()).url(EdenSocialMediaSite.WEBSITE.getUrl() + "/meet/" + nerd.getName().toLowerCase()));
			line();
		}
	}

	@Path("write")
	@Permission(Group.ADMIN)
	@Description("Write HTML files to disk for the website")
	void write() {
		writeHtml();
	}

	@EventHandler
	public void onNpcRightClick(NPCRightClickEvent event) {
		NPC npc = event.getNPC();
		Location location = event.getClicker().getLocation();
		WorldGuardUtils worldguard = new WorldGuardUtils(location);

		if (worldguard.getRegionsLikeAt("staffhall", location).size() > 0) {
			if (!npc.getName().contains(" "))
				runCommand(event.getClicker(), "staffhall view " + stripColor(npc.getName()));
		} else if (worldguard.getRegionsLikeAt("hallofhistory", location).size() > 0)
			runCommand(event.getClicker(), "hoh view " + stripColor(npc.getName()));
		else if (npc.getId() == 2678 || npc.getId() == 4657)
			runCommand(event.getClicker(), "griffinwelc");
		else if (npc.getId() == 2697)
			runCommand(event.getClicker(), "filidwelc");
		else if (npc.getId() == 2990)
			runCommand(event.getClicker(), "crates info");
	}

	@Path("config add <group> <npcId> [index]")
	@Permission(Group.ADMIN)
	@Description("Add an NPC ID to a staff hall room config")
	void config_add(StaffHallRankGroup group, int npcId, int index) {
		if (index > 0)
			config.add(group, npcId, index);
		else
			config.add(group, npcId);
		service.save(config);
	}

	@Path("config remove <group> <npcId>")
	@Permission(Group.ADMIN)
	@Description("Remove an NPC ID from a staff hall room config")
	void config_remove(StaffHallRankGroup group, int npcId) {
		config.remove(group, npcId);
		service.save(config);
	}

	@Path("config tp <group> <index>")
	@Permission(Group.ADMIN)
	@Description("Teleport to a staff hall NPC")
	void config_tp(StaffHallRankGroup group, @Switch int npcId, @Switch int index) {
		if (npcId > 0)
			player().teleportAsync(CitizensUtils.locationOf(npcId), TeleportCause.COMMAND);
		else
			player().teleportAsync(CitizensUtils.locationOf(config.getNpcId(group, index)), TeleportCause.COMMAND);
	}

	@Path("config list <group>")
	@Permission(Group.ADMIN)
	@Description("List all staff hall NPCs")
	void config_list(StaffHallRankGroup group) {
		config.getNpcIds().get(group).forEach(this::send);
	}

	@Path("config spawn")
	@Permission(Group.ADMIN)
	@Description("Spawn all staff hall NPCs")
	void config_spawn() {
		config.getNpcIds().values().forEach(npcIds -> npcIds.forEach(npcId -> {
			final NPC npc = CitizensUtils.getNPC(npcId);
			npc.spawn(npc.getStoredLocation());
		}));
	}

	@Async
	@SneakyThrows
	@Path("update")
	@Permission(Group.ADMIN)
	@Description("Automatically update all staff hall NPCs")
	void update() {
		Map<StaffHallRankGroup, List<Nerd>> groups = new HashMap<>();

		Rank.getStaffNerds().get().forEach((rank, nerds) -> {
			if (rank != Rank.OWNER)
				groups.computeIfAbsent(StaffHallRankGroup.of(rank), $ -> new ArrayList<>()).addAll(nerds);
		});

		AtomicInteger wait = new AtomicInteger();

		groups.forEach((group, nerds) -> {
			nerds.sort(new SeniorityComparator(group));
			final List<Integer> npcIds = config.getNpcIds(group);
			for (int index = 0; index < npcIds.size(); index++) {
				final int i = index;
				Tasks.wait(wait.getAndAdd(20), () -> {
					Integer npcId = npcIds.get(i);
					if (nerds.size() >= (i + 1))
						updateNpc(npcId, nerds.get(i));
					else
						despawnNpc(npcId);
				});
			}
		});
	}

	private void updateNpc(int npcId, Nerd nerd) {
		CitizensUtils.respawnNPC(npcId);
		CitizensUtils.updateNameAndSkin(CitizensUtils.getNPC(npcId), nerd);
		updateBlockBelow(npcId, ColorType.of(nerd.getRank().getSimilarChatColor()));
	}

	private void despawnNpc(int npcId) {
		CitizensUtils.despawnNPC(npcId);
		updateBlockBelow(npcId, ColorType.GRAY);
	}

	private void updateBlockBelow(int npcId, ColorType color) {
		@NotNull Block block = CitizensUtils.getNPC(npcId).getStoredLocation().getLocation().getBlock().getRelative(BlockFace.DOWN);
		if (!MaterialTag.SHULKER_BOXES.isTagged(block)) {
			Nexus.warn("Block below Staff Hall NPC " + npcId + " is not a shulker box but is " + block.getType());
			return;
		}

		block.setType(color.getShulkerBox());
		if (!(block.getBlockData() instanceof Directional directional)) {
			Nexus.warn("Block below Staff Hall NPC " + npcId + " is not directional");
			return;
		}

		directional.setFacing(BlockFace.UP);
		block.setBlockData(directional);
	}

	@AllArgsConstructor
	@RequiredArgsConstructor
	public static class SeniorityComparator implements Comparator<Nerd> {
		private StaffHallRankGroup group;

		@Override
		public int compare(Nerd nerd1, Nerd nerd2) {
			final HallOfHistoryService service = new HallOfHistoryService();

			final RankHistory currentRank1 = service.get(nerd1).getRankHistory().stream()
				.filter(RankHistory::isCurrent)
				.filter(history -> group == null || StaffHallRankGroup.of(history.getRank()) == group)
				.findFirst()
				.orElse(null);

			final RankHistory currentRank2 = service.get(nerd2).getRankHistory().stream()
				.filter(RankHistory::isCurrent)
				.filter(history -> group == null || StaffHallRankGroup.of(history.getRank()) == group)
				.findFirst()
				.orElse(null);

			if (currentRank1 == null || currentRank2 == null)
				return 0;

			int result;

			result = currentRank2.getRank().compareTo(currentRank1.getRank());
			if (result != 0)
				return result;

			result = currentRank1.getPromotionDate().compareTo(currentRank2.getPromotionDate());
			if (result != 0)
				return result;

			return nerd1.getNickname().compareTo(nerd2.getNickname());
		}

	}

	private static void writeHtml() {
		File folder = Paths.get("plugins/website/meetthestaff/").toFile();
		if (!folder.exists())
			folder.mkdir();

		Rank.getStaffNerds().thenAccept(ranks -> {
			for (Rank rank : ranks.keySet()) {
				for (Nerd staff : ranks.get(rank))
					try {
						String html = "";
						if (isNotNullOrEmpty(staff.getFilteredPreferredNames()))
							html += "<span style=\"font-weight: bold;\">" + StringUtils.plural("Preferred name", staff.getFilteredPreferredNames().size()) + ":</span> " + String.join(", ", staff.getFilteredPreferredNames()) + "<br/>";
						if (staff.getBirthday() != null)
							html += "<span style=\"font-weight: bold;\">Birthday:</span> " + shortDateFormat(staff.getBirthday())
								+ " (" + staff.getBirthday().until(LocalDate.now()).getYears() + " years)<br/>";
						if (staff.getPromotionDate() != null)
							html += "<span style=\"font-weight: bold;\">Promotion date:</span> " + shortDateFormat(staff.getPromotionDate()) + "<br/>";
						html += "<br/>";
						if (!Nullables.isNullOrEmpty(staff.getAbout()))
							html += "<span style=\"font-weight: bold;\">About me:</span> " + staff.getAbout();

						File file = Paths.get("plugins/website/meetthestaff/" + staff.getUuid() + ".html").toFile();
						if (!file.exists())
							file.createNewFile();
						Files.write(file.toPath(), html.getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
			}
		});
	}

}
