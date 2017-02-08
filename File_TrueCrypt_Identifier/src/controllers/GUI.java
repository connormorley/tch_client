package controllers;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;

import org.json.JSONException;

import objects.PostKey;

/*	Created by:		Connor Morley
 *  Date:			27/01/2017
 * 	Title:			TCrunch Client Main GUI
 *  Version:		1.6
 *  Notes:			Main GUI interface for the client component, provides interface for all related system functionality.
 *  
 *  References:		> GUI framework generated using Netbeans JFrame template with drag and drop utility. Since original version 
 *  				  additional elements have been added manually.
 */

public class GUI extends javax.swing.JFrame {

	public static File file;
	public static String typeOfSearch = "";
	public static String selectedItem = "";
	public static boolean connected = false;
	static int isScanning = 0;
	private static Future<String> scanningThread;
	private static Future<Integer> AMfuture;
	

    public GUI() {
        initComponents();
    }


    @SuppressWarnings("unchecked")                       
    private void initComponents() {

        button1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jTextField1 = new javax.swing.JTextField();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        fc = new JFileChooser();
        dc = new JFileChooser();
        dc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dc.setAcceptAllFileFilterUsed(false);
        
        
        //Image source = http://media.moddb.com/images/mods/1/21/20122/crosshairs.png
        ImageIcon img = new ImageIcon(".\\TCrunch.png");
        this.setIconImage(img.getImage());
        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.white);

		button1.setText("Scan");
		button1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				try {
					scanSelection(evt);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				selectTarget(evt);
			}
		});
		
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				attackSelection(evt);
			}
		});
		
		jButton3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				openOptions(evt);
			}
		});

		jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18));
		jLabel1.setText("TCrunch");

		jLabel4.setText("Status: ");

		jTextArea1.setColumns(20);
		jTextArea1.setRows(5);
		jScrollPane1.setViewportView(jTextArea1);

		ButtonGroup group = new ButtonGroup();
		group.add(jRadioButton1);
		group.add(jRadioButton2);

		jRadioButton1.setText("File Select");

		jRadioButton2.setText("Dir Select");

		jButton1.setText("Select Target");
		jButton2.setText("Attack");
		jButton3.setText("Options");
		
		jTextField1.setEditable(false);
		jTextField1.setText("<No Scan Target Selected>");


		jLabel2.setText("Selected:");
		
		jLabel6.setText("0");
		
		jLabel7.setText("0");

		jLabel5.setHorizontalTextPosition(SwingConstants.LEADING);
		jLabel5.setAlignmentX(SwingConstants.RIGHT);
		jLabel5.setText("Ready");
		
		jLabel8.setText("Total :");
		jLabel9.setText("Deep Scan :");

		jLabel10.setText("Connection Status :");
		jLabel11.setText("Disconnected");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRadioButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRadioButton2)
                        .addGap(34, 34, 34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1, Short.MAX_VALUE)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        		.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                        	.addGap(38,38,38)
                                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(100,100,100)
                                            //.addGap(130,130,130) //Original 150 all three
                                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(5,5,5)
                                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(15,15,15)
                                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(5,5,5)
                                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(275,275,275)
                                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(20,20,20)
                                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                ))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7)
                    .addComponent(jLabel9)
                    .addComponent(jLabel6)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(button1)
                    .addComponent(jButton1)
                    .addComponent(jButton3)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                   .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2))
                .addGap(21, 21, 21))
        );
        
        jTextArea1.setEditable(false);
        
		jTextArea1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() != MouseEvent.BUTTON1) {
					return;
				}
				int offset = jTextArea1.viewToModel(e.getPoint());

				try {
					if (!jTextArea1.getText().equals("")) {
						int rowStart = Utilities.getRowStart(jTextArea1, offset);
						int rowEnd = Utilities.getRowEnd(jTextArea1, offset);
						String selectedLine = jTextArea1.getText().substring(rowStart, rowEnd);
						if (!selectedLine.equals("")) {
							jTextArea1.select(rowStart, rowEnd);
							selectedItem = selectedLine;
							jLabel3.setText(selectedLine);
						}
/*						if (e.getClickCount() == 2
								&& (!selectedLine.equals("TicketID	Pebl	Source			Description")
										|| !selectedLine.equals(""))) {
							//AttackController.attack(selectedLine.substring(0, selectedLine.indexOf("	")), 0);
							AttackManager.issueAttack(selectedLine.substring(0, selectedLine.indexOf("	")));
							startAttackMonitor();
						}*/ // Old double click method of engaging attack
					}
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				} /*catch (InterruptedException e1) {
					e1.printStackTrace();
				}*/

			}
		});

		jRadioButton2.setSelected(true);

		pack();
    }
    
       
    private void openOptions(java.awt.event.ActionEvent evt) {
		OptionsScreen.display();
	}
    
    
	private void attackSelection(java.awt.event.ActionEvent evt) {
		if (connected == false)
			JOptionPane.showMessageDialog(null, "Server is currently disconnected, please check connection and try again.", "Warning",
					JOptionPane.INFORMATION_MESSAGE);
		else {
			if (!selectedItem.equals("") && jButton2.getText().equals("Attack")) {
				boolean executed = AttackManager.issueAttack(selectedItem.substring(0, selectedItem.indexOf("	")));
				if(!executed)
				{
					JOptionPane.showMessageDialog(null, "The associated wordlist database is empty!", "Warning", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				startAttackMonitor();
				jButton2.setText("Abort");
			} else if (jButton2.getText().equals("Abort")) {
				try {
					cancelAttack();
				} catch (JSONException e) {
					// to -do
					e.printStackTrace();
				} catch (IOException e) {
					// to-do
					e.printStackTrace();
				}
				jButton2.setText("Attack");
			}
			else
			{
				JOptionPane.showMessageDialog(null, "No attack target selected!", "Warning", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

    private void selectTarget(java.awt.event.ActionEvent evt) {   
		if (jRadioButton1.isSelected()) {
			int returnVal = fc.showOpenDialog(GUI.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
				jTextField1.setText(file.getPath());
				System.out.println("success");
				typeOfSearch = "file";
			} else {
				System.out.println("cancelled");
			}
		}

		else if (jRadioButton2.isSelected()) {
			int returnVal = dc.showOpenDialog(GUI.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = dc.getSelectedFile();
				jTextField1.setText(file.getPath());
				System.out.println("success");
				typeOfSearch = "dir";
			} else {
				System.out.println("cancelled");
			}

		}
    }  
    
    private void scanSelection(java.awt.event.ActionEvent evt) throws InterruptedException {   
    	if(file == null)
    	{
    		JOptionPane.showMessageDialog(null, "No Scan target selected!", "Warning",
    		JOptionPane.INFORMATION_MESSAGE);
    		return;
    	}
    	if(isScanning == 0)
    	{
			if (file != null) {
				isScanning = 1;
				jLabel5.setText("Scanning");
				jLabel5.paintImmediately(jLabel5.getVisibleRect());
				jTextArea1.setText("");

				ExecutorService exec = Executors.newSingleThreadExecutor();
				Callable<String> callable = new Callable<String>() {
					@Override
					public String call() throws InterruptedException, IOException {
						int tc = AnalysisController.analyseFile(file); //submits file for analysis, if it is solo file does text print, if directory this is handled within the subsequent threads.
						if (typeOfSearch.equals("file")) {
							jTextArea1.setText(file.getAbsolutePath() + "						TC DETECTED!");
							jLabel5.setText("Scan Complete");
							isScanning = 0;
							if (tc == 1)
								jTextArea1.setText(file.getAbsolutePath() + "					NOT TC! ");
						}
						return "Complete";
					}
				};
				scanningThread = exec.submit(callable); //Future added for cancellation option to be added!!!!
			}
		} else if(button1.getText().equals("Cancel")){
			scanningThread.cancel(true);
			button1.setText("Scan");
			button1.paintImmediately(button1.getVisibleRect());
		}
		else
			JOptionPane.showMessageDialog(null, "Scan is in the process of termination, please wait.", "Warning",
			JOptionPane.INFORMATION_MESSAGE);
	}
    
    public static void cancelAttack() throws JSONException, IOException
    {
        try {
    	AMfuture.cancel(true);
    	ArrayList<PostKey> sending = new ArrayList<PostKey>();
        sending.add(new PostKey("attackID", AttackManager.attackID));
        sending.add(new PostKey("password", "test"));
			TransmissionController.sendToServer(sending, "abortAttack");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public static void startAttackMonitor()
    {
    	ExecutorService exec = Executors.newSingleThreadExecutor();
    	Callable<Integer> callable = new Callable<Integer>() {
    		@Override
    		public Integer call() throws JSONException, IOException, InterruptedException {
    	        try {
    			String password = "";
    			while(true)
    			{
    			Thread.sleep(2500);
    			ArrayList<PostKey> sending = new ArrayList<PostKey>();
    	        sending.add(new PostKey("attackID", AttackManager.attackID));
    	        sending.add(new PostKey("password", "test"));
				password = TransmissionController.sendToServer(sending, "resultCheck");
				if(password.equals("Wordlist Exhausted"))
    	        {
    	        	AttackManager.passwordResult = password;
    	        	JOptionPane.showMessageDialog(null, "Password for target : " + AttackManager.attackTarget + " could not be found. \n Wordlist was exhausted during dictionary attack.", "Attack unsuccesful", JOptionPane.INFORMATION_MESSAGE);
    	        	jButton2.setText("Attack");
    	        	return 1;
    	        }
				else if(!password.equals("No result"))
    	        {
    	        	AttackManager.passwordResult = password;
    	        	JOptionPane.showMessageDialog(null, "Password for target : " + AttackManager.attackTarget + " has been identified. \n Password is : " + password, "Password Identified", JOptionPane.INFORMATION_MESSAGE);
    	        	jButton2.setText("Attack");
    	        	return 1;
    	        }
    			}
    	        } catch (Exception e) {
					e.printStackTrace();
				}
				return 1;
    		}
    	};
    	AMfuture = exec.submit(callable);
    	return;
    }
    
    public static void startServerMonitor()
    {
    	ExecutorService exec = Executors.newSingleThreadExecutor();
    	Callable<Integer> callable = new Callable<Integer>() {
    		@Override
    		public Integer call(){
    			try{
    			String ret = "";
    			while(true)
    			{
    			Thread.sleep(2500);
    			ArrayList<PostKey> sending = new ArrayList<PostKey>();
    	        sending.add(new PostKey("password", "test"));
    	        ret = TransmissionController.sendToServer(sending, "checkLive");
    	        if(ret.equals("Connection ok"))
    	        {
    	        	TransmissionController.firstCheck = true;
    	        	System.out.println("Server is Connected.");
    	        	ret = TransmissionController.sendToServer(sending, "clientAttackCheck");
    	        	if(!ret.equals("no") || !ret.equals("Unauthorised access"))
    	        	{
    	        		jButton2.setText("Abort");
    	        		AttackManager.attackID = ret;
    	        		startAttackMonitor();
    	        	}
    	        	jLabel11.setText("Connected");
    	        	jLabel11.paintImmediately(GUI.jLabel11.getVisibleRect());
    	        	connected = true;
    	        }
    	        else
    	        {
    	        	jLabel11.setText("Disconnected");
    	        	jLabel11.paintImmediately(GUI.jLabel11.getVisibleRect());
    	        	connected = false;
    	        }
    			}
    			} catch(Exception e)
    			{
    				
    				jLabel11.setText("Diconnected");
    				jLabel11.paintImmediately(GUI.jLabel11.getVisibleRect());
    				connected = false;
    				startServerMonitor();
    			}
				return 0;
    		}
    	};
    	exec.submit(callable);
    	return;
    }
    

    public static void main(String args[]) {
    	
    	StartController.readSettingsFile();
    	startServerMonitor();

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUI().setVisible(true);
            }
        });
    }
    
    public static javax.swing.JButton button1;
    private javax.swing.JButton jButton1;
    private static javax.swing.JButton jButton2;
    private static javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    public static javax.swing.JLabel jLabel5;
    public static javax.swing.JLabel jLabel6;
    public static javax.swing.JLabel jLabel7;
    public static javax.swing.JLabel jLabel8;
    public static javax.swing.JLabel jLabel9;
    public static javax.swing.JLabel jLabel10;
    public static javax.swing.JLabel jLabel11;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;       
    private JFileChooser fc;
    private JFileChooser dc;                 
}