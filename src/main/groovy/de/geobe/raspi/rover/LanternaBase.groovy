package de.geobe.raspi.rover

import com.googlecode.lanterna.TerminalPosition
import com.googlecode.lanterna.gui2.BasicWindow
import com.googlecode.lanterna.gui2.Direction
import com.googlecode.lanterna.gui2.LinearLayout
import com.googlecode.lanterna.gui2.MultiWindowTextGUI
import com.googlecode.lanterna.gui2.Panel
import com.googlecode.lanterna.gui2.WindowBasedTextGUI
import com.googlecode.lanterna.screen.Screen
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.MouseCaptureMode
import com.googlecode.lanterna.terminal.Terminal

class LanternaBase {
    final Screen screen
    final WindowBasedTextGUI textGUI
    final BasicWindow window
    final Panel basePanel

    LanternaBase(String title = 'Title', Direction panelDirection = Direction.HORIZONTAL) {
        try {
            screen = new DefaultTerminalFactory()
                    .setMouseCaptureMode(MouseCaptureMode.CLICK_RELEASE_DRAG_MOVE)
                    .createScreen()
            textGUI = new MultiWindowTextGUI(screen)
            screen.startScreen()
            screen.setCursorPosition(new TerminalPosition(3, 5))
            window = new BasicWindow(title)
            basePanel = new Panel(new LinearLayout(panelDirection))
            window.setComponent basePanel
        } catch (IOException ex) {
            ex.printStackTrace()
        }

    }

    def init() {
        textGUI.addWindowAndWait window
        textGUI.updateScreen()
    }
}
