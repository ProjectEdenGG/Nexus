package gg.projecteden.nexus.models.vote;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
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
