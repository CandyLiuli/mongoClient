import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hans
 */
public class Main {

    private MongoDatabase db;
    private MongoClient client;

    private static AtomicInteger count = new AtomicInteger();

    private void connect(String config) throws Exception {
        Properties properties = new Properties();
        properties.load(new BufferedInputStream(new FileInputStream(config)));
        String uri = properties.getProperty("dbconnection");
        String dbname = properties.getProperty("dbname");
        System.out.println("loaded configuration info with dbname: " + dbname + ", and conn: " + uri);

        MongoClientURI connectionURI = new MongoClientURI(uri);
        client = new MongoClient(connectionURI);
        db = client.getDatabase(dbname);
        System.out.println("Connected db : " + db.getName());
    }

    private void close() {
        if(db != null) {
            db = null;
        }

        if(client != null) {
            client.close();
        }
    }

    public static Block<Document> getBlock() {
        Block<Document> output = new Block<Document>() {
            @Override
            public void apply(Document document) {
                System.out.println(document.toJson());
            }
        };
        return output;
    }

    public static Block<Document> getCountBlock() {
        Block<Document> countDoc = new Block<Document>() {

            @Override
            public void apply(Document document) {
                count.incrementAndGet();
            }
        };
        return countDoc;
    }

    public static Block<String> getStringBlock() {
        Block<String> stringBlock = new Block<String>() {

            @Override
            public void apply(String s) {
                System.out.println(s);
            }
        };
        return stringBlock;
    }

    private void listCollections() throws Exception {
        db.listCollectionNames().forEach(getStringBlock());
    }

    private void listDBNames() throws Exception {
        client.listDatabaseNames().forEach(getStringBlock());
    }

    private void query(String collection, String key, String value) throws Exception {
        System.out.println(" ----- quering : collection = " + collection + "; key = " + key + "; value = " + value);
        db.getCollection(collection).find(Filters.eq(key, value)).forEach(getBlock());
        System.out.println(" ----- end query -----");
    }

    private void queryAll(String collection) throws Exception {
        System.out.println(" ----- quering all: collection = " + collection);
        db.getCollection(collection).find(new Document()).forEach(getBlock());
        System.out.println(" ----- end queryAll -----");
    }

    private void querySize(String collection) throws Exception {
        System.out.println(" ----- quering size : collection = " + collection);
        count.set(0);
        db.getCollection(collection).find(new Document()).forEach(getCountBlock());
        System.out.println(" ----- end querySize with size : " + count.get() +" of collection : "+collection+" -----");
    }

    private void queryRegex(String collection, String key, String pattern) throws Exception{
        System.out.println(" ----- quering regex: collection = " + collection + "; key = " + key + "; value = " + pattern);
        db.getCollection(collection).find(Filters.regex(key, pattern)).forEach(getBlock());
        System.out.println(" ----- end query -----");
    }

    private void writeKeyValue(String collection, String key, String value) throws Exception{
        System.out.println(" ----- writeKeyValue to : collection = "+ collection + "; key = " + key + "; value = " + value);
        db.getCollection(collection).insertOne(new Document(key, value));
        System.out.println(" ----- end writeKeyValue -----");
    }

    private void write(String collection, String json) throws Exception{
        System.out.println(" ----- write to : collection = "+ collection + "; json = " + json);
        db.getCollection(collection).insertOne(Document.parse(json));
        System.out.println(" ----- end write -----");
    }

    private void delete(String collection, String key, String value) throws Exception{
        System.out.println(" ----- delete : collection = "+ collection + "; key = " + key + "; value = " + value);
        Document doc = db.getCollection(collection).findOneAndDelete(Filters.eq(key, value));
        if(doc != null) {
            System.out.println(" ----- end delete : " + doc.toJson() + " -----");
        }else{
            System.out.println(" ----- end delete with nothing -----");
        }
    }

    public static void printUsage() {
        System.out.println("----------- usage ------------");
        System.out.println("Start: java --classpath:mongo-java-driver-3.4.0.jar Main configFile");
        System.out.println("1. list dbs:                          dbs");
        System.out.println("2. list collections:                  collections");
        System.out.println("3. query collection size:             collection size");
        System.out.println("4. query collection all :             collection all");
        System.out.println("5. query collection by key-value:     collection key value");
        System.out.println("6. query collection by regex pattern: collection key pattern reg");
        System.out.println("7. writeKeyValue to collection:       collection key value write");
        System.out.println("8. write json to collection:          collection json write");
        System.out.println("9. delete collection by key-value:    collection key value delete");
        System.out.println("10. quit:                             q (or quit exit)");
        System.out.println("----------- end ------------");
    }

    public static void main(String[] args) {
        String configFile = "config.properties";
        if(args.length > 0) {
            configFile = args[0];
        }

        Main main = new Main();
        try{
            main.connect(configFile);
        }catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        Scanner scaner = new Scanner(System.in);
        while(scaner.hasNextLine()) {
            String input = scaner.nextLine();
            if("q".equals(input.trim()) || "quit".equals(input.trim()) || "exit".equals(input.trim())) {
                break;
            }
            try{
                String[] parameters = input.split(" ");
                if(parameters.length == 1) {
                    if("help".equalsIgnoreCase(parameters[0].trim())){
                        printUsage();
                    }else if("dbs".equals(parameters[0].trim())) {
                        main.listDBNames();
                    }else{
                        main.listCollections();
                    }
                }else if(parameters.length == 2) {
                    if("size".equals(parameters[1].trim())){
                        main.querySize(parameters[0]);
                    }else{
                        main.queryAll(parameters[0]);
                    }
                }else if(parameters.length == 3){
                    if("write".equalsIgnoreCase(parameters[2])){
                        main.write(parameters[0], parameters[1]);
                    }else{
                        main.query(parameters[0], parameters[1], parameters[2]);
                    }
                }else if(parameters.length == 4){
                    if("write".equalsIgnoreCase(parameters[3])){
                        main.writeKeyValue(parameters[0], parameters[1], parameters[2]);
                    }else if("delete".equalsIgnoreCase(parameters[3])){
                        main.delete(parameters[0], parameters[1], parameters[2]);
                    }else{
                        main.queryRegex(parameters[0], parameters[1], parameters[2]);
                    }
                }else{
                    printUsage();
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }

        main.close();

    }
}
