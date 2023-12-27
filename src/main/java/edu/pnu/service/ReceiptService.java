package edu.pnu.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
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
import org.springframework.web.reactive.function.client.WebClient;

import edu.pnu.domain.Product;
import edu.pnu.domain.Receipt;
import edu.pnu.exception.ResourceNotFoundException;
import edu.pnu.persistence.ReceiptRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ReceiptService {
	
	private MongoTemplate mongoTemplate;

	private ReceiptRepository receiptRepo;
	
	private WebClient webclient;
	
	public ReceiptService(ReceiptRepository receiptRepo, MongoTemplate mongoTemplate, WebClient webclient) {
		this.receiptRepo = receiptRepo;
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

	public Flux<Receipt> runReceiptOCR(MultipartFile image) throws IllegalStateException, IOException {
        Path tempFile = Paths.get(System.getProperty("java.io.tmpdir"), image.getOriginalFilename());
        image.transferTo(tempFile.toFile());
        System.out.println(image);
        return webclient.post()
                .uri("http://10.125.121.211:8080/")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(new FileSystemResource(tempFile.toFile()))
                .retrieve()
                .bodyToFlux(Receipt.class)
                .doFinally(signalType -> tempFile.toFile().delete());
    }
	
}
