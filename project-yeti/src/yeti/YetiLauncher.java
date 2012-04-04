package yeti;

/**
 
 YETI - York Extensible Testing Infrastructure
 
 Copyright (c) 2009-2010, Manuel Oriol <manuel.oriol@gmail.com> - University of York
 All rights reserved.
 
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by the University of York.
 4. Neither the name of the University of York nor the
 names of its contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.
 
 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
 **/ 

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;


/**
  * Class that represents Graphical User Interface of YETI.
  * @author  Manuel Oriol (manuel@cs.york.ac.uk)
  * @date  Jan 12, 2011
  */


@SuppressWarnings("serial")
public class YetiLauncher extends JFrame {
	
	/**
	 * 
	 * Inner class that shows the test execution in window when Run button is clicked on the YetiLauncher GUI. 
	 * 
	 * @author Manuel Oriol (manuel@cs.york.ac.uk)
	 * @date 19 Jan 2011
	 *
	 */

        public class LogsWindow extends JFrame {
                private class YETILogsPrintStream extends PrintStream {
                        LogsWindow lw = null;
                        public YETILogsPrintStream(LogsWindow lw){
                                super(System.out);
                                this.lw = lw;
                        }
                        public void        print(String s) {
                                lw.addContentToLogs(s);
                                super.print(s);
                        }
                        public void        println(String s) {
                                lw.addContentToLogs(s);
                                super.print(s);
                        }
                        public void        print(boolean b) {
                                lw.addContentToLogs(b+"");
                                super.print(b);
                        }
                        public void        println(boolean b) {
                                lw.addContentToLogs(b+"");
                                super.println(b);
                        }
                        public void        print(int b) {
                                lw.addContentToLogs(b+"");
                                super.print(b);
                        }
                        public void        println(int b) {
                                lw.addContentToLogs(b+"");
                                super.println(b);
                        }
                        public void        print(long b) {
                                lw.addContentToLogs(b+"");
                                super.print(b);
                        }
                        public void        println(long b) {
                                lw.addContentToLogs(b+"");
                                super.println(b);
                        }
                        public void        print(double b) {
                                lw.addContentToLogs(b+"");
                        super.print(b);
                        }
                        public void        println(double b) {
                                lw.addContentToLogs(b+"");
                        super.println(b);
                        }
                        public void        print(char[] ca) {
                                lw.addContentToLogs(ca + "");
                                super.print(ca);
                        }
                        public void        println(char[] ca) {
                                lw.addContentToLogs(ca + "");
                                super.print(ca);
                        }
                        public void        print(char c) {
                                lw.addContentToLogs(c+"");
                                super.print(c);
                        }
                        public void        println(char c) {
                                lw.addContentToLogs(c+"");
                                super.println(c);
                        }
                        public void        print(float f) {
                                lw.addContentToLogs(f+"");
                                super.print(f);
                        }
                        public void        println(float f) {
                                lw.addContentToLogs(f+"");
                                super.println(f);
                        }
                        public void        print(Object obj) {
                                lw.addContentToLogs(obj+"");
                                super.print(obj);
                        }
                        public void        println(Object obj) {
                                lw.addContentToLogs(obj+"");
                                super.println(obj);
                        }
                        
                }
                private JTextArea jta = new JTextArea();
                public void addContentToLogs(String s){
                        String logs = jta.getText()+"\n"+s;
                        jta.setText(jta.getText()+"\n"+s);
                        jta.setCaretPosition(logs.length());
                }
                public LogsWindow(){
                        setTitle("YETI Logs");
                        JScrollPane jsp = new JScrollPane(jta);
                        jta.setText("Blah");
                        jsp.setPreferredSize(new Dimension(2000,3000));
                        this.getContentPane().add(jsp);
                        setSize(400,500);
                        setLocation(400,150);
                        System.setOut( new YETILogsPrintStream(this));
                        setResizable(true);
                        setVisible(true);
                }

        }

        /**
         * langArray contains the list of languages supported by YETI.
         */
        private String[] langArray = {"Java", ".NET", "JML"};

        /**
         * arry of arguments
         */
        private String[] command = new String[6];

        /**
         * Time Combobox allow users to select the test session times from the 
available list.
         */
        private JComboBox timeComboBox;

        /**
         * TimeArray contain the values of times for test session.
         */
        private String[] timeArray = {"5", "10", "15", "20", "30", 
"40","50","60", "70", "80", "90", "100", "150", "200", "300", "400", 
"500", "1000"};

        /**
         * Array containing the options of minute or seconds for a test session.
         */
        private String[] timeArray1 = {"mn", "s"};

        /**
         * strategy array stores the various strategies of testing.
         */
        private String[] strategy = {"-randomPlus", "-randomPlusPeriodic", 
"-randomPlusDecreasing"};

        /**
         * Main Label on top of the GUI showing York Extensible Testing 
Infrastructure.
         */
        private JLabel headingLabel;

        /**
         * The language label.
         */
        private JLabel langLabel;

        /**
         * The combo box to set the language for the test sessions from the 
availble list.
         */
        private JComboBox setLanguage;

        /**
         * The time label.
         */
        private JLabel timeLabel;

        /**
         * The combobox to set time for the current test session.
         */
        private JComboBox setTime;

        /**
         * The combobox to select the test session in seconds or minutes.
         */
        private JComboBox setMinSec;

        /**
         * The label for GUI checkbox.
         */
        private JLabel guiLabel;

        /**
         * The checkbox for GUI Session.
         */
        private JCheckBox guiSession;

        /**
         * The label for logs.
         */
        private JLabel logsLabel;

        /**
         * The checkbox for saving or not saving test logs.
         */
        private JCheckBox testLogs;

        /**
         * The label for select test files.
         */
        private JLabel selectFilesLabel;

        /**
         * The button to run test.
         */
        private JButton runTest;


        /**
         * The string variable to store the test session file name.
         */
        private String fileName;

        /**
         * The text field that allow user to type the file names to be tested. 
Files must be in the classpath.
         */
        private JTextField fileNameTextField;

        /**
         * The button to exit GUI.
         */
        private JButton exit;

        /**
         * The label for strategy.
         */
        private JLabel strategyLabel;

        /**
         * The combobox to set the strategy from the given strategies.
         */
        private JComboBox setStrategy;

        /**
         * The thread object to open the Test Execution window by a new thread 
for solving visibility problem.
         */
        Thread t1 = new Thread(new Threado());

        //private BrowseButtonHandler browseBHandler;
        private RunButtonHandler runBHandler;
        private ExitButtonHandler exitBHandler;

        /**
         * The constructor defining the layout, frame, and default settings.
         */
        public YetiLauncher () {

                /**
                 * The GridbagLayout is used for GUI.
                 */
                setLayout(new GridBagLayout());

                GridBagConstraints c = new GridBagConstraints(); // for object position.

                this.getContentPane().setBackground(Color.GREEN);

                setTitle("YETI-York Extensible Testing Infrastructure");
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setVisible(true);
                setSize(500,500);
                setLocation(400,150);



                headingLabel = new JLabel("York Extensible Testing Infrastructure");


                c.gridx = 0;            //object x axis.
                c.gridy = 0;            //object y axis.
                c.gridwidth  = 4;     //row span but not working for now
                c.gridheight = 2;     //col span but not working for now.
                c.insets = new Insets(15,15,15,15); //cell padding in pixels
                //c.anchor = GridBagConstraints.PAGE_END;
                headingLabel.setFont(new Font("Sarif", Font.BOLD, 15));
                add(headingLabel, c);

                langLabel = new JLabel("Test Language:");
                c.gridx = 1;
                c.gridy = 2;
                c.gridwidth = 1;
                c.gridheight =1;
                c.insets = new Insets(15,15,15,15);
                add(langLabel, c);

                setLanguage = new JComboBox(langArray);
                c.gridx = 2;
                c.gridy = 2;
                c.insets = new Insets(15,15,15,15);
                setLanguage.setSelectedIndex(0);
                add(setLanguage, c);

                strategyLabel = new JLabel("Test Strategy");
                c.gridx = 1;
                c.gridy = 3;
                c.gridwidth = 1;
                c.gridheight =1;
                c.insets = new Insets(15,15,15,15);
                add(strategyLabel, c);

                setStrategy = new JComboBox(strategy);
                c.gridx = 2;
                c.gridy = 3;
                c.gridwidth = 2;
                c.gridheight =1;
                c.insets = new Insets(15,15,15,15);
                setStrategy.setSelectedIndex(0);
                add(setStrategy, c);


                timeLabel = new JLabel("Test Duration:");
                c.gridx = 1;
                c.gridy = 4;
                c.gridwidth = 1;
                c.gridheight =1;
                c.insets = new Insets(15,15,15,15);
                add(timeLabel, c);

                timeComboBox = new JComboBox(timeArray);
                c.gridx = 2;
                c.gridy = 4;
                //c.insets = new Insets(15,15,15,15);
                timeComboBox.setSelectedIndex(2);
                add(timeComboBox, c);

                setMinSec = new JComboBox(timeArray1);
                c.gridx = 3;
                c.gridy = 4;
                //c.insets = new Insets(15,15,15,15);
                setMinSec.setSelectedIndex(0);
                add(setMinSec, c);

                guiLabel = new JLabel("GUI test Session");
                c.gridx = 1;
                c.gridy = 5;
                c.insets = new Insets(15,15,15,15);
                add(guiLabel, c);

                guiSession = new JCheckBox("",null, true);
                c.gridx = 2;
                c.gridy = 5;
                c.insets = new Insets(15,15,15,15);
                add(guiSession, c);

                logsLabel = new JLabel("Save Test Logs");
                c.gridx = 1;
                c.gridy = 6;
                c.insets = new Insets(15,15,15,15);
                add(logsLabel, c);

                testLogs = new JCheckBox("",null, false);
                c.gridx = 2;
                c.gridy = 6;
                c.insets = new Insets(15,15,15,15);
                add(testLogs, c);

                selectFilesLabel = new JLabel("Select Test Files");
                c.gridx = 1;
                c.gridy = 7;
                c.insets = new Insets(15,15,15,15);
                add(selectFilesLabel, c);


                fileNameTextField = new JTextField("yeti.test.YetiTest", 15);
                c.gridx = 2;
                c.gridy = 7;
                c.gridwidth = 2;
                c.gridheight =1;
                c.insets = new Insets(15,15,15,15);
                add(fileNameTextField, c);


                runTest = new JButton("Run Test");
                c.gridx = 2;
                c.gridy = 8;
                c.gridwidth = 1;
                c.gridheight =1;
                c.insets = new Insets(15,15,15,15);
                add(runTest, c);
                runBHandler = new RunButtonHandler();
                runTest.addActionListener(runBHandler);

                exit = new JButton("Exit Test");
                c.gridx = 3;
                c.gridy = 8;
                c.insets = new Insets(15,15,15,15);
                add(exit, c);
                exitBHandler = new ExitButtonHandler();
                exit.addActionListener(exitBHandler);


                pack(); // pack method collect the scattered objects on gui to one point.

        }


        /**
         * The implementation of thread. The thread starts the test session and 
open another window showing the execution of test in real time.
         */
        private class Threado implements Runnable{
                public void run(){
                        try{
                                Yeti.YetiRun(command);
                        }
                        catch(Exception e){

                        }
                }
        }

        /**
         * Main method of the class.
         */
        public static void main(String[] args){

                YetiLauncher gui = new YetiLauncher();



        }


        /**
         * The inner class to controll the action of run button. It takes 
selected values of all the components and store it in array command.
         */
        private class RunButtonHandler implements ActionListener {

                public void actionPerformed(ActionEvent e) {

                        if(guiSession.isSelected())
                                command[5]= "-gui";
                        else {
                                command = new String[5];
                                new LogsWindow();
                        }

                        String s0 = (String) (setLanguage.getSelectedItem());
                        String s1 = "-java";
                        if (s0.equals(".NET"))
                                s1="-dotnet";
                        if (s0.equals("JML"))
                                s1="-jml";

                        command[0]= s1;
                        String str1, str2, str3, time;
                        str1 = "-time=";
                        str2 = (String)timeComboBox.getSelectedItem();
                        str3 = (String)setMinSec.getSelectedItem();
                        time = str1+str2+str3;
                        command[1]= time;

                        String str4, str5, path;
                        str4 = "-testModules=";
                        //str5 = "yeti.test.YetiTest";
                        str5 = fileNameTextField.getText();
                        path = str4+str5;
                        command[2]= path;




                        if(testLogs.isSelected())
                                command[3]= "-logs";
                        else command[3]= "-nologs";

                        command[4] = (String) (setStrategy.getSelectedItem());


                        for (int i =0; i<command.length; i++)
                                System.out.print(command[i]);
                        setVisible(false);
                        t1.start();

                }


        }

        /**
         * The class to control the exit button and terminate the GUI once clicked.
         */
        private class ExitButtonHandler implements ActionListener{

                public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                }




        }
}
