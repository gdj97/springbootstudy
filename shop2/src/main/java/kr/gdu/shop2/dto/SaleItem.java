package kr.gdu.shop2.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor  //매개변수 없는 생성자
public class SaleItem {
	private int saleid;  //주문번호
	private int seq;     //주문상품번호
	private int itemid;  //상품번호
	private int quantity;//주문수량
	private Item item;   //상품정보 
	
	public SaleItem(int saleid, int seq, ItemSet itemSet) {
		this.saleid = saleid;
		this.seq = seq;
		this.item = itemSet.getItem();
		this.itemid = itemSet.getItem().getId();
		this.quantity = itemSet.getQuantity();  
	}
}
