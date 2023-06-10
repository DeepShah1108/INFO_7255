package edu.neu.info5.DemoApplication.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.neu.info5.DemoApplication.service.EncryptionService;
import edu.neu.info5.DemoApplication.service.PlanService;
import edu.neu.info5.DemoApplication.validator.SchemaValidator;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PlanController {

    private static final Logger logger = LoggerFactory.getLogger(PlanController.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PlanService planService;
    @Autowired
    private EncryptionService encryptionService;

    @GetMapping("/")
    public String hello() {
        return "Hello";
    }

    @PostMapping("/plan")
    public ResponseEntity<String> createPlan(@RequestBody String  reqJson,
                                             HttpEntity<String> req) {

        SchemaValidator planSchema = new SchemaValidator();

        try {
            logger.info(reqJson);
            planSchema.validateSchema(new JSONObject(reqJson));
        } catch (Exception e) {
            logger.info("VALIDATING ERROR: SCHEMA NOT MATCH - " + e.getMessage());

            return ResponseEntity.badRequest().body(e.getMessage());
        }

        JSONObject jsonObject = new JSONObject(reqJson);
        String internalKey = jsonObject.getString("objectId");
        planService.createPlan(internalKey, jsonObject.toString());

        logger.info("CREATING NEW DATA: key - " + internalKey + ": json - " + jsonObject.toString());
        return ResponseEntity.status(HttpStatus.CREATED).header("ETag",encryptionService.encrypt(jsonObject.toString())).body(" {\"objectId\": \""+ internalKey +"\" }");

    }

    @DeleteMapping("/plan/{id}")
    public ResponseEntity<String> deleteByKey(@PathVariable String id) {

        logger.info("DELETING OBJECT: id - " + id);

        String intervalKey = id;

        boolean deleteResult = planService.deletePlan(intervalKey);

        if(deleteResult) {
            return new ResponseEntity<>("{\"message\": \"Deleted\"}", HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(" {\"message\": \"item not found\" }", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/plan/{id}")
    public ResponseEntity<String> readByKey(@PathVariable String id,
                                            @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false) String ifNoneMatch) {

        logger.info("RETRIEVING REDIS DATA: id - " + id);

        logger.info("RETRIEVING REDIS DATA: id - " + id);

        String intervalKey = id;

        String foundValue = planService.readPlan(intervalKey);

        if (foundValue == null) {
            logger.info("OBJECT NOT FOUND - " + intervalKey);

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            logger.info("OBJECT FOUND - " + intervalKey);
            try {
                JsonNode jsonNode = objectMapper.readTree(foundValue);
                foundValue = jsonNode.toString();
                if (ifNoneMatch != null && ifNoneMatch.equals(encryptionService.encrypt(foundValue))) {
                    return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
                }
                return ResponseEntity.ok().body(foundValue);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        }

    }

}
