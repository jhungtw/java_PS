package ps.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SearchDAO {
	
	public ResultSet FlexableQuery(Connection connection,String fqquery) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
       
       
        ResultSet rs = null;
        Statement stmt = null;

        

        stmt = connection.createStatement();
        
        return stmt.executeQuery(fqquery);

    }

}
