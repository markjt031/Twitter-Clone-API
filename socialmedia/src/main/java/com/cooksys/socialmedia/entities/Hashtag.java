
package com.cooksys.socialmedia.entities;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
public class Hashtag {

	@Id
	@GeneratedValue
	private Long id;

	private String label;

	@Basic(optional = false)
	@Column(insertable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date firstUsed;

	@Basic(optional = false)
	@Column(insertable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUsed;
}
