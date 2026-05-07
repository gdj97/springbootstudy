package kr.gdu.shop2.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jakarta.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ShopService {
	@Value("${resources.dir}")   //application.properties 파일의 정보 가져오기
	private String RESOURCES_DIR;  //resources.dir=D:/20251222/springboot/workspace/shop2/src/main/resources/
	@Value("${board.upload.dir}")
	private String BOARD_UPLOAD_DIR;
	/*
	 * Spring AI에서 제공되는 클래스 
	 * OPENAI에게 요청하는 클래스
	 * application.properties 파일의 spring.ai.openai.api-key 값을 api-key로 사용함
	 * spring.ai.openai.api-key : 시스템 환경변수에 등록된 키값으로 설정함
	 */
    private ChatClient chatClient;  

    //생성자를 통해 ChatClient 객체 주입
    public ShopService(ChatClient.Builder chatClientBuilder) {
        chatClient = chatClientBuilder.build();
    }
	
	public String sidoSelect1(String si, String gu) {
		BufferedReader fr = null;
		String path = RESOURCES_DIR+"static/file/sido.txt";
		try {
			fr = new BufferedReader(new FileReader(path));
		}catch(Exception e) {
			e.printStackTrace();	
		}
		Set<String> set = new LinkedHashSet<>(); //중복불가 + 순서유지
//		Set<String> set = new TreeSet<>(); //중복불가 + 정렬
		String data= null;
		if(si==null && gu==null) {
			try {
				while((data=fr.readLine()) != null) {
					String[] arr = data.split("\\s+"); //공백으로 문자열 분리
					if(arr.length >= 3) set.add(arr[0].trim()); 
				}
			} catch(IOException e) {   e.printStackTrace();	}
		}
		List<String> list = new ArrayList<>(set);
		return list.toString(); //[서울특별시,경기도,경상북도,....] 		
	}

	public List<String> sidoSelect(String si, String gu) {
		BufferedReader fr = null;
		String path = RESOURCES_DIR +"static/file/sido.txt";
		try {
			fr = new BufferedReader(new FileReader(path));
		}catch(Exception e) {
			e.printStackTrace();
		}
		Set<String> set = new LinkedHashSet<>(); // 중복불가 + 순서유지. 
		String data= null;
		if(si==null && gu==null) { //sidoSelect1 처리함. 
			return null;
		} else if(gu == null) { //si 파라미터 존재. 시도선택한 경우. 구군을 검색하여 리턴
		   si = si.trim();
		   try {
			   //fr.readLine() : sido.txt 파일에서 한줄씩 읽기
			  while ((data = fr.readLine()) != null) {
				 String[] arr = data.split("\\s+"); //공백으로 분리
				 //arr[0].equals(si) : 시도가 si파라미터값과 같은경우
				 //!arr[1].contains(arr[0]) : 첫번째 배열의 값을 두번째 배열의 요소의 값이 같은 값을 포함하는 경우.
				 //                 서울특별시 서울특별시 ...
			  	 if(arr.length >= 3 && arr[0].equals(si) && !arr[1].contains(arr[0]) ) {
					 set.add(arr[1].trim()); //구군의 데이터를 set에 추가
				 }
			   }
		   } catch (IOException e) {
			   e.printStackTrace();
		   }
		} else {  //si 파라미터,gu 파라미터값 존재.
		   si = si.trim();
		   gu = gu.trim();
		   try {
			  while ((data = fr.readLine()) != null) {
				  String[] arr = data.split("\\s+");
		          if(arr.length >= 3 && arr[0].equals(si) && arr[1].equals(gu)
			    	  && !arr[0].equals(arr[1]) && !arr[2].contains(arr[1])) {
			          	 if(arr.length > 3 ) {
			          		if(arr[3].contains(arr[1])) continue;
			          		arr[2] += " " + arr[3];
			          	 }
			          	 set.add(arr[2].trim());
			      }
			  }
			} catch (IOException e) {
			    e.printStackTrace();
			}
		}
		List<String> list = new ArrayList<>(set); //Set 객체 => List 객체로 
		return list;		            
	}

	public String exchangeString() {
		Document doc = null;
		List<List<String>> trlist = new ArrayList<>(); 
		String url = "https://www.koreaexim.go.kr/wg/HPHKWG057M01";
		String exdate = null;
		try {
			doc = Jsoup.connect(url).get();
			Elements trs = doc.select("tr"); //tr 태그들
			exdate = doc.select("p.table-unit").html();  //조회기준일 : 2026.04.07
			for(Element tr : trs) {
				List<String> tdlist = new ArrayList<>();
				Elements tds = tr.select("td"); //td 태그들
				for(Element td : tds) {
					tdlist.add(td.html());
				}
			    if (tdlist.size() > 0) {
				   if(tdlist.get(0).equals("USD") //미달러 통화코드
			    	|| tdlist.get(0).equals("CNH") //중국 통화코드
			        || tdlist.get(0).equals("JPY(100)") //일본 통화코드
			        || tdlist.get(0).equals("EUR")) {   //유로 통화코드
				    trlist.add(tdlist); // 미국,중국,일본,유럽 통화에 해당하는 tr 태그들만 저장
				   }
			    }
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<p class='text-right'>" + exdate + "</p>"); //text-right : 오른쪽 정렬
		sb.append("<table class='table table-sm table-bordered'>");
		sb.append("<tr><th>통화</th><th>기준율</th>"  //text-nowrap : 한줄로 출력
				+ "<th class='text-nowrap'>받을실때</th><th class='text-nowrap'>보내실때</th></tr>");
		for(List<String> tds : trlist) {
			sb.append("<tr><td>"+tds.get(0)+"<br>"+tds.get(1)+"</td><td>"+tds.get(4)+"</td>");
			sb.append("<td>"+tds.get(2)+"</td><td>"+tds.get(3)+"</td></tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}

	public Map<String, Object> exchangeJson() {
		Document doc = null;
		List<List<String>> trlist = new ArrayList<>();
		String url = "https://www.koreaexim.go.kr/wg/HPHKWG057M01";
		String exdate = null;
		try {
			doc = Jsoup.connect(url).get();
			Elements trs = doc.select("tr"); 
			exdate = doc.select("p.table-unit").html();
			for(Element tr : trs) {
				List<String> tdlist = new ArrayList<>();
				Elements tds = tr.select("td");
				for(Element td : tds) {
					tdlist.add(td.html());
				}
			    if (tdlist.size() > 0) {
				   if(tdlist.get(0).equals("USD")
			    	|| tdlist.get(0).equals("CNH")
			        || tdlist.get(0).equals("JPY(100)")
			        || tdlist.get(0).equals("EUR")) {
				    trlist.add(tdlist);
				   }
			    }
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		Map<String,Object> map = new HashMap<>();
		map.put("exdate", exdate);
		map.put("trlist", trlist);
		return map;
	}

	public String summernoteImageUpload(MultipartFile multipartFile) {
		File dir = new File(BOARD_UPLOAD_DIR + "image/");
		if (!dir.exists()) dir.mkdirs();
		String filesystemName = multipartFile.getOriginalFilename();
		File file = new File(dir,filesystemName);
		try {
			multipartFile.transferTo(file); 
		} catch(Exception e) {
			e.printStackTrace();
		}
		return  "/board/image/" + filesystemName;	
	}
	public String goodeelogo() {
		Document doc = null;
		String url = "https://gudi.kr/";
		Elements imgs1 = null;
		Elements imgs2 = null;
		try {
			doc = Jsoup.connect(url).get();
			imgs1  = doc.select("div.img_box._img_box > a > img "); //img태그들
			imgs2  = doc.select("#w20241118ccb193cf23d4d > div > div > div > div.front_img._front_img.holder > img "); //img태그들
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(imgs1.get(0).toString() + imgs2.get(0).toString());
		return imgs1.get(0).toString() + imgs2.get(0).toString();
	}
	
	
/*
	public String getChatGPTResponse(String question) throws URISyntaxException,IOException,InterruptedException{
//	    final String API_KEY = "OPEN AI API_KEY";
		//github에 로드 하지 말것
		//openai에서 제공하는 key값. 
	    final String API_KEY ="";
	    final String ENDPOINT = "https://api.openai.com/v1/chat/completions";  //openai에 요청하는 url
	    
        HttpClient client = HttpClient.newHttpClient(); //openai에 요청할 수 있는 객체
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");  //gpt의 모델 
        /*  new HashMap(){  => 이름없는 내부객체
         *   {    }         => 인스턴스 초기화블럭
         *  }
         *  
         *  Map.of("role","system","content","당신은 자바 전문가 입니다.") => 변경 불가 Map 객체 생성
         *  =>
         *  new HashMap{{
         *     put("role","system");
                put("content", "당신은 자바 전문가 입니다.");
         *     
         *  }}
         * /
        requestBody.put("messages", new Object[] {  //요청 메세지
            new HashMap<String, String>() {{ //질문 내용
                put("role", "user");
                put("content", question);
            }},
            Map.of("role","system","content","당신은 자바 전문가 입니다.")
        });
        /*
         * role :
         *    system : 페르소나(정체성) 설정. 대화의 규칙,맥락 구체화 시킬수 있는 메세지. 대화시작시 한번. 옵션. 생략 가능
         *    user  : 실제 질문. 필수 데이터
         * /
        //자바의 객체를 JSON 형식의 문자열로 변환 할 수 있는 객체 생성
        ObjectMapper objectMapper = new ObjectMapper();
        //requestBody 객체를 json 형식의 문자열로 변경
        // requestBodyJson : { "model":"gpt-3.5-turbo","messages":[{"role":"user","content":question 값},] }
        //   gpt에 전송한 요청 문자열
        String requestBodyJson=objectMapper.writeValueAsString(requestBody);
        //HTTP 사용될 요청객체 조립하여 객체 완성
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(ENDPOINT)) //openai의 url값. 접속 API의 주소
            .header("Content-Type", "application/json") //요청 형식은 json 형식임을 명시
            .header("Authorization", "Bearer " + API_KEY) //인증을 위한 API키 설정
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson)) //POST 방식으로 요청객체에 설정
            .build();
        //요청 서버(openai)로 전송
        //HttpResponse.BodyHandlers.ofString() : 응답은 json형태로 처리하도록 설정. 응답이 올때 까지 대기함
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        //응답 결과코드 : 200 => 정상처리. gpt의 응답 성공
        if (response.statusCode() == 200) {
        	//objectMapper.readValue(문자열,타입) : 문자열을 Map 객체로 생성
        	//responseBody : 응답 데이터를 Map 객체로 저장
        	Map<String, Object> responseBody = 
        			objectMapper.readValue(response.body(), new TypeReference<Map<String, Object>>() {});
        	//key가 choices인 객체 리턴. 
            List<Map<String, Object>> choices = (List<Map<String, Object>>)responseBody.get("choices");
            Map<String, Object> firstChoice = choices.get(0);
            Map<String, String> message = (Map<String, String>)firstChoice.get("message");
            return message.get("content"); //gpt가 전송한 응답 메세지 
        } else {  //gpt 응답시 오류 발생
            throw new RuntimeException("API 요청 실패: " + response.body());
        }
	}
*/
	public String getChatGPTResponse(String question) { //기본 모델 : gpt-4o-mini
	      return chatClient.prompt()   //쳇봇의 시작
//	    	.system("당신은 자바 전문가 입니다")	 // 정체성설정
	        .user(question)            //질문사항
	        .call()                    //openai에 요청
	        .content();                 //응답 메세지
	    }
	
}
