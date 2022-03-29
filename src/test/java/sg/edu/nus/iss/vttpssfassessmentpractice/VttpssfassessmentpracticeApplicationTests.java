package sg.edu.nus.iss.vttpssfassessmentpractice;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import sg.edu.nus.iss.vttpssfassessmentpractice.model.Quotation;
import sg.edu.nus.iss.vttpssfassessmentpractice.service.QuotationService;

@SpringBootTest
class VttpssfassessmentpracticeApplicationTests {

	@Autowired
	QuotationService quoSvc;

	@Test
	void contextLoads() {
		List<String>items = new LinkedList<>();
		items.add("durian");
		items.add("plum");
		items.add("pear");

		Optional<Quotation> result = quoSvc.getQuotations(items);
		Assertions.assertFalse(result.isPresent());
	}
	
}
