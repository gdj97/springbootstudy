package kr.gdu.shop3.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("chat")
public class ChatController {
//	@Value("${naver.clientid}")
	private String clientId = System.getenv("NAVER_CLIENTID"); //이클립스 다시 띄우기
//	@Value("${naver.clientSecret}")
	private String clientSecret = System.getenv("NAVER_CLIENT_SECRET");
	
	@RequestMapping("*")
	public String getView() {
		return null;
	}
	/*
	 * Chatbot 구현하기
	 * 1. chatgpt를 활용한 챗봇 구현하기 : https://openai.com/ko-KR/
	 */
	
	@PostMapping("naversearch")
	@ResponseBody //view 없이 직접 데이터를 클라이언트로 전송. RestController 처럼 사용
	public JSONObject naversearch(String data,Integer display, Integer start, String type) {
		StringBuffer json = new StringBuffer();
		/*
		 * 100건의 데이터가 존재 : 한페이지에 10건
		 *   start     cnt
		 *     1         1
		 *     2         11
		 *     3         21
		 *     
		 *     네이버에 검색요청시 페이지로 검색하지 않고, 조회되는 검색레코드의 순서로 조회해야 함
		 */
		int cnt = (start - 1) * display +1;
		String text=null;
		try {
			text = URLEncoder.encode(data, "UTF-8");  //검색내용
			String apiURL =	"https://openapi.naver.com/v1/search/"+type+".json?query=" + text+
		                                      "&display="+display+"&start="+cnt;
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("X-Naver-Client-Id", clientId);
			con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
			int responseCode = con.getResponseCode();
			BufferedReader br;
			if (responseCode == 200) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
			} else {
				br = new BufferedReader(new InputStreamReader(con.getErrorStream(),"UTF-8"));
			}
			String inputLine=null;
			while ((inputLine = br.readLine()) != null) {	
				json.append(inputLine);	
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONParser parser = new JSONParser();
		JSONObject jsonObj=null;
		try {
			//json : 네이버에서 검색한 내용
			//jsonObj : JSon 객체 저장
			jsonObj = (JSONObject)parser.parse(json.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return jsonObj;
	}	
}