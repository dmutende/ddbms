package ke.co.scedar;

import com.fasterxml.jackson.core.type.TypeReference;
import ke.co.scedar.api.handlers.utils.CustomHandler;
import ke.co.scedar.db.Databases;
import ke.co.scedar.db.fragment_schema.Site;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@SuppressWarnings("unchecked")
public class TestReadFileFromResources {

    @Test public void readFragmentSchemaJson(){
        //Works when file in parent or working dir
        try {
            String strFragmentSchema = new String(Files.readAllBytes(Paths.get("fragment-schema.json")));
            Databases.FRAGMENT_SCHEMA = (List<Site>)
                    CustomHandler.getObject(strFragmentSchema, new TypeReference<List<Site>>(){});

            System.out.println(Databases.FRAGMENT_SCHEMA.get(0).getName());
            System.out.println(CustomHandler.toJson(Databases.FRAGMENT_SCHEMA));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
