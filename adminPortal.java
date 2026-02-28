import java.sql.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
public class adminPortal {
    public static void adminDb() {
        JFrame AdminFrame= new JFrame("Admin Portal");
        AdminFrame.setSize(400,400); 
        AdminFrame.setLayout(new GridLayout(4, 1, 10, 10));
        JButton view = new JButton("View Patient's Live Record");
        view.addActionListener(e->{
            PatientsTable();
        });
        AdminFrame.add(view);
        JButton edit = new JButton("Edit Rooms");
        AdminFrame.add(edit); 
        edit.addActionListener(e->{manageRooms();});   
        JButton fileBtn = new JButton("Import External Hospital Data");
        fileBtn.addActionListener(e -> Files());
        AdminFrame.add(fileBtn);
        JButton graphBtn = new JButton("Analysis");
        graphBtn.addActionListener(e-> Analysis());
        AdminFrame.add(graphBtn);
        AdminFrame.setVisible(true);
        }
    public static void PatientsTable(){
        JFrame table = new JFrame("Patient's record");
        table.setSize(800,400);
        
        String[] columns = {"ID","NAME","ROOM","ACTION","TIMESTAMP"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable Ptable = new JTable(model);

        try(Connection conn = HBMSmain.connectDB();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM patient_records ORDER BY log_time DESC")) {
        
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("id"),
                rs.getString("patient_name"),
                rs.getString("room_type"),
                rs.getString("action_type"),
                rs.getTimestamp("log_time")
            });
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
    }

    // 3. Put table in ScrollPane and add to frame
    table.add(new JScrollPane(Ptable));
    table.setVisible(true);
}  
    public static void manageRooms() {
        JFrame editFrame = new JFrame("Department Manager");
        editFrame.setSize(400, 300);
        editFrame.setLayout(new GridLayout(4, 2, 10, 10));

        JTextField typeInput = new JTextField();
        JTextField totalInput = new JTextField();
        JButton addBtn = new JButton("Add/Update Room");
        JButton deleteBtn = new JButton("Delete Room");

        editFrame.add(new JLabel("Room Name:"));
        editFrame.add(typeInput);
        editFrame.add(new JLabel("Total Beds:"));
        editFrame.add(totalInput);
        editFrame.add(addBtn);
        editFrame.add(deleteBtn);

        // LOGIC TO ADD OR UPDATE
        addBtn.addActionListener(e -> {
            String type = typeInput.getText().toUpperCase();
            String total = totalInput.getText();
            
            String sql = "INSERT INTO rooms (room_type, total_beds, vacant_beds) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE total_beds = ?, vacant_beds = ?";
            
            try (Connection conn = HBMSmain.connectDB();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, type);
                pstmt.setInt(2, Integer.parseInt(total));
                pstmt.setInt(3, Integer.parseInt(total)); 
                pstmt.setInt(4, Integer.parseInt(total));
                pstmt.setInt(5, Integer.parseInt(total));
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(editFrame, "Room " + type + " Updated!");
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        // LOGIC TO DELETE
        deleteBtn.addActionListener(e -> {
            String type = typeInput.getText().toUpperCase();
            try (Connection conn = HBMSmain.connectDB();
                PreparedStatement pstmt = conn.prepareStatement("DELETE FROM rooms WHERE room_type = ?")) {
                pstmt.setString(1, type);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(editFrame, "Room Deleted!");
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        editFrame.setVisible(true);
    } 
    public static void Files(){
        JFileChooser fileChooser= new JFileChooser();
        int response = fileChooser.showOpenDialog(null);
        if(response==JFileChooser.APPROVE_OPTION){
        File file = fileChooser.getSelectedFile();   
        String sql = "INSERT INTO patient_records (patient_name,room_type,action_type) values (?,?,?)";
        try(Connection conn = HBMSmain.connectDB();
        PreparedStatement psttmt = conn.prepareStatement(sql);
        BufferedReader br = new BufferedReader(new FileReader(file))){
        conn.setAutoCommit(false);
        String line;
        while((line=br.readLine())!=null){
            String[] data = line.split(",");
            if(data.length==3){
                psttmt.setString(1,data[0].trim());
                psttmt.setString(2,data[1].trim());
                psttmt.setString(3,data[2].trim());
                psttmt.addBatch();
            }
        }
        psttmt.executeBatch();
        conn.commit();
        JOptionPane.showMessageDialog(null,"DATA UPLOADED");
        }
        catch(Exception e){
        JOptionPane.showMessageDialog(null,"ERROR!");     
        }
        }      
    }
     public static void Analysis(){
            JFrame graph = new JFrame("ANALYSIS");
            graph.setSize(400,800);
            graph.setLayout(new GridLayout(0,1));
            try(Connection conn = HBMSmain.connectDB();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT room_type,total_beds,vacant_beds from rooms"))
            {
                while (rs.next()) {
                String room = rs.getString("room_type");
                int total = rs.getInt("total_beds");
                int vacant = rs.getInt("vacant_beds");
                int occupied = total- vacant;
                JProgressBar bar = new JProgressBar(0,total);
                bar.setValue(occupied);
                bar.setStringPainted(true);
                bar.setString(room+":"+occupied+"/"+total+"Beds Filled");  
                graph.add(bar);  
                }      
            }
        catch(SQLException e){
            JOptionPane.showMessageDialog(null,"DataBase Error");
        }
        graph.setVisible(true); 
       }
    }


