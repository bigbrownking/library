import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;
import java.sql.*;
public class Main {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Scanner sc = new Scanner(System.in);
        System.out.println("What do you want?");
        System.out.println("1. Show all books;\n" +
                "2. Take book;\n" +
                "3. Return book;\n" +
                "4. Show my books\n"+
                "5. Exit;");
        int selection = sc.nextInt();//пользователь не қалайтынын сан ретінде енгізеді
        ResultSet resultSet = null;
        ResultSet resultSet2 = null;
        ResultSet resultSet3 = null;
        ResultSet resultSet4 = null;
        ResultSet resultSet5 = null;
        ResultSet resultSet6 = null;
        Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();//конекшн для MySQL точнее драйвер
        while (true) {
            switch (selection) {
                case 1://егер 1 енгізсе
                    try (Connection conn = DriverManager.getConnection(DBEngine.url, DBEngine.user, DBEngine.password)) {//конесттан өтсе
                        Statement statement = conn.createStatement();// создаем будущий SQL запрос
                        resultSet = statement.executeQuery("SELECT * FROM books");//это SQL запрос
                        System.out.println("All available books: ");
                        while(resultSet.next()) {//пока есть строки в нашей таблице books
                            int id = resultSet.getInt(1);//бұл жерде таблицадағы коллоналардың мәндерін сақтаймыз
                            String title = resultSet.getString(2);
                            String author = resultSet.getString(3);
                            int year = resultSet.getInt(4);
                            int amount = resultSet.getInt(5);
                            double price = resultSet.getDouble(6);
                            System.out.printf("%d. %s, %s, %d, count: %d, %f tenge \n", id, title, author, year, amount, price);//консольге шығарамыз
                        }
                    }
                    catch (Exception ex) {//конекшн өтпей қалмаса
                        System.out.println("Connection failed...");
                        System.out.println(ex);
                    }
                    break;
                case 5://егер 5 енгізсе
                    System.out.print("Good bye!");
                    System.exit(1);//программа жабылады
                    break;
                case 2://егер ә енгізсе
                    System.out.println("Enter book's id:");
                    int book_id = sc.nextInt();//пользователь кітаптын айдишкасын енгізед
                    try (Connection conn = DriverManager.getConnection(DBEngine.url, DBEngine.user, DBEngine.password)) {//конекшн жасап аламыз
                        Statement statement = conn.createStatement();//2 запрос құрастырамыз
                        Statement statement1 = conn.createStatement();
                        resultSet2 = statement.executeQuery("SELECT * FROM books");//books кестесінен барлық жолдарды қарастырамыз
                        resultSet3 = statement1.executeQuery("SELECT * FROM RESERVE"); //reverse то же
                        boolean exist=false;//
                        while(resultSet2.next()) {//books кестесіндегі жолдар болғанша
                            if (resultSet2.getInt(1) == book_id) {//егер пользовательдн айдишкасы кітаптағы айдишкамен сәйкес келсе
                                 exist = true;//значит ол бар
                                Statement statement2 = conn.createStatement();
                                statement2.executeUpdate("UPDATE books SET amount = amount -1");//значит колво - 1 істеймз
                                String title = resultSet2.getString(2);//барлық  коллоналардан мәндерн аламыз
                                String author = resultSet2.getString(3);
                                int year = resultSet2.getInt(4);
                                int amount = resultSet2.getInt(5);
                                double price = resultSet2.getDouble(6);
                                boolean flag = false;
                                while (resultSet3.next()) {//егер ол резервте болса
                                    if (resultSet3.getInt(1) == book_id) {
                                        flag = true;
                                    }
                                }
                                if (flag == true) {//егер резервте болса
                                    statement = conn.createStatement();
                                    statement.executeUpdate("UPDATE RESERVE SET amount  = amount +1");//то колво + 1
                                } else {//болмаса
                                    PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO RESERVE(id, title, author, published_year, amount, price) Values (?, ?,?,?,?,?)");//жанадан енгіземз
                                    preparedStatement.setInt(1, book_id);//барлық коллоналарға енгіземіз , жана жолға
                                    preparedStatement.setString(2, title);
                                    preparedStatement.setString(3, author);
                                    preparedStatement.setInt(4, year);
                                    preparedStatement.setInt(5, 1);
                                    preparedStatement.setDouble(6, price);
                                    preparedStatement.executeUpdate();
                                }
                            }
                        }
                        if(exist==false){//егер пользователь кате жасаса
                            System.out.println("Book doesn't exist");
                        }
                                Statement statement3 = conn.createStatement();
                                statement3.executeUpdate("DELETE FROM books WHERE amount <=0");//барлық біткен кітаптарды жоямыз
                        if(exist == true){//пользователь кате жасамаса
                            System.out.println("Added");
                        }
                    }
                    catch (Exception ex) {//конект істей калмаса
                        System.out.println("Connection failed...");
                        System.out.println(ex);
                    }
                    break;
                case 4://4 енгізсе
                    try (Connection conn = DriverManager.getConnection(DBEngine.url, DBEngine.user, DBEngine.password)) {
                        Statement statement = conn.createStatement();
                        resultSet4 = statement.executeQuery("SELECT * FROM reserve");
                        System.out.println("All your books: ");
                        while(resultSet4.next()){//пока резервте жолдар болғанша
                            int id = resultSet4.getInt(1);
                            String title = resultSet4.getString(2);
                            String author = resultSet4.getString(3);
                            int year = resultSet4.getInt(4);
                            int amount = resultSet4.getInt(5);
                            double price = resultSet4.getDouble(6);
                            System.out.printf("%d. %s, %s, %d, count: %d, %f tenge \n", id, title, author, year, amount, price);
                        }
                    }//оларды шығарамыз
                    catch (Exception ex) {//конект істемесе
                        System.out.println("Connection failed...");
                        System.out.println(ex);
                    }
                    break;
                case 3://3 енгізсе
                    System.out.println("Enter book's id:");//122-174 жолдары идентичны с case 2, только наоборот, с резерва в библиотеку отавать
                    int id_of_book = sc.nextInt();
                    try (Connection conn = DriverManager.getConnection(DBEngine.url, DBEngine.user, DBEngine.password)) {
                        Statement statement1 = conn.createStatement();
                        Statement statement2 = conn.createStatement();
                        resultSet5 = statement1.executeQuery("SELECT * FROM RESERVE");
                        resultSet6 = statement2.executeQuery("SELECT * FROM books");
                        boolean exist = false;
                        while(resultSet5.next()) {
                            if (resultSet5.getInt(1) == id_of_book) {
                                exist = true;
                                Statement statement3 = conn.createStatement();
                                statement3.executeUpdate("UPDATE RESERVE SET amount = amount -1");
                                String title = resultSet5.getString(2);
                                String author = resultSet5.getString(3);
                                int year = resultSet5.getInt(4);
                                int amount = resultSet5.getInt(5);
                                double price = resultSet5.getDouble(6);
                                boolean flag = false;
                                while (resultSet6.next()) {
                                    if (resultSet6.getInt(1) == id_of_book) {
                                        flag = true;
                                    }
                                }
                                if (flag == true) {

                                    statement3.executeUpdate("UPDATE books SET amount  = amount +1");
                                } else {
                                    PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO books(book_id, title, name_author, published_year, amount, price) Values (?, ?,?,?,?,?)");
                                    preparedStatement.setInt(1, id_of_book);
                                    preparedStatement.setString(2, title);
                                    preparedStatement.setString(3, author);
                                    preparedStatement.setInt(4, year);
                                    preparedStatement.setInt(5, 1);
                                    preparedStatement.setDouble(6, price);
                                    preparedStatement.executeUpdate();
                                }
                            }
                        }
                            Statement statement4 = conn.createStatement();
                            statement4.executeUpdate("DELETE FROM RESERVE WHERE amount <=0");
                        if(exist == false){
                            System.out.println("Book doesn't exist");
                        }
                        if(exist == true){
                            System.out.println("Given");
                        }
                    }
                    catch (Exception ex) {
                        System.out.println("Connection failed...");
                        System.out.println(ex);
                    }
                    break;
                default:// егер пользователь кате енгизсе
                    System.out.print("Nothing has chosen ");
            }
            System.out.println("What do you want?");
            System.out.println("1. Show all books;\n" +
                    "2. Take book;\n" +
                    "3. Return book;\n" +
                    "4. Show my books\n"+
                    "5. Exit;");
            selection = sc.nextInt();
        }
    }
}