package me.pugabyte.bncore.models.votes;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
public class Vote {
	@NonNull
	private String site;
	@NonNull
	private int extra;
	@NonNull
	private LocalDateTime timestamp;

}
