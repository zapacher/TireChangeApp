package ee.smta.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ManchesterController.class)
public class TestManchesterController {

    @Autowired
    ManchesterController manchesterController;
}
