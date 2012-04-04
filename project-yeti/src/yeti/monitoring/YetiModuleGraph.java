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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import yeti.Yeti;
import yeti.YetiModule;

/**
 * Class that represents a table with all modules loaded and the ones tested/nottested.
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Sep 16, 2009
 *
 */
@SuppressWarnings("serial")
public class YetiModuleGraph extends JPanel {

	/**
	 * The model for the JTable
	 */
	public ModuleTableModel moduleTableModel = new ModuleTableModel();
	
	/**
	 * The button to add a module.
	 */
	public JButton addModule = new JButton("Add module to test");

	public YetiModuleGraph(int width, int height) {

		// we create the table
		JTable moduleTable = new JTable(moduleTableModel);
		// we set up layout info
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		moduleTable.getColumn("").setMaxWidth(25);
		moduleTable.setPreferredScrollableViewportSize(new Dimension(width, height));
		moduleTable.setFillsViewportHeight(true);
		// we encapsulate the table in a scroll pane
		JScrollPane scrollPane = new JScrollPane(moduleTable);
		this.setPreferredSize(new Dimension(250,0));
		scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		this.addModule.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.addModule.setActionCommand("AddModule");
		// we add the part to load the module to load
		this.addModule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// we ask the user which module to load
			    if ("AddModule".equals(e.getActionCommand())) {
			    	String value = JOptionPane.showInputDialog(null,
			    			"Enter the name of the module to add:",
			    			"Add module",
			    			JOptionPane.PLAIN_MESSAGE);
			    	// if the module is not null, we call the delegated method.
			    	if (value!=null) {
			    		Yeti.pl.getInitializer().addModule(value);
			    	}
			    }				
			}
		});

		// we add the two components
		this.add(scrollPane);
		this.add(this.addModule);
	}

	public ModuleTableModel getModel() {
		return moduleTableModel;
	}

	class ModuleTableModel extends AbstractTableModel implements YetiUpdatable {
		private String[] columns = {"","Module Name"};
		private Object[][] values = null;

		public ModuleTableModel() {
			updateValues();
		}

		public void updateValues() {
			int nModules =  YetiModule.allModules.size();
			if ((values == null) || (nModules!=values.length)) {
				Object[] keys = YetiModule.allModules.keySet().toArray();
				
				int skip0 = 0;
				for (int i=0; i<nModules; i++) {
					if (((String)keys[i]).startsWith("__yeti_")) {
						skip0++;
					}
					
				}
				
				values =  new Object[nModules-skip0][2];
				
				int skip = 0;
				for (int i=0; i<nModules-skip0; i++) {
					while (((String)keys[i+skip]).startsWith("__yeti_")||i+skip>=nModules) {
						skip++;
					}
					values[i][0] = new Boolean(Yeti.testModule.containsModuleName((String)keys[i+skip]));
					values[i][1] = keys[i+skip];
				}
				this.fireTableDataChanged();
			}
		}

		public int getColumnCount() {
			return columns.length;
		}

		public int getRowCount() {
			return values.length;
		}

		public String getColumnName(int col) {
			return columns[col];
		}

		public Object getValueAt(int row, int col) {
			return values[row][col];
		}

		@SuppressWarnings("unchecked")
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		/*
		 * Don't need to implement this method unless your table's
		 * editable.
		 */
		public boolean isCellEditable(int row, int col) {
			//Note that the data/cell address is constant,
			//no matter where the cell appears onscreen.
			if (col < 1) {
				return true;
			} else {
				return false;
			}
		}

		/*
		 * Don't need to implement this method unless your table's
		 * data can change.
		 */
		public void setValueAt(Object value, int row, int col) {
			if (col == 0) {
				values[row][col] = value;
				fireTableCellUpdated(row, col);
			}
		}
	}

}
