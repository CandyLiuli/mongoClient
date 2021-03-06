import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.hpe.mcloud.imgsvcs.model.persist.ImageHistoryJsonDao;
import com.hpe.mcloud.imgsvcs.model.persist.ImageIndexJsonDao;
import com.hpe.mcloud.imgsvcs.model.persist.ImageRequestJsonDao;
import dao.DBBase;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

/**
 * Created by hans on 1/9/2018.
 */
public class Export extends DBBase{

    private void export() {
        List<ImageRequestJsonDao> requestList = datastore.createQuery(ImageRequestJsonDao.class).asList();
        List<ImageIndexJsonDao> indexList = datastore.createQuery(ImageIndexJsonDao.class).asList();
        List<ImageHistoryJsonDao> historyList = datastore.createQuery(ImageHistoryJsonDao.class).asList();
        int total = 0;
        for(ImageRequestJsonDao req: requestList) {
            System.out.println(req.toExport());
            total++;
        }
        for(ImageIndexJsonDao index: indexList) {
            System.out.println(index.toExport());
            total++;
        }
        for(ImageHistoryJsonDao history: historyList) {
            System.out.println(history.toExport());
            total++;
        }
        System.out.println("Exported with total " + total);
    }

    public static void main(String[] args) {
        String configFile = "config.properties";
        if(args.length > 0) {
            configFile = args[0];
        }

        Export export = new Export();
        try{
            export.connect(configFile);
            export.export();
        }catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        export.close();
    }
}
