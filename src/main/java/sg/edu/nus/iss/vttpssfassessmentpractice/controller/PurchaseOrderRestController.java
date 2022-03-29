package sg.edu.nus.iss.vttpssfassessmentpractice.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import sg.edu.nus.iss.vttpssfassessmentpractice.model.Quotation;
import sg.edu.nus.iss.vttpssfassessmentpractice.model.UserInput;
import sg.edu.nus.iss.vttpssfassessmentpractice.service.QuotationService;

@RestController
@RequestMapping("/")
public class PurchaseOrderRestController {

    @Autowired
    private QuotationService quoSvc;
    
    @PostMapping (path="/api/po", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> orderSubmission (@RequestBody String payload) {

        System.out.println(">>> Payload: " + payload);

        //Task 4
        List<String> items = quoSvc.obtainLineItems(payload);
        System.out.println(">>> Items: " + items);

        //Task 5
        Optional<Quotation> obtainResponse = quoSvc.getQuotations(items);
        if (obtainResponse.isPresent()) {
            Quotation quotation = obtainResponse.get();
            System.out.println(quotation);

            UserInput userInput = quoSvc.obtainUserInput(payload);
            System.out.println(userInput);

            List<Integer> quantities = userInput.getQuantities();
            float total = 0f;
            for(int i = 0; i<items.size(); i++) {
                total += quotation.getQuotation(items.get(i))*quantities.get(i);
            }

            JsonObject jsonObjToReturn = Json.createObjectBuilder()
                                        .add("invoiceId", quotation.getQuoteId())
                                        .add("name", userInput.getName())
                                        .add("total", total)
                                        .build();
            
            return ResponseEntity.ok(jsonObjToReturn.toString());
        } else {
            JsonObjectBuilder emptyBuilder = Json.createObjectBuilder();
            JsonObject emptyObj = emptyBuilder.build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(emptyObj.toString());
        }
        
    }
    
}
