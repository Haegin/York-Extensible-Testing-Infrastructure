package yeti.monitoring;

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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import yeti.Yeti;
import yeti.YetiLog;
import yeti.YetiModule;
import yeti.YetiNoCoverageException;
import yeti.YetiRoutine;
import yeti.YetiStrategy;
import yeti.YetiType;
import yeti.YetiVariable;
import yeti.environments.java.YetiJavaPrefetchingLoader;

/**
 * Class that represents the GUI for Yeti.
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Sep 3, 2009
 *
 */
public class YetiGUI implements Runnable {
	
	/*
	 * We store here the main GUI to be able to query it.
	 */
	public static YetiGUI mainGUI = null;
	
	/**
	 * The dimensions of the screen. 
	 */
	Dimension screenDimensions = null;

	/**
	 * The usable dimensions on the screen. 
	 */
	Dimension usableDimensions = null;
	/**
	 * The sampler to update the samplable objects.
	 */
	public static YetiSampler sampler = new YetiSampler(100);

	/**
	 * Checks whether the update thread should be stopped or not.
	 */
	public boolean isToUpdate = true;

	/**
	 * The timeout between updates. 
	 */
	public long nMSBetweenUpdates;

	/**
	 * Method to stop the update of the GUI.
	 */
	public void stopRoutine() {
		this.isToUpdate = false;
		this.sampler.setToUpdate(false);
	}

	/**
	 * The main frame of the gui.
	 */
	public JFrame f = null;
	
	/**
	 * All the components in the current GUI.
	 */
	public static ArrayList<YetiUpdatable> allComponents= new ArrayList<YetiUpdatable>();

	public static YetiJavaPrefetchingLoader branchCoverageIndicator = null;

	public static boolean closed;

	/**
	 * Simple creation procedure for YetiGUI.
	 * 
	 * @param nMSBetweenUpdates the time in ms between 2 updates.
	 */
	public YetiGUI(long nMSBetweenUpdates) {

		// we set the size of the window to fill in the screen
		screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();

		// we set up the dimensions of the tool
		usableDimensions=new Dimension(screenDimensions.width,screenDimensions.height-100);

		// we create the sampler
		this.nMSBetweenUpdates = nMSBetweenUpdates;
		//sampler = new YetiSampler(nMSBetweenUpdates);

		// we create the panel with the methods
		JComponent p = this.generateToolsAndMainPanel();

		// we create a frame for it and show the frame
		f = new JFrame();
		f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		f.add(p,BorderLayout.CENTER);
		
		f.addWindowListener(new WindowListener(){
			public void windowActivated(WindowEvent arg0) {}
			public void windowClosed(WindowEvent arg0) {}
			public void windowClosing(WindowEvent arg0) {
				YetiGUI.closed = true;
				Yeti.engine.stopTesting();
				if (Yeti.finished) System.exit(0);
			}
			public void windowDeactivated(WindowEvent arg0) {}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowOpened(WindowEvent arg0) {}
		});

		
		// we set the size
		f.setSize(screenDimensions);
		f.setLocation(0,0);
		
		// we set the menubar
		f.setJMenuBar(this.generateMainMenuBar());
		f.pack();
		f.setVisible(true);

		mainGUI=this;

		new Thread(this).start();
		new Thread(sampler).start();

	}


	/**
	 * A method that generates a panel for monitoring methods.
	 * 
	 * @return a panel that has a panel with all methods being tested 
	 */
	public JComponent generateMethodPane() {
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(0,3));

		// we generate the size of the panel depending on the number of methods we have
		int numberOfMethods = Yeti.testModule.routinesInModule.values().size();
		p.setPreferredSize(new Dimension(300,10*numberOfMethods));

		p.setBackground(Color.white);

		// we first sort the routines
		ArrayList<YetiRoutine> l = new ArrayList<YetiRoutine>();

		// we check all considered modules in the system
		ArrayList<YetiModule> modules =new ArrayList<YetiModule>();
		// if we test sevral modules at the same time
		if (Yeti.testModule.getCombiningModules()==null) {
			modules.add(Yeti.testModule);
			// otherwise
		} else {
			for (YetiModule mod: Yeti.testModule.getCombiningModules()) {
				modules.add(mod);
			}
		}


		// we build the list by sorting out instances
		for (YetiRoutine r: Yeti.testModule.routinesInModule.values()) {

			// if the routine is not defined in the considered module
			if (!modules.contains(r.getOriginatingModule()))
				continue;

			// if the size of the list is null, we add the first element
			if (l.size()==0) {
				l.add(r);
			} else {
				// otherwise, we iterate through and add it where needed 
				String signature = r.getSignature();
				for (int i=0; i<l.size(); i++) {
					// if we have found a bigger one, we insert the routine in there
					if (l.get(i).getSignature().compareTo(signature)>=0) {
						l.add(i, r);
						break;
					} else {
						// if we reached the maximum size, we insert it aftewards
						if (i==l.size()-1) {
							l.add(i+1,r);
							break;
						}
					}
				}
			}
		}

		// we add all routines to the panel of routines.		
		for (YetiRoutine r: l) {
			YetiRoutineGraph graph = new YetiRoutineGraph(r);
			graph.setSize(50, 30);
			p.add(graph);
			this.allComponents.add(graph);
		}

		// we generate tht toolbar
		JToolBar toolbar = new JToolBar("Method Monitor");
		toolbar.add(new JScrollPane(p));

		return toolbar;
	}


	/* (non-Javadoc)
	 * We use this method to actually update the values in real time.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		// We use these two points in time to set an interval up.
		// It happens that the update takes more time than a cycle to proceed.
		// Do not use this loop to sample values.
		while (isToUpdate) {
			for (YetiUpdatable u: allComponents) {
				u.updateValues();
			}
			try {
				Thread.sleep(nMSBetweenUpdates);
			} catch (InterruptedException e) {
				// Should never happen
				// e.printStackTrace();
			}

		}
		// we update one last time
		for (YetiUpdatable u: allComponents) {
			u.updateValues();
		}
		
	}

	/**
	 * We generate the property pane (traditionally on the left side, might become a toolbar in the future)
	 * 
	 * @return the panel containing the whole set of properties. 
	 */
	public JToolBar generatePropertyPane() {
		// the panel containing the graphs
		JPanel p = new JPanel();
		
		p.setLayout(new BoxLayout(p,BoxLayout.LINE_AXIS));

		// we add the slider for null values
		p.add(this.generateNullValuesSlider());
		p.add(Box.createRigidArea(new Dimension(15,0)));		
		
		// we add the slider for new instances
		p.add(this.generateNewInstancesSlider());
		p.add(Box.createRigidArea(new Dimension(15,0)));		

		// we add the instance cap
		JPanel pan = generateInstanceCapTextField();
		p.add(pan);
		
		// INSERT NEW PANES HERE


		// we add some glue stuff here
		p.add(Box.createHorizontalGlue());
		
		// we add the progress bar
		JPanel progress = new YetiUpdatableProgressBar();
		p.add(progress);
		
		JToolBar p1 = new JToolBar();
		p1.add(p);
		
		return p1;
	}


	/**
	 * Generate the text field containing controlling the instance cap per type of the system.
	 * 
	 * @return the panel allowing to control the instance caps.
	 */
	public JPanel generateInstanceCapTextField() {
		// we generate the textfield, the textfield is going to be formated
		// to receive only integers
		JFormattedTextField tf = new JFormattedTextField();
		tf.setColumns(10);
		tf.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		tf.setValue(YetiType.DEFAULT_MAXIMUM_NUMBER_OF_INSTANCES);
		tf.setMaximumSize(tf.getPreferredSize());
		
		// we set a listener for when the value changed
		tf.addPropertyChangeListener(new PropertyChangeListener() {
			
			// when the value is changed we update it
			public void propertyChange(PropertyChangeEvent evt) {
				Object o = evt.getNewValue();
				if ((o instanceof Integer) && (((Integer)o).intValue()>0)) {
					YetiType.DEFAULT_MAXIMUM_NUMBER_OF_INSTANCES = ((Integer)evt.getNewValue()).intValue();
				}
			}
		});
		JPanel pan = new JPanel();
		pan.setLayout(new BoxLayout(pan,BoxLayout.LINE_AXIS));
		
		pan.setMaximumSize(new Dimension(200,50));
		
		// we generate the label
		pan.add(new JLabel("Max variables per type: "));
		pan.add(tf);
		return pan;
	}

	/**
	 * Generate the split panel on the right of the interface.
	 * 
	 * @return the JComponent for the right part of the GUI
	 */
	public JComponent generateRightSubpanel() {

		//Create a split pane with the two scroll panes in it.
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				generateRightCentralSubpanel(), generateLowerRightSubpanel());
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(screenDimensions.height-500);
		
		return splitPane;
	}


	/**
	 * Generates the panel containing the graphs and the methods
	 * 
	 * @return the splitPane containing all methods and monitoring graphs.
	 */
	public JComponent generateRightCentralSubpanel() {

		// create a panel with methods monitor
		JPanel pMeth = new JPanel(new BorderLayout());
		JComponent pMInt = generateMethodPane();
		// to set up the dimensions to the screen size
		pMInt.setPreferredSize(this.usableDimensions);
		pMeth.add(pMInt, BorderLayout.CENTER);

		//Create a split pane with the two scroll panes in it.
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				generateGraphsPane(), pMeth );

		splitPane.setOneTouchExpandable(true);
		// reestablish when finalized
		splitPane.setDividerLocation(screenDimensions.width-650);

		return splitPane;
	}

	public YetiGraph getGraphFaults() {
		return graphFaults;
	}


	public YetiGraph getGraphNumberOfCallsOverTime() {
		return graphNumberOfCallsOverTime;
	}


	public YetiGraph getGraphNumberOfVariable() {
		return graphNumberOfVariable;
	}


	public YetiGraph getGraphBranchCoverage() {
		return graphBranchCoverage;
	}


	public YetiGraph getGraphNumberOfFailures() {
		return graphNumberOfFailures;
	}

	private YetiGraph graphFaults = null;

	private YetiGraph graphNumberOfCallsOverTime = null;
	
	private YetiGraph graphNumberOfVariable = null;

	private YetiGraph graphBranchCoverage = null;

	private YetiGraph graphNumberOfFailures = null;

	/**
	 * Generates the panel with the monitoring graphs.
	 * 
	 * @return the panel containing the graphs.
	 */
	public JComponent generateGraphsPane() {


		// we add the number of faults over time	
		graphFaults = new YetiGraphFaultsOverTime(YetiLog.proc,nMSBetweenUpdates);
		sampler.addSamplable(graphFaults);
		this.allComponents.add(graphFaults);

		// we add the number of calls over time
		graphNumberOfCallsOverTime = new YetiGraphNumberOfCallsOverTime(YetiLog.proc,nMSBetweenUpdates);
		sampler.addSamplable(graphNumberOfCallsOverTime);
		this.allComponents.add(graphNumberOfCallsOverTime);

		// we add the number of failures over time
		boolean coverageEnabled = true;
		try {
			Yeti.testModule.getCoverage();
		} catch (YetiNoCoverageException e) {
			coverageEnabled=false;
		}
		YetiGraph graph2 = null;
		if (coverageEnabled) {
			graph2 = new YetiGraphCoverageOverTime(Yeti.testModule,nMSBetweenUpdates);
			graphBranchCoverage = graph2;
		}
		else {
			graph2 = new YetiGraphNumberOfFailuresOverTime(YetiLog.proc,nMSBetweenUpdates);
			graphNumberOfFailures = graph2;
		}
		sampler.addSamplable(graph2);
		this.allComponents.add(graph2);


		// we add the number of failures over time
		graphNumberOfVariable = new YetiGraphNumberOfVariablesOverTime(YetiLog.proc,nMSBetweenUpdates);	
		sampler.addSamplable(graphNumberOfVariable);
		this.allComponents.add(graphNumberOfVariable);

		// the panel containing the graphs
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(0,2));

		// we add all the graphs
		p.add(graphFaults);
		p.add(graphNumberOfCallsOverTime);
		p.add(graph2);
		p.add(graphNumberOfVariable);

		return p;
	}


	/**
	 * Generates the lower right panel.
	 * Nothing here at the moment
	 * 
	 * @return the panel at the bottom of the interface.
	 */
	public JComponent generateLowerRightSubpanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
	
		/////////////////////////
		// add a module graph
		/////////////////////////
		YetiUniqueFaultsGraph graph = new YetiUniqueFaultsGraph(240,this.screenDimensions.height-250);
		this.allComponents.add(graph.getModel());
		panel.add(graph);
		return panel;
	}

	/**
	 * Generates the main panel for the interface.
	 * 
	 * @return the JSplitPane of the of the interface.
	 */
	public JSplitPane generateMainPanel() {

		//Create a split pane with the two scroll panes in it.
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				generateLeftPanel(), generateRightSubpanel());
		splitPane.setOneTouchExpandable(true);
		
		// reestablish when finalized:
		splitPane.setDividerLocation(250);

		return splitPane;

	}
	
	/**
	 * 
	 * @return
	 */
	public JSplitPane generateToolsAndMainPanel() {

		// create a panel with tools monitor
		JPanel p = new JPanel(new BorderLayout());

		// we generate the property pane
		JComponent props = generatePropertyPane();
		p.add(props, BorderLayout.CENTER);
		
		//Create a split pane with the two scroll panes in it.
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				p , generateMainPanel());
		splitPane.setOneTouchExpandable(true);
		// reestablish when finalized:
		splitPane.setDividerLocation(50);

		return splitPane;
	}
	
	/**
	 * Generates the left panel.
	 * 
	 * @return the generated panel.
	 */
	public JPanel generateLeftPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));

		/////////////////////////
		// we add the strategy panel if any
		/////////////////////////
		JPanel stratPanel = Yeti.strategy.getPreferencePane();
		if (stratPanel!=null) {
			panel.add(stratPanel);
			if (panel instanceof YetiUpdatable)
				this.allComponents.add((YetiUpdatable)stratPanel);
			
		}

		
		/////////////////////////
		// add a module graph
		/////////////////////////
		YetiModuleGraph graph = new YetiModuleGraph(240,this.screenDimensions.height-250);
		this.allComponents.add(graph.getModel());
		panel.add(graph);
		
		
		panel.setMinimumSize(new Dimension(247,this.screenDimensions.height-250));
		return panel;
	}
	
	

	/**
	 * Generates the slider for null values.
	 * 
	 * @return the pane containing null values probability.
	 */
	@SuppressWarnings("serial")
	public JComponent generateNullValuesSlider() {
		
		// we generate a panel to contain both the label and the slider
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p,BoxLayout.LINE_AXIS));
		p.add(new JLabel("% null values: "));

		
		// we create the slider, this slider is updated both ways
		YetiUpdatableSlider useNullValuesSlider = new YetiUpdatableSlider(JSlider.HORIZONTAL, 
				0, 100, (int) YetiVariable.PROBABILITY_TO_USE_NULL_VALUE*100) {

			/* (non-Javadoc)
			 * Updates the value by taking its value from the variable
			 * 
			 * @see yeti.monitoring.YetiUpdatableSlider#updateValues()
			 */
			public void updateValues() {
				super.updateValues();
				this.setValue((int)(YetiVariable.PROBABILITY_TO_USE_NULL_VALUE*100));

			}
		};
		
		// we set up the listener that updates the value
		useNullValuesSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				if (!source.getValueIsAdjusting()) {
					int nullValuesP = (int)source.getValue();
					YetiVariable.PROBABILITY_TO_USE_NULL_VALUE = ((double)nullValuesP)/100;
				}			
			}
		});

		//Turn on labels at major tick marks.
		useNullValuesSlider.setMajorTickSpacing(25);
		useNullValuesSlider.setMinorTickSpacing(5);
		useNullValuesSlider.setPaintTicks(true);
		useNullValuesSlider.setPaintLabels(true);

		this.allComponents.add(useNullValuesSlider);
		p.add(useNullValuesSlider);
		p.setMaximumSize(new Dimension(300,50));
		return p;
	}

	/**
	 * Generates the new instances slider (for the probability to use new instances).
	 * 
	 * @return the panel containing the slider for new instances.
	 */
	@SuppressWarnings("serial")
	public JComponent generateNewInstancesSlider() {

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p,BoxLayout.LINE_AXIS));
		p.add(new JLabel("% new variables: "));
		
		// we create the slider, this slider is updated both ways
		YetiUpdatableSlider generateNewInstancesSlider = new YetiUpdatableSlider(JSlider.HORIZONTAL, 
				0, 100, (int) YetiStrategy.NEW_INSTANCES_INJECTION_PROBABILITY*100) {

			/* (non-Javadoc)
			 * Updates the value by taking its value from the variable
			 * 
			 * @see yeti.monitoring.YetiUpdatableSlider#updateValues()
			 */
			public synchronized void updateValues() {
				super.updateValues();
				this.setValue((int)(100*YetiStrategy.NEW_INSTANCES_INJECTION_PROBABILITY));

			}
		};

		// we set up the listener that updates the value
		generateNewInstancesSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				synchronized (source) {
					if (!source.getValueIsAdjusting()) {
						int nullValuesP = (int)source.getValue();
						YetiStrategy.NEW_INSTANCES_INJECTION_PROBABILITY = ((double)nullValuesP)/100;
					}
				}
			}
		});

		//Turn on labels at major tick marks.
		generateNewInstancesSlider.setMajorTickSpacing(25);
		generateNewInstancesSlider.setMinorTickSpacing(5);
		generateNewInstancesSlider.setPaintTicks(true);
		generateNewInstancesSlider.setPaintLabels(true);

		this.allComponents.add(generateNewInstancesSlider);
		p.add(generateNewInstancesSlider);
		p.setMaximumSize(new Dimension(300,50));
		return p;
	}
	
	/**
	 * Generates the menu a the top of the main window.
	 * 
	 * @return the generated JMenuBar.
	 */
	public JMenuBar generateMainMenuBar() {
		JMenuBar bar = new JMenuBar();
		
		//////////////////////////////////////////////////////////
		//build the file menu.
		//////////////////////////////////////////////////////////
		JMenu yetimenu = new JMenu("Yeti");
		yetimenu.setMnemonic(KeyEvent.VK_Y);
		yetimenu.getAccessibleContext().setAccessibleDescription(
		        "Menu allowing to quit");
		bar.add(yetimenu);

		// we create the About menu item
		JMenuItem about = new JMenuItem("About Yeti");
		about.setMnemonic(KeyEvent.VK_A);
		// handles the About action
		about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Yeti is a project developed at University of York in the United Kingdom.\n\nProject Leader:\n  Manuel Oriol (manuel@cs.york.ac.uk)\n\nContributors: \n  Vasileios Dimitriadis (JML binding)\n  Faheem Kahn (Cloud Infrastructure)\n  Manuel Oriol (Main, Java binding, Monitoring)\n  Sotirios Tassis (.Net/Code Contract Binding)");
				
			}
		});
		
		// we create the Quit menu item
		JMenuItem quit = new JMenuItem("Quit    ",KeyEvent.VK_Q);
		quit.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_Q, ActionEvent.META_MASK));
		quit.setMnemonic(KeyEvent.VK_Q);

		// handles the quit action
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);		
			}
		});
		
		// we finally add items to the menu
		yetimenu.add(about);
		yetimenu.add(quit);
		
		//////////////////////////////////////////////////////////
		//build the file menu.
		//////////////////////////////////////////////////////////
		JMenu filemenu = new JMenu("File");
		filemenu.setMnemonic(KeyEvent.VK_F);
		filemenu.getAccessibleContext().setAccessibleDescription(
		        "Menu allowing to manipulate files");
		
		// creates the menu item for opening new traces file.
		JMenuItem openTraceFile = new JMenuItem("Open trace file...    ");
		openTraceFile.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_O, ActionEvent.META_MASK));
		openTraceFile.setMnemonic(KeyEvent.VK_O);
		// handles the open action traces action
		openTraceFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// we pick the file
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int result = chooser.showOpenDialog(null);
				
				// if users click on OK, we try to open the file
				if (result==JFileChooser.APPROVE_OPTION) {
					File f = chooser.getSelectedFile();
					
				}
				
			}
		});
		filemenu.add(openTraceFile);
		
		// we add file menu to the menu bar
		bar.add(filemenu);
		return bar;
	}

	
	/**
	 * Method to reset the GUI.
	 */
	public void reset() {
		stopRoutine();
		allComponents = new ArrayList<YetiUpdatable>();
		branchCoverageIndicator = null;
		sampler= new YetiSampler(100);
		if (f!=null) {
			f.setVisible(false);
			f.dispose();
		}
		f=null;
		mainGUI=null;
		
	}
}
