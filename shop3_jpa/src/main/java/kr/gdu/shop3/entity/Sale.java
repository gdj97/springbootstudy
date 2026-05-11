package kr.gdu.shop3.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
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
	/* 엔티티간의 관계 표현할 수 있는 어노테이션
	 * OneToOne : Sale 레코드에 하나의 User만 가능. User 레코드는 유일함.
	 * ManyToOne : 여러개 Sale 레코드에  한개에 User 레코드가 가능. 외래키 설정
	 * OneToMany : 한개의 Sale 레코드에 여러개의 User 레코드가 가능. User는 List로 설정
	 * ManyToMany : 여러개의 Sale 레코드에 여러개의 User 레코드가 가능. User는 List로 설정
	 */
	@ManyToOne
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
