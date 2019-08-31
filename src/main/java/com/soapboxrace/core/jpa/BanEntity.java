package com.soapboxrace.core.jpa;

import com.soapboxrace.core.jpa.convert.LocalDateTimeConverter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "BAN")
@NamedQueries({ //
		@NamedQuery(name = "BanEntity.findAll", query = "SELECT obj FROM BanEntity obj") })
public class BanEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;

	@OneToOne(targetEntity = UserEntity.class)
	@JoinColumn(name = "user_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_BAN_USER"))
	private UserEntity userEntity;

	@ManyToOne(targetEntity = PersonaEntity.class)
	@JoinColumn(name = "banned_by_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_BANNED_BY"))
	private PersonaEntity bannedBy;

	@Column
	private String reason;

	@Column
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime started;

	@Column
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime endsAt;

	@Column
	private String hwid;

	@Column
	private String ip;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserEntity getUserEntity() {
		return userEntity;
	}

	public void setUserEntity(UserEntity userEntity) {
		this.userEntity = userEntity;
	}

	public PersonaEntity getBannedBy()
	{
		return bannedBy;
	}

	public void setBannedBy(PersonaEntity bannedBy)
	{
		this.bannedBy = bannedBy;
	}

	public LocalDateTime getEndsAt() {
		return endsAt;
	}

	public void setEndsAt(LocalDateTime endsAt) {
		this.endsAt = endsAt;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public LocalDateTime getStarted()
	{
		return started;
	}

	public void setStarted(LocalDateTime started)
	{
		this.started = started;
	}

	public String getHwid() {
		return hwid;
	}

	public void setHwid(String hwid) {
		this.hwid = hwid;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}