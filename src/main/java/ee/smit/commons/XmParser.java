package ee.smit.commons;

import ee.smit.commons.errors.InternalServerErrorException;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.Objects;

@Component
public class XmParser {
    public static <T> T fromXml(String xmlString, Class<T> clazz) {

        if(Objects.requireNonNull(xmlString).isEmpty()) {
            throw new InternalServerErrorException(500, "blank response for class " + clazz.getCanonicalName() );
        }

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (T) unmarshaller.unmarshal(new StringReader(xmlString));
        } catch (JAXBException ex) {
            throw new InternalServerErrorException(500, ex.getMessage());
        }
    }
}
