package Services;
import Entite.Personne;

import java.sql.SQLException;
import java.util.List;

public interface Iservices <T>{
    void add(T t) throws SQLException;
    void update(T t) throws SQLException;
    void delete(T t) throws SQLException;
    List<T> getAll() throws SQLException;
    //List<T> Search(String searchTerm) throws SQLException;
   // boolean search(T t) throws SQLException;

}


