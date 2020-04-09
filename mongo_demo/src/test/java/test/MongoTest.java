package test;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MongoTest {

    //集合
    private MongoCollection<Document> comment;
    //客户端
    private MongoClient mongoClient;

    @Before
    public void init() {
        //1.创建操作MongoDB的客户端
        mongoClient = new MongoClient("192.168.0.222");

        //2.选择数据库 use commentdb
        MongoDatabase commentdb = mongoClient.getDatabase("commentdb");

        //3.获取结合 db.commentdb
        comment = commentdb.getCollection("comment");
    }

    //查询所有数据db.comment.find()
    @Test
    public void test1() {
        //4.使用集合进行查询，查询所有db.comment.find()
        FindIterable<Document> documents = comment.find();

        //5.解析结果集（打印）
        for (Document document : documents) {
            System.out.println("-------------------------");
            System.out.println("_id" + document.get("_id"));
            System.out.println("content" + document.get("content"));
            System.out.println("userid" + document.get("userid"));
            System.out.println("thumbup" + document.get("thumbup"));

        }
    }

    @After
    public void after() {
        //释放资源,关闭客户端
        mongoClient.close();
    }
}
