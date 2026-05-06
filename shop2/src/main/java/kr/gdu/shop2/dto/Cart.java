package kr.gdu.shop2.dto;

import java.util.ArrayList;
import java.util.List;

public class Cart {
	private List<ItemSet> itemSetList = new ArrayList<>();
	public List<ItemSet> getItemSetList() {
		return itemSetList;
	}
	public void push(ItemSet itemSet) {
		//itemSet : db에서 읽은 Item객체, 수량 저장. 장바구니에 추가될 ItemSet 객체
		//장바구니에 등록된 ItemSet의 Item 객체와 등록예정인 ItemSet의 Item 객체를 비교 
		// 같은 객체(상품)가 존재 : List 객체에 수량만 수정.
		// 같은 객체(상품)가 없으면 : List 객체에 ItemSet 객체를 추가.
		int count = itemSet.getQuantity(); //수량
		//itemSetList : 기존에 추가된 ItemSet 객체 목록
		for(ItemSet old : itemSetList) {
			if(itemSet.getItem().getId() == old.getItem().getId()) { //같은 객체(상품)가 존재하는 경우
				count = old.getQuantity() + itemSet.getQuantity();
				old.setQuantity(count); //기존객체에 수량 증가시킴
				return;
			}
		}
	   //같은 객체(상품)가 없으면 itemSetList객체에 추가
	   itemSetList.add(itemSet);
	}
	/* 전체 장바구니에 속한 상품가격*수량의 합을 
	 * 출력하도록 수정하기
	*/
	public int getTotal() { //total get프로퍼티
		int sum=0;
		for(ItemSet set : itemSetList) {
			sum += set.getItem().getPrice() * set.getQuantity();
		}
		return sum;
	}
}
