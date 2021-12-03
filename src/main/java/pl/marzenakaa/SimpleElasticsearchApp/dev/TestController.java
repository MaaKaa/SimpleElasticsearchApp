package pl.marzenakaa.SimpleElasticsearchApp.dev;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.marzenakaa.SimpleElasticsearchApp.service.UserService;

@Slf4j
@Profile("test")
@RestController
@AllArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final UserService userService;

    @GetMapping("/loadData")
    public void loadTestData() {
        log.info("Loading test data...");
        userService.loadTestData();
    }

    @GetMapping("/deleteData")
    public void deleteTestData() {
        log.info("Deleting test data...");
        userService.deleteTestData();
    }
}
