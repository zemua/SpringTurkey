package devs.mrp.springturkey.database.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "turkey_user")
@Table(name = "turkey_user",
indexes = @Index(name = "user_external_id_index", columnList = "external_id", unique = true))
@EntityListeners(AuditingEntityListener.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class TurkeyUser {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id; // Internal auto-generated db id

	@NotBlank
	@Column(name = "external_id")
	private String externalId; // External keycloak id

	@OneToMany(mappedBy = "user")
	private List<Device> devices;

	@CreatedDate
	private LocalDateTime created;

	@LastModifiedDate
	private LocalDateTime edited;

	@Nullable
	private LocalDateTime deleted;

}
