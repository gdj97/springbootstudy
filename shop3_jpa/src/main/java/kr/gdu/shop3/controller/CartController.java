package kr.gdu.shop3.controller;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import kr.gdu.shop3.dto.CartDto;
import kr.gdu.shop3.dto.ItemDto;
import kr.gdu.shop3.dto.ItemSetDto;
import kr.gdu.shop3.dto.SaleDto;
import kr.gdu.shop3.dto.UserDto;
import kr.gdu.shop3.service.ItemService;

@Controller
@RequestMapping("cart")
public class CartController {
	@Autowired
	ItemService service;
	//장바구니에 상품 추가
	@RequestMapping("cartAdd")
	public String add(int id, int quantity, HttpSession session,Model model) {
		ItemDto item = service.getItem(id);
		CartDto cart = (CartDto)session.getAttribute("CART");
		if(cart == null) {
			cart = new CartDto();
			session.setAttribute("CART", cart);
		}
		cart.push(new ItemSetDto(item,quantity));
		model.addAttribute("message", item.getName() + ":" + quantity +"개 장바구니에 추가");
		model.addAttribute("cart", cart);
		return "cart/cart";
	}
	@RequestMapping("cartView")
	public String view(HttpSession session,Model model) {
		CartDto cart = (CartDto)session.getAttribute("CART");
		model.addAttribute("message", "장바구니 상품 조회");
		model.addAttribute("cart", cart);
		return "cart/cart";
	}
	@RequestMapping("cartDelete")
	public String delete(int index, HttpSession session,Model model) {
		CartDto cart = (CartDto)session.getAttribute("CART");
		ItemSetDto deleteItem = cart.getItemSetList().remove(index);
//		boolean deleteItem = cart.getItemSetList().remove(index);
		/*
		 * Object List.remove(int index) : index에 해당하는 객체를 제거하여 제거된 객체를 리턴 
		 * boolean List.remove(Object obj) : obj객체를 List에서 제거하여 제거 여부를 boolean으로 리턴
		 */
		model.addAttribute("message", "장바구니에서 " + deleteItem.getItem().getName() + "상품이 삭제 되었습니다.");
		model.addAttribute("cart", cart);
		return "cart/cart";
	}
	/*
	 * CartAspect.cartCheck()의 대상이 되는 메서드
	 */
	@RequestMapping("checkout")
	public String checkout(HttpSession session) {
		return null;  // cart/checkout.jsp 요청
	}
	@RequestMapping("kakao")
	@ResponseBody
	public Map<String,Object> kakao(HttpSession session) {
		Map<String,Object> map = new HashMap<>();
		
		CartDto cart = (CartDto)session.getAttribute("CART");
		UserDto loginUser = (UserDto)session.getAttribute("loginUser");
		
		map.put("merchant_uid", loginUser.getUserid()+"-"+session.getId());
		map.put("name",cart.getItemSetList().get(0).getItem().getName() 
				+ "외 " + (cart.getItemSetList().size() - 1));
		map.put("amount", cart.getTotal());
		map.put("buyer_email",loginUser.getEmail()); 
		map.put("buyer_name",loginUser.getUsername());
		map.put("buyer_tel",loginUser.getPhoneno());
		map.put("buyer_addr",loginUser.getAddress());
		map.put("buyer_postcode",loginUser.getPostcode());
		return map; //클라이언트는 json 객체로 전달
	}
	
	/*
	 * CartAspect.cartCheck()의 대상이 되는 메서드
	 * 1. 로그인
	 * 2. Cart 상품 존재
	 * 3. 관리자는 거래 불가
	 */
	@RequestMapping("end")
	public ModelAndView checkend(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		CartDto cart = (CartDto)session.getAttribute("CART");           //장바구니 상품
		UserDto loginUser = (UserDto)session.getAttribute("loginUser"); //로그인 정보
		//sale,saleitem 테이블에 저장. Sale 객체 리턴
		SaleDto sale = service.checkend(loginUser,cart);
		session.removeAttribute("CART"); //장바구니 상품 제거
		mav.addObject("sale",sale); //데이터 전송
		return mav;
	}
	
}
