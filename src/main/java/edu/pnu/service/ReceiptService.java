package edu.pnu.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import edu.pnu.domain.OriginReceipt;
import edu.pnu.domain.Receipt;
import edu.pnu.domain.ReceiptDocument;
import edu.pnu.exception.ResourceNotFoundException;
import edu.pnu.persistence.OriginReceiptRepository;
import edu.pnu.persistence.ReceiptDocumentRepository;
import edu.pnu.persistence.ReceiptRepository;
import reactor.core.publisher.Flux;

@Service
public class ReceiptService {
	
	private MongoTemplate mongoTemplate;
	private ReceiptRepository receiptRepo;
	private ReceiptDocumentRepository receiptDocumentRepo;
	private OriginReceiptRepository originReceiptRepository;
	private WebClient webclient;
	
	public ReceiptService(ReceiptRepository receiptRepo, ReceiptDocumentRepository receiptDocumentRepo, OriginReceiptRepository originReceiptRepository, MongoTemplate mongoTemplate, WebClient webclient) {
		this.receiptRepo = receiptRepo;
		this.receiptDocumentRepo = receiptDocumentRepo;
		this.originReceiptRepository = originReceiptRepository;
		this.mongoTemplate = mongoTemplate;
		this.webclient = webclient;
	}

	public Page<Receipt> getPageReceipt(int pageNo, int pageSize, String orderCriteria, String searchCriteria, String searchWord) throws ParseException {
		
		Sort sort = Sort.by(Sort.Order.desc(orderCriteria));
		PageRequest pageable = PageRequest.of(pageNo, pageSize, sort);
		System.out.println(searchWord);
		
		if (searchWord == null) {
			Page<Receipt> page = receiptRepo.findAll(pageable);
			return page;
		}
		
		Query query = new Query();
		query.with(pageable);
		long total = mongoTemplate.count(query, Receipt.class);
		
		// 회사이름 또는 품명 검색
		if (searchCriteria.equals("companyName") || searchCriteria.equals("item")) {
		    query.addCriteria(Criteria.where(searchCriteria).regex(searchWord, "i"));
	        List<Receipt> list = mongoTemplate.find(query, Receipt.class);
		    return new PageImpl<>(list, pageable, total); 
		}
		
		// 수량 또는 단가 또는 금액 검색
		if (searchCriteria.equals("quantity") || searchCriteria.equals("unitPrice") || searchCriteria.equals("price")) {
			int number = Integer.parseInt(searchWord);
			query.addCriteria(Criteria.where(searchCriteria).gte(number));
	        List<Receipt> list = mongoTemplate.find(query, Receipt.class);
		    return new PageImpl<>(list, pageable, total);
		}
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		// 날짜 검색
		if (searchCriteria.equals("tradeDate") || searchCriteria.equals("createDate")) {
			
			String[] parts = searchWord.split("~");
		    Date startDate = format.parse(parts[0]);
		    Date endDate = format.parse(parts[1]);

	        query.addCriteria(Criteria.where(searchCriteria).gte(startDate).lte(endDate));
	        List<Receipt> list = mongoTemplate.find(query, Receipt.class);
		    return new PageImpl<>(list, pageable, total);
		}
		
		Criteria criteria = new Criteria();
		
		// 회사이름 & 품명 검색
		if (searchCriteria.equals("companyName&item")) {
			String[] keyword = searchWord.split("&");
			criteria.andOperator(Criteria.where("companyName").regex(keyword[0], "i"), 
									Criteria.where("item").regex(keyword[1], "i"));
			query.addCriteria(criteria);
		    List<Receipt> list = mongoTemplate.find(query, Receipt.class);
		    return new PageImpl<>(list, pageable, total); 
		}
		
		// 날짜 & 회사이름 검색
		if (searchCriteria.equals("tradeDate&companyName")) {
			String[] keyword = searchWord.split("&");
			
			String[] dateParts = keyword[0].split("~");
		    Date startDate = format.parse(dateParts[0]);
		    Date endDate = format.parse(dateParts[1]);
		    
			criteria.andOperator(Criteria.where("tradeDate").gte(startDate).lte(endDate),
									Criteria.where("companyName").regex(keyword[1], "i"));
			query.addCriteria(criteria);
			List<Receipt> list = mongoTemplate.find(query, Receipt.class);
		    return new PageImpl<>(list, pageable, total);
		}
		// 날짜 & 품명 검색
		if (searchCriteria.equals("tradeDate&item")) {
			String[] keyword = searchWord.split("&");
			
			String[] dateParts = keyword[0].split("~");
		    Date startDate = format.parse(dateParts[0]);
		    Date endDate = format.parse(dateParts[1]);
		    
			criteria.andOperator(Criteria.where("tradeDate").gte(startDate).lte(endDate),
									Criteria.where("item").regex(keyword[1], "i"));
			query.addCriteria(criteria);
			List<Receipt> list = mongoTemplate.find(query, Receipt.class);
		    return new PageImpl<>(list, pageable, total);
		}
		// 날짜 & 회사이름 & 품명 검색
		if (searchCriteria.equals("tradeDate&companyName&item")) {
			String[] keyword = searchWord.split("&");
			
			String[] dateParts = keyword[0].split("~");
		    Date startDate = format.parse(dateParts[0]);
		    Date endDate = format.parse(dateParts[1]);
		    
			criteria.andOperator(Criteria.where("tradeDate").gte(startDate).lte(endDate),
									Criteria.where("companyName").regex(keyword[1], "i"),
									Criteria.where("item").regex(keyword[2], "i"));
			query.addCriteria(criteria);
			List<Receipt> list = mongoTemplate.find(query, Receipt.class);
		    return new PageImpl<>(list, pageable, total);
		}
		
		return null;
		
		
	}

	public String saveReceipt(Receipt receipt) {
		Receipt receiptId = receiptRepo.save(receipt);
		return receiptId.getReceiptId();
	}
	
	public void saveListReceipt(List<Receipt> receipt) {
		List<Receipt> receiptList = new ArrayList<>();
	    for (Receipt data : receipt) {
	    	Receipt temp = Receipt.builder()
	    				.companyName(data.getCompanyName())
	    				.item(data.getItem())
	    				.quantity(data.getQuantity())
	    				.unitPrice(data.getUnitPrice())
	    				.price(data.getPrice())
	    				.tradeDate(data.getTradeDate())
	    				.createDate(data.getCreateDate())
	    				.receiptDocumentId(data.getReceiptDocumentId())
	    				.originReceiptId(data.getReceiptId())
	    				.build();
	    	receiptList.add(temp);
	    }
		receiptRepo.saveAll(receiptList);
		return;
	}

	public void deleteBoard(Receipt receiptId) {
		
		System.out.println(receiptId.toString());

		Optional<Receipt> receipt = receiptRepo.findById(receiptId.getReceiptId());
		
		if (receipt.isPresent()) {
			receiptRepo.deleteById(receiptId.getReceiptId());
			return;
		}
		
		throw new ResourceNotFoundException("not exist receipt");
		
	}

	public List<Receipt> getAllReceipt() {
		return receiptRepo.findAll();
	}

	public List<OriginReceipt> runReceiptOCR(MultipartFile image) throws IllegalStateException, IOException {
		
		ByteArrayResource byteArrayResource = new ByteArrayResource(image.getBytes()) {
			@Override
			public String getFilename() {
                return image.getOriginalFilename();
            }
		};
      
		Flux<OriginReceipt> imageText = webclient.post()
        .uri("http://10.125.121.211:5000/")
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .body(BodyInserters.fromMultipartData("image", byteArrayResource))
        .retrieve()
        .bodyToFlux(OriginReceipt.class);
		
	    List<OriginReceipt> receipts = imageText.collectList().block();

//		imageText.subscribe(
//        item -> System.out.println(
//        		item.toString()),
////        		Receipt.builder().item(item.getItem()).quantity(item.getQuantity()).unitPrice(item.getUnitPrice()).price(item.getPrice()).tradeDate(LocalDateTime.parse(item.getTradeDate()))), // onNext - 데이터 처리
//        error -> System.err.println("Error: " + error), // onError - 에러 처리
//        () -> System.out.println("Done") // onComplete - 완료 처리
//    );
	    String userHomeDir = System.getProperty("user.home");
	    String imgDir = userHomeDir + File.separator + "Youngman";
	    Path path = Paths.get(imgDir + File.separator + image.getOriginalFilename());
	    
	    if(!Files.exists(path)) {
	    	Files.createDirectories(path.getParent());
	    }
	    
	    OutputStream os = new FileOutputStream(path.toFile());
	    os.write(image.getBytes());
	    os.close();
	    
	    String documentId = receiptDocumentRepo.save(ReceiptDocument.builder()
	    												.imgPath(path.toString())
	    												.build()).getId();
	    List<OriginReceipt> receiptList = new ArrayList<>();
	    
	    for (OriginReceipt data : receipts) {
	    	OriginReceipt temp = OriginReceipt.builder()
	    				.companyName(data.getCompanyName())
	    				.item(data.getItem())
	    				.quantity(data.getQuantity())
	    				.unitPrice(data.getUnitPrice())
	    				.price(data.getPrice())
	    				.tradeDate(data.getTradeDate())
	    				.createDate(data.getCreateDate())
	    				.receiptDocumentId(documentId)
	    				.build();
	    	receiptList.add(temp);
	    }
	    originReceiptRepository.saveAll(receiptList);
	    return receiptList;
    }

	public Resource getReceiptImage(String receiptDocumentId) throws MalformedURLException {
		
		Optional<ReceiptDocument> existDocument = receiptDocumentRepo.findById(receiptDocumentId);
		
		if(!existDocument.isPresent()) {
			throw new ResourceNotFoundException("not exist ReceiptDocument");
		}
		
		String imgPath = existDocument.get().getImgPath();
		
		Path path = Paths.get(imgPath);
		Resource resource = new UrlResource(path.toUri());
		
		if(!resource.exists() || !resource.isReadable()) {
			throw new ResourceNotFoundException("not exist img");
		}
		
		return resource;
	}

	public OriginReceipt getOriginReceipt(String originReceiptId) {
		Optional<OriginReceipt> existOriginReceipt = originReceiptRepository.findById(originReceiptId);
		if (!existOriginReceipt.isPresent()) {
			throw new ResourceNotFoundException("not exist originReceipt");
		}
		return existOriginReceipt.get();
	}


	
}
