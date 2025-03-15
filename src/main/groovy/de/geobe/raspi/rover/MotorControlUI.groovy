package de.geobe.raspi.rover

import com.googlecode.lanterna.gui2.Borders
import com.googlecode.lanterna.gui2.Button
import com.googlecode.lanterna.gui2.LinearLayout
import com.googlecode.lanterna.gui2.Panel
import de.geobe.raspi.rover.IMotorControl.Side

/**
 * Mock implementation for test and ui development
 */
class MotorControlUI extends LanternaBase {

    static IMotorControl getMotorControl() {
        if (!mctl) {
            mctl = mctlClass.getDeclaredConstructor(String.class).newInstance(restUri)
        }
        mctl
    }

    static String restUri = null

    static private IMotorControl mctl
    static private Class<? extends IMotorControl> mctlClass = MockMotorControl.class

    MotorControlUI(IMotorControl motorCtl = new MockMotorControl(), String title = 'Motorsteuerung') {
        super(title)
        mctlClass = motorCtl.class
        basePanel.addComponent(PanelFactory.buildMotorPanel(/*motorControl,*/ Side.LEFT, 'links'))
        basePanel.addComponent(PanelFactory.buildMotorPanel(/*motorControl,*/ Side.RIGHT, 'rechts'))
        Panel exitPanel = new Panel(new LinearLayout())
        Button shutdownButton = new Button('Pi Stop',
                {
                    motorControl.shutdown(false)
                    screen.close()
//                    mctl = null
                })
        Button exitButton = new Button('Exit',
                {
                    screen.close()
                    motorControl.shutdown(true)
                })
        exitPanel.addComponent(exitButton.withBorder(Borders.singleLineBevel()))
        exitPanel.addComponent(shutdownButton.withBorder(Borders.singleLineBevel()))
        basePanel.addComponent(exitPanel.withBorder(Borders.singleLine('beenden')))
    }

    static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "TRACE");
        def arch = System.getProperty('os.arch')
        println "started at $arch"
        if (arch.startsWith('aarch64')) {
            def ui = new MotorControlUI(new BasicMotorControl())
            println 'created'
            ui.init()
            println 'initialized'
        } else {
            MotorControlUI ui
            try {
                restUri = 'http://192.168.101.134:7070'
                ui = new MotorControlUI(new JMotorControlClient(restUri))
            } catch (RuntimeException rex) {
                ui = new MotorControlUI(new MockMotorControl())
            }
            println 'created'
            ui.init()
            println 'initialized'
        }
    }
}
