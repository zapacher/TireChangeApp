import ee.smta.clients.ManchesterClient;
import ee.smta.controllers.LondonController;
import ee.smta.controllers.ManchesterController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {LondonController.class, ManchesterController.class})
public class ControllerTests {

    @Autowired
    LondonController londonController;
    @Autowired
    ManchesterClient manchesterClient;

}
