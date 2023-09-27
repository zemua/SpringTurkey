package devs.mrp.springturkey.database.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import devs.mrp.springturkey.delta.Delta;
import devs.mrp.springturkey.delta.DeltaTable;
import devs.mrp.springturkey.delta.DeltaType;
import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "turkey_delta")
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

	@NotNull
	private LocalDateTime deltaTimeStamp;
	@NotNull
	private DeltaType deltaType;
	@NotNull
	private DeltaTable deltaTable;
	@NotNull
	private UUID recordId;
	@NotBlank
	private String fieldName;
	@NotBlank
	private String textValue;

	@CreatedDate
	private LocalDateTime created;
	@LastModifiedDate
	private LocalDateTime edited;
	@Nullable
	private LocalDateTime deleted;

	public static DeltaEntity from(Delta delta) {
		return builder()
				.deltaTimeStamp(delta.getTimestamp())
				.deltaType(delta.getDeltaType())
				.deltaTable(delta.getTable())
				.recordId(delta.getRecordId())
				.fieldName(delta.getFieldName())
				.textValue(delta.getJsonValue())
				.build();
	}

	public Delta toDelta() {
		return Delta.builder()
				.timestamp(getDeltaTimeStamp())
				.deltaType(getDeltaType())
				.table(getDeltaTable())
				.recordId(getRecordId())
				.fieldName(getFieldName())
				.jsonValue(getTextValue())
				.build();
	}

}
