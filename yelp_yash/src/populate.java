

import org.json.*;
import java.sql.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class Business {
    String business_id;
    String city;
    String state;
    String name;
    double stars;
    Business(String business_id, String city, String state, String name, double stars) {
        this.business_id = business_id;
        this.city = city;
        this.state = state;
        this.name = name;
        this.stars = stars;
    }
}

class Attribute {
    String business_id;
    String attribute;
    Attribute(String business_id, String attribute) {
        this.business_id = business_id;
        this.attribute = attribute;
    }
}



class Main_Category {
    String business_id;
    String mainCategory;
    Main_Category(String business_id, String mainCategory) {
        this.business_id = business_id;
        this.mainCategory = mainCategory;
    }
}

class Sub_Category {
    String business_id;
    String subcategory;
    Sub_Category(String business_id, String subcategory) {
        this.business_id = business_id;
        this.subcategory = subcategory;
    }
}






public class populate {

    private static final String DB_USER = "yash";
    private static final String DB_PASS = "yash";
    private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String BUSINESS_JSON_FILE_PATH = "C:\\Users\\yashp\\eclipse-workspace\\populate1.java\\src\\com\\populate1\\yelp_business.json";
    private static final String USER_JSON_FILE_PATH = "C:\\Users\\yashp\\eclipse-workspace\\populate1.java\\src\\com\\populate1\\yelp_user.json";
    private static final String REVIEW_JSON_FILE_PATH = "C:\\Users\\yashp\\eclipse-workspace\\populate1.java\\src\\com\\populate1\\yelp_review.json";

    public static List<Business> businesses = new ArrayList();
    public static List<Main_Category> mainCategories = new ArrayList();
    public static List<Sub_Category> subCategories = new ArrayList();
    public static List<Attribute> attributes = new ArrayList();
    public static HashSet<String> mainCategoriesHash = new HashSet();

    public static void run() {
        try {
            init();            
            insertBusinessFileData();
           parseAndInsertUser();
           parseAndInsertReview();
            
        } catch (SQLException ex) {
            Logger.getLogger(populate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(populate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(populate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private static void init() throws SQLException, ClassNotFoundException {
        System.out.println("+++++++++++++ START initialization +++++++++++++");
        addMainCategories();
        cleanTable();
        System.out.println("------------- END initialization -------------");
    }

    private static void addMainCategories() {
        System.out.println("Set MainCategories...");
        mainCategoriesHash.add("Active Life");
        mainCategoriesHash.add("Arts & Entertainment");
        mainCategoriesHash.add("Automotive");
        mainCategoriesHash.add("Car Rental");
        mainCategoriesHash.add("Cafes");
        mainCategoriesHash.add("Beauty & Spas");
        mainCategoriesHash.add("Convenience Stores");
        mainCategoriesHash.add("Dentists");
        mainCategoriesHash.add("Doctors");
        mainCategoriesHash.add("Drugstores");
        mainCategoriesHash.add("Department Stores");
        mainCategoriesHash.add("Education");
        mainCategoriesHash.add("Event Planning & Services");
        mainCategoriesHash.add("Flowers & Gifts");
        mainCategoriesHash.add("Food");
        mainCategoriesHash.add("Health & Medical");
        mainCategoriesHash.add("Home Services");
        mainCategoriesHash.add("Home & Garden");
        mainCategoriesHash.add("Hospitals");
        mainCategoriesHash.add("Hotels & Travel");
        mainCategoriesHash.add("Hardware Stores");
        mainCategoriesHash.add("Grocery");
        mainCategoriesHash.add("Medical Centers");
        mainCategoriesHash.add("Nurseries & Gardening");
        mainCategoriesHash.add("Nightlife");
        mainCategoriesHash.add("Restaurants");
        mainCategoriesHash.add("Shopping");
        mainCategoriesHash.add("Transportation");
    }
    
    private static void cleanTable() throws SQLException, ClassNotFoundException {
        try (Connection connection = getConnect()) {
            String sql;
            PreparedStatement preparedStatement;
            
            System.out.println("Clean Review table...");
            sql = "DELETE FROM Review";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
            
            System.out.println("Clean Attribute table...");
            sql = "DELETE FROM Attribute";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            
            System.out.println("Clean Sub_Category table...");
            sql = "DELETE FROM Sub_Category";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            
            System.out.println("Clean Main_Category table...");
            sql = "DELETE FROM Main_Category";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            
            System.out.println("Clean Business table...");
            sql = "DELETE FROM Business";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
    }


    
    
    private static void parseBusiness() {
        File file = new File(BUSINESS_JSON_FILE_PATH);
        try (FileReader fileReader = new FileReader(file);
             BufferedReader reader = new BufferedReader(fileReader);
             Connection connection = getConnect();){
            String line;
            while ((line = reader.readLine()) != null) {
                JSONObject obj = new JSONObject(line);
                String business_id = obj.getString("business_id");
                String city = obj.getString("city");
                String state = obj.getString("state");
                String name = obj.getString("name");
                Double stars = obj.getDouble("stars");
                JSONArray arr = obj.getJSONArray("categories");
                for (int i = 0; i < arr.length(); i++) {
                    String category = arr.getString(i);
                    if (mainCategoriesHash.contains(category)) {
                        mainCategories.add(new Main_Category(business_id, category));
                    }
                    else {
                        subCategories.add(new Sub_Category(business_id, category));
                    }
                }
                businesses.add(new Business(business_id, city, state, name, stars));
                
                JSONObject attributes1 = obj.getJSONObject("attributes");
                Iterator<?> keys1 = attributes1.keys();
                while (keys1.hasNext()) {
                    String key1 = (String) keys1.next();
                    StringBuilder sb1 = new StringBuilder(key1);
                    if (attributes1.get(key1) instanceof JSONObject) {
                        JSONObject attributes2 = attributes1.getJSONObject(key1);
                        Iterator<?> keys2 = attributes2.keys();
                        while (keys2.hasNext()) {
                            String key2 = (String) keys2.next();
                            StringBuilder sb2 = new StringBuilder(key2);
                            sb2.append("_");
                            sb2.append(attributes2.get(key2));
                            attributes.add(new Attribute(business_id, sb1.toString() + "_" + sb2.toString()));
                        }
                    }
                    else {
                        sb1.append("_");
                        sb1.append(attributes1.get(key1));
                        attributes.add(new Attribute(business_id, sb1.toString()));
                    }
                }
                
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException ex) {
            Logger.getLogger(populate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(populate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private static void parseAndInsertUser() throws IOException, SQLException, ClassNotFoundException {
        System.out.println("Parsing user.json file and inserting data into User table...");
        File file = new File(USER_JSON_FILE_PATH);
        try (FileReader fileReader = new FileReader(file);
             BufferedReader reader = new BufferedReader(fileReader);
             Connection connection = getConnect();) {
            String line;
            String sql;
            PreparedStatement preparedStatement = null;
            while ((line = reader.readLine()) != null) {
                // parse user data
                JSONObject obj = new JSONObject(line);
                String user_id = obj.getString("user_id");
                String name = obj.getString("name");
                String yelping_since = obj.getString("yelping_since");
                int review_count = obj.getInt("review_count");
                double average_stars = obj.getDouble("average_stars");
                int friend_count = obj.getJSONArray("friends").length();
                int votes = obj.getJSONObject("votes").getInt("useful")
                          + obj.getJSONObject("votes").getInt("funny")
                          + obj.getJSONObject("votes").getInt("cool");
                // insert user data
                sql = "INSERT INTO User (user_id, name, yelping_since, review_count, average_stars, friend_count, votes) VALUES (?, ?, ?, ?, ?, ?, ?)";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, user_id);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, yelping_since);
                preparedStatement.setInt(4, review_count);
                preparedStatement.setDouble(5, average_stars);
                preparedStatement.setInt(6, friend_count);
                preparedStatement.setInt(7, votes);
                preparedStatement.executeUpdate();
                preparedStatement.close();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
   
    private static void parseAndInsertReview() throws IOException, SQLException, ClassNotFoundException {
        System.out.println("Parsing review.json file and inserting data into Review table...");
        File file = new File(REVIEW_JSON_FILE_PATH);

        try (FileReader fileReader = new FileReader(file);
             BufferedReader reader = new BufferedReader(fileReader);
             Connection connection = getConnect();) {
            String line;
            String sql;
            PreparedStatement preparedStatement = null;
            while ((line = reader.readLine()) != null) {
                // parse review data
                JSONObject obj = new JSONObject(line);
                String review_id = obj.getString("review_id");
                String business_id = obj.getString("business_id");
                String user_id = obj.getString("user_id");
                String review_date = obj.getString("date");
                int stars = obj.getInt("stars");
                int votes = obj.getJSONObject("votes").getInt("useful")
                          + obj.getJSONObject("votes").getInt("funny")
                          + obj.getJSONObject("votes").getInt("cool");
        
                sql = "INSERT INTO Review (review_id, business_id, user_id, review_date, stars, votes) VALUES (?, ?, ?, ?, ?, ?)";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, review_id);
                preparedStatement.setString(2, business_id);
                preparedStatement.setString(3, user_id);
                preparedStatement.setString(4, review_date);
                preparedStatement.setInt(5, stars);
                preparedStatement.setInt(6, votes);
                preparedStatement.executeUpdate();
                preparedStatement.close();
            }
                             
        } catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    

        
    
    public static Connection getConnect() throws SQLException, ClassNotFoundException{
        System.out.println("Checking JDBC...");
        Class.forName(JDBC_DRIVER);
        System.out.println("Connecting to database...");
        return DriverManager.getConnection(ORACLE_URL, DB_USER, DB_PASS);
    }

    public static void insertBusinessFileData() throws SQLException, ClassNotFoundException {
        try (Connection connection = getConnect()){

            System.out.println("Creating statement...");

            System.out.println("Parsing Business.json file...");
            parseBusiness();

            String sql;
            PreparedStatement preparedStatement;

            System.out.println("Insert data into Business table...");
            sql = "INSERT INTO Business (business_id, city, state, name, stars) VALUES (?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            for (Business b: businesses) {
                preparedStatement.setString(1, b.business_id);
                preparedStatement.setString(2, b.city);
                preparedStatement.setString(3, b.state);
                preparedStatement.setString(4, b.name);
                preparedStatement.setDouble(5, b.stars);
                preparedStatement.executeUpdate();
            }
            preparedStatement.close();

            System.out.println("Insert data into Main_Category table...");
            sql = "INSERT INTO Main_Category (business_id, mainCategory) VALUES (?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            for (Main_Category m: mainCategories) {
                preparedStatement.setString(1, m.business_id);
                preparedStatement.setString(2, m.mainCategory);
                preparedStatement.executeUpdate();
            }
            preparedStatement.close();

            System.out.println("Insert data into Sub_Category table...");
            sql = "INSERT INTO Sub_Category (business_id, subCategory) VALUES (?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            for (Sub_Category s: subCategories) {
                preparedStatement.setString(1, s.business_id);
                preparedStatement.setString(2, s.subcategory);
                preparedStatement.executeUpdate();
            }
            preparedStatement.close();
            
            System.out.println("Insert data into Attribute table...");
            sql = "INSERT INTO Attribute (business_id, attribute) VALUES (?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            for (Attribute a: attributes) {
                preparedStatement.setString(1, a.business_id);
                preparedStatement.setString(2, a.attribute);
                preparedStatement.executeUpdate();
            }
            preparedStatement.close();
        }
    }

    
    public static void main(String[] args) {
       // System.out.println("Hello world."); 
        populate.run();

    }
}