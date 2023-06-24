package devs.mrp.springturkey.database.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "uncloseable")
@Table(name = "turkey_uncloseable")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class Uncloseable {

	@Id
	private UUID id;

	@OneToOne(cascade = CascadeType.ALL)
	@PrimaryKeyJoinColumn
	@MapsId
	private Activity activity;

	@Column(name = "prevent_closing")
	@Nullable
	private Boolean preventClosing;

	@CreatedDate
	private LocalDateTime created;

	@LastModifiedDate
	private LocalDateTime edited;

	@Nullable
	private LocalDateTime deleted;

}
