package kr.gdu.shop3.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import kr.gdu.shop3.dto.ItemSetDto;
import kr.gdu.shop3.dto.SaleItemDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="saleitem")
@IdClass(SaleItemId.class)
@Data
@NoArgsConstructor
public class SaleItem {
	@Id
	private int saleid;
	@Id
	private int seq;
	private int itemid;
	private int quantity;
	@OneToOne
	private Item item;
	public SaleItem(int saleid, int seq, ItemSetDto itemset) {
		this.saleid = saleid;
		this.seq = seq;
		this.item = new Item(itemset.getItem());
		this.itemid = itemset.getItem().getId();
		this.quantity = itemset.getQuantity();
	}
	public SaleItem(SaleItemDto saleItem) {
		this.saleid = saleItem.getSaleid();
		this.seq = saleItem.getSeq();
		this.itemid = saleItem.getItemid();
		this.quantity = saleItem.getQuantity();
	}
}
