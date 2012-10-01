package data;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Item extends NamedObject {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	Location location;
}