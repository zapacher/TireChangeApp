package ee.smta.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = LondonController.class)
public class TestLondonController {

    @Autowired
    LondonController londonController;
}
