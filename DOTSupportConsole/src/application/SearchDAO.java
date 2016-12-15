package application;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SearchDAO {
	
	public ResultSet FlexableQuery(Connection connection,String fqquery) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        final String HYBRIS_DRIVER_CLASS = "de.hybris.vjdbc.VirtualDriver";
        //private  String HYBRIS_DRIVER_URL = "jdbc:hybris:flexiblesearch:http://172.24.41.104:9001/virtualjdbc/service";
        //private  String HYBRIS_DRIVER_URL = "jdbc:hybris:flexiblesearch:http://backoffice.totalwine.com/virtualjdbc/service";

        //private  String HYBRIS_USER = "jhung";
        //private  String HYBRIS_PASSWORD = "hhj1101";
        ResultSet rs = null;
        Statement stmt = null;

        

        stmt = connection.createStatement();
        
        return stmt.executeQuery(fqquery);

    }

}
