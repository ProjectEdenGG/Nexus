package me.pugabyte.bncore.models.hallofhistory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class HallOfHistory {
	@NonNull
	private String uuid;
	@NonNull
	private String rank;
	@NonNull
	private boolean current;
	private LocalDate promotionDate;
	private LocalDate resignationDate;
}
