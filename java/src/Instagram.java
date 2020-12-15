/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


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
import java.util.Scanner;

import java.io.*;
import java.text.SimpleDateFormat;  
import java.util.Date;  
import java.util.Calendar;
import java.sql.*;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import javax.swing.*; 
import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList; 
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

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
	        this._connection.setAutoCommit(false); // ADDED |||||||||||||||||||||||||||||||
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
	public int executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		int val = stmt.executeUpdate (sql);

		this._connection.commit();
		// close the instruction
	    stmt.close ();
	    return val;
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
	 * @return the ResultSet object of query
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public ResultSet executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);
		return rs;
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

			// Variables
			Scanner input = new Scanner(System.in);
			String value;
			String tag;
			String user_id;
			int parent_id;
			
			boolean keepon = true; //initiates main menu
			boolean login = false; //initiates login menu
			boolean user_id_taken = true;
			String username;
			String password;
			String confirmation;
			
			while(login){
				System.out.println("\nWelcome to Instagram! Create a new account or sign in.\n"); // Helps start with user to sign in or create account. 
				System.out.println("---------");
				System.out.println("1. Create account");
				System.out.println("2. Sign in");
				System.out.println("3. Exit");
				switch(readChoice()) {
					case 1:
						System.out.println("Please enter your new username: ");
						username = input.nextLine();
						//CHECK IF user_id TAKEN

						if(!UserExists(esql, username)){
							System.out.println("Please enter your new password: ");
							password = input.nextLine();
							System.out.println("password: \'" + password + "\'");
							System.out.println("Confirm password: ");
							//CHECK IF passwords match
							confirmation = input.nextLine();
							System.out.println("confirmation: \'" + confirmation + "\'");
							if(!password.equals(confirmation)){
								System.out.println("Passwords do not match. Please try again.");
							}
							else{
								CreateUser(esql, username, password);
								System.out.println("Account created successfully! Returning to login menu.");
							}
						}
						else{
							System.out.println("Username is taken. Please try again.\n");
						}
					break;
					case 2:
						System.out.println("Please enter username: ");
						username = input.nextLine();
						System.out.println("Please enter password: ");
						password = input.nextLine();

						if(CheckValidLogin(esql, username, password)){
							System.out.println("Logged in successfully!\n");
							keepon = true;
							login = false;
						}
					break;
					case 3:
						keepon = false;
						login = false;
					break;
					default:
						System.out.println("\nERROR: Invalid Input.\n"); 
					break;
				}
			}

			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Upload Photo");
				System.out.println("2. Download Photo");
				System.out.println("3. Search for User"); // based on titles, tags, ratings
				System.out.println("4. Follow User");
				System.out.println("5. Generate News Feed");
				System.out.println("6. Tag Photo");
				System.out.println("7. Search for Photos"); // on tags, ratings, dates, or publishing users
				System.out.println("8. Comment on Photo");
				System.out.println("9. Tag User to Photo");
				System.out.println("10. Most Popular Users"); // All time not only User's followers
				System.out.println("11. Most Popular Photos"); // All time not only User's follower's posts
				System.out.println("12. EXIT");
				
				/*
				 * FOLLOW THE SPECIFICATION IN THE PROJECT DESCRIPTION
				 */
				switch (readChoice()){
					case 1: // UploadPhoto takes file name from user and if file exists gathers other info for new post.  
						System.out.print("\nEnter file name of photo to upload:\n");
						String filename = input.nextLine();
						UploadPhoto(esql, user, filename, input); // CHANGE TO USERNAME LATER
					break;
					
					case 2: //DownloadPhoto

					break;
					
					case 3: // SearchForUser Searches for a photo based on a photo's title, tags, or ratings
						System.out.print("\nWould you like to search for User based on\n1) Photo Title\n2) Tags\n3) Ratings\nPlease enter an integer(1-3)\n");
						value = input.nextLine();
						int choice = Integer.parseInt(value);
						if(choice < 1 || choice > 3) { // Make sure we have a valid choice
							System.out.print("\nInvalid choice: Returning to menu.\n");
							break;
						}
						else if(choice == 1) { //search user based on photo's title
							System.out.print("\nPlease enter the photo title: \n");
							String title = input.nextLine();
							System.out.print("\n");
							SearchForUserTitle(esql, title);
						}
						else if(choice == 2) { //search user based on photo's tag
							System.out.print("\nPlease enter the tag: \n");
							tag = input.nextLine();
							System.out.print("\n");
							SearchForUserTag(esql, tag);
						}
						else { //search user based on photo
							System.out.print("\nPlease enter a rating: \n");
							value = input.nextLine();
							int rating = Integer.parseInt(value);
							System.out.print("\n");
							SearchForUserRating(esql, rating);
						}

					break;
					
					case 4: // FollowUser allows user signed in to follow another user
						System.out.print("\nPlease enter the username of the user you'd like to follow:\n");
						value = input.nextLine();
						if(UserExists(esql, value)) { // Helper function checks if user exists in database
							if(AlreadyFollows(esql, user, value)) { // Helper function to see if user already follows the person
								System.out.print("\nYou already follow " + value + ". Returning to menu.\n");
								break;
							}
							FollowUser(esql, user, value); // Function follows the user 
						}
						else {
							System.out.print("The user \"" + value + "\" does not exist. Returning to menu.\n"); 
						}
					break;
					
					case 5: //GenerateNewsFeed
						GenerateNewsFeed(esql, user); // CHANGE TO USERNAME
					break;
					
					case 6: //TagPhoto
						System.out.print("\nPlease enter the photo ID:\n");
						value = input.nextLine();
						parent_id = Integer.parseInt(value);
						System.out.print("\nPlease enter the photo tag you would like to add:\n");
						tag = input.nextLine();

						if (ParentExists(esql, parent_id)) { // ParentExists makes sure the photo id or parent_id is valid 
							if (!PhotoTagExists(esql, parent_id, tag)) { // Checks if the tag already exists on the given post
								TagPhoto(esql, parent_id, tag); // Adds tag to table for given parent_id
							} else {
								System.out.println("Tag \'" + tag + "\' already exists on this photo. Returning to menu.\n");
							}
						} else {
							System.out.println("\nInvalid photo ID. Returning to menu.\n");
						}
						 //ViewPhotoTags(esql, parent_id); for testing
					break;
					
					case 7: // SearchForPhotos searches for photos based on photo tags, ratings, and the date published
						System.out.print("\nWould you like to search for Photo based on\n1) Tags\n2) Ratings\n3) Date\n4) Publishing User\nPlease enter an integer(1-4)\n");
						value = input.nextLine();
						choice = Integer.parseInt(value);
						if(choice < 1 || choice > 4) {
							System.out.print("\nInvalid choice: Returning to menu.\n");
							break;
						}
						else if(choice == 1) { // Searches based on tag
							System.out.print("\nPlease enter the photo tag: \n");
							tag = input.nextLine();
							System.out.print("\n");
							SearchForPhotoTag(esql, tag);
						}
						
						else if(choice == 2) { // Searches based on rating
							System.out.print("\nPlease enter a rating: \n");
							value = input.nextLine();
							int rating = Integer.parseInt(value);
							System.out.print("\n");
							SearchForPhotoRating(esql, rating);
						}
						
						else if(choice == 3) { // Searches for photo based on date
							System.out.print("\nPlease enter a date(MM-dd-yyyy): \n");
							String dates = input.nextLine();
							System.out.print("\n");
							SearchForPhotoDate(esql, dates);
						}
						
						else { //choice == 4 Searches for photo based on user who published the photo
							System.out.print("\nPlease enter the publishing user: \n");
							user_id = input.nextLine();
							System.out.print("\n");
							SearchForPhotoUser(esql, user_id);
						}

					break;
					
					case 8: //CommentOnPhoto
						System.out.print("\nEnter the photo ID for the photo you would like to comment on:\n");
						parent_id = Integer.parseInt(in.readLine());
						System.out.println("\nEnter your comment:\n");
						String content = input.nextLine();
						System.out.print("\n");
						CommentOnPhoto(esql, parent_id, content);

					break;
					
					case 9: //TagUserToPhoto
						System.out.print("\nEnter the photo ID of the photo you wish to tag a user on:\n");
						parent_id = Integer.parseInt(in.readLine());
						System.out.println("\nEnter the user ID of the user you want to tag:\n");
						user_id = input.nextLine();
						System.out.print("\n");
						if (ParentExists(esql, parent_id)) {
							if (!UserTagExists(esql, parent_id, user_id)) {
								TagUserToPhoto(esql, parent_id, user_id);
							} else {
								System.out.println("User \'" + user_id + "\' is already tagged on this photo. Returning to menu.\n");
							}
						} else {
							System.out.println("\nInvalid photo ID. Returning to menu.\n");
						}
						// ViewUserTags(esql, parent_id);
					break;
					
					case 10: //MostPopularUsers 
						MostPopularUsers(esql);
					break;
					
					case 11: //MostPopularPhotos
						MostPopularPhotos(esql);
					break;
					
					case 12: keepon = false; 

					break;
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
	}//end readChoice3

	public static void UploadPhoto(Instagram esql, String user_id, String filename, Scanner input){//1
		try {

	    	// BufferedImage bImage = ImageIO.read(new File("/home/clee/cs179/Instagram/sample_photo.jpg"));
      		// 		ByteArrayOutputStream bos = new ByteArrayOutputStream();
      		// 		ImageIO.write(bImage, "jpg", bos );
      		// 		byte [] data = bos.toByteArray();
      		// 		System.out.println(data);
	      
	      	File file = new File("/home/clee/cs179/Instagram/" + filename + ".jpg");
	     	FileInputStream fis = new FileInputStream(file);
	      	Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:9998/clee_DB");

	      	System.out.print("\nEnter the title of the photo:\n");
	      	String title = input.nextLine();

	      	int parent_id = GetNextPhotoId(esql);

	      	

	      	Date current_date = Calendar.getInstance().getTime();
	      	String timestamp = current_date.toString();
	      	// System.out.println(timestamp);
	      	SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
	      	String date = format.format(current_date);

	      	PreparedStatement ps = con.prepareStatement("INSERT INTO Photo VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
		    ps.setInt(1, parent_id);
		    ps.setString(2, user_id);
		    ps.setString(3, title);
		    ps.setInt(4, 0);
		    ps.setString(5, date);
		    ps.setString(6, timestamp);
		    ps.setInt(7, 0);
		    ps.setBinaryStream(8, fis, (int)file.length());
		    
		    ps.executeUpdate();
		    ps.close();
		    fis.close();

		    System.out.println("\nSuccess! Your photo has been uploaded :)\n ");
		} catch (Exception e){
			System.err.println(e.getMessage());
		}
	}
	
	public static void DownloadPhoto(Instagram esql){//2
		
	}
	
	public static void SearchForUserTitle(Instagram esql, String title){//3
		try {
			ResultSet rs = esql.executeQuery("SELECT user_id FROM Photo WHERE title = \'" + title + "\'");
			if (rs.isBeforeFirst()){
				System.out.println("Search Results for title \"" + title + "\":");
				while(rs.next()){
					String user_id = rs.getString(1);
					System.out.println(user_id + "\n");
				}
			}else{
				System.out.println("No results found. Returning to menu.\n");
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}	
	}
	
	public static void SearchForUserTag(Instagram esql, String tag){//3
		try {
			ResultSet rs = esql.executeQuery("SELECT user_id FROM Photo WHERE parent_id IN (SELECT parent_id FROM PhotoTags WHERE tag = \'" + tag + "\')"); 
			if (rs.isBeforeFirst()){
				System.out.println("Search Results for tag \"" + tag + "\":");
				while(rs.next()){
					String user_id = rs.getString(1);
					System.out.println(user_id + "\n");
				}
			}else{
				System.out.println("No results found. Returning to menu.\n");
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}		
	}

	public static void SearchForUserRating(Instagram esql, int rating){//3
		try {
			ResultSet rs = esql.executeQuery("SELECT user_id FROM Photo WHERE rating = \'" + rating + "\'"); 
			if (rs.isBeforeFirst()){
				System.out.println("Search Results for rating \"" + rating + "\":");
				while(rs.next()){
					String user_id = rs.getString(1);
					System.out.println(user_id + "\n");
				}
			}else{
				System.out.println("No results found. Returning to menu.\n");
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}		
	}

	public static void FollowUser(Instagram esql, String follower, String follows){
		try {
			int following_id = GetNextFollowId(esql);
			int changes = esql.executeUpdate("INSERT INTO Followings VALUES(\'" + following_id + "\', \'" + follower + "\', \'" + follows + "\')");
			changes = changes + esql.executeUpdate("UPDATE Users SET follow_count = follow_count + 1 WHERE user_id = \'" + follows + "\'");
			System.out.println("Changes = " + changes + "\n");
			CheckNumFollower(esql, follows);
		} catch(Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
	}

	public static void GeneratePhotoInfo(Instagram esql, String parent_id){ // HELPER FOR GENERATE NEWS FEED
		try{
			ResultSet base_info = esql.executeQuery("SELECT title, user_id, dates, rating, image FROM Photo WHERE parent_id = \'" + parent_id + "\'");
			ResultSet photo_tags = esql.executeQuery("SELECT tag FROM PhotoTags WHERE parent_id = \'" + parent_id + "\'");
			ResultSet tagged_users = esql.executeQuery("SELECT user_id FROM UserTags WHERE parent_id = \'" + parent_id + "\'");
			ResultSet comments = esql.executeQuery("SELECT user_id, content FROM Comments WHERE parent_id = \'" + parent_id + "\'");
			byte[] image = new byte[0];


			if (base_info.isBeforeFirst()){
				base_info.next();
				String title = base_info.getString(1);
				String user_id = base_info.getString(2);
				String dates = base_info.getString(3);
				String rating = base_info.getString(4);
				image = base_info.getBytes(5);
				System.out.println("\nPhoto title: " + title + "\nPublished by: " + user_id + "\nDate: " + dates);
			}
			
			/*
			JFrame frame = new JFrame("Feed");

			ByteArrayInputStream bis = new ByteArrayInputStream(image);
			BufferedImage bImg = ImageIO.read(bis);
			ImageIcon imageIcon = new ImageIcon(bImg);
			JLabel jLabel = new JLabel(imageIcon, SwingConstants.CENTER);
			frame.getContentPane().add(jLabel, BorderLayout.CENTER);
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 

			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
			*/

			System.out.println("\nTagged users:");

			if (tagged_users.isBeforeFirst()){
				while(tagged_users.next()){
					String user = tagged_users.getString(1);
					System.out.println(user + " ");
				}
			}

			System.out.println("\nPhoto tags:");

			if (photo_tags.isBeforeFirst()){
				while(photo_tags.next()){
					String tag = photo_tags.getString(1);
					System.out.println("#" + tag + " ");
				}
			}

			System.out.println("\nComments:");
			System.out.println("----------------------------------------------------------------------");

			if (comments.isBeforeFirst()){
				while(comments.next()){
					String user_id = comments.getString(1);
					String content = comments.getString(2);
					System.out.println("User: " + user_id + "\n");
					System.out.println("-" + content);
					System.out.println("----------------------------------------------------------------------\n");
				}
			}

		}catch (Exception e){
			System.err.println(e.getMessage());
		}
	}
	
	public static void GenerateNewsFeed(Instagram esql, String user_id) throws Exception{//5
		try{
			ResultSet rs = esql.executeQuery("SELECT parent_id FROM Photo WHERE user_id IN (SELECT follows from Followings WHERE follower = \'" + user_id + "\')ORDER BY rating DESC;");
			if (rs.isBeforeFirst()){
				while(rs.next()){
					String parent_id = rs.getString(1);
					GeneratePhotoInfo(esql, parent_id);
				}
			}else{
				System.out.println("No photos found. Returning to menu.\n");
			}
		}catch (Exception e){
			System.err.println(e.getMessage());
		}
	}
	
	public static void TagPhoto(Instagram esql, int parent_id, String tag){//6
		try {
			ResultSet rs = esql.executeQuery("SELECT parent_id, tag FROM PhotoTags WHERE parent_id = " + parent_id);
			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					if (tag == rs.getString(2)) {
						System.out.println("\nTag \'" + tag + "\' already exists on this photo. Returning to menu.\n");
						break;
					}
				}
				int photo_tag_id = GetNextPhotoTagId(esql);
				int changes = esql.executeUpdate("INSERT INTO PhotoTags VALUES(" + parent_id + ", " + photo_tag_id + ", \'" + tag + "\')");
				ViewPhotoTags(esql, parent_id); // FIXME REMOVE
			} else {
				System.out.println("\nPhoto ID is invalid. Returning to menu.\n");
			}

		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void SearchForPhotoTag(Instagram esql, String tag){//7
		try {
			ResultSet rs = esql.executeQuery("SELECT parent_id FROM PhotoTags WHERE tag = \'" + tag + "\'");
			if (rs.isBeforeFirst()){
				System.out.println("Search Results for tag \'" + tag + "\':");
				while(rs.next()){
					int parent_id = rs.getInt(1);
					GetPostInfoFromParent(esql, parent_id);
				}
			}else{
				System.out.println("No results found. Returning to menu.\n");
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}		
	}

	public static void SearchForPhotoRating(Instagram esql, int rating) {
		try {
			ResultSet rs = esql.executeQuery("SELECT parent_id FROM Photo WHERE rating = \'" + rating + "\'");
			if (rs.isBeforeFirst()){
				System.out.println("Search Results for rating \'" + rating + "\':");
				while(rs.next()){
					int parent_id = rs.getInt(1);
					GetPostInfoFromParent(esql, parent_id);
				}
			}else{
				System.out.println("No results found. Returning to menu.\n");
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}	
	}

	public static void SearchForPhotoDate(Instagram esql, String dates) {
		try {
			ResultSet rs = esql.executeQuery("SELECT parent_id FROM Photo WHERE dates = \'" + dates + "\'");
			if (rs.isBeforeFirst()){
				System.out.println("Search Results for date \'" + dates + "\':");
				while(rs.next()){
					int parent_id = rs.getInt(1);
					GetPostInfoFromParent(esql, parent_id);
				}
			}else{
				System.out.println("No results found. Returning to menu.\n");
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	}

	public static void SearchForPhotoUser(Instagram esql, String user_id) {
		try {
			ResultSet rs = esql.executeQuery("SELECT parent_id FROM Photo WHERE user_id = \'" + user_id + "\'");
			if (rs.isBeforeFirst()){
				System.out.println("Search Results for publishing user \'" + user_id + "\':");
				while(rs.next()){
					int parent_id = rs.getInt(1);
					GetPostInfoFromParent(esql, parent_id);
				}
			}else{
				System.out.println("No results found. Returning to menu.\n");
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
	}
	
	public static void CommentOnPhoto(Instagram esql, int parent_id, String content){//8
		try {
			ResultSet rs = esql.executeQuery("SELECT parent_id, user_id FROM Photo WHERE parent_id = " + parent_id);
			if (rs.isBeforeFirst()){
				rs.next();
				String user_id = rs.getString(2);
				int comment_id = GetNextCommentId(esql);

				int changes = esql.executeUpdate("INSERT INTO Comments VALUES(" + parent_id + ", " + comment_id + ", \'" + user_id + "\', \'" + content + "\')"); //pid, cid, userid, content
				// ViewComments(esql, parent_id); FIXME REMOVE
			}else{
				System.out.println("\nInvalid photo ID. Returning to menu.\n");
			}
		}catch(Exception e){
			System.err.println(e.getMessage());
		}						
	}
	
	public static void TagUserToPhoto(Instagram esql, int parent_id, String user_id){//9
		try {
			ResultSet rs = esql.executeQuery("SELECT user_id FROM UserTags WHERE parent_id = " + parent_id);
			if (rs.isBeforeFirst()) {
				while (rs.next()) {
					System.out.print("in while");
					if (user_id == rs.getString(1)) {
						System.out.println("\nUser tag for \'" + user_id + "\' already exists on this photo. Returning to menu.\n");
						return;
					}
				}
				int user_tag_id = GetNextUserTagId(esql);
				int changes = esql.executeUpdate("INSERT INTO UserTags VALUES(" + parent_id + ", " + user_tag_id + ", \'" + user_id + "\')");
				// ViewUserTags(esql, parent_id); // FIXME REMOVE
			} else {
				System.out.println("\nPhoto ID is invalid. Returning to menu.\n");
			}

		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}
	
	public static void MostPopularUsers(Instagram esql){//10
		try {
      		ResultSet rs = esql.executeQuery("SELECT user_id, follow_count FROM Users ORDER BY follow_count DESC");
      		if(rs.isBeforeFirst()) {
      			System.out.println("\nUsers with most followers: \n");
      			while(rs.next()) {
      				String user_id = rs.getString(1);
      				int follow_count = rs.getInt(2);
      				System.out.println(user_id + " --- " + follow_count + " followers\n");
      			}
      			System.out.println("\n");
        	}else {
        		System.out.println("\nNo results found. Returning to menu.\n");
        	}
    	} catch(Exception e) {
      		System.err.println(e.getMessage());
    	}
	}

	public static void MostPopularPhotos(Instagram esql){//11
		try {
      		ResultSet rs = esql.executeQuery("SELECT parent_id, title, user_id, dates, views FROM Photo ORDER BY views DESC");
      		if(rs.isBeforeFirst()) {
      			System.out.println("\nMost popular photos: \n");
      			while(rs.next()) {
      				//int parent_id = rs.getInt(1); CHECK LATER FOR ADDING COMMENTS/TAGS
      				String title = rs.getString(2);
      				String user_id = rs.getString(3);
      				String dates = rs.getString(4);
      				int views = rs.getInt(5);
      				System.out.println("Title: " + title + "\nPosted by: " + user_id + "\nDate: " + dates + "\nTotal views: " + views + "\n");
      			}
      			System.out.println("\n");
        	}else {
        		System.out.println("\nNo results found. Returning to menu.\n");
        	}
    	} catch(Exception e) {
      		System.err.println(e.getMessage());
    	}
	}

	//*****************************************
	//			HELPER FUNCTIONS
	//*****************************************

	//helper function to check if user already follows other user
	public static boolean AlreadyFollows(Instagram esql, String user, String follows) {
		try {
			ResultSet rs = esql.executeQuery("SELECT following_id FROM Followings WHERE follower = \'" + user + "\' AND follows = \'" + follows + "\'");
			if(rs.isBeforeFirst()) {
				return true;
			}
			return false;
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		return false;
	}

	//testing helper function to check number of followers
	public static void CheckNumFollower(Instagram esql, String user_id) {
		try {
			ResultSet rs = esql.executeQuery("SELECT follow_count FROM Users WHERE user_id = \'" + user_id + "\'");
			rs.next();
			int numFollowers = rs.getInt(1);
			System.out.println("User \'" + user_id + "\' has " + numFollowers + " followers.\n");
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	// helper function that gets info to output from the parent (title, user_id, dates, views, rating)
	public static void GetPostInfoFromParent(Instagram esql, int parent_id) {
		try {
			ResultSet rs = esql.executeQuery("SELECT title, user_id, dates, views, rating FROM Photo WHERE parent_id = " + parent_id);
			rs.next();
			String title = rs.getString(1);
      		String user_id = rs.getString(2);
      		String dates = rs.getString(3);
      		int views = rs.getInt(4);
      		int rating = rs.getInt(5);
      		System.out.println("Title: " + title + "\nPosted by: " + user_id + "\nDate: " + dates + "\nTotal views: " + views + "\nRating: " + rating + "\n");

		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	//helper function for to check if user_id already exists within our User table
	public static boolean UserExists(Instagram esql, String user_id){
		try {
			ResultSet rs = esql.executeQuery("SELECT user_id FROM Users WHERE user_id = \'" + user_id + "\'");
			if(rs.isBeforeFirst()) {
				return true;
			}
			return false;
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		return false;
	}

	//helper function to get the next id for the Followings table
	public static int GetNextFollowId(Instagram esql) {
		try {
			ResultSet rs = esql.executeQuery("SELECT following_id FROM Followings WHERE following_id = (SELECT MAX(following_id) FROM Followings)");
			if(rs.isBeforeFirst()) {
				rs.next();
				int following_id = rs.getInt(1) + 1;
				return following_id;
			}
			else {
				return 1;
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		return -1;
	}

	//helper function to get the next id for the Comments table
	public static int GetNextCommentId(Instagram esql) {
		try {
			ResultSet rs = esql.executeQuery("SELECT comment_id FROM Comments WHERE comment_id = (SELECT MAX(comment_id) FROM Comments)");
			if(rs.isBeforeFirst()) {
				rs.next();
				int comment_id = rs.getInt(1) + 1;
				return comment_id;
			}
			else {
				return 1;
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		return -1;		
	}

	//helper function to get the next id for the PhotoTags table
	public static int GetNextPhotoTagId(Instagram esql) {
		try {
			ResultSet rs = esql.executeQuery("SELECT photo_tag_id FROM PhotoTags WHERE photo_tag_id = (SELECT MAX(photo_tag_id) FROM PhotoTags)");
			if(rs.isBeforeFirst()) {
				rs.next();
				int photo_tag_id = rs.getInt(1) + 1;
				return photo_tag_id;
			}
			else {
				return 1;
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		return -1;		
	}	

	//helper function for TagUserToPhoto()
	public static int GetNextUserTagId(Instagram esql) {
		try {
			ResultSet rs = esql.executeQuery("SELECT user_tag_id FROM UserTags WHERE user_tag_id = (SELECT MAX(user_tag_id) FROM UserTags)");
			if(rs.isBeforeFirst()) {
				rs.next();
				int user_tag_id = rs.getInt(1) + 1;
				return user_tag_id;
			}
			else {
				return 1;
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		return -1;		
	}

	//helper function to check if parent already exists (parent_id in Photo)
	public static Boolean ParentExists(Instagram esql, int parent_id) {
		try {
			ResultSet rs = esql.executeQuery("SELECT parent_id FROM Photo WHERE parent_id = " + parent_id);
			if(rs.isBeforeFirst()) {
				return true;
			} else {
				return false;
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		return false;
	}

	//helper function to check if photo tag already exists
	public static Boolean PhotoTagExists(Instagram esql, int parent_id, String tag) {
		try {
			ResultSet rs = esql.executeQuery("SELECT tag FROM PhotoTags WHERE parent_id = " + parent_id + " AND tag = \'" + tag + "\'");
			if(rs.isBeforeFirst()) {
				return true;
			} else {
				return false;
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		return false;
	}	

	//helper function to check if user tag already exists
	public static Boolean UserTagExists(Instagram esql, int parent_id, String user_id) {
		try {
			ResultSet rs = esql.executeQuery("SELECT user_tag_id FROM UserTags WHERE parent_id = " + parent_id + " AND user_id = \'" + user_id + "\'");
			if(rs.isBeforeFirst()) {
				return true;
			} else {
				return false;
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		return false;
	}

	//helper function for login menu: creates new user
	public static void CreateUser(Instagram esql, String username, String password){
		try {
			esql.executeUpdate("INSERT INTO Users VALUES(\'" + username + "\', \'" + password + "\', 0)");	
			ViewUser(esql, username);
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	}

	//helper function for login menu: checks if user login is correct(username and password match)
	public static boolean CheckValidLogin(Instagram esql, String username, String password) {
		try{
			ResultSet rs = esql.executeQuery("SELECT user_id FROM Users WHERE user_id = \'" + username + "\' AND password = \'" + password + "\'");
			
			if(rs.isBeforeFirst()){
				rs.next();
				System.out.println(rs.getString(1));
				return true;
			}
			else {
				return false;
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		return false;
	}

  //helper function to get the next id for the PhotoTags table
	public static int GetNextPhotoId(Instagram esql) {
		try {
			ResultSet rs = esql.executeQuery("SELECT parent_id FROM Photo WHERE parent_id = (SELECT MAX(parent_id) FROM Photo)");
			if(rs.isBeforeFirst()) {
				rs.next();
				int parent_id = rs.getInt(1) + 1;
				return parent_id;
			}
			else {
				return 1;
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		return -1;		
	}


	//********************************************
	//		TEST FUNCTIONS
	//********************************************

	//test function to display output for user
	public static void ViewUser(Instagram esql, String username) { // FIXME REMOVE
		try {
			ResultSet rs = esql.executeQuery("SELECT user_id FROM Users WHERE user_id = \'" + username + "\'");
			if(rs.isBeforeFirst()) {
				while(rs.next()) {
					System.out.println(rs.getString(1));
				}
			} else {
				System.out.println("\nruh roh\n");
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	return;
	}

	//test function to display output for comments
	public static void ViewComments(Instagram esql, int parent_id) { // FIXME REMOVE
		try {
			ResultSet rs = esql.executeQuery("SELECT content FROM Comments WHERE parent_id = " + parent_id);
			if(rs.isBeforeFirst()) {
				while(rs.next()) {
					System.out.println(rs.getString(1));
				}
			} else {
				System.out.println("\nruh roh\n");
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		return;
	}
	
	//test function to display output for photo tags
	public static void ViewPhotoTags(Instagram esql, int parent_id) { // FIXME REMOVE
		try {
			ResultSet rs = esql.executeQuery("SELECT photo_tag_id, tag FROM PhotoTags WHERE parent_id = " + parent_id);
			if(rs.isBeforeFirst()) {
				while(rs.next()) {
					System.out.print(rs.getInt(1) + " ");
					System.out.println(rs.getString(2));
				}
			} else {
				System.out.println("\nruh roh\n");
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
		return;
	}

	//test function to display output for user tags
	public static void ViewUserTags(Instagram esql, int parent_id) { // FIXME REMOVE
		try {
			ResultSet rs = esql.executeQuery("SELECT user_tag_id, user_id FROM UserTags WHERE parent_id = " + parent_id);
			if(rs.isBeforeFirst()) {
				while(rs.next()) {
					System.out.print(rs.getInt(1) + " ");
					System.out.println(rs.getString(2));
				}
			} else {
				System.out.println("\nruh roh\n");
			}
		} catch(Exception e) {
			System.err.println(e.getMessage());
		}
	return;
	}
}
