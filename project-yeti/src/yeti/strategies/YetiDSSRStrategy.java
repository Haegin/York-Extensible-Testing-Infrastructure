package yeti.strategies;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import yeti.ImpossibleToMakeConstructorException;
import yeti.Yeti;
import yeti.YetiCard;
import yeti.YetiIdentifier;
import yeti.YetiLog;
import yeti.YetiLogProcessor;
import yeti.YetiRoutine;
import yeti.YetiType;
import yeti.YetiVariable;
import yeti.environments.YetiTestManager;
import yeti.monitoring.YetiGUI;
import yeti.monitoring.YetiUpdatableSlider;

public class YetiDSSRStrategy extends YetiRandomStrategy {

	public static double INTERESTING_VALUE_INJECTION_PROBABILITY = 0.50;
	private int count = 0;
	private int control = Yeti.numberOfTests / 2;

	public YetiDSSRStrategy(YetiTestManager ytm) {
		super(ytm);

	}

	@Override
	public YetiCard getNextCard(YetiRoutine routine, int argumentNumber,
			int recursiveRank) throws ImpossibleToMakeConstructorException {
		YetiType cardType = routine.getOpenSlots()[argumentNumber];
		if (cardType.hasInterestingValues())
			if (Math.random() < INTERESTING_VALUE_INJECTION_PROBABILITY) {
				if (count < control) {

					count++;
					return cardType.getRandomInterestingVariable();

				} else {
					count++;
					return cardType.getDSSRInterestingVariable();

				}
			}

		return super.getNextCard(routine, argumentNumber, recursiveRank);
	}

	/**
	 * This overrides the strategy and chooses to pick from a set of interesting
	 * values if available.
	 * 
	 * @see yeti.strategies.YetiRandomStrategy#getNextCard(yeti.YetiRoutine,
	 *      int, int)
	 */
	@Override
	public YetiCard getNextCard(YetiRoutine routine, int argumentNumber)
			throws ImpossibleToMakeConstructorException {
		YetiType cardType = routine.getOpenSlots()[argumentNumber];
		if (cardType.hasInterestingValues())
			if (Math.random() < INTERESTING_VALUE_INJECTION_PROBABILITY) {
				if (count < control) {
					count++;
					Object value = cardType.getRandomInterestingValue();
					YetiLog.printDebugLog("Interesting value: " + value, this);
					YetiIdentifier id = YetiIdentifier.getFreshIdentifier();
					return new YetiVariable(id, cardType, value);

				} else {
					count++;
					Object value = cardType.getDSSRInterestingValue();
					YetiLog.printDebugLog("Interesting value: " + value, this);
					YetiIdentifier id = YetiIdentifier.getFreshIdentifier();
					return new YetiVariable(id, cardType, value);

				}

			}

		return super.getNextCard(routine, argumentNumber);
	}

	@SuppressWarnings("serial")
	@Override
	public JPanel getPreferencePane() {
		// we generate a panel to contain both the label and the slider
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		JLabel txt = new JLabel("% DSS interesting values: YDS2011 ");
		p.add(txt);
		txt.setAlignmentX(0);

		// we create the slider, this slider is updated both ways
		YetiUpdatableSlider useInterestingValuesSlider = new YetiUpdatableSlider(
				JSlider.HORIZONTAL,
				0,
				100,
				(int) YetiDSSRStrategy.INTERESTING_VALUE_INJECTION_PROBABILITY * 100) {

			/*
			 * (non-Javadoc) Updates the value by taking its value from the
			 * variable
			 * 
			 * @see yeti.monitoring.YetiUpdatableSlider#updateValues()
			 */
			public void updateValues() {
				super.updateValues();
				this.setValue((int) (YetiDSSRStrategy.INTERESTING_VALUE_INJECTION_PROBABILITY * 100));

			}
		};

		// we set up the listener that updates the value
		useInterestingValuesSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					int nullValuesP = (int) source.getValue();
					YetiDSSRStrategy.INTERESTING_VALUE_INJECTION_PROBABILITY = ((double) nullValuesP) / 100;
				}
			}
		});

		// Turn on labels at major tick marks.
		useInterestingValuesSlider.setMajorTickSpacing(25);
		useInterestingValuesSlider.setMinorTickSpacing(5);
		useInterestingValuesSlider.setPaintTicks(true);
		useInterestingValuesSlider.setPaintLabels(true);

		YetiGUI.allComponents.add(useInterestingValuesSlider);
		useInterestingValuesSlider.setMaximumSize(new Dimension(130, 50));
		useInterestingValuesSlider.setAlignmentX(0);
		p.add(useInterestingValuesSlider);

		TitledBorder title = BorderFactory.createTitledBorder(Yeti.strategy
				.getName() + " Panel");
		p.setBorder(title);
		p.setMinimumSize(new Dimension(300, 250));
		p.setMaximumSize(new Dimension(300, 250));
		p.setAlignmentX(Component.CENTER_ALIGNMENT);

		return p;

	}

	long oldFaults1 = 0;

	YetiCard[] oldyt = null;

	public YetiCard[] getAllCards(YetiRoutine routine)
			throws ImpossibleToMakeConstructorException {

		long currentErrors = YetiLogProcessor.numberOfNewErrors;

		YetiLog.printDebugLog("nErrors " + currentErrors, this);

		if (currentErrors > oldFaults1) {
			YetiLog.printDebugLog("found bug in the strategy", this);
			oldFaults1 = currentErrors;
			for (YetiCard yc : oldyt) {
				yc.getValue();

				if (yc.getType().getName().equals("int")) {
					int intFaultValue = ((Integer) (yc.getValue())).intValue();

					System.out.println("intFaultValue = " + intFaultValue);
					// This statement will add the fault value first

					yc.getType().addDSSRInterestingValues(
							(Integer) intFaultValue);

					yc.getType().addDSSRInterestingValues(
							(Integer) intFaultValue + 1);
					yc.getType().addDSSRInterestingValues(
							((Integer) intFaultValue - 1));
				}

				if (yc.getType().getName().equals("double")) {
					double doubleFaultValue = ((Double) (yc.getValue()))
							.doubleValue();

					// This statement will add the fault value first
					yc.getType().addDSSRInterestingValues(
							(Double) doubleFaultValue);

					System.out.println("fault value = "
							+ (Double) doubleFaultValue);
					System.out.println((Double) doubleFaultValue + 1.0);
					System.out.println(((Double) doubleFaultValue - 1.0));

					yc.getType().addDSSRInterestingValues(
							(Double) doubleFaultValue + 1);
					yc.getType().addDSSRInterestingValues(
							(Double) doubleFaultValue - 1);

				}

				if (yc.getType().getName().equals("String")) {

					String c = (String) yc.getValue();

					yc.getType().addDSSRInterestingValues((String) c);
					yc.getType().addDSSRInterestingValues((String) c + " ");
					yc.getType().addDSSRInterestingValues((String) " " + c);
					yc.getType().addDSSRInterestingValues(
							(String) c.toUpperCase());
					yc.getType().addDSSRInterestingValues(
							(String) c.toLowerCase());
					yc.getType().addDSSRInterestingValues((String) c.trim());
					yc.getType().addDSSRInterestingValues(
							(String) c.substring(2));
					yc.getType().addDSSRInterestingValues(
							(String) c.substring(1, c.length() - 1));

				}

				if (yc.getType().getName().equals("byte")) {
					byte byteFaultValue = ((Byte) (yc.getValue())).byteValue();

					// This statement will add the fault value first
					yc.getType()
							.addDSSRInterestingValues((Byte) byteFaultValue);

					yc.getType().addDSSRInterestingValues(
							(Byte) byteFaultValue + 1);
					yc.getType().addDSSRInterestingValues(
							(Byte) byteFaultValue - 1);

				}

				if (yc.getType().getName().equals("short")) {
					short shortFaultValue = ((Short) (yc.getValue()))
							.shortValue();

					// This statement will add the fault value first
					yc.getType().addDSSRInterestingValues(
							(Short) shortFaultValue);

					yc.getType().addDSSRInterestingValues(
							(Short) shortFaultValue + 1);
					yc.getType().addDSSRInterestingValues(
							(Short) shortFaultValue - 1);

				}

				if (yc.getType().getName().equals("long")) {
					long longFaultValue = ((Long) (yc.getValue())).longValue();

					// This statement will add the fault value first
					yc.getType()
							.addDSSRInterestingValues((Long) longFaultValue);

					yc.getType().addDSSRInterestingValues(
							(Long) longFaultValue + 1);
					yc.getType().addDSSRInterestingValues(
							(Long) longFaultValue - 1);

				}

				if (yc.getType().getName().equals("char")) {
					char charFaultValue = ((Character) (yc.getValue()))
							.charValue();

					// This statement will add the fault value first
					yc.getType().addDSSRInterestingValues(
							(Character) charFaultValue);

					yc.getType().addDSSRInterestingValues(
							(Character) (char) (charFaultValue + 2));
					yc.getType().addDSSRInterestingValues(
							(Character) (char) (charFaultValue - 1));

				}

				if (yc.getType().getName().equals("float")) {
					float floatFaultValue = ((Float) (yc.getValue()))
							.floatValue();

					// This statement will add the fault value first
					yc.getType().addDSSRInterestingValues(
							(Float) floatFaultValue);

					yc.getType().addDSSRInterestingValues(
							(Float) floatFaultValue + 1);
					yc.getType().addDSSRInterestingValues(
							(Float) floatFaultValue - 1);
				}
			}
		}

		oldyt = super.getAllCards(routine);
		return oldyt;
	}

	@Override
	public String getName() {
		return "Dirt Spot Sweeping Strategy";
	}

}
