package sg.edu.nus.iss.vttpssfassessmentpractice.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import sg.edu.nus.iss.vttpssfassessmentpractice.model.Quotation;
import sg.edu.nus.iss.vttpssfassessmentpractice.model.UserInput;

@Service
public class QuotationService {
    private Logger logger = Logger.getLogger(QuotationService.class.getName());
    
    UserInput userInput = new UserInput();

    public JsonObject changeJsonStringtoJsonObj (String data) {
        
        JsonObject bodyData = null;
        //converting JSON string to JSON object
        /*take note that you need to pass in the parameter with 
        a named variable that you have passed on your constructor*/
        try (InputStream is = new ByteArrayInputStream(data.getBytes())) {
            JsonReader reader = Json.createReader(is);
            bodyData = reader.readObject();
        } catch (IOException ex) {
            logger.warning(ex.getMessage());
            System.exit(1);
        }
        return bodyData;
    }

    public List<String> obtainLineItems (String payload) {
        JsonObject order = changeJsonStringtoJsonObj(payload);
        //Linkedlist array can add/remove items. Don't use arraylist as it is immutable
        List<String> items = new LinkedList<>();
        JsonArray lineItems = order.getJsonArray("lineItems");
        for(JsonValue item : lineItems) {
            String fruits = item.asJsonObject().getString("item");
            //the add keyword below is to add items onto Linkedlist
            items.add(fruits);
        }
        return items;
    }

    public UserInput obtainUserInput (String payload) {
        JsonObject order = changeJsonStringtoJsonObj(payload);
        JsonArray lineItems = order.getJsonArray("lineItems");
        for (JsonValue item : lineItems) {
            Integer fruitQuantity = item.asJsonObject().getInt("quantity");
            userInput.getQuantities().add(fruitQuantity);
        }
        userInput.setName(order.getString("name"));

        return userInput;
    }

    public Optional<Quotation> getQuotations (List<String> items) {
        
        String url = "https://quotation.chuklee.com/quotation";

        JsonArrayBuilder fruitsBuilder = Json.createArrayBuilder();
        for (String fruit : items) {
            fruitsBuilder.add(fruit);
        }
        JsonArray itemsJsonArr = fruitsBuilder.build();

        RequestEntity<String> request = RequestEntity
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept", "application/json")
                .body(itemsJsonArr.toString(), String.class);
        
        RestTemplate template = new RestTemplate ();
        ResponseEntity<String> response = template.exchange(request, String.class);

        try {
            JsonObject jsonObjFromQsys = changeJsonStringtoJsonObj(response.getBody());
            
            Quotation quotation = new Quotation();
            quotation.setQuoteId(jsonObjFromQsys.getString("quoteId"));
            
            JsonArray quotationFruits = jsonObjFromQsys.getJsonArray("quotations");
            System.out.println(">>> quotationFruits: " + quotationFruits.toString());
            for (JsonValue fruit : quotationFruits) {
                String eachFruit = fruit.asJsonObject().getString("item");
                JsonValue unitPrice = fruit.asJsonObject().get("unitPrice");
                quotation.addQuotation(eachFruit, Float.parseFloat(unitPrice.toString()));
            }
            return Optional.of(quotation);
        } catch (Exception ex) {
            logger.warning(ex.getMessage());
            return Optional.empty();
        }
    }
        
}
