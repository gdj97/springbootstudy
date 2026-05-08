package kr.gdu.shop3.service;

import java.io.File;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kr.gdu.shop3.dto.CartDto;
import kr.gdu.shop3.dto.ItemDto;
import kr.gdu.shop3.dto.ItemSetDto;
import kr.gdu.shop3.dto.SaleDto;
import kr.gdu.shop3.dto.SaleItemDto;
import kr.gdu.shop3.dto.UserDto;
import kr.gdu.shop3.entity.Item;
import kr.gdu.shop3.entity.Sale;
import kr.gdu.shop3.entity.SaleItem;
import kr.gdu.shop3.repository.ItemRepository;
import kr.gdu.shop3.repository.SaleRepository;
import kr.gdu.shop3.repository.SaleItemRepository;
//Service : Controller와 Repository(Model) 사이의 중간 역할
@Service  //@Component + Service 기능. 
public class ItemService {
	@Autowired
	private ItemRepository itemDao;
	@Autowired
	private SaleRepository saleDao;
	@Autowired
	private SaleItemRepository saleItemDao;

	public List<ItemDto> itemList() {
		//findAll : item 테이블의 모든 정보 조회. List<Item> 리턴
		return itemDao.findAll().stream().map(item->new ItemDto(item)).toList();
	}
	public ItemDto getItem(Integer id) {
		return new ItemDto(itemDao.findById(id).get());
	}

	public void itemDelete(Integer id) {
		itemDao.deleteById(id);		
	}

	public void itemCreate(ItemDto item, HttpServletRequest request) {
		//업로드할 파일이 존재하면 파일을 저장
		if(item.getPicture() != null && !item.getPicture().isEmpty()) { //업로드된 파일 존재
		  String path = request.getServletContext().getRealPath("/")+"img/"; //업로드될 폴더
		  uploadFileCreate(item.getPicture(),path);
		  item.setPictureUrl(item.getPicture().getOriginalFilename());
		}
		//db에 저장
		int maxid = itemDao.maxId(); //저장된 id의 최대값 조회
		item.setId(maxid + 1);
		itemDao.save(new Item(item)); //save : 추가 또는 수정
	}
	public void itemUpdate(ItemDto item, HttpServletRequest request) {
		//업로드할 파일이 존재하면 파일을 저장
		if(item.getPicture() != null && !item.getPicture().isEmpty()) { //업로드된 파일 존재
		  String path = request.getServletContext().getRealPath("/")+"img/"; //업로드될 폴더
		  uploadFileCreate(item.getPicture(),path);
		  item.setPictureUrl(item.getPicture().getOriginalFilename());
		}
		//db에 저장
		itemDao.save(new Item(item));
	}	
	//파일 업로드하기
	private void uploadFileCreate(MultipartFile picture, String path) {
		//picture : Item 객체의 MultipartFile 객체. 파일의 내용을 저장
		String orgFile = picture.getOriginalFilename(); //원본파일 이름 
		File f = new File(path);
		if(!f.exists()) f.mkdirs();  //업로드 폴더가 없는 경우 폴더 생성
		try {
			//picture의 파일내용을 path + orgFile이름의 파일로 이동(저장).
			picture.transferTo(new File(path + orgFile));
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SaleDto checkend(UserDto loginUser, CartDto cart) {
	    int maxsaleid = saleDao.getMaxSaleId(); //saleid의 최대값
	    SaleDto sale = new SaleDto();                 //Sale 객체 생성
	    sale.setSaleid(maxsaleid+1);            //최대값 + 1
	    sale.setUser(loginUser);                //로그인한 User 객체 정보
	    sale.setUserid(loginUser.getUserid());  //로그인한 userid
	    saleDao.save(new Sale(sale));                   //sale 테이블에 저장
	    int seq = 0;
	    for(ItemSetDto is : cart.getItemSetList()) {//주문상품
	    	SaleItemDto saleItem = new SaleItemDto(sale.getSaleid(),++seq,is);
	    	sale.getItemList().add(saleItem);
	    	saleItemDao.save(new SaleItem(saleItem)); //주문상품(saleitem)테이블에 저장
	    }
		return sale; //주문정보, 고객정보, 주문상품
	}

	public List<SaleDto> saleList(String userid) {
		//[{saleid:1,userid:test1,..},{saleid:3,userid:test1,..}]
		List<Sale> list = saleDao.findByUserid(userid); //userid가 주문한 sale 정보 목록
		//list의 Sale 객체에 SaleItem 객체목록 저장
		for(Sale sa : list) { //sa : {saleid:1,userid:test1,..}
			//saleItemList : saleid에 해당하는 주문상품 목록
			List<SaleItem> saleItemList = saleItemDao.findBySaleid(sa.getSaleid()); //주문번호에 해당하는 주문상품 목록
			for(SaleItem si : saleItemList) {
				Item item = itemDao.findById(si.getItemid()).get();  //주문상품의 상품정보
				si.setItem(item); //상품정보
			}
			sa.setItemList(saleItemList);
		}
		return list.stream().map(sale->new SaleDto(sale)).toList();		
	}
}
