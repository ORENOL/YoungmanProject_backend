package edu.pnu.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.core.io.ByteArrayResource;
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

import edu.pnu.domain.Receipt;
import edu.pnu.domain.ReceiptDocument;
import edu.pnu.domain.dto.ReceiptPOJO;
import edu.pnu.exception.ResourceNotFoundException;
import edu.pnu.persistence.ReceiptDocumentRepository;
import edu.pnu.persistence.ReceiptRepository;
import reactor.core.publisher.Flux;

@Service
public class ReceiptService {
	
	private MongoTemplate mongoTemplate;
	private ReceiptRepository receiptRepo;
	private ReceiptDocumentRepository receiptDocumentRepo;
	private WebClient webclient;
	
	public ReceiptService(ReceiptRepository receiptRepo, ReceiptDocumentRepository receiptDocumentRepo, MongoTemplate mongoTemplate, WebClient webclient) {
		this.receiptRepo = receiptRepo;
		this.receiptDocumentRepo = receiptDocumentRepo;
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
		
		if (searchCriteria.equals("companyName") || searchCriteria.equals("item")) {
		    query.addCriteria(Criteria.where(searchCriteria).regex(searchWord, "i"));
	        List<Receipt> list = mongoTemplate.find(query, Receipt.class);
		    return new PageImpl<>(list, pageable, total); 
		}
		
		if (searchCriteria.equals("quantity") || searchCriteria.equals("unitPrice") || searchCriteria.equals("price")) {
			int number = Integer.parseInt(searchWord);
			query.addCriteria(Criteria.where(searchCriteria).gte(number));
	        List<Receipt> list = mongoTemplate.find(query, Receipt.class);
		    return new PageImpl<>(list, pageable, total);
		}
		
		if (searchCriteria.equals("tradeDate") || searchCriteria.equals("createDate")) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	
			String[] parts = searchWord.split("~");
		    Date startDate = format.parse(parts[0]);
		    Date endDate = format.parse(parts[1]);

	        query.addCriteria(Criteria.where(searchCriteria).gte(startDate).lte(endDate));
	        List<Receipt> list = mongoTemplate.find(query, Receipt.class);
		    return new PageImpl<>(list, pageable, total);
		}
		
		return null;
		
		
	}

	public String saveReceipt(Receipt receipt) {
		Receipt receiptId = receiptRepo.save(receipt);
		return receiptId.getReceiptId();
	}
	
	public String saveListReceipt(List<Receipt> receipt) {
		receiptRepo.saveAll(receipt);
		return null;
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

	public List<ReceiptPOJO> runReceiptOCR(MultipartFile image) throws IllegalStateException, IOException {
		
		ByteArrayResource byteArrayResource = new ByteArrayResource(image.getBytes()) {
			@Override
			public String getFilename() {
                return image.getOriginalFilename();
            }
		};
      
		Flux<ReceiptPOJO> imageText = webclient.post()
        .uri("http://10.125.121.211:5000/")
        .contentType(MediaType.MULTIPART_FORM_DATA)
        .body(BodyInserters.fromMultipartData("image", byteArrayResource))
        .retrieve()
        .bodyToFlux(ReceiptPOJO.class);
		
	    List<ReceiptPOJO> receipts = imageText.collectList().block();

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
	    List<ReceiptPOJO> receiptList = new ArrayList<>();
	    
	    for (ReceiptPOJO data : receipts) {
	    	ReceiptPOJO temp = ReceiptPOJO.builder()
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
	    
	    return receiptList;
    }


	
}
