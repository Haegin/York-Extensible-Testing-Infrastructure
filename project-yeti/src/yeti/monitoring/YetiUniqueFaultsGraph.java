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

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import yeti.Yeti;
import yeti.YetiLog;
import yeti.YetiModule;

public class YetiUniqueFaultsGraph extends JPanel {
	/**
	 * The model for the JTable
	 */
	public UniqueFaultsTableModel uniqueFaultsTableModel = new UniqueFaultsTableModel();

	int currentSize = -1;


	public YetiUniqueFaultsGraph(int width, int height) {

		// we create the table
		JTable moduleTable = new JTable(uniqueFaultsTableModel);
		// we set up layout info
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		moduleTable.getColumn("").setMaxWidth(25);
		moduleTable.getColumn("Date").setMaxWidth(200);

		moduleTable.setPreferredScrollableViewportSize(new Dimension(width, height));
		moduleTable.setFillsViewportHeight(true);
		// we encapsulate the table in a scroll pane
		JScrollPane scrollPane = new JScrollPane(moduleTable);
		this.setPreferredSize(new Dimension(250,0));
		scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

		// we add the two components
		this.add(scrollPane);
	}

	public UniqueFaultsTableModel getModel() {
		return uniqueFaultsTableModel;
	}

	@SuppressWarnings("serial")
	class UniqueFaultsTableModel extends AbstractTableModel implements YetiUpdatable {
		private String[] columns = {"", "Date","Unique Faults"};
		private Object[][] values = null;

		public UniqueFaultsTableModel() {
			updateValues();
		}

		public void updateValues() {
			int nUnique =  YetiLog.proc.listOfNewErrors.size();
			if (nUnique!=currentSize)
				if ((values == null) || (nUnique!=values.length)) {
					Object[] keys = YetiLog.proc.listOfNewErrors.keySet().toArray(); 
					values =  new Object[nUnique][3];
					for (int i=0; i<nUnique; i++) {
						values[i][0] = (i+1)+" ";
						values[i][1] = YetiLog.proc.listOfNewErrors.get(keys[i]);
						values[i][2] = keys[i];
					}
					this.fireTableDataChanged();
				}
				currentSize = nUnique;
		}

		public int getColumnCount() {
			return columns.length;
		}

		public int getRowCount() {
			if (values==null)
				return 0;
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
			if (col > 0 ) {
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
