package ee.smit.controllers;

import ee.smit.services.ManchesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ManchesterService.class)
public class TestManchesterController {

    @Autowired
    ManchesterService manchesterService;
}
