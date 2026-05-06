package kr.gdu.shop2.service;

import java.io.File;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kr.gdu.shop2.dao.ItemDao;
import kr.gdu.shop2.dao.SaleDao;
import kr.gdu.shop2.dao.SaleItemDao;
import kr.gdu.shop2.dto.Cart;
import kr.gdu.shop2.dto.Item;
import kr.gdu.shop2.dto.ItemSet;
import kr.gdu.shop2.dto.Sale;
import kr.gdu.shop2.dto.SaleItem;
import kr.gdu.shop2.dto.User;
//Service : Controller와 Repository(Model) 사이의 중간 역할
@Service  //@Component + Service 기능. 
public class ItemService {
	@Autowired
	private ItemDao itemDao;
	@Autowired
	private SaleDao saleDao;
	@Autowired
	private SaleItemDao saleItemDao;

	public List<Item> itemList() {
		return itemDao.list();
	}

	public Item getItem(Integer id) {
		return itemDao.selectOne(id);
	}

	public void itemDelete(Integer id) {
		itemDao.delete(id);		
	}

	public void itemCreate(Item item, HttpServletRequest request) {
		//업로드할 파일이 존재하면 파일을 저장
		if(item.getPicture() != null && !item.getPicture().isEmpty()) { //업로드된 파일 존재
		  String path = request.getServletContext().getRealPath("/")+"img/"; //업로드될 폴더
		  uploadFileCreate(item.getPicture(),path);
		  item.setPictureUrl(item.getPicture().getOriginalFilename());
		}
		//db에 저장
		int maxid = itemDao.maxId(); //저장된 id의 최대값 조회
		item.setId(maxid + 1);
		itemDao.insert(item);
	}
	public void itemUpdate(Item item, HttpServletRequest request) {
		//업로드할 파일이 존재하면 파일을 저장
		if(item.getPicture() != null && !item.getPicture().isEmpty()) { //업로드된 파일 존재
		  String path = request.getServletContext().getRealPath("/")+"img/"; //업로드될 폴더
		  uploadFileCreate(item.getPicture(),path);
		  item.setPictureUrl(item.getPicture().getOriginalFilename());
		}
		//db에 저장
		itemDao.update(item);
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

	public Sale checkend(User loginUser, Cart cart) {
	    int maxsaleid = saleDao.getMaxSaleId(); //saleid의 최대값
	    Sale sale = new Sale();                 //Sale 객체 생성
	    sale.setSaleid(maxsaleid+1);            //최대값 + 1
	    sale.setUser(loginUser);                //로그인한 User 객체 정보
	    sale.setUserid(loginUser.getUserid());  //로그인한 userid
	    saleDao.insert(sale);                   //sale 테이블에 저장
	    int seq = 0;
	    for(ItemSet is : cart.getItemSetList()) {//주문상품
	    	SaleItem saleItem = new SaleItem(sale.getSaleid(),++seq,is);
	    	sale.getItemList().add(saleItem);
	    	saleItemDao.insert(saleItem); //주문상품(saleitem)테이블에 저장
	    }
		return sale; //주문정보, 고객정보, 주문상품
	}

	public List<Sale> saleList(String userid) {
		//[{saleid:1,userid:test1,..},{saleid:3,userid:test1,..}]
		List<Sale> list = saleDao.list(userid); //userid가 주문한 sale 정보 목록
		//list의 Sale 객체에 SaleItem 객체목록 저장
		for(Sale sa : list) { //sa : {saleid:1,userid:test1,..}
			//saleItemList : saleid에 해당하는 주문상품 목록
			List<SaleItem> saleItemList = saleItemDao.list(sa.getSaleid()); //주문번호에 해당하는 주문상품 목록
			for(SaleItem si : saleItemList) {
				Item item = itemDao.selectOne(si.getItemid());  //주문상품의 상품정보
				si.setItem(item); //상품정보
			}
			sa.setItemList(saleItemList);
		}
		return list;		
	}
}
