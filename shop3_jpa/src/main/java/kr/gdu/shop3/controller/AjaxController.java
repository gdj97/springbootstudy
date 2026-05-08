package kr.gdu.shop3.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import kr.gdu.shop3.service.BoardService;
import kr.gdu.shop3.service.ShopService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
/*
 * @Controller : @Component + Controller 기능
 *    @Component : 객체화
 *    Controller : 요청 url 맞춰 해당 메서드 호출 기능
 *    
 *    리턴타입 :
 *      - ModelAndView : 뷰이름 + 전달데이터
 *      - String       : 뷰이름, 데이터는 Model 객체에 따로 전송가능
 *      
 * @RestController :  @Component + Controller + 클라이언트로 직접 데이터 전송(뷰없음)
 *     리턴타입 :
 *       - String : 클라이언트에 전송할 문자열데이터
 *       - Object(Map<DTO>,List<DTO>) : 클라이언트로 직접 객체 전달. JSON 형식으로 전달됨
 *
 *  Spring 4.0 이후에 RestController 기능 추가됨.
 *  이전에는 요청메서드마다 @ResponseBody 기능을 추가하여 사용함      
 */
@RestController  //view 없이 직접 데이터를 클라이언트로 전송
@RequestMapping("ajax")
public class AjaxController {
	@Autowired
	private ShopService service;
	@Autowired
	private BoardService boardService;
	
	@RequestMapping(value="select1", produces="text/plain; charset=utf-8")
	//produces : 클라이언트에 정보 전달
	//text/plain : 문서형식. MIME타입. 
	//charset=utf-8 : 한글 인코딩방식 설정
	public String sidoSelect1(String si, String gu) {
		return service.sidoSelect1(si,gu);  //클라이언트로 전달할 문자열 데이터
	}
	//리턴타입 : List<String> => 클라이언트에서는 배열로 인식. com.fasterxml.jackson.core 설정필요
	@RequestMapping("select")
	public List<String> select(String si, String gu) {
		return service.sidoSelect(si,gu);  //클라이언트로 전달할 문자열 데이터
	}
	
	@RequestMapping(value="exchangeString", produces="text/html; charset=utf-8")
	public String exchangeString() {
//		return "<h3>수출입은행 환율정보 데이터</h3>";			
		return service.exchangeString();
	}	
	@RequestMapping(value="exchangeJson")
	public Map<String,Object> exchangeJson() {
		return service.exchangeJson();
	}	
	@PostMapping(value="uploadImage",produces="text/plain; charset=utf-8")
	  public String summernoteImageUpload (@RequestParam("image") MultipartFile multipartFile) {
		return service.summernoteImageUpload(multipartFile);
	  }
	@RequestMapping("goodeelogo")
	public String goodeelogo() {
		return service.goodeelogo();	               
	}

	@RequestMapping("graph1")
	public List<Map.Entry<String, Integer>> graph1(String boardid) {
		Map<String,Integer> map = boardService.graph1(boardid);
		//map : {홍길동:3,111:2,...}
		List<Map.Entry<String, Integer>> list = new ArrayList<>();
		for(Map.Entry<String, Integer> m : map.entrySet() ) {
			list.add(m); // [홍길동:3,111:2]
		}
		//m.getValue() 의 내림차순으로 list를 정렬하기
		Collections.sort(list,(m1,m2)->m2.getValue() - m1.getValue());
		return list; //[홍길동:3,111:2]
	}	
	@RequestMapping("graph2")
	public List<Map.Entry<String, Integer>> graph2(String id) {
	   Map<String,Integer> map = boardService.graph2(id);
	   List<Map.Entry<String, Integer>> list =  new ArrayList<>(map.entrySet());
	   return list;	               
	}	
	
	@PostMapping(value="gptquestion", produces="text/html; charset=utf-8")
	public String gptquestion (String question) {
      String gptResponse = null;
//	  try {
		  //gptResponse : gpt의 응답 메세지
		  gptResponse = service.getChatGPTResponse(question);
//	  } catch (URISyntaxException | IOException | InterruptedException e) {
//		e.printStackTrace();
//	  }
	  return gptResponse;
	}	
}
