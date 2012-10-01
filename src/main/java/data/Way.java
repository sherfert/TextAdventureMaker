package data;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Way extends NamedObject {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	Location origin;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	Location destiny;
}