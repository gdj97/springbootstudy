package kr.gdu.shop2.controller;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import kr.gdu.shop2.dto.Cart;
import kr.gdu.shop2.dto.Item;
import kr.gdu.shop2.dto.ItemSet;
import kr.gdu.shop2.dto.Sale;
import kr.gdu.shop2.dto.User;
import kr.gdu.shop2.service.ItemService;

@Controller
@RequestMapping("cart")
public class CartController {
	@Autowired
	ItemService service;
	//장바구니에 상품 추가
	@RequestMapping("cartAdd")
	public String add(int id, int quantity, HttpSession session,Model model) {
		Item item = service.getItem(id);
		Cart cart = (Cart)session.getAttribute("CART");
		if(cart == null) {
			cart = new Cart();
			session.setAttribute("CART", cart);
		}
		cart.push(new ItemSet(item,quantity));
		model.addAttribute("message", item.getName() + ":" + quantity +"개 장바구니에 추가");
		model.addAttribute("cart", cart);
		return "cart/cart";
	}
	@RequestMapping("cartView")
	public String view(HttpSession session,Model model) {
		Cart cart = (Cart)session.getAttribute("CART");
		model.addAttribute("message", "장바구니 상품 조회");
		model.addAttribute("cart", cart);
		return "cart/cart";
	}
	@RequestMapping("cartDelete")
	public String delete(int index, HttpSession session,Model model) {
		Cart cart = (Cart)session.getAttribute("CART");
		ItemSet deleteItem = cart.getItemSetList().remove(index);
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
		
		Cart cart = (Cart)session.getAttribute("CART");
		User loginUser = (User)session.getAttribute("loginUser");
		
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
		Cart cart = (Cart)session.getAttribute("CART");           //장바구니 상품
		User loginUser = (User)session.getAttribute("loginUser"); //로그인 정보
		//sale,saleitem 테이블에 저장. Sale 객체 리턴
		Sale sale = service.checkend(loginUser,cart);
		session.removeAttribute("CART"); //장바구니 상품 제거
		mav.addObject("sale",sale); //데이터 전송
		return mav;
	}
	
}
