package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Async;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Nerd.StaffMember;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static gg.projecteden.utils.StringUtils.paste;
import static gg.projecteden.utils.TimeUtils.shortDateFormat;
import static gg.projecteden.utils.TimeUtils.shortDateTimeFormat;

public class NerdCommand extends CustomCommand {

	public NerdCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void nou() {
		send("no u");
	}

	@Path("setFirstJoin <player> <date>")
	@Permission("group.admin")
	void setFirstJoin(OfflinePlayer player, LocalDateTime firstJoin) {
		new NerdService().edit(player, nerd -> nerd.setFirstJoin(firstJoin));
		send(PREFIX + "Set " + Nickname.of(player) + "'s first join date to &e" + shortDateTimeFormat(nerd().getFirstJoin()));
	}

	@Path("setPromotionDate <player> <date>")
	@Permission("group.admin")
	void setPromotionDate(OfflinePlayer player, LocalDate promotionDate) {
		new NerdService().edit(player, nerd -> nerd.setPromotionDate(promotionDate));
		send(PREFIX + "Set " + Nickname.of(player) + "'s promotion date to &e" + shortDateFormat(nerd().getPromotionDate()));
	}

	@Async
	@Path("getDataFile [player]")
	@Permission("group.admin")
	void getDataFile(@Arg("self") Nerd nerd) {
		send(json().next(paste(nerd.getDataFile().asNBTString())));
	}

	@ConverterFor(Nerd.class)
	Nerd convertToNerd(String value) {
		return Nerd.of(convertToOfflinePlayer(value));
	}

	@TabCompleterFor(Nerd.class)
	List<String> tabCompleteNerd(String value) {
		return tabCompletePlayer(value);
	}

	@ConverterFor(StaffMember.class)
	StaffMember convertToStaffMember(String value) {
		OfflinePlayer player = convertToOfflinePlayer(value);
		if (!Rank.of(player).isStaff())
			error(Nickname.of(player) + " is not staff");
		return new StaffMember(player.getUniqueId());
	}

	@TabCompleterFor(StaffMember.class)
	List<String> tabCompleteStaffMember(String filter) {
		return new HoursService().getActivePlayers().stream()
			.filter(player -> Rank.of(player).isStaff())
			.map(PlayerUtils::getPlayer)
			.map(Nickname::of)
			.filter(name -> name.toLowerCase().startsWith(filter.toLowerCase()))
			.collect(Collectors.toList());
	}

}
