import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
public class HBMSmain {
public static Connection connectDB() throws SQLException {
    // Replace 'your_password' with your actual MySQL password
    String url = "jdbc:mysql://localhost:3306/hbms_project";
    String user = "root";
    String pass = "ENTER_YOUR_OWN_SQL_PASSWORD"; 
    return DriverManager.getConnection(url, user, pass);
}
public static void updateBedCount(String type) {
    String sql = "UPDATE rooms SET vacant_beds = vacant_beds - 1 WHERE room_type = ? AND vacant_beds > 0";

    try (Connection conn = connectDB(); 
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, type); // This replaces the first '?' with 'ICU'
        int rowsUpdated = pstmt.executeUpdate(); // This actually sends the command to MySQL
        if (rowsUpdated > 0) {
            System.out.println("Success: Database updated!");
        } else {
            System.out.println("Alert: No beds left in " + type);
        }
    } catch (SQLException e) {
        System.out.println("SQL Error: " + e.getMessage());
    }
}
public static int getLiveVacancy(String type) {
    String sql = "SELECT vacant_beds FROM rooms WHERE room_type = ?";
    try (Connection conn = connectDB(); 
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, type);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("vacant_beds"); // Return the REAL number from SQL
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0; 
}
public static void releaseBedCount(String type) {
    String sql = "UPDATE rooms SET vacant_beds = vacant_beds + 1 WHERE room_type = ? AND vacant_beds < total_beds";
    
    try (Connection conn = connectDB(); 
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, type);
        pstmt.executeUpdate();
        System.out.println("Bed released in Database!");
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
static ArrayList<Room> roomList = new ArrayList<>();
   public static void main(String[] args){
    refreshRoomData();
    //Window making
        JFrame frame= new JFrame("HBMS project");
        frame.setSize(600,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Layout checking
        frame.setLayout(new FlowLayout());
        //Label adding
        JLabel title = new JLabel("Welcome to HBMS!");
        frame.add(title);
        //Button adding
        JButton patientbtn = new JButton("Patient Access");
        frame.add(patientbtn);
        //ActionListener Adding
        patientbtn.addActionListener(e ->{
            refreshRoomData();
            patientportal();
        });
        //JButton for admin
        JButton adminbtn = new JButton("Admin Access");
        frame.add(adminbtn);
        //Actionlistener for admin
        adminbtn.addActionListener(e ->{
        String input= JOptionPane.showInputDialog(frame,"Enter Password");
        if(input !=null && input.equals("1234")){
            JOptionPane.showMessageDialog(frame,"Permission granted!!");
            adminPortal.adminDb();
        }
        else{
            JOptionPane.showMessageDialog(frame,"Wrong password");
        }
        });
     frame.setVisible(true);
}

public static void patientportal(){
            JFrame patframe = new JFrame("Patient's Portal");
            patframe.setSize(700,700);
            patframe.setLayout(new GridLayout(8, 2, 5, 5));
            JLabel namLabel = new JLabel("Enter Patient's name");
            JTextField name = new JTextField();
            name.setBorder(BorderFactory.createTitledBorder("Patient Full Name"));
            patframe.add(namLabel);
            patframe.add(name);
            JComboBox<String> roomDropdown = new JComboBox<>();
            for(Room r: roomList){
                roomDropdown.addItem(r.type);
            }
            JLabel statusLabel = new JLabel("Select a department to see live vacancy.", SwingConstants.CENTER);
        // Whenever the patient clicks the dropdown, the label updates
            roomDropdown.addActionListener(e -> {
                String selected = (String) roomDropdown.getSelectedItem();
                int vacant = getLiveVacancy(selected); // Fetches real-time data from MySQL
                statusLabel.setText(selected + " currently has " + vacant + " beds available.");
            });
            JLabel display = new JLabel("Select a room to check vacancy");
            //CHECKIN BUTTON
            JButton checkinBtn =new JButton("BOOK A Bed");
            checkinBtn.addActionListener(e->{
            String selectedRoom = (String) roomDropdown.getSelectedItem();
            int currentBeds = getLiveVacancy(selectedRoom);
            if(currentBeds>0){
            updateBedCount(selectedRoom);
            int newCount = getLiveVacancy(selectedRoom);
            display.setText("Success new Bed Count is "+newCount);
            login(name.getText(),selectedRoom,"Check-in");
            JOptionPane.showMessageDialog(patframe, "Bed Booked in " + selectedRoom);
        }   
        else{
            display.setText("Sorry! No beds Available in "+selectedRoom);
            JOptionPane.showMessageDialog(patframe, "Rooms are full");
        }  
    });
        JButton checkoutBtn = new JButton("CHECK OUT!");
        checkoutBtn.addActionListener(e->{
            String selectedRoom = (String) roomDropdown.getSelectedItem();
            releaseBedCount(selectedRoom);
            int currentBeds= getLiveVacancy(selectedRoom);
            display.setText("Patient Successfully CheckedOut from "+selectedRoom +". New Vacancy: "+currentBeds);
            login(name.getText(), selectedRoom, "Check-out");
            JOptionPane.showMessageDialog(patframe,"Thanks for visiting us");
        });
            patframe.add(new JLabel("Select Department:", SwingConstants.CENTER));
            patframe.add(roomDropdown);
            patframe.add(statusLabel);
            patframe.add(checkinBtn);
            patframe.add(checkoutBtn);
            patframe.add(display);
            patframe.setVisible(true);
        
        }
        public static void login(String name,String room,String action){
        String sql="INSERT into patient_records (patient_name,room_type,action_type) values (?,?,?)"; 
            try(Connection conn= connectDB();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, name);
        pstmt.setString(2, room);
        pstmt.setString(3, action);
        pstmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
public static void refreshRoomData(){
    roomList.clear(); // Wipe the old RAM data
    try (Connection conn = connectDB();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM rooms")) {
        
        while (rs.next()) {
            roomList.add(new Room(rs.getString("room_type"), rs.getInt("vacant_beds")));
        }
        System.out.println("Room list synced with Database.");
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}
class Room{
    String type;
    int totalbeds;
    int occupied;

    public Room(String type,int totalbeds){
        this.type=type;
        this.totalbeds=totalbeds;
        this.occupied=0;
    }
}


