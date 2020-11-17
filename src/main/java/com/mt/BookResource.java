package com.mt;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import org.apache.commons.codec.digest.DigestUtils;
import org.glassfish.jersey.server.ManagedAsync;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.*;
import java.util.Collection;

@Path("/books")
public class BookResource {

    //BookDao dao = new BookDao();
    @Context
    BookDao dao;

    @Context
    Request request;

    @GET
    @Produces({"application/json;qs=1", "application/xml;qs=0.5"})
    @ManagedAsync
    public void getBooks(@Suspended final AsyncResponse response) {
//        return (dao.getBooks());
        ListenableFuture<Collection<Book>> future = dao.getBooksAsync();
        Futures.addCallback(future, new FutureCallback<Collection<Book>>() {

             public void onSuccess(Collection<Book> books) {

                 response.resume(books);

            }


             public void onFailure(Throwable throwable) {
                response.resume(throwable);
            }
        });
    }

    @PoweredBy("Pluralsight")
    @Path("/{id}")
    @GET
    @Produces({"application/json;qs=1", "application/xml;qs=0.5"})
    @ManagedAsync
    public void getBook(@PathParam("id") String id, @Suspended final AsyncResponse response) {
        //return (dao.getBook(id));
        ListenableFuture<Book> future = dao.getBookAsync(id);
        Futures.addCallback(future, new FutureCallback<Book>() {
            @Override
            public void onSuccess(Book book) {
                response.resume(book);
               /* EntityTag entityTag = generateEntityTag(book);
                final Response.ResponseBuilder rb = request.evaluatePreconditions(entityTag);
                if (rb != null) {
                    response.resume(rb.build());
                } else {
                    response.resume(Response.ok().tag(entityTag).entity(book).build());
                }*/
            }

            @Override
            public void onFailure(Throwable throwable) {
                response.resume(throwable);
            }
        });
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ManagedAsync
    public void addBook(@Valid @NotNull Book book, @Suspended final AsyncResponse response) {
//        response.resume(dao.addBook(book));
        ListenableFuture<Book> future = dao.addBookAsync(book);

        Futures.addCallback(future, new FutureCallback<Book>() {
            @Override
            public void onSuccess(Book addedBook) {
                response.resume(addedBook);
            }


            @Override
            public void onFailure(Throwable throwable) {
                response.resume(throwable);
            }
        });
    }

@Path("/{id}")
@PATCH
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ManagedAsync
public void updateBook(@PathParam("id") String id, Book book, @Suspended final AsyncResponse response){
    ListenableFuture<Book> future = dao.updateBookAsync(id, book);
    Futures.addCallback(future, new FutureCallback<Book>() {
        @Override
        public void onSuccess(Book updatedBook) {
            response.resume(updatedBook);
        }
        @Override
        public void onFailure(Throwable throwable) {
            response.resume(throwable);
        }
    });

}


    EntityTag generateEntityTag(Book book) {
//        return new EntityTag(DigestUtils.md5Hex(book.toString().replaceAll ("^\"|\"$", "")));
        return new EntityTag(
                DigestUtils.md2Hex(book.getAuthor()
                        +book.getTitle()
                        +book.getIsbn()
                        +book.getPublished()
                        +book.getExtras()
                ));
    }

}
