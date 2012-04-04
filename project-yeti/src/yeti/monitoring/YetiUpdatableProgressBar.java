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

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import yeti.Yeti;

public class YetiUpdatableProgressBar extends JPanel implements YetiUpdatable{

	/**
	 * The progress bar itself.
	 */
	JProgressBar prog = new JProgressBar(0,100);
	
	/**
	 * A simple constructor for a nice progress bar.
	 */
	public YetiUpdatableProgressBar() {
		super();
		// we add an unecesary label
		JLabel label = new JLabel("Testing session:");
		this.add(label);
		label.setAlignmentX(.5f);
		
		// we set preferences up for the progress bar
		prog.setMaximumSize(new Dimension (300,20));
		prog.setPreferredSize(new Dimension (300,20));
		prog.setStringPainted(true);
		
		// we add the progress bar to the panel
		this.add(prog);
		prog.setAlignmentX(.5f);
		
		// we add it to compontents to update
		YetiGUI.allComponents.add(this);
		// sets the layount correctly
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

	}

	/* (non-Javadoc)
	 * @see yeti.monitoring.YetiUpdatable#updateValues()
	 */
	public void updateValues() {
		int progress = Yeti.engine.getProgress();
		prog.setValue(progress);
		if (progress==100) {
			
		}
	}
	
	

}
