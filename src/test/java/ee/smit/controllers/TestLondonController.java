package ee.smit.controllers;

import ee.smit.services.LondonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = LondonService.class)
public class TestLondonController {

    @Autowired
    LondonService londonService;
}
