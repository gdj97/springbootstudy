package kr.gdu.shop3.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import kr.gdu.shop3.dto.SaleDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="sale")
@Data
@NoArgsConstructor
public class Sale {
	@Id
	private int saleid;
	private String userid;
	@Temporal(TemporalType.TIMESTAMP)
	private Date saledate;
	@OneToOne
	private User user;
	@OneToMany
	private List<SaleItem> itemList = new ArrayList<>();
	public Sale(SaleDto sale) {
		this.saleid = sale.getSaleid();
		this.userid = sale.getUserid();
		this.saledate = sale.getSaledate();
		this.user = new User(sale.getUser());
	}
	@PrePersist
	public void onPrePersist() {
		this.saledate = new Date();
	}
}
