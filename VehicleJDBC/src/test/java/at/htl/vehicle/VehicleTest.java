package at.htl.vehicle;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class VehicleTest {

    public static final String DRIVER_STRING = "org.apache.derby.jdbc.ClientDriver";
    static final String CONNECTION_STRING = "jdbc:derby://localhost:1527/db;create=true";
    static final String USER="app";
    static final String PASSWORD="app";
    private static Connection conn;

    @BeforeClass
    public static void initJdbc() {
        try {
            Class.forName(DRIVER_STRING);
            conn = DriverManager.getConnection(CONNECTION_STRING, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Verbindung zur Datenbank nicht möglich:\n" + e.getMessage() + "\n");
            System.exit(1);
        }

        try{
            Statement stmt = conn.createStatement();

            String sql = "CREATE TABLE vehicle (" +
                    "id int constraint vehicle_pk primary key generated always as identity (start with 1, increment by 1)," +
                    "brand varchar(255) not null," +
                    "type varchar(255) not null)";

            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void dml(){
        int countInserts = 0;
        try {
            Statement stmt = conn.createStatement();
            String sql = "insert into vehicle (brand,type) values ('Opel','Commodore')";
            countInserts += stmt.executeUpdate(sql);
            sql = "insert into vehicle (brand,type) values ('Opel','Kapitän')";
            countInserts += stmt.executeUpdate(sql);
            sql = "insert into vehicle (brand,type) values ('Opel','Kadett')";
            countInserts += stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        assertThat(countInserts,is(3));


        try {
            PreparedStatement pstmt = conn.prepareStatement("Select id,brand,type from vehicle");
            ResultSet rs = pstmt.executeQuery();

            rs.next();
            assertThat(rs.getString("BRAND"),is("Opel"));
            assertThat(rs.getString("TYPE"),is("Commodore"));
            rs.next();
            assertThat(rs.getString("BRAND"),is("Opel"));
            assertThat(rs.getString("TYPE"),is("Kapitän"));
            rs.next();
            assertThat(rs.getString("BRAND"),is("Opel"));
            assertThat(rs.getString("TYPE"),is("Kadett"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void teardownJdbc(){
        try{
            conn.createStatement().execute("DROP TABLE Vehicle");
            System.out.println("Tabelle Vehicle gelöscht");
        } catch (SQLException e) {
            System.out.println("Tabelle Vehicle konnte nicht gelöscht werden:\n"+e.getMessage()+"\n");
        }
        try {
            if (conn !=null || !conn.isClosed()){
                conn.close();
                System.out.println("Goodbye");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
