package com.mt;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.grizzly.connector.GrizzlyConnectorProvider;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

public class BooksResourceTest extends JerseyTest {

    private String book1_id;
    private String book2_id;

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        final BookDao dao=new BookDao();
        return new BookApplication(dao);
    }

    protected void configureClient(ClientConfig clientConfig){
        JacksonJsonProvider jsonProvider=new JacksonJsonProvider();
        jsonProvider.configure(SerializationFeature.WRITE_NULL_MAP_VALUES,false);
        clientConfig.register(jsonProvider);
        clientConfig.connectorProvider(new GrizzlyConnectorProvider());
    }
    @Before
    public void setup(){
        book1_id= (String)toHashMap(addBook("Author1","Title1", "ISBN1", new Date(), "Cover")).get("id");
        book2_id=(String)toHashMap(addBook("Author2","Title2", "ISBN2", new Date(), "Ex1")).get("id");
    }

    protected Response addBook(String author, String title, String isbn, Date published, String...extras){
       HashMap<String, Object> book = new HashMap<String, Object>();
        book.put("author", author);
        book.put("title", title);
        book.put("isbn", isbn);
        book.put("published", published);
        if(null!=extras){
            int count =1;
            for(String s: extras){
                book.put("extra"+count++, s);
            }
        }
        Entity<HashMap<String, Object>> bookEntity= Entity.entity(book, MediaType.APPLICATION_JSON);
        return target("books").request(MediaType.APPLICATION_JSON).post(bookEntity );
    }

    protected HashMap<String, Object> toHashMap(Response response){
        return (response.readEntity(new GenericType<HashMap<String, Object>>(){}));
    }

    @Test
    public void testAddBook() throws ParseException {
        Date thisDate = new Date();
        Response response= addBook("Author","Title", "12345", thisDate,"Ex45");
        Assert.assertEquals(200, response.getStatus());
        HashMap<String, Object> responseBook = toHashMap(response);
        Assert.assertNotNull(responseBook.get("id"));
        Assert.assertEquals("Author",responseBook.get("author"));
        Assert.assertEquals("Title",responseBook.get("title") );
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Assert.assertEquals(thisDate, dateFormat.parse((String) responseBook.get("published")));
        Assert.assertEquals("12345",responseBook.get("isbn"));
    }

   @Test
    public void testBook(){
        HashMap<String,Object> response = toHashMap(  target("books").path(book1_id).request().get());
        Assert.assertNotNull(response);
    }

    @Test
    public void getBooks(){
        Collection<HashMap<String, Object>> response =
                target("books").request().get(new GenericType<Collection<HashMap<String, Object>>>(){});
        Assert.assertNotNull(response);
        Assert.assertEquals(2,response.size());
    }


    @Test
    public void testAddBookExtras(){
        Date thisDate = new Date();
        Response response= addBook("Author","Title", "12345", thisDate, "cover", "Pics");
        Assert.assertEquals(200, response.getStatus());
        HashMap<String, Object> respBook =toHashMap(response);
        Assert.assertNotNull(respBook.get("id"));
        Assert.assertEquals(respBook.get("extra1"),"cover" );
    }

    @Test
    public void getBookAsXml(){
        String output = target("books").request(MediaType.APPLICATION_XML).get().readEntity(String.class);
        XML xml = new XMLDocument(output);
        Assert.assertEquals(2,xml.xpath("//book/author/text()").size());
    }

    @Test
    public void addBookWithNoAuthor(){
        Date thisDate = new Date();
        Response response= addBook(null,"Title", "12345", thisDate,"Ex45");
        Assert.assertEquals( 400,response.getStatus() );
        String message=response.readEntity(String.class);
        Assert.assertTrue( message.contains("author is required field"));
    }

    @Test
    public void addBookWithNoTitle(){
        Date thisDate = new Date();
        Response response= addBook("Author one",null, "12345", thisDate,"Ex45");
        Assert.assertEquals( 400,response.getStatus() );
        String message=response.readEntity(String.class);
        Assert.assertTrue( message.contains("title is required field"));
    }

    @Test
    public void addNoBook(){
        Response response=target("books").request().post(null);
        Assert.assertEquals( 400,response.getStatus() );

    }

    @Test
    public void getBookNotFoundWithMessage(){
        Response response=target("books").path("1").request().get();
        Assert.assertEquals( 404,response.getStatus() );
        String message = response.readEntity(String.class);
        Assert.assertTrue( message.contains("Book 1 is not found"));
    }

   /* @Test
    public void BookEntityTagNotModified(){
        EntityTag entityTag = target("books").path(book1_id).request().get().getEntityTag();
        Assert.assertNotNull(entityTag);
        Response response =target("books").path(book1_id).request().header("If-None-Match", entityTag).get();
        Assert.assertEquals(304, response.getStatus());
    }*/


    @Test
    public void updateBookAuthor(){
        HashMap<String, Object> updates=new HashMap<String, Object>();
        updates.put("author","UpdatedAuthor");
        Entity<HashMap<String, Object>> updateBook = Entity.entity(updates, MediaType.APPLICATION_JSON);
        Response updateResponse = target("books").path(book1_id).request().build("PATCH", updateBook).invoke();
        Assert.assertEquals(200, updateResponse.getStatus());

        Response getResponse = target("books").path(book1_id).request().get();
        HashMap<String, Object> getRespMap = toHashMap(getResponse);
        Assert.assertEquals("UpdatedAuthor", getRespMap.get("author"));
    }

    @Test
    public void updateBookExtra(){
        HashMap<String, Object> updates=new HashMap<String, Object>();
        updates.put("hello","World");
        Entity<HashMap<String, Object>> updateBook = Entity.entity(updates, MediaType.APPLICATION_JSON);
        Response updateResponse = target("books").path(book1_id).request().build("PATCH", updateBook).invoke();
        Assert.assertEquals(200, updateResponse.getStatus());

        Response getResponse = target("books").path(book1_id).request().get();
        HashMap<String, Object> getRespMap = toHashMap(getResponse);
        Assert.assertEquals("World", getRespMap.get("hello"));
    }

    @Test
    public void patchMethodOverride(){
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("author", "updatedAuthor");
        Entity<HashMap<String, Object>> updatedEntity = Entity.entity(updates,MediaType.APPLICATION_JSON_TYPE);

        Response updatedResponse = target("books").path(book1_id).queryParam("_method", "PATCH")
                .request().post(updatedEntity);

        Assert.assertEquals(200, updatedResponse.getStatus());
        Response getResponse = target("books").path(book1_id).request().get();
        HashMap<String, Object> getResponseMap = toHashMap(getResponse);
        Assert.assertEquals("updatedAuthor", getResponseMap.get("author"));
    }

    @Test
    public void contentNegotiationExtentions(){
        Response xmlResp = target("books").path(book1_id+".xml").request().get();
        Assert.assertEquals(MediaType.APPLICATION_XML, xmlResp.getHeaderString("Content-Type"));

        Response jsonResp = target("books").path(book2_id+".json").request().get();
        Assert.assertEquals(MediaType.APPLICATION_JSON, jsonResp.getHeaderString("Content-Type"));
    }

    @Test
    public void poweredByHeader(){
        Response resp = target("books").path(book1_id).request().get();
        Assert.assertEquals("Pluralsight", resp.getHeaderString("X-Powered-By"));

        Response emptyResp = target("books").request().get();
        Assert.assertNull(emptyResp.getHeaderString("X-Powered-By"));
    }
}
