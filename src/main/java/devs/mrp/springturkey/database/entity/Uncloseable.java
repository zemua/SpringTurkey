package devs.mrp.springturkey.database.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "uncloseable")
@Table(name = "TURKEY_UNCLOSEABLE",
indexes = @Index(name = "uncloseable_to_user_index", columnList = "user"),
uniqueConstraints = { @UniqueConstraint(name = "uk__device_process", columnNames = { "user", "activity" }) })
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class Uncloseable {

	@Id
	@Column(name = "activity_id")
	private UUID id;

	@OneToOne
	@MapsId
	@JoinColumn(name = "activity_id")
	private Activity activity;

	@Nullable
	private Boolean preventClosing;

	@CreatedDate
	private LocalDateTime created;

	@LastModifiedDate
	private LocalDateTime edited;

	@Nullable
	private LocalDateTime deleted;

}
