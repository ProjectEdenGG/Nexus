package gg.projecteden.nexus.models.extraplots;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.commands.staff.admin.PermHelperCommand;
import gg.projecteden.nexus.features.commands.staff.admin.PermHelperCommand.NumericPermission;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Data
@Entity(value = "extra_plot_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class ExtraPlotUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private int extraPlots;

	public void update() {
		if (extraPlots > 0)
			PermHelperCommand.set(NumericPermission.PLOTS, uuid, getBasePlots() + extraPlots);
	}

	public int getBasePlots() {
		final Rank rank = Rank.of(uuid);
		for (PlotRanks value : PlotRanks.REVERSED)
			if (rank.gte(value.getRank()))
				return value.getPlots();
		return 0;
	}

	public void addExtraPlot() {
		++extraPlots;
	}

	public void setTotalPlots(int totalPlots) {
		extraPlots = totalPlots - getBasePlots();
	}

	@Getter
	@AllArgsConstructor
	public enum PlotRanks {
		GUEST(1),
		TRUSTED(2),
		;

		private static final List<PlotRanks> REVERSED = Utils.reverse(Arrays.asList(values()));

		private final int plots;

		public Rank getRank() {
			return Rank.valueOf(name());
		}

	}

}
