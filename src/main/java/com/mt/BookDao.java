package com.mt;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
public class BookDao {

    private final ListeningExecutorService service;

    private final Map<String, Book> books;

    public BookDao() {
        books = new ConcurrentHashMap<String, Book>();
        service=MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
    }



    Collection<Book> getBooks(){
        return books.values();
    }

    ListenableFuture<Collection<Book>> getBooksAsync(){
        ListenableFuture<Collection<Book>> future=service.submit(
                new Callable() {
                    @Override
                    public Object call() throws Exception {
                        return getBooks();
                    }
                });
        return future;
    }



    Book getBook(String id)throws BookNotFoudException{

        if(books.containsKey(id)){
            return books.get(id);
        }else{
            throw new BookNotFoudException("Book "+id+" is not found");
        }
    }

    /**
     *
     * @param id
     * @return
     */
    ListenableFuture<Book> getBookAsync(final String id){
        ListenableFuture<Book> future=service.submit(
                new Callable() {
                    @Override
                    public Object call() throws Exception {
                        return getBook(id);
                    }
                });
        return future;
    }


    Book addBook(Book book){
        book.setId(UUID.randomUUID().toString());
        books.put(book.getId(), book);
        return(book);
    }


    ListenableFuture<Book> addBookAsync(final Book book){
        ListenableFuture<Book> future=
                service.submit(new Callable<Book>() {
            public Book call() throws Exception{
                return addBook(book);
            }
        });
        return future;
    }

    Book updateBook(String id, Book updates) throws BookNotFoudException {
        if(books.containsKey(id)){
            Book book = books.get(id);
            if(null!=updates.getAuthor()){
                book.setAuthor(updates.getAuthor());
            }

            if(null!=updates.getAuthor()){
                book.setAuthor(updates.getAuthor());
            }

            if(null!=updates.getTitle()){
                book.setTitle(updates.getTitle());
            }

            if(null!=updates.getIsbn()){
                book.setIsbn(updates.getIsbn());
            }

            if(null!=updates.getPublished()){
                book.setPublished(updates.getPublished());
            }

            if(null!=updates.getExtras()){
                for(String key: updates.getExtras().keySet()){
                    book.setExtras(key, updates.getExtras().get(key));
                }
              }
            books.put(book.getId(), book);
            return book;
        }else{
            throw new BookNotFoudException("Book "+id+" is not found");
        }
    }


    ListenableFuture<Book> updateBookAsync(final String id,final Book book){
        ListenableFuture<Book> future=
                service.submit(new Callable<Book>() {
                    public Book call() throws Exception{
                        return updateBook(id, book);
                    }
                });
        return future;
    }

}
