package kr.gdu.shop3.dto;

import kr.gdu.shop3.entity.SaleItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor  //매개변수 없는 생성자
public class SaleItemDto {
	private int saleid;  //주문번호
	private int seq;     //주문상품번호
	private int itemid;  //상품번호
	private int quantity;//주문수량
	private ItemDto item;   //상품정보 
	
	public SaleItemDto(int saleid, int seq, ItemSetDto itemSet) {
		this.saleid = saleid;
		this.seq = seq;
		this.item = itemSet.getItem();
		this.itemid = itemSet.getItem().getId();
		this.quantity = itemSet.getQuantity();  
	}

	public SaleItemDto(SaleItem si) {
		// TODO Auto-generated constructor stub
	}
}
