package gg.projecteden.nexus.features.commands;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.api.common.utils.TimeUtils.Timespan;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nerd.NBTPlayer;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Nerd.StaffMember;
import gg.projecteden.nexus.models.nerd.NerdService;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class NerdCommand extends CustomCommand {
	private final NerdService service = new NerdService();

	public NerdCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	@HideFromHelp
	@HideFromWiki
	void nou() {
		send("no u");
	}

	@Path("about <nerd> [about...]")
	@Description("Set a player's About Me")
	@Permission(Group.ADMIN)
	void about(Nerd nerd, String about) {
		service.edit(nerd, _nerd -> _nerd.setAbout("null".equalsIgnoreCase(about) ? null : about));
		send(PREFIX + "Set " + nerd.getNickname() + "'s about to: &e" + nerd.getAbout());
	}

	@Path("setFirstJoin <player> <date>")
	@Permission(Group.ADMIN)
	@Description("Update a player's first join date")
	void setFirstJoin(Nerd nerd, LocalDateTime firstJoin) {
		service.edit(nerd, _nerd -> _nerd.setFirstJoin(firstJoin));
		send(PREFIX + "Set " + nerd.getNickname() + "'s first join date to &e" + TimeUtils.shortDateTimeFormat(nerd.getFirstJoin()));
	}

	@Path("setPromotionDate <player> <date>")
	@Permission(Group.ADMIN)
	@Description("Update a player's promotion date")
	void setPromotionDate(Nerd nerd, LocalDate promotionDate) {
		service.edit(nerd, _nerd -> _nerd.setPromotionDate(promotionDate));
		send(PREFIX + "Set " + nerd.getNickname() + "'s promotion date to &e" + TimeUtils.shortDateFormat(nerd.getPromotionDate()));
	}

	@Async
	@Path("getDataFile [player]")
	@Permission(Group.ADMIN)
	@Description("Generate a paste of a player's NBT data file")
	void getDataFile(@Arg("self") Nerd nerd) {
		send(json().next(StringUtils.paste(new NBTPlayer(nerd).getNbtFile().asNBTString())));
	}

	@Path("timeSinceDeath [player] [value]")
	@Permission(Group.ADMIN)
	void timeSinceDeath(@Arg("self") Player player, Integer value) {
		var initial = player.getStatistic(Statistic.TIME_SINCE_DEATH);
		if (value != null) {
			player.setStatistic(Statistic.TIME_SINCE_DEATH, value);
			new PlayerStatisticIncrementEvent(player, Statistic.TIME_SINCE_DEATH, initial, value).callEvent();
		}

		send(Timespan.ofSeconds(player.getStatistic(Statistic.TIME_SINCE_DEATH) / 20).format());
	}

	@ConverterFor(Nerd.class)
	Nerd convertToNerd(String value) {
		return Nerd.of(convertToOfflinePlayer(value));
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
