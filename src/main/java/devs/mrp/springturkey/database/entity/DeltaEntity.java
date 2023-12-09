package devs.mrp.springturkey.database.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.DeltaType;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;

@Entity(name = "turkey_delta")
@Table(name = "turkey_delta",
indexes = @Index(name = "position_and_user_index", columnList = "id, user_id"))
@EntityListeners(AuditingEntityListener.class)
@Getter
@Builder
@With
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class DeltaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// TODO add index on TurkeyUser, recordId and deltaTimeStamp to verify the most recent delta to a given record

	@ManyToOne
	@NotNull
	private TurkeyUser user;


	@NotNull
	private LocalDateTime deltaTimeStamp;
	@NotNull
	private DeltaType deltaType;
	@NotNull
	private DeltaTable deltaTable;
	@NotNull
	private UUID recordId;
	@NotBlank
	private String jsonValue;

	@CreatedDate
	private LocalDateTime created;
	@LastModifiedDate
	private LocalDateTime edited;
	@Nullable
	private LocalDateTime deleted;

}
