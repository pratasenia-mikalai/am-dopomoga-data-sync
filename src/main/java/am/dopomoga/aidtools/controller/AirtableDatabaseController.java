package am.dopomoga.aidtools.controller;

import am.dopomoga.aidtools.controller.dto.AirtableDatabaseApiModel;
import am.dopomoga.aidtools.service.AirtableDatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v0/base")
public class AirtableDatabaseController {

    private final AirtableDatabaseService airtableDatabaseService;

    @RequestMapping(method = RequestMethod.GET, path = "/airtable")
    public ResponseEntity<List<AirtableDatabaseApiModel>> getAirtableBasesList() {
        return ResponseEntity.ok(airtableDatabaseService.getDatabaseListFromAirtable());
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<AirtableDatabaseApiModel>> getBases() {
        return ResponseEntity.ok(airtableDatabaseService.getDatabaseListFromLocal());
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<List<AirtableDatabaseApiModel>> saveBases(@RequestBody List<AirtableDatabaseApiModel> bases) {
        return ResponseEntity.ok(airtableDatabaseService.saveDatabasesToLocal(bases));
    }

}
