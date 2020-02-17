package me.pugabyte.bncore.models.vote;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
@Builder
public class Vote {
	@NonNull
	private String uuid;
	@NonNull
	private VoteSite site;
	@NonNull
	private int extra;
	@NonNull
	private LocalDateTime timestamp;
	private boolean expired = false;

}
