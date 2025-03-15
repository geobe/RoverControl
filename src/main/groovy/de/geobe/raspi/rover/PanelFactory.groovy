package de.geobe.raspi.rover

import com.googlecode.lanterna.gui2.*
import com.googlecode.lanterna.input.KeyStroke
import de.geobe.raspi.rover.IMotorControl.Side
import de.geobe.raspi.rover.IMotorControl.Motion
import static de.geobe.raspi.rover.MotorControlUI.getMotorControl

class PanelFactory {
    static Component buildMotorPanel(/*IMotorControl motorControl, */Side side, title = '') {
        String direction = '<>'
        Panel motorPanel = new Panel(new LinearLayout())
        ActionListBox runBox = new ActionListBox()
        Label msgLabel = new Label(direction)
        runBox.addItem('Vorwärts') {
            direction = '->'
            msgLabel.setText "$direction ${motorControl.setMotion(side, Motion.FORWARD).speed}" +
                    "@${motorControl.getFrequency(side).frequency}"
        }
        runBox.addItem('Halt') {
            direction = '<>'
            msgLabel.setText "$direction ${motorControl.setMotion(side, Motion.STOP).speed}" +
                    "@${motorControl.getFrequency(side).frequency}"
        }
        runBox.addItem('Rückwärts') {
            direction = '<-'
            msgLabel.setText "$direction ${motorControl.setMotion(side, Motion.BACKWARD).speed}" +
                    "@${motorControl.getFrequency(side).frequency}" }
        motorPanel.addComponent(runBox.withBorder(Borders.singleLineBevel()),
                GridLayout.createHorizontallyEndAlignedLayoutData(1))
        // Speed control
        Panel speedPanel = new Panel(new LinearLayout(Direction.VERTICAL))
        Button plusSpeed =
                new Button('+',
                        {
                            float sp = Math.min(100.0f, motorControl.getSpeed(side).speed + 5.0f)
                            msgLabel.setText("$direction ${motorControl.setSpeed(side, sp).speed}" +
                                    "@${motorControl.getFrequency(side).frequency}")
                        }
                )
        Button minusSpeed =
                new Button('-',
                        {
                            float sp = Math.max(0f, motorControl.getSpeed(side).speed - 5f)
                            msgLabel.setText("$direction ${motorControl.setSpeed(side, sp).speed}" +
                                    "@${motorControl.getFrequency(side).frequency}")
                        }
                )
        speedPanel.addComponent(plusSpeed.withBorder(Borders.singleLineBevel()))
        speedPanel.addComponent(minusSpeed.withBorder(Borders.singleLineBevel()))
        motorPanel.addComponent(speedPanel.withBorder(Borders.singleLine('Speed')),
                GridLayout.createHorizontallyEndAlignedLayoutData(1))
        motorPanel.addComponent(msgLabel.withBorder(Borders.singleLineBevel()),
                GridLayout.createHorizontallyEndAlignedLayoutData(1))
        // Frequency control
        Panel freqPanel = new Panel(new LinearLayout(Direction.VERTICAL))
        Button plusFreq =
                new Button('+',
                        {
                            int freq = motorControl.getFrequency(side).frequency * 2
                            float speed = motorControl.getSpeed(side).speed
                            msgLabel.setText("$direction $speed@${motorControl.setFrequency(side, freq).frequency}")
                        }
                )
        Button minusFreq =
                new Button('-',
                        {
                            int freq = motorControl.getFrequency(side).frequency.intdiv(2)
                            float speed = motorControl.getSpeed(side).speed
                            msgLabel.setText("$direction $speed@${motorControl.setFrequency(side, freq).frequency}")
                        }
                )
        freqPanel.addComponent(plusFreq.withBorder(Borders.singleLineBevel()))
        freqPanel.addComponent(minusFreq.withBorder(Borders.singleLineBevel()))
        motorPanel.addComponent(freqPanel.withBorder(Borders.singleLine('Frequenz')),
                GridLayout.createHorizontallyEndAlignedLayoutData(1))
        if (title) {
            motorPanel.withBorder(Borders.singleLine(title))
        } else {
            motorPanel.withBorder(Borders.singleLineBevel())
        }
    }
}
//
//class MyButton extends Button {
//
//    MyButton(String label, Runnable action) {
//        super(label, action)
//    }
//
//    @Override
//    Result handleKeyStroke(KeyStroke keyStroke) {
//        return super.handleKeyStroke(keyStroke)
//    }
//}