import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

public class Instagram{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public Instagram(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + Instagram.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		Instagram esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new Instagram (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Login User");
				System.out.println("2. Add Photo");
				System.out.println("3. Title Photo");
				System.out.println("4. Tag Photo");
				System.out.println("5. Add Comment");
				System.out.println("6. Like Comment");
				System.out.println("7. Delete Comment");
				System.out.println("8. Create new User");
				System.out.println("9. Set Password");
				System.out.println("10. Add Rating");
				System.out.println("11. Delete Rating");
				System.out.println("12. Search Tag");
				System.out.println("13. Delete Tag");
				System.out.println("14. Delete Photo");
				System.out.println("15. Logout User");

				
				/*
				 * FOLLOW THE SPECIFICATION IN THE PROJECT DESCRIPTION
				 */
				switch (readChoice()){
					case 1: LoginUser(esql); break;
					case 2: AddPhoto(esql); break;
					case 3: EditPhoto(esql); break;
					case 4: TagPhoto(esql); break;
					case 5: AddComment(esql); break;
					case 6: LikeComment(esql); break;
					case 7: DeleteComment(esql); break;
					case 8: CreatePassword(esql); break;
					case 9: SetPassword(esql); break;
					case 10: AddRating(esql); break;
					case 11: DeleteRating(esql); break;
					case 12: SearchTag(esql); break;
					case 13: DeleteTag(esql); break;
					case 14: DeleteUser(esql); break;
					case 15: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice
	
	public static void LoginUser(Instagram esql){//1
		string username;
		string password;
		
		do {
			System.out.print("Enter username: ");
			try {
				username = String.parse(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Username is invalid");
				continue;
			}

			System.out.print("Enter password: ");
			try {
				password = String.parse(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Password is invalid");
				continue;
			}
		}while (true);

	}

	public static void AddPhoto(Instagram esql){//2
		string photoTitle;
		string caption;
		
		do {
			System.out.print("Enter photo title: ");
			try {
				username = String.parse(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Photo title is invalid");
				continue;
			}
			
			System.out.print("Enter caption: ");
			try {
				password = String.parse(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Caption is invalid");
				continue;
			}
		}while (true);

	}
	
	public static void EditPhoto(Instagram esql){//3
		string newPhotoTitle;
		string newCaption;
		
		do {
			System.out.print("Enter new photo title: ");
			try {
				username = String.parse(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("New photo title is invalid");
				continue;
			}
			
			System.out.print("Enter new caption: ");
			try {
				password = String.parse(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("New caption is invalid");
				continue;
			}
		}while (true);
	}
	
	public static void TagPhoto(Instagram esql){//4
		string newPhotoTitle;
		string newCaption;
		
		do {
			System.out.print("Enter new photo title: ");
			try {
				username = String.parse(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("New photo title is invalid");
				continue;
			}
			
			System.out.print("Enter new caption: ");
			try {
				password = String.parse(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("New caption is invalid");
				continue;
			}
		}while (true);
		
	}
	
	public static void AddComment(Instagram esql){//5
		
	}
	
	public static void LikeComment(Instagram esql) throws Exception{//6
		
	}
	
	public static void DeleteComment(Instagram esql){//7
		
	}
	
	public static void CreatePassword(Instagram esql){//8
		
	}
	
	public static void SetPassword(Instagram esql){//9
		
	}
	
	public static void AddRating(Instagram esql){//10
		//
		
	}
	
	public static void DeleteRating(Instagram esql){//11
		//
		
	}

	public static void SearchTag(Instagram esql){//12
		//
		
	}

	public static void DeleteTag(Instagram esql){//13
		//
		
	}

	public static void DeleteUser(Instagram esql){//14
		//
		
	}
	
}
