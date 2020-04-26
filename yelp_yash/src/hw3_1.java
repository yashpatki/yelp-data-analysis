

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import javax.swing.*;
//import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.jdesktop.swingx.JXDatePicker;
import java.awt.Color;
import java.awt.SystemColor;
/**
 *
 * @author 
 */



public class hw3_1 extends javax.swing.JFrame {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    private static HashSet<String> mainCategoriesSet = new HashSet();
    private static HashSet<String> subCategoriesSet = new HashSet();
    private static HashSet<String> attributesSet = new HashSet();
    private static StringBuilder mainCategoriesString = new StringBuilder();
    private static StringBuilder subCategoriesString = new StringBuilder();
    private static StringBuilder attributesString = new StringBuilder();
    /**
     * Creates new form main
     */
    public hw3_1() {
        initComponents();
        try {
            init();
        } catch (SQLException ex) {
            Logger.getLogger(hw3_1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(hw3_1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    public void ERROR(String msg) {
        queryTextArea.append(msg);
    }

    private void init() throws SQLException, ClassNotFoundException {
        System.out.println("+++init+++");
        try (Connection connection = populate.getConnect();){
            StringBuilder sql = new StringBuilder();
            PreparedStatement preparedStatement;
            ResultSet rs;
            
            //init radioButton
            ButtonGroup group = new ButtonGroup();
            group.add(businessRadioButton);
            group.add(userRadioButton);
            
            //init mainCategory
            sql.append("SELECT DISTINCT mainCategory").append("\n")
               .append("FROM MainCategory").append("\n")
               .append("ORDER BY mainCategory");
            preparedStatement = connection.prepareStatement(sql.toString());
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String mainCategoryName = rs.getString(rs.findColumn("mainCategory"));
                JCheckBox mc = new JCheckBox(mainCategoryName);
                
                mc.addMouseListener(new MouseListener(){                                   
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        try {
                            JCheckBox mc = (JCheckBox) e.getSource();
                            String mainCategory = mc.getText();
                            if (mc.isSelected()) {
                                mainCategoriesSet.add(mainCategory);
                            }
                            else {
                                mainCategoriesSet.remove(mainCategory);
                            }
                            // get mainCategories from hashSet to arrayList
                            mainCategoriesString.setLength(0);
                            Iterator<String> it = mainCategoriesSet.iterator();
                            while (it.hasNext()) {
                              mainCategoriesString.append("'").append(it.next()).append("',");
                            }
                            if (mainCategoriesString.length() > 0) {
                                mainCategoriesString.deleteCharAt(mainCategoriesString.length() - 1);
                            }
                            System.out.println("DEBUG=========== select mainCategories: " + mainCategoriesString.toString());
                            getSubCategories();
                        } catch (SQLException ex) {
                            Logger.getLogger(hw3_1.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(hw3_1.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }
                });
                mCategoryListPanel.add(mc);
            }           
            rs.close();
            preparedStatement.close();
        }
    }
    
    private void getSubCategories() throws SQLException, ClassNotFoundException {
        try (Connection connection = populate.getConnect()) {
            sCategoryListPanel.removeAll();
            System.out.println("Get subCategories...");
            
            StringBuilder sql = new StringBuilder();
            PreparedStatement preparedStatement;
            ResultSet rs;
            
            HashMap<String, Integer> subCategories = new HashMap();
            Iterator<String> mc = mainCategoriesSet.iterator();
            while (mc.hasNext()) {
                sql.setLength(0);
                sql.append("SELECT DISTINCT sc.subCategory").append("\n")
                   .append("FROM SubCategory sc, MainCategory mc").append("\n")
                   .append("WHERE sc.business_id = mc.business_id AND mc.mainCategory = '").append(mc.next()).append("'\n")
                   .append("ORDER BY sc.subCategory");
                preparedStatement = connection.prepareStatement(sql.toString());
                rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    String subCategory = rs.getString(rs.findColumn("subCategory"));
                    if (subCategories.containsKey(subCategory)) {
                        subCategories.put(subCategory, subCategories.get(subCategory) + 1);
                    }
                    else {
                        subCategories.put(subCategory, 1);
                    }
                }
                rs.close();
                preparedStatement.close();
            }
            List<String> subCategoriesList = new ArrayList();        
            for (Map.Entry<String, Integer> entry: subCategories.entrySet()) {
                if (entry.getValue() == mainCategoriesSet.size()) {
                    subCategoriesList.add(entry.getKey());
                }
            }
            Collections.sort(subCategoriesList);
            for (String scName: subCategoriesList) {
                JCheckBox sc = new JCheckBox(scName);
                
                sc.addMouseListener(new MouseListener(){                                   
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JCheckBox sc = (JCheckBox) e.getSource();
                        String subCategory = sc.getText();
                        if (sc.isSelected()) {
                            subCategoriesSet.add(subCategory);
                        }
                        else {
                            subCategoriesSet.remove(subCategory);
                        }
                        subCategoriesString.setLength(0);
                        Iterator<String> it = subCategoriesSet.iterator();
                        while (it.hasNext()) {
                            subCategoriesString.append("'").append(it.next()).append("',");
                        }
                        if (subCategoriesString.length() > 0) {
                            subCategoriesString.deleteCharAt(subCategoriesString.length() - 1);
                        }
                        System.out.println("DEBUG=========== select subCategories: " + subCategoriesString.toString() + "\n");
                        try {
                        	getAttributes();
                        }catch(Exception ex) {
                        	ex.printStackTrace();
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }
                });
                sCategoryListPanel.add(sc);
            }
            sCategoryListPanel.updateUI();
        }
    }
    
    
    private void getAttributes() throws SQLException, ClassNotFoundException {
        try (Connection connection = populate.getConnect()) {
            attributeListPanel.removeAll();
            System.out.println("Get attributes...");
            
            StringBuilder sql = new StringBuilder();
            PreparedStatement preparedStatement;
            ResultSet rs;
            
            HashMap<String, Integer> attributesHash = new HashMap();
            Iterator<String> mc = mainCategoriesSet.iterator();
            Iterator<String> sc = subCategoriesSet.iterator();
            while (mc.hasNext()) {
                while (sc.hasNext()) {
                    sql.setLength(0);
                    sql.append("SELECT a.attribute\n")
                       .append("FROM Attribute a, MainCategory mc, SubCategory sc\n")
                       .append("WHERE a.business_id = mc.business_id AND a.business_id =  sc.business_id")
                       .append(" AND mc.mainCategory = '").append(mc.next()).append("' AND sc.subCategory = '").append(sc.next()).append("'\n")
                       .append("ORDER BY a.attribute\n");
                    System.out.println("DEBUG============== select attributes: " + sql.toString());
                    preparedStatement = connection.prepareStatement(sql.toString());
                    rs = preparedStatement.executeQuery();
                    while (rs.next()) {
                        String attribute = rs.getString(rs.findColumn("attribute"));
                        if (attributesHash.containsKey(attribute)) {
                            attributesHash.put(attribute, attributesHash.get(attribute) + 1);
                        }
                        else {
                            attributesHash.put(attribute, 1);
                        }
                    }
                    rs.close();
                    preparedStatement.close();
                }
            }
            List<String> attributesList = new ArrayList();        
            for (Map.Entry<String, Integer> entry: attributesHash.entrySet()) {
                if (entry.getValue() == mainCategoriesSet.size() * subCategoriesSet.size()) {
                    attributesList.add(entry.getKey());
                }
            }
            Collections.sort(attributesList);
            for (String aName: attributesList) {
                JCheckBox a = new JCheckBox(aName);
                
                a.addMouseListener(new MouseListener(){                                   
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JCheckBox a = (JCheckBox) e.getSource();
                        String attribute = a.getText();
                        if (a.isSelected()) {
                            attributesSet.add(attribute);
                        }
                        else {
                            attributesSet.remove(attribute);
                        }
                        attributesString.setLength(0);
                        Iterator<String> it = attributesSet.iterator();
                        while (it.hasNext()) {
                            attributesString.append("'").append(it.next()).append("',");
                        }
                        if (attributesString.length() > 0) {
                            attributesString.deleteCharAt(attributesString.length() - 1);
                        } 
                        System.out.println("DEBUG=========== attributes: " + attributesString.toString());
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }
                });
                attributeListPanel.add(a);
            }
            attributeListPanel.updateUI();
        }
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        businessPanel = new javax.swing.JPanel();
        businessLabel = new javax.swing.JLabel();
        categoriesPanel = new javax.swing.JPanel();
        mainCategoryPanel = new javax.swing.JPanel();
        categoryLabel = new javax.swing.JLabel();
        mainCategoryScrollPane = new javax.swing.JScrollPane();
        mCategoryListPanel = new javax.swing.JPanel();
        subCategoryPanel = new javax.swing.JPanel();
        subCategoryLabel = new javax.swing.JLabel();
        subCategoryScrollPane = new javax.swing.JScrollPane();
        sCategoryListPanel = new javax.swing.JPanel();
        attributePanel = new javax.swing.JPanel();
        attributeLabel = new javax.swing.JLabel();
        attributeScrollPane = new javax.swing.JScrollPane();
        attributeListPanel = new javax.swing.JPanel();
        businessSearchForPanel = new javax.swing.JPanel();
        businessSearchForLabel = new javax.swing.JLabel();
        businessSearchForComboBox = new javax.swing.JComboBox<>();
        reviewResultPanel = new javax.swing.JPanel();
        reviewPanel = new javax.swing.JPanel();
        reviewLabel = new javax.swing.JLabel();
        innerReviewPanel = new javax.swing.JPanel();
        reviewPanel1 = new javax.swing.JPanel();
        reviewLabel2 = new javax.swing.JLabel();
        reviewLabel1 = new javax.swing.JLabel();
        fromTextField = new javax.swing.JTextField();
        toTextField = new javax.swing.JTextField();
        reviewPanel2 = new javax.swing.JPanel();
        reviewLabel3 = new javax.swing.JLabel();
        reviewLabel4 = new javax.swing.JLabel();
        starComboBox = new javax.swing.JComboBox<>();
        starTextField = new javax.swing.JTextField();
        reviewPanel3 = new javax.swing.JPanel();
        reviewLabel5 = new javax.swing.JLabel();
        reviewLabel6 = new javax.swing.JLabel();
        votesComboBox = new javax.swing.JComboBox<>();
        votesTextField = new javax.swing.JTextField();
        resultPanel = new javax.swing.JPanel();
        resultLabel = new javax.swing.JLabel();
        resultScrollPane = new javax.swing.JScrollPane();
        resultTable = new javax.swing.JTable();
        userPanel = new javax.swing.JPanel();
        usersLabel = new javax.swing.JLabel();
        userAttributesPanel = new javax.swing.JPanel();
        memberSinceLabel = new javax.swing.JLabel();
        userComboBoxPanel1 = new javax.swing.JPanel();
        userValuePanel1 = new javax.swing.JPanel();
        userValueLabel1 = new javax.swing.JLabel();
        memberSinceTextField = new javax.swing.JTextField();
        reviewCountLabel = new javax.swing.JLabel();
        userComboBoxPanel2 = new javax.swing.JPanel();
        reviewCountComboBox = new javax.swing.JComboBox<>();
        userValuePanel2 = new javax.swing.JPanel();
        userValueLabel2 = new javax.swing.JLabel();
        reviewCountTextField = new javax.swing.JTextField();
        numberOfFriendsLabel = new javax.swing.JLabel();
        userComboBoxPanel3 = new javax.swing.JPanel();
        numberOfFriendsComboBox = new javax.swing.JComboBox<>();
        userValuePanel3 = new javax.swing.JPanel();
        userValueLabel3 = new javax.swing.JLabel();
        numberOfFriendsTextField = new javax.swing.JTextField();
        averageStarsLabel = new javax.swing.JLabel();
        userComboBoxPanel4 = new javax.swing.JPanel();
        averageStarsComboBox = new javax.swing.JComboBox<>();
        userValuePanel4 = new javax.swing.JPanel();
        userValueLabel4 = new javax.swing.JLabel();
        averageStarsTextField = new javax.swing.JTextField();
        numberOfVotesLabel = new javax.swing.JLabel();
        userComboBoxPanel5 = new javax.swing.JPanel();
        numberOfVotesComboBox = new javax.swing.JComboBox<>();
        userValuePanel5 = new javax.swing.JPanel();
        userValueLabel5 = new javax.swing.JLabel();
        numberOfVotesTextField = new javax.swing.JTextField();
        userSearchForPanel = new javax.swing.JPanel();
        userSearchForLabel = new javax.swing.JLabel();
        userSearchForComboBox = new javax.swing.JComboBox<>();
        queryPanel = new javax.swing.JPanel();
        queryScrollPane = new javax.swing.JScrollPane();
        queryTextArea = new javax.swing.JTextArea();
        queryButton = new javax.swing.JButton();
        queryButton.setForeground(UIManager.getColor("Button.foreground"));
        businessRadioButton = new javax.swing.JRadioButton();
        userRadioButton = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        mainPanel.setBackground(SystemColor.activeCaption);
        mainPanel.setToolTipText("hw3_1");
        mainPanel.setPreferredSize(new java.awt.Dimension(1200, 800));
        mainPanel.setLayout(new java.awt.GridLayout(2, 2));

        businessPanel.setBackground(SystemColor.activeCaption);
        businessPanel.setToolTipText("Business");
        businessPanel.setLayout(new java.awt.BorderLayout());

        businessLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        businessLabel.setText("Business");
        businessPanel.add(businessLabel, java.awt.BorderLayout.PAGE_START);

        categoriesPanel.setLayout(new java.awt.GridLayout(1, 3));

        mainCategoryPanel.setLayout(new java.awt.BorderLayout());

        categoryLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        categoryLabel.setText("Category");
        mainCategoryPanel.add(categoryLabel, java.awt.BorderLayout.PAGE_START);

        mCategoryListPanel.setLayout(new java.awt.GridLayout(0, 1));

        //for (int i = 0; i < 20; i++) {
            //    mCategoryListPanel.add(new JCheckBox("aaa"));
            //    if (i == 18) {
                //        ((JCheckBox) mCategoryListPanel.getComponent(i)).setSelected(true);
                //    }
            //}

        mainCategoryScrollPane.setViewportView(mCategoryListPanel);

        mainCategoryPanel.add(mainCategoryScrollPane, java.awt.BorderLayout.CENTER);

        categoriesPanel.add(mainCategoryPanel);

        subCategoryPanel.setLayout(new java.awt.BorderLayout());

        subCategoryLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        subCategoryLabel.setText("Sub-category");
        subCategoryPanel.add(subCategoryLabel, java.awt.BorderLayout.PAGE_START);

        sCategoryListPanel.setLayout(new java.awt.GridLayout(0, 1));

     

        subCategoryScrollPane.setViewportView(sCategoryListPanel);

        subCategoryPanel.add(subCategoryScrollPane, java.awt.BorderLayout.CENTER);

        categoriesPanel.add(subCategoryPanel);

        attributePanel.setLayout(new java.awt.BorderLayout());

        attributeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        attributeLabel.setText("Attribute");
        attributePanel.add(attributeLabel, java.awt.BorderLayout.PAGE_START);

        attributeListPanel.setLayout(new java.awt.GridLayout(0, 1));

    

        attributeScrollPane.setViewportView(attributeListPanel);

        attributePanel.add(attributeScrollPane, java.awt.BorderLayout.CENTER);

        categoriesPanel.add(attributePanel);

        businessPanel.add(categoriesPanel, java.awt.BorderLayout.CENTER);

        businessSearchForPanel.setBackground(SystemColor.activeCaption);
        businessSearchForPanel.setForeground(new java.awt.Color(0, 102, 0));

        businessSearchForLabel.setBackground(SystemColor.activeCaption);
        businessSearchForLabel.setForeground(SystemColor.text);
        businessSearchForLabel.setText("Search for");

        businessSearchForComboBox.setBackground(SystemColor.text);
        businessSearchForComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select AND, OR between attributes", "AND", "OR" }));
        businessSearchForComboBox.setToolTipText("");

        javax.swing.GroupLayout businessSearchForPanelLayout = new javax.swing.GroupLayout(businessSearchForPanel);
        businessSearchForPanel.setLayout(businessSearchForPanelLayout);
        businessSearchForPanelLayout.setHorizontalGroup(
            businessSearchForPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(businessSearchForPanelLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(businessSearchForPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(businessSearchForComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(businessSearchForLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(272, Short.MAX_VALUE))
        );
        businessSearchForPanelLayout.setVerticalGroup(
            businessSearchForPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(businessSearchForPanelLayout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addComponent(businessSearchForLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(businessSearchForComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        businessPanel.add(businessSearchForPanel, java.awt.BorderLayout.PAGE_END);

        mainPanel.add(businessPanel);

        reviewResultPanel.setBackground(SystemColor.activeCaption);

        reviewPanel.setLayout(new java.awt.BorderLayout());

        reviewLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        reviewLabel.setText("Review");
        reviewPanel.add(reviewLabel, java.awt.BorderLayout.PAGE_START);

        innerReviewPanel.setLayout(new java.awt.GridLayout(3, 1));

        reviewLabel2.setText("to");

        reviewLabel1.setText("from");

        fromTextField.setText("yyyy-MM-dd");

        toTextField.setText("yyyy-MM-dd");

        javax.swing.GroupLayout reviewPanel1Layout = new javax.swing.GroupLayout(reviewPanel1);
        reviewPanel1.setLayout(reviewPanel1Layout);
        reviewPanel1Layout.setHorizontalGroup(
            reviewPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reviewPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(reviewPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(reviewLabel1)
                    .addComponent(reviewLabel2))
                .addGap(49, 49, 49)
                .addGroup(reviewPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(fromTextField)
                    .addComponent(toTextField))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        reviewPanel1Layout.setVerticalGroup(
            reviewPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, reviewPanel1Layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addGroup(reviewPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reviewLabel1)
                    .addComponent(fromTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(reviewPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reviewLabel2)
                    .addComponent(toTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38))
        );

        innerReviewPanel.add(reviewPanel1);

        reviewLabel3.setText("star");

        reviewLabel4.setText("value");

        starComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "=, >, <", "=", ">", "<" }));

        starTextField.setToolTipText("");

        javax.swing.GroupLayout reviewPanel2Layout = new javax.swing.GroupLayout(reviewPanel2);
        reviewPanel2.setLayout(reviewPanel2Layout);
        reviewPanel2Layout.setHorizontalGroup(
            reviewPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reviewPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(reviewPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(reviewLabel3)
                    .addComponent(reviewLabel4))
                .addGap(43, 43, 43)
                .addGroup(reviewPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(starTextField)
                    .addComponent(starComboBox, 0, 85, Short.MAX_VALUE))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        reviewPanel2Layout.setVerticalGroup(
            reviewPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reviewPanel2Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(reviewPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reviewLabel3)
                    .addComponent(starComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(reviewPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reviewLabel4)
                    .addComponent(starTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(38, Short.MAX_VALUE))
        );

        innerReviewPanel.add(reviewPanel2);

        reviewLabel5.setText("votes");

        reviewLabel6.setText("value");

        votesComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "=, >, <", "=", ">", "<" }));

        javax.swing.GroupLayout reviewPanel3Layout = new javax.swing.GroupLayout(reviewPanel3);
        reviewPanel3.setLayout(reviewPanel3Layout);
        reviewPanel3Layout.setHorizontalGroup(
            reviewPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reviewPanel3Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(reviewPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(reviewLabel5)
                    .addComponent(reviewLabel6))
                .addGap(42, 42, 42)
                .addGroup(reviewPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(votesComboBox, 0, 82, Short.MAX_VALUE)
                    .addComponent(votesTextField))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        reviewPanel3Layout.setVerticalGroup(
            reviewPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reviewPanel3Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(reviewPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reviewLabel5)
                    .addComponent(votesComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(reviewPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(reviewLabel6)
                    .addComponent(votesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        innerReviewPanel.add(reviewPanel3);

        reviewPanel.add(innerReviewPanel, java.awt.BorderLayout.CENTER);

        resultPanel.setLayout(new java.awt.BorderLayout());

        resultLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        resultLabel.setText("Result");
        resultPanel.add(resultLabel, java.awt.BorderLayout.PAGE_START);

        resultScrollPane.setViewportView(resultTable);

        resultPanel.add(resultScrollPane, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout reviewResultPanelLayout = new javax.swing.GroupLayout(reviewResultPanel);
        reviewResultPanel.setLayout(reviewResultPanelLayout);
        reviewResultPanelLayout.setHorizontalGroup(
            reviewResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(reviewResultPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(reviewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE)
                .addContainerGap())
        );
        reviewResultPanelLayout.setVerticalGroup(
            reviewResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, reviewResultPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(reviewResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(resultPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(reviewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE))
                .addContainerGap())
        );

        mainPanel.add(reviewResultPanel);

        userPanel.setBackground(SystemColor.activeCaption);
        userPanel.setLayout(new java.awt.BorderLayout());

        usersLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        usersLabel.setText("Users");
        userPanel.add(usersLabel, java.awt.BorderLayout.PAGE_START);

        userAttributesPanel.setLayout(new java.awt.GridLayout(5, 3));

        memberSinceLabel.setText("           Member Since");
        userAttributesPanel.add(memberSinceLabel);

        javax.swing.GroupLayout userComboBoxPanel1Layout = new javax.swing.GroupLayout(userComboBoxPanel1);
        userComboBoxPanel1.setLayout(userComboBoxPanel1Layout);
        userComboBoxPanel1Layout.setHorizontalGroup(
            userComboBoxPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
        userComboBoxPanel1Layout.setVerticalGroup(
            userComboBoxPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 57, Short.MAX_VALUE)
        );

        userAttributesPanel.add(userComboBoxPanel1);

        userValueLabel1.setText("value");

        memberSinceTextField.setText("yyyy-MM-dd");

        javax.swing.GroupLayout userValuePanel1Layout = new javax.swing.GroupLayout(userValuePanel1);
        userValuePanel1.setLayout(userValuePanel1Layout);
        userValuePanel1Layout.setHorizontalGroup(
            userValuePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userValuePanel1Layout.createSequentialGroup()
                .addComponent(userValueLabel1)
                .addContainerGap(174, Short.MAX_VALUE))
            .addGroup(userValuePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(userValuePanel1Layout.createSequentialGroup()
                    .addGap(31, 31, 31)
                    .addComponent(memberSinceTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                    .addGap(31, 31, 31)))
        );
        userValuePanel1Layout.setVerticalGroup(
            userValuePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, userValuePanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(userValueLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(userValuePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(userValuePanel1Layout.createSequentialGroup()
                    .addGap(18, 18, 18)
                    .addComponent(memberSinceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(19, Short.MAX_VALUE)))
        );

        userAttributesPanel.add(userValuePanel1);

        reviewCountLabel.setText("           Review Count");
        userAttributesPanel.add(reviewCountLabel);

        reviewCountComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "=, >, <", "=", ">", "<" }));

        javax.swing.GroupLayout userComboBoxPanel2Layout = new javax.swing.GroupLayout(userComboBoxPanel2);
        userComboBoxPanel2.setLayout(userComboBoxPanel2Layout);
        userComboBoxPanel2Layout.setHorizontalGroup(
            userComboBoxPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userComboBoxPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(reviewCountComboBox, 0, 180, Short.MAX_VALUE)
                .addContainerGap())
        );
        userComboBoxPanel2Layout.setVerticalGroup(
            userComboBoxPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, userComboBoxPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(reviewCountComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                .addContainerGap())
        );

        userAttributesPanel.add(userComboBoxPanel2);

        userValueLabel2.setText("value");

        javax.swing.GroupLayout userValuePanel2Layout = new javax.swing.GroupLayout(userValuePanel2);
        userValuePanel2.setLayout(userValuePanel2Layout);
        userValuePanel2Layout.setHorizontalGroup(
            userValuePanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userValuePanel2Layout.createSequentialGroup()
                .addComponent(userValueLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 152, Short.MAX_VALUE))
            .addGroup(userValuePanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(userValuePanel2Layout.createSequentialGroup()
                    .addGap(31, 31, 31)
                    .addComponent(reviewCountTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                    .addGap(31, 31, 31)))
        );
        userValuePanel2Layout.setVerticalGroup(
            userValuePanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, userValuePanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(userValueLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(userValuePanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(userValuePanel2Layout.createSequentialGroup()
                    .addGap(18, 18, 18)
                    .addComponent(reviewCountTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(19, Short.MAX_VALUE)))
        );

        userAttributesPanel.add(userValuePanel2);

        numberOfFriendsLabel.setText("           Number of Friends");
        userAttributesPanel.add(numberOfFriendsLabel);

        numberOfFriendsComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "=, >, <", "=", ">", "<" }));

        javax.swing.GroupLayout userComboBoxPanel3Layout = new javax.swing.GroupLayout(userComboBoxPanel3);
        userComboBoxPanel3.setLayout(userComboBoxPanel3Layout);
        userComboBoxPanel3Layout.setHorizontalGroup(
            userComboBoxPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userComboBoxPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(numberOfFriendsComboBox, 0, 180, Short.MAX_VALUE)
                .addContainerGap())
        );
        userComboBoxPanel3Layout.setVerticalGroup(
            userComboBoxPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, userComboBoxPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(numberOfFriendsComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                .addContainerGap())
        );

        userAttributesPanel.add(userComboBoxPanel3);

        userValueLabel3.setText("value");

        javax.swing.GroupLayout userValuePanel3Layout = new javax.swing.GroupLayout(userValuePanel3);
        userValuePanel3.setLayout(userValuePanel3Layout);
        userValuePanel3Layout.setHorizontalGroup(
            userValuePanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userValuePanel3Layout.createSequentialGroup()
                .addComponent(userValueLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 152, Short.MAX_VALUE))
            .addGroup(userValuePanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(userValuePanel3Layout.createSequentialGroup()
                    .addGap(31, 31, 31)
                    .addComponent(numberOfFriendsTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                    .addGap(31, 31, 31)))
        );
        userValuePanel3Layout.setVerticalGroup(
            userValuePanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, userValuePanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(userValueLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(userValuePanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(userValuePanel3Layout.createSequentialGroup()
                    .addGap(18, 18, 18)
                    .addComponent(numberOfFriendsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(19, Short.MAX_VALUE)))
        );

        userAttributesPanel.add(userValuePanel3);

        averageStarsLabel.setText("           Average Stars");
        userAttributesPanel.add(averageStarsLabel);

        averageStarsComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "=, >, <", "=", ">", "<" }));

        javax.swing.GroupLayout userComboBoxPanel4Layout = new javax.swing.GroupLayout(userComboBoxPanel4);
        userComboBoxPanel4.setLayout(userComboBoxPanel4Layout);
        userComboBoxPanel4Layout.setHorizontalGroup(
            userComboBoxPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userComboBoxPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(averageStarsComboBox, 0, 180, Short.MAX_VALUE)
                .addContainerGap())
        );
        userComboBoxPanel4Layout.setVerticalGroup(
            userComboBoxPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, userComboBoxPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(averageStarsComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                .addContainerGap())
        );

        userAttributesPanel.add(userComboBoxPanel4);

        userValueLabel4.setText("value");

        javax.swing.GroupLayout userValuePanel4Layout = new javax.swing.GroupLayout(userValuePanel4);
        userValuePanel4.setLayout(userValuePanel4Layout);
        userValuePanel4Layout.setHorizontalGroup(
            userValuePanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userValuePanel4Layout.createSequentialGroup()
                .addComponent(userValueLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 152, Short.MAX_VALUE))
            .addGroup(userValuePanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(userValuePanel4Layout.createSequentialGroup()
                    .addGap(31, 31, 31)
                    .addComponent(averageStarsTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                    .addGap(31, 31, 31)))
        );
        userValuePanel4Layout.setVerticalGroup(
            userValuePanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, userValuePanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(userValueLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(userValuePanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(userValuePanel4Layout.createSequentialGroup()
                    .addGap(18, 18, 18)
                    .addComponent(averageStarsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(19, Short.MAX_VALUE)))
        );

        userAttributesPanel.add(userValuePanel4);

        numberOfVotesLabel.setText("           Number of Votes");
        userAttributesPanel.add(numberOfVotesLabel);

        numberOfVotesComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "=, >, <", "=", ">", "<" }));

        javax.swing.GroupLayout userComboBoxPanel5Layout = new javax.swing.GroupLayout(userComboBoxPanel5);
        userComboBoxPanel5.setLayout(userComboBoxPanel5Layout);
        userComboBoxPanel5Layout.setHorizontalGroup(
            userComboBoxPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userComboBoxPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(numberOfVotesComboBox, 0, 180, Short.MAX_VALUE)
                .addContainerGap())
        );
        userComboBoxPanel5Layout.setVerticalGroup(
            userComboBoxPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, userComboBoxPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(numberOfVotesComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                .addContainerGap())
        );

        userAttributesPanel.add(userComboBoxPanel5);

        userValueLabel5.setText("value");

        javax.swing.GroupLayout userValuePanel5Layout = new javax.swing.GroupLayout(userValuePanel5);
        userValuePanel5.setLayout(userValuePanel5Layout);
        userValuePanel5Layout.setHorizontalGroup(
            userValuePanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userValuePanel5Layout.createSequentialGroup()
                .addComponent(userValueLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 152, Short.MAX_VALUE))
            .addGroup(userValuePanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(userValuePanel5Layout.createSequentialGroup()
                    .addGap(31, 31, 31)
                    .addComponent(numberOfVotesTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                    .addGap(31, 31, 31)))
        );
        userValuePanel5Layout.setVerticalGroup(
            userValuePanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, userValuePanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(userValueLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(userValuePanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(userValuePanel5Layout.createSequentialGroup()
                    .addGap(18, 18, 18)
                    .addComponent(numberOfVotesTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(19, Short.MAX_VALUE)))
        );

        userAttributesPanel.add(userValuePanel5);

        userPanel.add(userAttributesPanel, java.awt.BorderLayout.CENTER);

        userSearchForPanel.setBackground(SystemColor.activeCaption);

        userSearchForLabel.setBackground(SystemColor.activeCaption);
        userSearchForLabel.setForeground(SystemColor.text);
        userSearchForLabel.setText("Search for");

        userSearchForComboBox.setBackground(SystemColor.text);
        userSearchForComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select AND, OR between attributes", "AND", "OR" }));
        userSearchForComboBox.setToolTipText("");

        javax.swing.GroupLayout userSearchForPanelLayout = new javax.swing.GroupLayout(userSearchForPanel);
        userSearchForPanel.setLayout(userSearchForPanelLayout);
        userSearchForPanelLayout.setHorizontalGroup(
            userSearchForPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userSearchForPanelLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(userSearchForPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(userSearchForComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(userSearchForLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(275, Short.MAX_VALUE))
        );
        userSearchForPanelLayout.setVerticalGroup(
            userSearchForPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(userSearchForPanelLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(userSearchForLabel)
                .addGap(18, 18, 18)
                .addComponent(userSearchForComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        userPanel.add(userSearchForPanel, java.awt.BorderLayout.PAGE_END);

        mainPanel.add(userPanel);

        queryPanel.setBackground(SystemColor.activeCaption);
        queryPanel.setToolTipText("");

        queryTextArea.setColumns(20);
        queryTextArea.setRows(5);
        queryTextArea.setText("<Show Query Here: >");
        queryScrollPane.setViewportView(queryTextArea);

        queryButton.setBackground(UIManager.getColor("Button.toolBarBorderBackground"));
        queryButton.setText("Execute Query");
        queryButton.setToolTipText("");
        queryButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                queryButtonMouseClicked(evt);
            }
        });

        businessRadioButton.setBackground(SystemColor.activeCaption);
        businessRadioButton.setForeground(SystemColor.text);
        businessRadioButton.setText("search for business");
        businessRadioButton.setToolTipText("");

        userRadioButton.setBackground(SystemColor.activeCaption);
        userRadioButton.setForeground(SystemColor.text);
        userRadioButton.setText("search for user");
        userRadioButton.setToolTipText("");

        javax.swing.GroupLayout queryPanelLayout = new javax.swing.GroupLayout(queryPanel);
        queryPanel.setLayout(queryPanelLayout);
        queryPanelLayout.setHorizontalGroup(
            queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(queryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(queryScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, queryPanelLayout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(userRadioButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(businessRadioButton, javax.swing.GroupLayout.PREFERRED_SIZE, 151, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(queryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(71, 71, 71))
        );
        queryPanelLayout.setVerticalGroup(
            queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(queryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(queryScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(queryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(queryPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(queryButton))
                    .addGroup(queryPanelLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(businessRadioButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(userRadioButton)))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        mainPanel.add(queryPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void queryButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_queryButtonMouseClicked
        // TODO add your handling code here:
        DefaultTableModel defaultTableModel;
        String[][] data;
        
        try (Connection connection = populate.getConnect();) {
            StringBuilder query = new StringBuilder();
            PreparedStatement preparedStatement;
            ResultSet rs;
            queryTextArea.setText("<Show Query Here:> \n\n");
            // if it's business turn
            if (businessRadioButton.isSelected()) {
                if (mainCategoriesString.length() == 0) {
                    ERROR("ERROR: ON ACTION ON SELECT \"Category\"!\n\n");
                    return; 
                }
                else {
                    query = getBusinessQueryString();
                }
            }
            // if it's user turn
            else if (userRadioButton.isSelected()) {
                query = getUserQueryString();
            }
            else {
                ERROR("ERROR: ON ACTION SELECT BUSINESS INTERFACE OR USER INTERFACE!\n\n");
                return;
            }
            
            if (query.length() == 0) {
                ERROR("ERROR: NO ACTION!\n\n");
                return;
            }
            preparedStatement = connection.prepareStatement(query.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = preparedStatement.executeQuery();
            
            rs.last();
            ResultSetMetaData rsmd = rs.getMetaData();
            int rowCount = rs.getRow();
            int columnCount = rsmd.getColumnCount();
            data = new String[rowCount][columnCount];
            String[] columnNames = new String[columnCount];
            
            // get column names
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = rsmd.getColumnName(i);
            }
            
            rs.beforeFirst();
            for (int i = 0; i < rowCount; i++) {
                if (rs.next()) {
                    for (int j = 1; j <= columnCount; j++) {
                        data[i][j - 1] = rs.getString(j);
                    }
                }
            }
            rs.close();
            preparedStatement.close();
            defaultTableModel = new DefaultTableModel(data, columnNames);
            resultTable.setModel(defaultTableModel);
            queryTextArea.append(query.toString());
            
            // lisening to resultTable and get the information of review
            resultTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1) {
                        JTable target = (JTable)e.getSource();
                        int row = target.getSelectedRow();
                        String id = resultTable.getModel().getValueAt(row, 1).toString();
                        try {
                            showReview(id);
                        } catch (SQLException ex) {
                            Logger.getLogger(hw3_1.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(hw3_1.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });

        } catch (SQLException ex) {
            Logger.getLogger(hw3_1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(hw3_1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_queryButtonMouseClicked

    
    private StringBuilder getBusinessQueryString() {
        StringBuilder query = new StringBuilder();
        
        //get query from category
        query.append("SELECT b.name, b.business_id, b.city, b.state, b.stars, mc.MainCategory\n")
             .append("FROM Business b, MainCategory mc\n")
             .append("WHERE b.business_id = mc.business_id AND mc.mainCategory IN (").append(mainCategoriesString).append(")\n");
        
        //check subcategory and get query from subCategory
        if (subCategoriesString.length() != 0) {
            query.append("\nAND b.business_id IN (\n")
                 .append("  SELECT bc.business_id\n")
                 .append("  FROM SubCategory bc\n")
                 .append("  WHERE bc.subCategory IN (").append(subCategoriesString).append(")\n")
                 .append(")\n");
        }
        
        //check star and votes and get query from review
        boolean from = isValidDateFormat(fromTextField.getText());
        boolean to = isValidDateFormat(fromTextField.getText());
        boolean stars = isNumeric(starTextField.getText()) && starComboBox.getSelectedIndex() > 0;
        boolean votes = isNumeric(votesTextField.getText()) && votesComboBox.getSelectedIndex() > 0;
        if ((from && to) || stars || votes) {
            query.append("\nAND b.business_id IN (\n")
                 .append("  SELECT r.business_id\n")
                 .append("  FROM Review r\n")
                 .append("  WHERE r.business_id = r.business_id\n");
            if (from && to) {
                query.append("          AND r.review_date >= '").append(getDate(fromTextField.getText())).append("' AND r.review_date <= '").append(getDate(toTextField.getText())).append("'\n");
            }
            if (stars) {
                query.append("          AND r.stars ").append(starComboBox.getSelectedItem().toString()).append(" ").append(starTextField.getText()).append("\n");
            }
                 
            if (votes) {
                query.append("          AND r.votes ").append(votesComboBox.getSelectedItem().toString()).append(" ").append(votesTextField.getText()).append("\n");
            }
            query.append(")\n");
        }
        
        System.out.println("DEBUG==============business query: \n" + query.toString());
        return query;
    }
    
    private StringBuilder getUserQueryString() {
        StringBuilder query = new StringBuilder();
        if (userSearchForComboBox.getSelectedIndex() < 1) {
            return query;
        }
        boolean memberSince = isValidDateFormat(memberSinceTextField.getText());
        boolean reviewCount = isNumeric(reviewCountTextField.getText()) && reviewCountComboBox.getSelectedIndex() > 0;
        boolean numberOfFriends = isNumeric(numberOfFriendsTextField.getText()) && numberOfFriendsComboBox.getSelectedIndex() > 0;
        boolean averageStars = isNumeric(averageStarsTextField.getText()) && averageStarsComboBox.getSelectedIndex() > 0;
        boolean numberOfVotes = isNumeric(numberOfVotesTextField.getText()) && numberOfVotesComboBox.getSelectedIndex() > 0;
        String selector = userSearchForComboBox.getSelectedItem().toString();
        query.append("SELECT y.name, y.user_id, y.yelping_since, y.review_count, y.friend_count, y.average_stars, y.votes\n")
             .append("FROM YelpUser y\n");
        query.append("WHERE y.name = y.name");
        if (memberSince || reviewCount || numberOfFriends || averageStars || numberOfVotes) {         
            //check Review Count and get value
            if (memberSince) {
                query.append(" ").append(selector)
                     .append(" y.yelping_since >= '").append(getDate(memberSinceTextField.getText())).append("'");
            }
            if (reviewCount) {
                query.append(" ").append(selector)
                     .append(" y.review_count ").append(reviewCountComboBox.getSelectedItem()).append(" ").append(reviewCountTextField.getText());
            }
            if (numberOfFriends) {
                query.append(" ").append(selector)
                     .append(" y.friend_count ").append(numberOfFriendsComboBox.getSelectedItem()).append(" ").append(numberOfFriendsTextField.getText());
            }
            if (averageStars) {
                query.append(" ").append(selector)
                     .append(" y.average_stars ").append(averageStarsComboBox.getSelectedItem()).append(" ").append(averageStarsTextField.getText());
            }
            if (numberOfVotes) {
                 query.append(" ").append(selector)
                      .append(" y.votes ").append(numberOfVotesComboBox.getSelectedItem()).append(" ").append(numberOfVotesTextField.getText());
            }
        }
        
        //check star and votes and get query from review
        boolean from = isValidDateFormat(fromTextField.getText());
        boolean to = isValidDateFormat(toTextField.getText());
        boolean star = isNumeric(starTextField.getText()) && starComboBox.getSelectedIndex() > 0;
        boolean votes = isNumeric(votesTextField.getText()) && votesComboBox.getSelectedIndex() > 0;
        if ((from && to )|| star || votes) {
            query.append("\n\nAND y.user_id IN (\n")
                 .append("  SELECT r.user_id\n")
                 .append("  FROM Review r\n")
                 .append("  WHERE r.business_id = r.business_id\n");
            if (from && to) {
                query.append("          AND r.review_date >= '").append(fromTextField.getText()).append("' AND r.review_date <= '").append(toTextField.getText()).append("'");
            }
            if (star) {
                query.append("          AND r.stars " + starComboBox.getSelectedItem().toString() + " " + starTextField.getText() + "\n");
            }                
            if (votes) {
                query.append("          AND r.votes" + votesComboBox.getSelectedItem().toString() + " " + votesTextField.getText() + "\n");
            }
            query.append(")\n");
        }
        System.out.println("DEBUG============== user query: \n" + query.toString());
        return query;
    }
    
    private void showReview(String id) throws SQLException, ClassNotFoundException {
        System.out.println("Get review information...");
        JFrame reviewFrame = new JFrame("Review");
        reviewFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        reviewFrame.setSize(500, 600);
        reviewFrame.getContentPane().setLayout(new GridLayout(1, 1));
        reviewFrame.setVisible(true);
        TableModel dataModel = new DefaultTableModel();
        JTable reviewTable = new JTable(dataModel);
        JScrollPane scrollpane = new JScrollPane(reviewTable);
        
        DefaultTableModel defaultTableModel;
        String[][] data;
        try (Connection connection = populate.getConnect();) {
            StringBuilder query = new StringBuilder();
            PreparedStatement preparedStatement;
            ResultSet rs;
            query.append("SELECT y.name, r.business_id, r.user_id, r.review_date, r.stars, r.votes\n")
                 .append("FROM Review r, YelpUser y\n")
                 .append("WHERE r.user_id = y.user_id");
            if (businessRadioButton.isSelected()) {
                query.append(" AND r.business_id = '").append(id).append("'\n");
            }
            else if (userRadioButton.isSelected()) {
                query.append(" AND r.user_id = '").append(id).append("'\n");
            }
            if (isValidDateFormat(fromTextField.getText()) && isValidDateFormat(toTextField.getText())) {
                query.append(" AND r.review_date >= '").append(getDate(fromTextField.getText())).append("' AND r.review_date <= '").append(getDate(toTextField.getText())).append("'");
            }
            if (isNumeric(starTextField.getText()) && starComboBox.getSelectedIndex() > 0){
                query.append(" AND r.stars ").append(starComboBox.getSelectedItem().toString()).append(" ").append(starTextField.getText()).append("\n");
            }
            if (isNumeric(votesTextField.getText()) && votesComboBox.getSelectedIndex() > 0) {
                query.append(" AND r.votes ").append(votesComboBox.getSelectedItem().toString()).append(" ").append(votesTextField.getText()).append("\n");
            }
            System.out.println("DEBUG================= review query: \n" + query.toString());
            preparedStatement = connection.prepareStatement(query.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = preparedStatement.executeQuery();

            rs.last();
            ResultSetMetaData rsmd = rs.getMetaData();
            int rowCount = rs.getRow();
            int columnCount = rsmd.getColumnCount();
            data = new String[rowCount][columnCount];
            String[] columnNames = new String[columnCount];

            // get column names
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = rsmd.getColumnName(i);
            }

            rs.beforeFirst();
            for (int i = 0; i < rowCount; i++) {
                if (rs.next()) {
                    for (int j = 1; j <= columnCount; j++) {
                        data[i][j - 1] = rs.getString(j);
                    }
                }
            }
            rs.close();
            preparedStatement.close();
            defaultTableModel = new DefaultTableModel(data, columnNames);
            reviewTable.setModel(defaultTableModel);
        

            reviewFrame.getContentPane().add(scrollpane);
        }
    }
    
    private boolean isNumeric(String s) {
        if (s == null || s.length() == 0) {
            return false;
        }
        try {
            Integer num = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    private static String getDate(String inDate) {
        SimpleDateFormat formater = new SimpleDateFormat(DATE_FORMAT);
        DateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(inDate);
        } catch (ParseException ex) {
            Logger.getLogger(hw3_1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return formater.format(date);
    }
    
    private static boolean isValidDateFormat(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setLenient(false);
        try {
          dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
          return false;
        }
        return true;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(hw3_1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(hw3_1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(hw3_1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(hw3_1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new hw3_1().setVisible(true);
            }
        });
    }
    
//    private JXDatePicker datePicker = new JXDatePicker(System.currentTimeMillis());

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel attributeLabel;
    private javax.swing.JPanel attributeListPanel;
    private javax.swing.JPanel attributePanel;
    private javax.swing.JScrollPane attributeScrollPane;
    private javax.swing.JComboBox<String> averageStarsComboBox;
    private javax.swing.JLabel averageStarsLabel;
    private javax.swing.JTextField averageStarsTextField;
    private javax.swing.JLabel businessLabel;
    private javax.swing.JPanel businessPanel;
    private javax.swing.JRadioButton businessRadioButton;
    private javax.swing.JComboBox<String> businessSearchForComboBox;
    private javax.swing.JLabel businessSearchForLabel;
    private javax.swing.JPanel businessSearchForPanel;
    private javax.swing.JPanel categoriesPanel;
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JTextField fromTextField;
    private javax.swing.JPanel innerReviewPanel;
    private javax.swing.JPanel mCategoryListPanel;
    private javax.swing.JPanel mainCategoryPanel;
    private javax.swing.JScrollPane mainCategoryScrollPane;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel memberSinceLabel;
    private javax.swing.JTextField memberSinceTextField;
    private javax.swing.JComboBox<String> numberOfFriendsComboBox;
    private javax.swing.JLabel numberOfFriendsLabel;
    private javax.swing.JTextField numberOfFriendsTextField;
    private javax.swing.JComboBox<String> numberOfVotesComboBox;
    private javax.swing.JLabel numberOfVotesLabel;
    private javax.swing.JTextField numberOfVotesTextField;
    private javax.swing.JButton queryButton;
    private javax.swing.JPanel queryPanel;
    private javax.swing.JScrollPane queryScrollPane;
    private javax.swing.JTextArea queryTextArea;
    private javax.swing.JLabel resultLabel;
    private javax.swing.JPanel resultPanel;
    private javax.swing.JScrollPane resultScrollPane;
    private javax.swing.JTable resultTable;
    private javax.swing.JComboBox<String> reviewCountComboBox;
    private javax.swing.JLabel reviewCountLabel;
    private javax.swing.JTextField reviewCountTextField;
    private javax.swing.JLabel reviewLabel;
    private javax.swing.JLabel reviewLabel1;
    private javax.swing.JLabel reviewLabel2;
    private javax.swing.JLabel reviewLabel3;
    private javax.swing.JLabel reviewLabel4;
    private javax.swing.JLabel reviewLabel5;
    private javax.swing.JLabel reviewLabel6;
    private javax.swing.JPanel reviewPanel;
    private javax.swing.JPanel reviewPanel1;
    private javax.swing.JPanel reviewPanel2;
    private javax.swing.JPanel reviewPanel3;
    private javax.swing.JPanel reviewResultPanel;
    private javax.swing.JPanel sCategoryListPanel;
    private javax.swing.JComboBox<String> starComboBox;
    private javax.swing.JTextField starTextField;
    private javax.swing.JLabel subCategoryLabel;
    private javax.swing.JPanel subCategoryPanel;
    private javax.swing.JScrollPane subCategoryScrollPane;
    private javax.swing.JTextField toTextField;
    private javax.swing.JPanel userAttributesPanel;
    private javax.swing.JPanel userComboBoxPanel1;
    private javax.swing.JPanel userComboBoxPanel2;
    private javax.swing.JPanel userComboBoxPanel3;
    private javax.swing.JPanel userComboBoxPanel4;
    private javax.swing.JPanel userComboBoxPanel5;
    private javax.swing.JPanel userPanel;
    private javax.swing.JRadioButton userRadioButton;
    private javax.swing.JComboBox<String> userSearchForComboBox;
    private javax.swing.JLabel userSearchForLabel;
    private javax.swing.JPanel userSearchForPanel;
    private javax.swing.JLabel userValueLabel1;
    private javax.swing.JLabel userValueLabel2;
    private javax.swing.JLabel userValueLabel3;
    private javax.swing.JLabel userValueLabel4;
    private javax.swing.JLabel userValueLabel5;
    private javax.swing.JPanel userValuePanel1;
    private javax.swing.JPanel userValuePanel2;
    private javax.swing.JPanel userValuePanel3;
    private javax.swing.JPanel userValuePanel4;
    private javax.swing.JPanel userValuePanel5;
    private javax.swing.JLabel usersLabel;
    private javax.swing.JComboBox<String> votesComboBox;
    private javax.swing.JTextField votesTextField;
    // End of variables declaration//GEN-END:variables
}