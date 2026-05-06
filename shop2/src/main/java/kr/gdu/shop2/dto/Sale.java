package kr.gdu.shop2.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Sale {	        //db의 sale 테이블의 내용 + 사용자정보 + 주문상품정보
	private int saleid;     //주문번호
	private String userid;  //주문 고객의 아이디
	private Date saledate;  //주문 일자
	private User user;      //고객정보
	private List<SaleItem> itemList = new ArrayList<>(); //주문상품 목록

	public int getTotal() {
		/*
		 * itemList.stream() : SaleItem 객체를 Stream으로 리턴
		 * mapToInt(int) : Stream객체를 IntStream 객체로 변형 
		 *    s :  SaleItem 객체
		 *    s.getItem().getPrice() : 가격
		 *    s.getQuantity()        : 수량
		 *    가격 * 수량 = 합계금액
		 *    => SaleItem 객체의 상품가격*수량 데이터를 IntStream으로 리턴
		 *    
		 * sum() : IntStream의 모든 요소들의 합 리턴   
		 */
		return itemList.stream()
			 .mapToInt(s->s.getItem().getPrice() * s.getQuantity()).sum(); 
	}
}
